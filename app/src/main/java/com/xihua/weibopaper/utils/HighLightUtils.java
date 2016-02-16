package com.xihua.weibopaper.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String START = "start";
    private static final String END = "end";
    public static final String TOPIC = "#.+?#";
    public static final String USER_NAME = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";
    public static final String URL = "http://.*";
    public static SpannableString getHighLight(String content) {
        List<Map<String,Integer>> list = getStartAndEnd(content,Pattern.compile(TOPIC));
        list.addAll(getStartAndEnd(content,Pattern.compile(USER_NAME)));
        list.addAll(getStartAndEnd(content,Pattern.compile(URL)));
        SpannableString ss = new SpannableString(content);
        for (Map<String,Integer> map : list) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#6666ff")),
                    map.get(START),map.get(END), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
    private static List<Map<String,Integer>> getStartAndEnd(String content,Pattern pattern) {
        List<Map<String,Integer>> list = new ArrayList<>() ;
        Matcher matcher = pattern.matcher(content);
        Map<String,Integer> map;
        while (matcher.find()) {
            map = new HashMap<>();
            map.put(START,matcher.start());
            map.put(END,matcher.end());
            list.add(map);
        }
        return list;
    }
}
