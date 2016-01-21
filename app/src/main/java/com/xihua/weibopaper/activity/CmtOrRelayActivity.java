package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

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

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.ChatTextView;
import cn.hadcn.keyboard.view.HadEditText;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.activity
 * @ClassName: PublishActivity
 * @Description: 转发，评论微博
 * @date 2016/1/15/15:52
 */

public class CmtOrRelayActivity extends BaseActivity{
    private ChatTextView tvContent;
    private CheckBox cb;
    private String url;
    private ChatKeyboardLayout keyboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmt_relay);
        tvContent = (ChatTextView) findViewById(R.id.tv_content);
        cb = (CheckBox) findViewById(R.id.checkbox);
        keyboardLayout = (ChatKeyboardLayout) findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();
        keyboardLayout.setToInput();

        final int info = getIntent().getIntExtra("info", -1);
        final StatusContent statusContent = (StatusContent) getIntent().
                getSerializableExtra("StatusContent");
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
                tvContent.setText(statusContent.getText());
                break;
            //评论
            case 1:
                getSupportActionBar().setTitle("评论微博");
                cb.setText("同时转发");
                tvContent.setText(statusContent.getText());
                break;
            //发微博
            default:
                break;
        }

        keyboardLayout.getSendButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(MyApplication.getInstance());
                Map<String, String> params = new HashMap<>();
                params.put("source", Constants.APP_KEY);
                params.put("access_token", accessToken.getToken());
                params.put("id", statusContent.getIdstr());
                String input;
//                try {
//                    input = URLEncoder.encode(etInput.getText().toString(), "utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    input = "";
//                }
//                input = etInput.getText().toString();
                input = keyboardLayout.getInputArea().getText().toString();
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
                        } else {
                            url = Constants.COMMENTS_CREATE;
                            params.put("comment", input);
                        }
                        break;
                    default:
                        break;
                }
                GsonRequest<StatusContent> Request = new GsonRequest<>(params,
                        url, StatusContent.class,
                        new Response.Listener<StatusContent>() {
                            @Override
                            public void onResponse(StatusContent response) {
                                ToastUtil.showShort(CmtOrRelayActivity.this, "成功");
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showShort(CmtOrRelayActivity.this, error.getMessage());
                    }
                });
                requestQueue.add(Request);
            }
        });
    }

}
