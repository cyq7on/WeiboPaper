package com.xihua.weibopaper.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

import com.xihua.weibopaper.textspan.MentionLinkOnTouchListener;
import com.xihua.weibopaper.utils.HighLightUtils;

import cn.hadcn.keyboard.ChatTextView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.view
 * @Description: 高亮话题，人名，链接，显示表情
 * @date 2016/2/1717:13
 */
public class XTextView extends ChatTextView {
    public XTextView(Context context) {
        super(context);
        init();
    }

    public XTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(new MentionLinkOnTouchListener());
        //这句很重要，缺少会导致MyClickableSpan中的点击失效
        setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            text = HighLightUtils.highLight(text,
                    HighLightUtils.TOPIC, HighLightUtils.USER_NAME, HighLightUtils.URL);
        }
        super.setText(text, type);
    }
}
