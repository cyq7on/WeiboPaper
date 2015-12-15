package com.xihua.weibopaper.common;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * @Package com.xihua.weibopaper.common
 * @ClassName: MyApplication
 * @Description:全局
 * @author cyq7on
 * @date 2015/12/9 15:47
 * @version V1.0
 */

public class MyApplication extends Application {
    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static MyApplication instance;
    //实例化一次
    public synchronized static MyApplication getInstance(){
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    //remove activity
    public void removeActivity(Activity activity) {
        mList.remove(activity);
    }
    //关闭每一个list内的activity
    public void finishAll() {
        for (Activity activity:mList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}