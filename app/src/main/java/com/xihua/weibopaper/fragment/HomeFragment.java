package com.xihua.weibopaper.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import android.text.style.RasterizerSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.melnykov.fab.FloatingActionButton;
import com.mingle.widget.LoadingView;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.adapter.WeiboAdapter;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.bean.WeiboContent;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.DividerItemDecoration;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Package com.xihua.weibopaper.fragment
 * @ClassName: HomeFragment
 * @Description:主页的fragment
 * @author cyq7on
 * @date 2015/12/28 14:55
 * @version V1.0
 */

public class HomeFragment extends Fragment {
    private WeiboAdapter adapter;
    private Context context;
    private RequestQueue requestQueue;
    private GsonRequest<WeiboContent> requestContent;
    private String url;
    private XRecyclerView recyclerView;
    private FloatingActionButton fab;
    private List<StatusContent> list;
    private boolean request = true;
    private LoadingView loadingView;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;
    private XRecyclerView.LoadingListener loadingListener;
    private DB snappydb;

    public static Fragment newInstance(Bundle bundle){
        Fragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if (request) {
                //只有当该Fragment被用户可见时,才加载网络数据，并且只加载一次
                requestQueue.add(requestContent);
            }
            else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        try {
            snappydb = DBFactory.open(context, "WeiboDB");
        } catch (SnappydbException e) {
            ToastUtil.showShort(context,"数据库打开失败");
        }
        requestQueue = Volley.newRequestQueue(context);
        url = getArguments().getString("url");
        list = new ArrayList<>();
        try {
            String[] keys = snappydb.findKeys(getArguments().getString("which"));
            int length = keys.length;
            if (length > 0) {
                WeiboContent content = snappydb.get(keys[length - 1],WeiboContent.class);
                list.addAll(content.getStatuses());
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        adapter = new WeiboAdapter(context,list,requestQueue);
        requestContent = new GsonRequest<>(url,
                WeiboContent.class, new Response.Listener<WeiboContent>() {
            @Override
            public void onResponse(WeiboContent response) {
                list.clear();
                list.addAll(response.getStatuses());
                adapter.notifyDataSetChanged();
                recyclerView.refreshComplete();
                if(request) {
                    loadingView.setVisibility(View.GONE);
                    request = false;
                }
                StringBuilder key = new StringBuilder();
                key.append(getArguments().getString("which")).
                        append(list.get(list.size() - 1).getIdstr());
                try {
                    snappydb.put(key.toString(),response);
                    Log.i("db", snappydb.get(key.toString(),WeiboContent.class).toString());
                } catch (SnappydbException e) {
                    ToastUtil.showShort(context, "缓存数据失败");
                }
                Log.i("content", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShort(context, error.getMessage());
                if(request) {
                    loadingView.setVisibility(View.GONE);
                    request = false;
                }
            }
        });
        manager = new LinearLayoutManager(context);
        dividerItemDecoration = new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL_LIST);
        loadingListener = new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                requestQueue.add(requestContent);
            }

            @Override
            public void onLoadMore() {

            }
        };
//        SQLiteDatabase database = Connector.getDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadingView = (LoadingView) view.findViewById(R.id.loadView);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        if (request) {
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(manager);
            recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
            recyclerView.setLaodingMoreProgressStyle(ProgressStyle.BallRotate);
        }
        else {
            //这里不能重用manager，不解
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
            snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    private byte[] saveObject(WeiboContent content) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(content);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            return data;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

//    public WeiboContent getObject() {
//        WeiboContent weiboContent = null;
//                byte data[] = cursor.getBlob(cursor.getColumnIndex("classtabledata"));
//                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
//                try {
//                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
//                    weiboContent = (WeiboContent) inputStream.readObject();
//                    inputStream.close();
//                    arrayInputStream.close();
//                    return weiboContent;
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return null;
//                }
//
//    }
}
