package com.xihua.weibopaper.view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.xihua.weibopaper.utils.ToastUtil;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.view
 * @Description: 特定文字点击响应
 * @date 2016/2/1619:41
 */
public class MyClickspan extends ClickableSpan {
    String text;

    public MyClickspan(String text) {
        super();
        this.text = text;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor("#6666ff"));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
//        processHyperLinkClick(text);
        ToastUtil.showShort(widget.getContext(),text);
        LogUtils.i(text);
    }
}
