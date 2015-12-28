package com.xihua.weibopaper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.adapter.WeiboAdapter;
import com.xihua.weibopaper.bean.WeiboContent;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.DividerItemDecoration;

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
    private RecyclerView recyclerView;

    public static Fragment newInstance(String url){
        Fragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        requestQueue = Volley.newRequestQueue(context);
        url = getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        requestContent = new GsonRequest<>(url,
                WeiboContent.class, new Response.Listener<WeiboContent>() {
            @Override
            public void onResponse(WeiboContent response) {
                adapter = new WeiboAdapter(context,response,requestQueue);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL_LIST));
                Log.i("content", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShort(context, error.getMessage());
            }
        });
        requestQueue.add(requestContent);
        return view;
    }
}
