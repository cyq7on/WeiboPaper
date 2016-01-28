package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xihua.weibopaper.bean.PicUrls;
import com.xihua.weibopaper.common.MyApplication;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;

/**
 * @Package com.xihua.weibopaper.activity
 * @ClassName: ImageDetailActivity
 * @Description: 查看图片详情
 * @author cyq7on
 * @date 2016/1/28/15:41
 * @version V1.0
 */

public class ImageDetailActivity extends BaseActivity {

    private ViewPager viewPager;
    private ArrayList<String> picUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        initData();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar("");
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
                ImageLoader.getInstance().displayImage(picUrls.get(position),photoView,
                        MyApplication.getOptions());
                container.addView(photoView);
                return photoView;
            }
        });
    }

    @Override
    public void initData() {
        picUrls = getIntent().getStringArrayListExtra("urlList");
    }
}
