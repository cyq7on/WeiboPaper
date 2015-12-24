package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.AccessTokenKeeper;

/**
 * @Package com.xihua.weibopaper.activity
 * @ClassName: BaseActivity
 * @Description:activity基类
 * @author cyq7on
 * @date 2015/12/9 15:46
 * @version V1.0
 */


public class BaseActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addActivity(this);
    }

    public void initView(){
    }
    public void initData(){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
