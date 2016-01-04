package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.melnykov.fab.FloatingActionButton;
import com.xihua.weibopaper.common.MyApplication;

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
