package cn.hadcn.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.util.DefEmoticons;
import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.emoticon.view.EmoticonLayout;
import cn.hadcn.keyboard.emoticon.view.EmoticonsToolBarView;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.media.MediaLayout;
import cn.hadcn.keyboard.utils.EmoticonBase;
import cn.hadcn.keyboard.utils.HadLog;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.HadEditText;
import cn.hadcn.keyboard.view.SoftHandleLayout;

/**
 * @Package cn.hadcn.keyboard
 * @ClassName: PublishKeyboardLayout
 * @Description: 发布(微博、动态)布局
 * @author cyq7on
 * @date 2016/1/21/15:02
 * @version V1.0
 */

public class PublishKeyboardLayout extends SoftHandleLayout implements EmoticonsToolBarView.OnToolBarItemClickListener {

    public int FUNC_EMOTICON_POS = 0; //display emoticons area
    public int FUNC_MEDIA_POS = 0;    //display medias area
    public int FUNC_ORDER_COUNT = 0;

    public int mChildViewPosition = -1;

    private HadEditText etInputArea;
    private LinearLayout lyBottomLayout;
    private Context mContext;
    private LinearLayout camera, emoticon, mention,topic, send;
    private RecyclerView recyclerView;


    public PublishKeyboardLayout(Context context) {
        super(context, null);
        initView(context);
    }

