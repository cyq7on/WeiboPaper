package com.xihua.weibopaper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    public void setToolbar(String title) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.mipmap.back_white);
        setSupportActionBar(toolbar);//toolbar.setTitle在这句之前设置，Listener需要在这句之后
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
