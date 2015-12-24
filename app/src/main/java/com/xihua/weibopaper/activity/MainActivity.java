package com.xihua.weibopaper.activity;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.bean.UnreadCount;
import com.xihua.weibopaper.bean.WeiBoUser;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.service.UnreadService;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.fragment.ContentFragment;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.ImageUtils;
import com.xihua.weibopaper.utils.PollingUtils;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package com.xihua.weibopaper.activity
 * @ClassName: MainActivity
 * @Description:主页面
 * @author cyq7on
 * @date 2015/12/9 15:47
 * @version V1.0
 */

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Fragment> fragmentList;
    private List<String> gruopList;
    private Oauth2AccessToken accessToken;
    private CircleImageView ivUser;
    private TextView tvName;
    private NavigationView navigationView;
    private UnreadReceiver unreadReceiver;
    private LocalBroadcastManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        manager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(UnreadService.ACTION_UNREAD_CHANGED);
        unreadReceiver = new UnreadReceiver();
        manager.registerReceiver(unreadReceiver, intentFilter);
        PollingUtils.startPollingService(this, 5, UnreadService.class, UnreadService.
                ACTION_UNREAD_CHANGED);

    }

    @Override
    public void initData() {
        accessToken = AccessTokenKeeper.readAccessToken(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        params.put("source",Constants.APP_KEY);
        params.put("access_token", accessToken.getToken());
        params.put("uid", accessToken.getUid());
        String url = GsonRequest.getUrl(Constants.USER_SHOW,params);
        GsonRequest<WeiBoUser> requestImage = new GsonRequest<>(url,
                WeiBoUser.class, new Response.Listener<WeiBoUser>() {
            @Override
            public void onResponse(WeiBoUser response) {
                if (response.getAvatar_large() != null) {
                    ImageUtils.getInstance().displayImage(requestQueue, response.getAvatar_large(),
                            ivUser);
                }
                if (response.getScreen_name() != null) {
                    tvName.setText(response.getScreen_name());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShort(MainActivity.this, error.getMessage());
            }
        });
        requestQueue.add(requestImage);
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "刷新中...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        ivUser = (CircleImageView) view.findViewById(R.id.iv_user);
        tvName = (TextView) view.findViewById(R.id.tv_name);

        gruopList = new ArrayList<>();
        gruopList.add("全部微博");
        gruopList.add("互相关注");
        gruopList.add("朋友圈");

        initTabLayout();

    }


    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragmentList = new ArrayList<>();
        for (int i = 0;i < gruopList.size();i ++) {
            tabLayout.addTab(tabLayout.newTab());
            fragmentList.add(new ContentFragment());
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return  fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return gruopList.get(position);
            }

        });
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_write:
                break;
            case R.id.action_group:
                break;
            case R.id.action_offline:
                break;
            case R.id.action_exit:
                MyApplication.finishAll();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PollingUtils.stopPollingService(this, UnreadService.class, UnreadService.
                ACTION_UNREAD_CHANGED);
        manager.unregisterReceiver(unreadReceiver);
    }

    private class UnreadReceiver extends BroadcastReceiver {
        private NotificationManager manager;
        private Notification notification;

        public UnreadReceiver() {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Menu menu;
            MenuItem item;
            int mention = intent.getIntExtra("mention",0);
            if (mention != 0) {
                menu = navigationView.getMenu();
                item = menu.getItem(1);
                item.setTitle("提及 " + Integer.toString(mention));
                initNotification(0,mention);
            }
            int cmt = intent.getIntExtra("cmt", 0);
            if (cmt != 0) {
                menu = navigationView.getMenu();
                item = menu.getItem(2);
                item.setTitle("评论 " + Integer.toString(cmt));
                initNotification(1, cmt);
            }
            int dm = intent.getIntExtra("dm", 0);
            if (dm != 0) {
                menu = navigationView.getMenu();
                item = menu.getItem(3);
                item.setTitle("私信 " + Integer.toString(dm));
                initNotification(2,dm);
            }
        }


        private void initNotification(int which,int num) {
            int icon = 0;
            String title = "";
            switch (which) {
                case 0:
                    icon = R.mipmap.ic_drawer_at;
                    title = String.format("%d个提及你的评论", num);

                    break;
                case 1:
                    icon = R.mipmap.ic_question_answer_grey600_24dp;
                    title = String.format("%d条新评论", num);
                    break;
                case 2:
                    icon = R.mipmap.ic_email_grey600_24dp;
                    title = String.format("%d条新私信", num);
                    break;
            }
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            notification = new Notification.Builder(MainActivity.this)
                    .setSmallIcon(icon) // 设置状态栏中的小图片，
                    // 尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，
                    // 如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap icon)
                    .setTicker("有人找啦")// 设置在status bar上显示的提示文字
                    .setContentTitle(title)// 设置在下拉status
                            // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                    .setContentText("来自WeiboPaper")// TextView中显示的详细内容
                    .setContentIntent(pendingIntent) // 关联PendingIntent
//                    .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。
                    // 这个number同时也起到一个序列号的作用，如果多个触发多个通知（同一ID），
                    // 可以指定显示哪一个。
                    .getNotification(); // 需要注意build()是在API level
                                // 16及之后增加的，在API11中可以使用getNotificatin()来代替
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            manager.notify(0, notification);//通过通知管理器来发起通知。如果id不同，则每发起，
                                            // 在status那里增加一个提示
        }

    }
}