    public PublishKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(Context context) {
        mContext = context;
        // must be before inflate
        //我将这一句提前到了MyApplication中
//        EmoticonHandler.getInstance(context).loadEmoticonsToMemory();
        LayoutInflater.from(context).inflate(R.layout.view_publish_keyboardbar, this);

        lyBottomLayout = (LinearLayout) findViewById(R.id.ly_foot_func);
        camera = (LinearLayout) findViewById(R.id.btnCamera);
        emoticon = (LinearLayout) findViewById(R.id.btnEmotion);
        mention = (LinearLayout) findViewById(R.id.btnMention);
        topic = (LinearLayout) findViewById(R.id.btnTrends);
        send = (LinearLayout) findViewById(R.id.btnSend);
        emoticon.setOnClickListener(new FaceClickListener());
        send.setOnClickListener(new SendClickListener());
        etInputArea = (HadEditText) findViewById(R.id.et_chat);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        setAutoHeightLayoutView(lyBottomLayout);

        etInputArea.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!etInputArea.isFocused()) {
                    etInputArea.setFocusable(true);
                    etInputArea.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        etInputArea.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            etInputArea.setFocusable(true);
            etInputArea.setFocusableInTouchMode(true);
            etInputArea.requestFocus();
        } else {
            etInputArea.setFocusable(false);
            etInputArea.setFocusableInTouchMode(false);
        }
    }
    public View getCamera() {
        return camera;
    }
    public View getEmoticon() {
        return emoticon;
    }
    public View getMention() {
        return mention;
    }

    public View getTopic() {
        return topic;
    }
    public View getSend() {
        return send;
    }

    public HadEditText getInputArea() {
        return etInputArea;
    }
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void clearInputArea(){
        etInputArea.setText("");
    }

    public void del(){
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public void hideKeyboard() {
        findViewById(R.id.main_view_id).setVisibility(GONE);
    }


    public void setSendBtnBackground(int drawable) {

    }

    /**
     * hide keyboard or emoticon area or media area
     */
    public void hideBottomPop() {
        hideAutoView();
        closeSoftKeyboard(etInputArea);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (lyBottomLayout != null && lyBottomLayout.isShown()) {
                    hideAutoView();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    private class SendClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if(mOnChatKeyBoardListener != null){
                mOnChatKeyBoardListener.onSendBtnClick(etInputArea.getText().toString());
            }
        }
    }



    private class FaceClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(etInputArea);
                    show(FUNC_EMOTICON_POS);
                    break;
                case KEYBOARD_STATE_NONE:
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();
                    showAutoView();
                    show(FUNC_EMOTICON_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    break;
            }
        }
    }

    private class MediaClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(etInputArea);
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_NONE:
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();
                    showAutoView();
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if(mChildViewPosition == FUNC_MEDIA_POS){
                        openSoftKeyboard(etInputArea);
                    } else {
                        show(FUNC_MEDIA_POS);
                    }
                    break;
            }
        }
    }



    public void showEmoticons( ) {
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setContents(builder, new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (etInputArea != null) {
                    etInputArea.setFocusable(true);
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        if ( mOnChatKeyBoardListener != null ) {
                            mOnChatKeyBoardListener.onUserDefEmoticonClicked(bean.getTag(), bean.getIconUri());
                        }
                        return;
                    }

                    int index = etInputArea.getSelectionStart();
                    Editable editable = etInputArea.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getTag());
                    } else {
                        editable.insert(index, bean.getTag());
                    }
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(layout, params);
        FUNC_EMOTICON_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void show(int position){
        int childCount = lyBottomLayout.getChildCount();
        if(position < childCount){
            for(int i = 0 ; i < childCount ; i++){
                if(i == position){
                    lyBottomLayout.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition  = i;
                } else{
                    lyBottomLayout.getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }

    OnChatKeyBoardListener mOnChatKeyBoardListener;
    public void setOnKeyBoardBarListener( OnChatKeyBoardListener l ) { this.mOnChatKeyBoardListener = l; }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public static boolean isEmoticonInitSuccess(Context context) {
        return Utils.isInitDb(context);
    }

    public static void initEmoticonsDB(final Context context, final boolean isShowEmoji, final List<EmoticonEntity> emoticonEntities) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmoticonDBHelper emoticonDbHelper = EmoticonHandler.getInstance(context).getEmoticonDbHelper();
                if ( isShowEmoji ) {
                    ArrayList<EmoticonBean> emojiArray = Utils.ParseData(DefEmoticons.emojiArray, EmoticonBean.FACE_TYPE_NORMAL, EmoticonBase.Scheme.DRAWABLE);
                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                    emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
                    emojiEmoticonSetBean.setItemPadding(20);
                    emojiEmoticonSetBean.setVerticalSpacing(10);
                    emojiEmoticonSetBean.setShowDelBtn(true);
                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
                    emoticonDbHelper.insertEmoticonSet(emojiEmoticonSetBean);
                }

                List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();
                for ( EmoticonEntity entity : emoticonEntities ) {
                    try {
                        EmoticonSetBean bean = Utils.ParseEmoticons(context, entity.getPath(), entity.getScheme());
                        emoticonSetBeans.add(bean);
                    } catch (IOException e) {
                        e.printStackTrace();
                        HadLog.e(String.format("read %s config.xml error", entity.getPath()));
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        HadLog.e( String.format("parse %s config.xml error", entity.getPath()) );
                    }
                }

                for ( EmoticonSetBean setBean : emoticonSetBeans ) {
                    emoticonDbHelper.insertEmoticonSet(setBean);
                }
                emoticonDbHelper.cleanup();

                if ( emoticonSetBeans.size() == emoticonEntities.size() ) {
                    Utils.setIsInitDb(context, true);
                }
            }
        }).start();
    }

    private EmoticonsKeyboardBuilder getBuilder(Context context) {
        if ( context == null ) {
            throw new RuntimeException(" Context is null, cannot create db helper" );
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public interface OnChatKeyBoardListener {
        void onSendBtnClick(String msg);
        void onRecordingAction(RecordingAction action);
        void onUserDefEmoticonClicked(String tag, String uri);
    }

    public enum RecordingAction {
        START,    // start recording
        COMPLETE,  // recording end
        CANCELED,  // recording canceled
        WILLCANCEL,   // state which can be canceled
        RESTORE     // state which is restored from WILLCANCEL
    }

}