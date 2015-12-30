package com.xihua.weibopaper.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.utils
 * @Description:处理超链接等
 * @date 2015/12/3023:27
 */
public class TextUtils extends ClickableSpan {
    private String mUrl;

    TextUtils(String url) {
        mUrl = url;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor("#034198"));    //设置超链接颜色
        ds.setUnderlineText(false); //超链接去掉下划线
    }

    @Override
    public void onClick(View widget) {
            //判断是否热门话题
        if (mUrl.startsWith("#") && mUrl.endsWith("#")) {
//            openHotTopic(mUrl.substring(1, mUrl.length() - 1));
        } else {
            //@用户名
//            openOtherUserInfoByName(mUrl);
        }
    }
}
