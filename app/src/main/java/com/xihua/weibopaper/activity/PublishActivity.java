package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
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

import java.util.HashMap;
import java.util.Map;

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
        keyboardLayout.getSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());
                Map<String, String> params = new HashMap<>();
                params.put("source", Constants.APP_KEY);
                params.put("access_token", accessToken.getToken());
                params.put("status", etContent.getText().toString());

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

}
