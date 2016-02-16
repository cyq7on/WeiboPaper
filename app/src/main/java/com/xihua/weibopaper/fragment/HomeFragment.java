package com.xihua.weibopaper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.apkfuns.logutils.LogUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.xihua.weibopaper.activity.ImageDetailActivity;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.adapter.ImageAdapter;
import com.xihua.weibopaper.adapter.WeiboAdapter;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.bean.WeiboContent;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.http.GsonRequest;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.fragment
 * @ClassName: HomeFragment
 * @Description: 主页的fragment
 * @date 2015/12/28 14:55
 */

public class HomeFragment extends Fragment {
    private WeiboAdapter adapter;
    private Context context;
    private RequestQueue requestQueue;
    private GsonRequest<WeiboContent> requestContent;
    private XRecyclerView recyclerView;
    private List<StatusContent> list;
    private boolean request = true;
    private LinearLayoutManager manager;
    private XRecyclerView.LoadingListener loadingListener;
    private DB snappydb;
    private MyHandler handler;
    private MaterialProgressBar progressBar;
    private static final int LOAD_MORE_COMPLETE = 0;
    private static final int LOADING_VIEW_GONE = 1;
    private static final int LOADING_COUNT = 20;
    private WeiboAdapter.OnItemClickListener listener;
    private Map<String, String> params;
    private String baseUrl;

