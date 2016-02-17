package com.xihua.weibopaper.textspan;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.view
 * @Description:
 * @date 2016/2/1619:41
 */
public class NoUnderLineClickableSpan extends ClickableSpan {
    String text;

    public NoUnderLineClickableSpan(String text) {
        super();
        this.text = text;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//        ds.setColor(ds.linkColor);
        ds.setColor(Color.GRAY);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
//        processHyperLinkClick(text);
    }
}
