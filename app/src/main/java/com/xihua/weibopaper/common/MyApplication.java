package com.xihua.weibopaper.common;

import android.app.Activity;
import android.content.Context;

import org.litepal.LitePalApplication;

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

public class MyApplication extends LitePalApplication {
    //运用list来保存们每一个activity是关键
    private static List<Activity> mList = new LinkedList<Activity>();
    // add Activity
    public static void addActivity(Activity activity) {
        if (!mList.contains(activity)) {
            mList.add(activity);
        }
    }
    //remove activity
    public static void removeActivity(Activity activity) {
        if (mList.contains(activity)) {
            mList.remove(activity);
        }
    }
    //关闭每一个list内的activity
    public static void finishAll() {
        for (Activity activity:mList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getInstance() {
        return getContext();
    }
    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
