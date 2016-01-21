package com.xihua.weibopaper.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.EmoticonEntity;
import cn.hadcn.keyboard.utils.EmoticonBase;

/**
 * @Package com.xihua.weibopaper.common
 * @ClassName: MyApplication
 * @Description:全局
 * @author cyq7on
 * @date 2015/12/9 15:47
 * @version V1.0
 */

public class MyApplication extends Application {
    private static Context context;
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
        context = this;
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

//        if ( !ChatKeyboardLayout.isEmoticonInitSuccess(this) ) {
            List<EmoticonEntity> entities = new ArrayList<>();
            entities.add(new EmoticonEntity("emotions/sina", EmoticonBase.Scheme.ASSETS));
            ChatKeyboardLayout.initEmoticonsDB(this, true, entities);
//        }
    }

    public static Context getInstance() {
        return context;
    }
    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
