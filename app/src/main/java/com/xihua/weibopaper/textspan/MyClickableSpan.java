package com.xihua.weibopaper.textspan;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.ToastUtil;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.view
 * @Description: 特定文字点击响应
 * @date 2016/2/1619:41
 */
public class MyClickableSpan extends ClickableSpan {
    String text;

    public MyClickableSpan(String text) {
        super();
        this.text = text;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(MyApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
//        processHyperLinkClick(text);
        ToastUtil.showShort(widget.getContext(),text);
        LogUtils.i(text);
    }
}
