package com.xihua.weibopaper.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;
import cn.finalteam.galleryfinal.widget.zoonview.PhotoViewAttacher;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.activity
 * @ClassName: ImageDetailActivity
 * @Description: 查看图片详情
 * @date 2016/1/28/15:41
 */

public class ImageDetailActivity extends BaseActivity {

    private ViewPager viewPager;
    private ArrayList<String> picUrls;
    private List<PhotoView> photoViewList;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        initData();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        index = getIntent().getIntExtra("index", 0);
        setToolbar(String.format("%d/%d", index + 1, picUrls.size()));
        viewPager = (ViewPager) findViewById(R.id.vp_image);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return picUrls.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView photoView = new PhotoView(container.getContext());
                ImageLoader.getInstance().displayImage(picUrls.get(position), photoView,
                        MyApplication.getOptions());
                photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                       finish();
                    }
                });
                container.addView(photoView);
                photoViewList.add(photoView);
                return photoView;
            }
        });
        viewPager.setCurrentItem(index, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(String.format("%d/%d", position + 1, picUrls.size()));
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {
        picUrls = getIntent().getStringArrayListExtra("urlList");
        photoViewList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                download();
                break;
            case R.id.action_original:
                //原图
                ImageLoader.getInstance().displayImage(picUrls.get(index).
                                replace("bmiddle", "large"),
                        photoViewList.get(index), MyApplication.getOptions());
                break;
            case R.id.action_share:
                break;
            case R.id.action_copy:
                break;
            case R.id.action_re_download:
                download();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void download() {
        DownloadManager downloadManager = (DownloadManager)
                getSystemService(Context.DOWNLOAD_SERVICE);
        String path = picUrls.get(index).replace("bmiddle", "large");
        Uri uri = Uri.parse(path);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("weibo", path.substring(path.lastIndexOf("/") + 1));
//        request.allowScanningByMediaScanner();
        final Long id = downloadManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == reference) {
                    ToastUtil.showShort(ImageDetailActivity.this,"成功");
                }
            }
        };
        registerReceiver(downloadReceiver, filter);
    }
}
