package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.xihua.weibopaper.common.GsonRequest;
import com.xihua.weibopaper.fragment.ContentFragment;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        gruopList = new ArrayList<>();
        gruopList.add("好友");
        gruopList.add("IT");
        gruopList.add("娱乐");
        gruopList.add("学习");
        gruopList.add("好友");
        gruopList.add("IT");
        gruopList.add("娱乐");
        gruopList.add("学习");
        initTabLayout();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        GsonRequest<Weather> request = new GsonRequest<Weather>("http://www.weather." +
                "com.cn/data/sk/101010100.html",
                Weather.class, new Response.Listener<Weather>() {
            @Override
            public void onResponse(Weather response) {
                Toast.makeText(MainActivity.this,response.getWeatherinfo().getCity(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error+"",Toast.LENGTH_SHORT).show();
            }
        });


//        StringRequest request = new StringRequest("https://www.baidu.com",
//                new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this,error+"",Toast.LENGTH_SHORT).show();
//            }
//        });

        requestQueue.add(request);
    }

    class Weather {

        private WeatherInfo weatherinfo;

        public WeatherInfo getWeatherinfo() {
            return weatherinfo;
        }

        public void setWeatherinfo(WeatherInfo weatherinfo) {
            this.weatherinfo = weatherinfo;
        }

        class WeatherInfo {

            private String city;

            private String temp;

            private String time;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getTemp() {
                return temp;
            }

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

        }
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
