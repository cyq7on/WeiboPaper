   package com.xihua.weibopaper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.activity.MainActivity;
import com.xihua.weibopaper.bean.UnreadCount;
import com.xihua.weibopaper.bean.WeiBoUser;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ImageUtils;
import com.xihua.weibopaper.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

   /**
 * @Package com.xihua.weibopaper.service
 * @ClassName: UnreadService
 * @Description:访问后台各种未读信息的service
 * @author cyq7on
 * @date 2015/12/23 15:06
 * @version V1.0
 */

public class UnreadService extends Service {
    public static final String ACTION_UNREAD_CHANGED = "com.xihua.weibopaper.ACTION_UNREAD_CHANGED";
    private LocalBroadcastManager manager;
    private RequestQueue requestQueue;
    private GsonRequest<UnreadCount> requestUnread;

       @Override
       public void onCreate() {
           super.onCreate();
           manager = LocalBroadcastManager.getInstance(this);
           Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
           requestQueue = Volley.newRequestQueue(this);
           Map<String, String> params = new HashMap<>();
           params.put("source", Constants.APP_KEY);
           params.put("access_token", accessToken.getToken());
           params.put("uid", accessToken.getUid());
           params.put("unread_message","1");
           String url = GsonRequest.getUrl(Constants.REMIND_UNREAD_COUNT,params);
           requestUnread = new GsonRequest<>(url,
                   UnreadCount.class, new Response.Listener<UnreadCount>() {
               int lastMention;
               int lastCmt;
               int lastDm;
               @Override
               public void onResponse(UnreadCount response) {
                   int mention = response.getMention_cmt() + response.getMention_status();
                   int cmt = response.getCmt();
//                   int dm = response.getDm();
                   int dm = (int) (10 * Math.random());
                   //如果和上次信息完全一致，则返回
                   if (mention == lastMention && cmt == lastCmt && dm == lastDm) {
                       return;
                   }
//                   ToastUtil.showShort(getApplicationContext(), response.toString());
                   Intent intent = new Intent(ACTION_UNREAD_CHANGED);
                   if (mention != 0) {
                       intent.putExtra("mention",mention);
                       manager.sendBroadcast(intent);
                   }
                   if (cmt != 0) {
                       intent.putExtra("cmt",cmt);
                       manager.sendBroadcast(intent);
                   }
                   if (dm != 0) {
                       intent.putExtra("dm",dm);
                       manager.sendBroadcast(intent);
                   }
                   lastMention = mention;
                   lastCmt = cmt;
                   lastDm = dm;
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   ToastUtil.showShort(getApplicationContext(), error.getMessage());
               }
           });
       }

       @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        requestQueue.add(requestUnread);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}


