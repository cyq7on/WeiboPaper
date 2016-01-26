package com.xihua.weibopaper.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.apkfuns.logutils.LogUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.xihua.weibopaper.adapter.ImageAdapter;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.GalleryImagLoader;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.http.MultipartEntity;
import com.xihua.weibopaper.http.MultipartRequest;
import com.xihua.weibopaper.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.*;
import cn.finalteam.galleryfinal.model.PhotoInfo;
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
    private ContentLoadingProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
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
        progressBar = new ContentLoadingProgressBar(this);
        photoInfoList = new ArrayList<>();
        adapter = new ImageAdapter(photoInfoList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        keyboardLayout.getCamera().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //多选模式
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, functionConfig,
                        mOnHanlderResultCallback);
            }
        });
        keyboardLayout.getSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                //没有内容和图片则返回
                if (content.equals("")) {
                    if (photoInfoList.size() == 0) {
                        return;
                    }else {
                        ToastUtil.showShort(PublishActivity.this,"您还没有输入内容");
                    }
                }
                RequestQueue requestQueue = Volley.newRequestQueue(PublishActivity.this);
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());

                if (photoInfoList.size() > 0) {
                    String path = photoInfoList.get(0).getPhotoPath();
                   int begin = path.lastIndexOf("/") + 1;
                    String fileName = path.substring(begin);
                    LogUtils.i(fileName);
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    byte[] data = bitmap2Bytes(bitmap);
                    LogUtils.i(data);
                    /* MultipartRequest multipartRequest = new MultipartRequest(
                            Constants.STATUSES_UPLOAD, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("", "### response : " + response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtil.showShort(PublishActivity.this, error.getMessage());
                        }
                    });
                    // 添加header
//                    multipartRequest.addHeader("image", "png");
//                    multipartRequest.addHeader("source", Constants.APP_KEY);
//                    multipartRequest.addHeader("access_token", accessToken.getToken());
//                    multipartRequest.addHeader("status", content);
//                    multipartRequest.addHeader();
                    // 通过MultipartEntity来设置参数
                    MultipartEntity multi = multipartRequest.getMultiPartEntity();
                    // 直接上传Bitmap
                    multi.addBinaryPart("pic", data);
                    // 上传文件
//                    multi.addFilePart("pic", new File(path));
                    multi.addStringPart("source", Constants.APP_KEY);
                    multi.addStringPart("access_token",accessToken.getToken());
                    multi.addStringPart("status",content);
                    requestQueue.add(multipartRequest);*/
                    StatusesAPI statusesAPI = new StatusesAPI(PublishActivity.this,
                            Constants.APP_KEY,accessToken);
                    statusesAPI.upload(content, bitmap, null, null, new RequestListener() {
                        @Override
                        public void onComplete(String s) {
                            ToastUtil.showShort(PublishActivity.this, "成功");
                            finish();
                        }

                        @Override
                        public void onWeiboException(WeiboException e) {
                            ToastUtil.showShort(PublishActivity.this, e.getMessage());
                        }
                    });
                }else {
                    Map<String, String> params = new HashMap<>();
                    params.put("source", Constants.APP_KEY);
                    params.put("access_token", accessToken.getToken());
                    params.put("status", content);
                    GsonRequest<StatusContent> request = new GsonRequest<>(params,
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
                    requestQueue.add(request);
                }

            }
        });
        initGallery();
    }

    private void initGallery() {

        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder().build();
        //配置功能
        functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .setMutiSelectMaxSize(9)
                .setSelected(photoInfoList)//添加已选集合
//                .setFilter(photoInfoList)
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

    private byte[] bitmap2Bytes(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

}
