package com.xihua.weibopaper.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * @Package com.xihua.weibopaper.http
 * @ClassName: GsonRequest
 * @Description: 封装了Gson的请求
 * @author cyq7on
 * @date 2015/12/9 20:31
 * @version V1.0
 */

public class GsonRequest<T> extends Request<T> {

    private final Listener<T> mListener;

    private Gson mGson;

    private Class<T> mClass;

    private Map<String, String> params;

    //get请求拼接参数
    public static String getUrl(String url,Map<String, String> params) {
        StringBuilder str = new StringBuilder();
        for (String key : params.keySet()) {
            str.append("&").append(key).append("=").append(params.get(key));
        }
        return url + "?" + str.substring(1);
    }

    public GsonRequest(Map<String, String> params, String url, Class<T> clazz, Listener<T> listener,
                       ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.params = params;
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    public GsonRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url,errorListener);
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

}
