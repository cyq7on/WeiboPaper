package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.ToastUtil;

public class LoginOrRegisterActivity extends AppCompatActivity {
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginorregister);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("微博");
        setSupportActionBar(toolbar);
        AuthInfo mAuthInfo = new AuthInfo(LoginOrRegisterActivity.this,
                Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(LoginOrRegisterActivity.this, mAuthInfo);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                mSsoHandler.authorize(new AuthListener());
                mAccessToken = AccessTokenKeeper.readAccessToken(LoginOrRegisterActivity.this);
                break;
            case R.id.btn_register:
                mSsoHandler.registerOrLoginByMobile("手机注册",new AuthListener());
                break;
        }
    }

    private class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle bundle) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (mAccessToken.isSessionValid()) {
                // 不为空
                // 保存 Token 到SharePreferences
                AccessTokenKeeper.writeAccessToken(LoginOrRegisterActivity.this, mAccessToken);
                ToastUtil.showShort(LoginOrRegisterActivity.this, "授权成功");
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = bundle.getString("code");
                String message = "失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                ToastUtil.showShort(LoginOrRegisterActivity.this, message);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ToastUtil.showShort(LoginOrRegisterActivity.this, "Auth exception: " + e.getMessage());
        }

        @Override
        public void onCancel() {
            ToastUtil.showShort(LoginOrRegisterActivity.this, "取消授权");
        }
    }

}


