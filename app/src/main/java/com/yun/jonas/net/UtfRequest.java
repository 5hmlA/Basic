package com.yun.jonas.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jonas on 2015/11/25.
 */
public class UtfRequest extends StringRequest {


    public UtfRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method, url, listener, errorListener);
    }

    public UtfRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(url, listener, errorListener);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse networkResponse){
        String mString = "";
        try {
            //解决utf-8 乱码问题(因为响应头没有 表明编码)
            mString = new String(networkResponse.data, "utf-8");
        }catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(mString, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }
}
