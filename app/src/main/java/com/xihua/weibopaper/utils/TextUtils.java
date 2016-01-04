package com.xihua.weibopaper.utils;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private SpannableStringBuilder dealWeiboContent(String weiboContent,
                                                    TextView textView) {
        Pattern pattern = Pattern
                .compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)");
        String temp = weiboContent;
        Matcher matcher = pattern.matcher(temp);
        List<String> list = new LinkedList<String>();
        while (matcher.find()) {
            if (!list.contains(matcher.group())) {
                temp = temp.replace(
                        matcher.group(),
                        "<a href=\"" + matcher.group() + "\">"
                                + matcher.group() + "</a>");
            /*temp = temp.replace(
                    matcher.group(),
                    "<font color='#365C7C'><a href='" + matcher.group() + "'>"
                            + matcher.group() + "</a></font>");*/
            }
            list.add(matcher.group());
        }
        textView.setText(Html.fromHtml(temp));
        System.out.println("temp" + temp);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            for (URLSpan url : urls) {
//                style.setSpan(((ThinksnsWeiboContent) activityContent)
//                        .typeClick(url.getURL()), sp.getSpanStart(url), sp
//                        .getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return style;
        }
        return null;
    }

    private ClickableSpan typeClick(final String value) {
        char type = value.charAt(0);
        switch (type) {
            case '@':
                return new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // TODO Auto-generated method stub
                        String uname = "";
                        uname = value.substring(1, value.length());
                        System.out.println("weiboContent---uanme---" + uname);

                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(Color.BLUE);
                        ds.setUnderlineText(false);
                    }
                };
        }
        return  null;
    }
}