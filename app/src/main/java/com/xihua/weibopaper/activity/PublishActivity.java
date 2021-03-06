package com.xihua.weibopaper.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.apkfuns.logutils.LogUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.adapter.ImageAdapter;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.http.MultipartEntity;
import com.xihua.weibopaper.http.MultipartRequest;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.GalleryImagLoader;
import com.xihua.weibopaper.http.GsonRequest;
import com.xihua.weibopaper.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
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

public class PublishActivity extends BaseActivity {
    private View camera, emoticon, mention, topic, send;
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
        keyboardLayout = (PublishKeyboardLayout) findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar("发微博");
        etContent = keyboardLayout.getInputArea();
        recyclerView = keyboardLayout.getRecyclerView();
        photoInfoList = new ArrayList<>();
        adapter = new ImageAdapter(photoInfoList, this);
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
                    ToastUtil.showShort(PublishActivity.this, "您还没有输入内容");
                    return;
                }
                final RequestQueue requestQueue = Volley.newRequestQueue(PublishActivity.this);
                final ProgressDialog dialog = new ProgressDialog(PublishActivity.this);
                dialog.setMessage("发送中...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        requestQueue.cancelAll("cancle");
                    }
                });
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());

                if (photoInfoList.size() > 0) {
                    String path = photoInfoList.get(0).getPhotoPath();
                    MultipartRequest multipartRequest = new MultipartRequest(
                            Constants.STATUSES_UPLOAD, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ToastUtil.showShort(PublishActivity.this, "成功");
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtil.showShort(PublishActivity.this, error.getMessage());
                            dialog.dismiss();
                        }
                    });
                    // 通过MultipartEntity来设置参数
                    MultipartEntity multi = multipartRequest.getMultiPartEntity();
                    // 上传文件
                    multi.addFilePart("pic", new File(path));
                    multi.addStringPart("source", Constants.APP_KEY);
                    multi.addStringPart("access_token", accessToken.getToken());
                    multi.addStringPart("status", content);
                    multipartRequest.setTag("cancle");
                    requestQueue.add(multipartRequest);
                } else {
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
                            dialog.dismiss();
                        }
                    });
                    request.setTag("cancle");
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
            ToastUtil.showShort(PublishActivity.this, errorMsg);
        }
    };

    private byte[] bitmap2Bytes(Bitmap bitmap) {
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