    public static Fragment newInstance(Bundle bundle) {
        Fragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (request) {
                //只有当该Fragment被用户可见时,才加载网络数据，并且只加载一次
                requestQueue.add(requestContent);
            } else {
                adapter.notifyDataSetChanged();
            }
            String which = getArguments().getString("which");
            switch (which) {
                case "a":
                    baseUrl = Constants.STATUSES_PUBLIC_TIMELINE;
                    break;
                case "b":
                    baseUrl = Constants.STATUSES_FRIENDS_TIMELINE;
                    break;
                case "c":
                    baseUrl = Constants.STATUSES_USER_TIMELINE;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        handler = new MyHandler(this);
        listener = new WeiboAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showShort(context,Integer.toString(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        };
        try {
            snappydb = DBFactory.open(context, "WeiboDB");
        } catch (SnappydbException e) {
            ToastUtil.showShort(context, "数据库打开失败");
        }
        LogUtils.i(this);
        requestQueue = Volley.newRequestQueue(context);
        params = new HashMap<>();
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
        params.put("source", Constants.APP_KEY);
        params.put("access_token", accessToken.getToken());
        LogUtils.e(params);
        list = new ArrayList<>();
        String which = getArguments().getString("which");
        switch (which) {
            case "a":
                baseUrl = Constants.STATUSES_PUBLIC_TIMELINE;
                break;
            case "b":
                baseUrl = Constants.STATUSES_FRIENDS_TIMELINE;
                break;
            case "c":
                baseUrl = Constants.STATUSES_USER_TIMELINE;
                break;
            default:
                break;
        }
        try {
            String[] keys = snappydb.findKeys(which);
            int length = keys.length;
            if (length > 0) {
                int k = length < LOADING_COUNT ? 0 : length - LOADING_COUNT;
                for (int i = length - 1; i >= k; i--) {
                    list.add(snappydb.get(keys[i], StatusContent.class));
                }
                params.put("since_id",list.get(0).getIdstr());
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        adapter = new WeiboAdapter(context, list, requestQueue);
        refresh(GsonRequest.getUrl(baseUrl, params));
        manager = new LinearLayoutManager(context);
        loadingListener = new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                params.put("since_id",list.get(0).getIdstr());
                params.put("max_id","0");
                refresh(GsonRequest.getUrl(baseUrl, params));
                requestQueue.add(requestContent);
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        adapter.setOnItemClickLitener(listener);
        progressBar = (MaterialProgressBar) view.findViewById(R.id.progressBar);
        if (request) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(manager);
            recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
            recyclerView.setLaodingMoreProgressStyle(ProgressStyle.BallRotate);
        } else {
            //这里不能重用manager，不解
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(loadingListener);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            String which = getArguments().getString("which");
            String[] keys = snappydb.findKeys(which);
            int length = keys.length;
            if (length >= 300) {
                for (int i = length - 1; i > length / 2; i--) {
                    snappydb.del(keys[i]);
                }
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        try {
            snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        if (getUserVisibleHint()) {
            requestQueue.add(requestContent);
            progressBar.setVisibility(View.VISIBLE);
            request = true;
        }
    }

    private void refresh(String url) {
        requestContent = new GsonRequest<>(url,
                WeiboContent.class, new Response.Listener<WeiboContent>() {
            @Override
            public void onResponse(WeiboContent response) {
                List<StatusContent> data = response.getStatuses();
                recyclerView.refreshComplete();
                if (data.size() == 0) {
                    if (request) {
                        progressBar.setVisibility(View.GONE);
                        request = false;
                    }
                    return;
                }
                if (data.size() > 10) {
                    list.clear();
                    list.addAll(response.getStatuses());
                }else {
                    list.addAll(0,response.getStatuses());
                }
                adapter.notifyDataSetChanged();
                if (request) {
                    progressBar.setVisibility(View.GONE);
                    request = false;
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            handler.sendEmptyMessage(LOADING_VIEW_GONE);
//                            request = false;
//                        }
//                    }, 1500);
                }
                StringBuilder key;
                for (int i = 0; i < list.size(); i++) {
                    key = new StringBuilder();
                    StatusContent content = list.get(i);
                    key.append(getArguments().getString("which")).append(content.getIdstr());
                    try {
                        snappydb.put(key.toString(), content);
                    } catch (SnappydbException e) {
                        ToastUtil.showShort(context, "缓存数据失败");
                    }
                }
                LogUtils.i(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShort(context, error.getMessage());
                if (request) {
                    progressBar.setVisibility(View.GONE);
                    request = false;
                }
            }
        });
    }

    private void loadMore() {
        final int size = list.size();
        LogUtils.i(size);
        long lastId = list.get(size - 1).getId();
        LogUtils.i(lastId);
        String which = getArguments().getString("which");
        try {
            String[] keys = snappydb.findKeysBetween(which,which + (lastId - 1));
            int length = keys.length;
            LogUtils.i(length);
            if (length > 0) {
                LogUtils.i(keys);
                int k = length < LOADING_COUNT ? 0 : length - LOADING_COUNT;
                for (int i = length - 1; i >= k; i--) {
                    list.add(snappydb.get(keys[i], StatusContent.class));
                }
            }else {
                params.put("since_id","0");
                params.put("max_id",lastId + "");
                requestContent = new GsonRequest<>(GsonRequest.getUrl(baseUrl, params),
                        WeiboContent.class, new Response.Listener<WeiboContent>() {
                    @Override
                    public void onResponse(WeiboContent response) {
                        List<StatusContent> data = response.getStatuses();
                        LogUtils.i(response);
                        LogUtils.i(baseUrl);
                        LogUtils.i(data);
                        recyclerView.refreshComplete();
                        if (data.size() > 0) {
                            data.remove(0);
                            list.addAll(data);
                            adapter.notifyDataSetChanged();
                        }
                        if (request) {
                            progressBar.setVisibility(View.GONE);
                            request = false;
                        }
                        StringBuilder key;
                        for (int i = 0; i < list.size(); i++) {
                            key = new StringBuilder();
                            StatusContent content = list.get(i);
                            key.append(getArguments().getString("which")).append(content.getIdstr());
                            try {
                                snappydb.put(key.toString(), content);
                            } catch (SnappydbException e) {
                                ToastUtil.showShort(context, "缓存数据失败");
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showShort(context, error.getMessage());
                    }
                });
                requestQueue.add(requestContent);
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        } finally {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage(LOAD_MORE_COMPLETE);
                    msg.arg1 = size;
                    handler.sendMessage(msg);
                }
            }, 3000);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<HomeFragment> fragmentWeakReference;

        MyHandler(HomeFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final HomeFragment fragment = fragmentWeakReference.get();
            switch (msg.what) {
                case LOADING_VIEW_GONE:
                    break;
                case LOAD_MORE_COMPLETE:
                    fragment.adapter.notifyDataSetChanged();
                    ((LinearLayoutManager) fragment.recyclerView.getLayoutManager()).
                            scrollToPositionWithOffset(msg.arg1, 0);
                    fragment.recyclerView.loadMoreComplete();
                    break;
                default:
                    break;
            }
        }
    }

}
