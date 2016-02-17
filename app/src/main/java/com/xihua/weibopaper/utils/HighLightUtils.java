package com.xihua.weibopaper.utils;

import android.text.SpannableString;
import android.text.Spanned;

import com.xihua.weibopaper.textspan.MyClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.utils
 * @Description: 高亮##，@以及链接
 * @date 2016/2/1620:27
 */
public class HighLightUtils {
    public static final String TOPIC = "#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#";
    public static final String USER_NAME = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}";
    public static final String URL = "http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]";

    public static SpannableString highLight(String content,String ... pattern) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            stringBuilder.append(pattern[i]).append("|");
        }
        Pattern p = Pattern.compile(stringBuilder.toString());
        Matcher matcher = p.matcher(content);
        SpannableString ss = new SpannableString(content);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String text = content.substring(start,end);
//            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#6666ff")),
            ss.setSpan(new MyClickableSpan(text),
                    start, end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
}
