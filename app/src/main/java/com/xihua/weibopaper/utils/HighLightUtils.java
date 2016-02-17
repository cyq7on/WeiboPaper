package com.xihua.weibopaper.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.utils
 * @Description: 高亮##，@以及连接
 * @date 2016/2/1620:27
 */
public class HighLightUtils {
    public static final String TOPIC = "#.+?#";
    public static final String USER_NAME = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
    public static final String URL = "http://.*";

    public static SpannableString highLight(String content,String ... pattern) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            stringBuilder.append(pattern[i]).append("|");
        }
        Pattern p = Pattern.compile(stringBuilder.toString());
        Matcher matcher = p.matcher(content);
        SpannableString ss = new SpannableString(content);
        while (matcher.find()) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#6666ff")),
                    matcher.start(), matcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
}
