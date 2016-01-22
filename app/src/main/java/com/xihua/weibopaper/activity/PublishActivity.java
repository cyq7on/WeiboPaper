package com.xihua.weibopaper.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.apkfuns.logutils.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.adapter.ImageAdapter;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.GalleryImagLoader;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.*;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.hadcn.keyboard.ChatTextView;
import cn.hadcn.keyboard.PublishKeyboardLayout;
import cn.hadcn.keyboard.view.HadEditText;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.activity
 * @ClassName: PublishActivity
 * @Description: 发布微博
 * @date 2016/1/15/15:52
 */

public class PublishActivity extends BaseActivity{
    private View camera, emoticon, mention,topic, send;
    private PublishKeyboardLayout keyboardLayout;
    private HadEditText etContent;
    private final int REQUEST_CODE_GALLERY = 1001;
    private RecyclerView recyclerView;
    private List<PhotoInfo> photoInfoList;
    private ImageAdapter adapter;
    private FunctionConfig functionConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        init();
        keyboardLayout = (PublishKeyboardLayout) findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发微博");
        toolbar.setNavigationIcon(R.mipmap.back_white);
        setSupportActionBar(toolbar);//toolbar.setTitle在这句之前设置，Listener需要在这句之后
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etContent = keyboardLayout.getInputArea();
        recyclerView = keyboardLayout.getRecyclerView();
        photoInfoList = new ArrayList<>();
        adapter = new ImageAdapter(photoInfoList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        keyboardLayout.getCamera().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY,functionConfig,
                        mOnHanlderResultCallback);
            }
        });
        keyboardLayout.getSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                //没有内容和图片则返回
                if (content.equals("") && photoInfoList.size() == 0) {
                    return;
                }
                RequestQueue requestQueue = Volley.newRequestQueue(PublishActivity.this);
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());
                Map<String, String> params = new HashMap<>();
                params.put("source", Constants.APP_KEY);
                params.put("access_token", accessToken.getToken());
                params.put("status",content);

                GsonRequest<StatusContent> Request = new GsonRequest<>(params,
                        Constants.STATUSES_UPDATE, StatusContent.class,
                        new Response.Listener<StatusContent>() {
                            @Override
                            public void onResponse(StatusContent response) {
                                ToastUtil.showShort(PublishActivity.this, "成功");
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showShort(PublishActivity.this, error.getMessage());
                    }
                });
                requestQueue.add(Request);
            }
        });
    }

    private void init() {

        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder()
                .build();
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setSelected(photoInfoList);//添加过滤集合
        //配置功能
        functionConfig = functionConfigBuilder
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .setMutiSelectMaxSize(9)
                .build();

        //配置imageloader
        GalleryImagLoader imageLoader = new GalleryImagLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageLoader, theme)
                .setDebug(cn.finalteam.galleryfinal.BuildConfig.DEBUG)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
    }
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList == null) {
                return;
            }
            photoInfoList.clear();
            photoInfoList.addAll(resultList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtil.showShort(PublishActivity.this,errorMsg);
        }
    };

}
