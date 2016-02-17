package com.xihua.weibopaper.textspan;

import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.textspan
 * @Description: 特定文字点击响应
 * @date 2016/2/1715:10
 */
public class MentionLinkOnTouchListener implements View.OnTouchListener {

    private boolean find = false;

    private int color;

    public MentionLinkOnTouchListener(int color) {
        this.color = color;
    }

    public MentionLinkOnTouchListener() {
        this.color = Color.parseColor("#33969696");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TextView tv = (TextView) v;
        Layout layout = tv.getLayout();

        if (layout == null)
            return false;

        int x = (int) event.getX();
        int y = (int) event.getY();

        int line = layout.getLineForVertical(tv.getScrollY() + y);
        int offset = layout.getOffsetForHorizontal(line, x);

        SpannableString value = SpannableString.valueOf(tv.getText());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                MyClickableSpan[] spans = value.getSpans(0, value.length(), MyClickableSpan.class);
                int findStart = 0;
                int findEnd = 0;
                for (MyClickableSpan span : spans) {
                    int start = value.getSpanStart(span);
                    int end = value.getSpanEnd(span);
                    if (start <= offset && offset <= end) {
                        find = true;
                        findStart = start;
                        findEnd = end;
                        break;
                    }
                }

                float lineWidth = layout.getLineWidth(line);

                find &= (lineWidth >= x);

                if (find) {
//                    LongClickableLinkMovementMethod.getInstance().onTouchEvent(tv, value, event);
                    BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(color);
                    value.setSpan(backgroundColorSpan, findStart, findEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    // Android has a bug, sometime TextView wont change its value
                    // when you modify SpannableString,
                    // so you must setText again, test on Android 4.3 Nexus4
                    tv.setText(value);
                }

                return find;
            case MotionEvent.ACTION_MOVE:
//                if (find) {
//                    LongClickableLinkMovementMethod.getInstance().onTouchEvent(tv, value, event);
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                if (find) {
//                    LongClickableLinkMovementMethod.getInstance().onTouchEvent(tv, value, event);
//                    LongClickableLinkMovementMethod.getInstance().removeLongClickCallback();
//                }
                BackgroundColorSpan[] backgroundColorSpans = value.getSpans(0, value.length(), BackgroundColorSpan.class);
                for (BackgroundColorSpan backgroundColorSpan : backgroundColorSpans) {
                    value.removeSpan(backgroundColorSpan);
                }
                tv.setText(value);
                find = false;
                break;
        }

        return false;

    }

}
