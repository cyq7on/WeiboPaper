package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.hadcn.keyboard.ChatKeyboardLayout;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.activity
 * @ClassName: PublishActivity
 * @Description: 发布，转发，评论微博
 * @date 2016/1/15/15:52
 */

public class PublishActivity extends BaseActivity {
    private LinearLayout camera, emotion, topic, send;
    private EditText etInput;
    private CheckBox cb;
    private String url;
    private ChatKeyboardLayout keyboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        emotion = (LinearLayout) findViewById(R.id.btnEmotion);
        topic = (LinearLayout) findViewById(R.id.btnTrends);
        send = (LinearLayout) findViewById(R.id.btnSend);
        etInput = (EditText) findViewById(R.id.editContent);
        cb = (CheckBox) findViewById(R.id.checkbox);
        keyboardLayout = (ChatKeyboardLayout)findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();

        final int info = getIntent().getIntExtra("info", -1);
        final String id = getIntent().getStringExtra("id");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(info);
        toolbar.setNavigationIcon(R.mipmap.back_white);
        setSupportActionBar(toolbar);//toolbar.setTitle在这句之前设置，Listener需要在这句之后
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switch (info) {
            //转发
            case 0:
                getSupportActionBar().setTitle("转发微博");
                cb.setText("同时评论");
                break;
            //评论
            case 1:
                getSupportActionBar().setTitle("评论微博");
                cb.setText("同时转发");
                break;
            //发微博
            default:
                getSupportActionBar().setTitle("发微博");
                camera = (LinearLayout) findViewById(R.id.btnCamera);
                camera.setVisibility(View.VISIBLE);
                cb.setVisibility(View.INVISIBLE);
                break;
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());
                Map<String, String> params = new HashMap<>();
                GsonRequest<StatusContent> AnotherRequest;//评论时同时转发
                params.put("source", Constants.APP_KEY);
                params.put("access_token", accessToken.getToken());
                params.put("id", id);
                String input;
//                try {
//                    input = URLEncoder.encode(etInput.getText().toString(), "utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    input = "";
//                }
                input = etInput.getText().toString();
                switch (info) {
                    //转发
                    case 0:
                        url = Constants.STATUSES_REPOST;
                        params.put("status", input);
                        //转发并评论
                        if (cb.isChecked()) {
                            params.put("is_comment", "3");
                        }
                        break;
                    //评论
                    case 1:
                        //评论并转发
                        if (cb.isChecked()) {
                            url = Constants.STATUSES_REPOST;
                            params.put("status", input);
                            params.put("is_comment", "3");
                        }else {
                            url = Constants.COMMENTS_CREATE;
                            params.put("comment", input);
                        }
                        break;
                    //发微博
                    default:

                        break;
                }
                GsonRequest<StatusContent> Request = new GsonRequest<>(params,
                        url, StatusContent.class,
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

}
