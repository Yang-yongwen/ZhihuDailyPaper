package com.yangyongwen.zhihudailypaper.Network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.yangyongwen.zhihudailypaper.Utils.LogUtils;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

/**
 * Created by yangyongwen on 16/2/6.
 */
public class ZhihuStrRequest extends StringRequest {

    private static final String TAG= LogUtils.makeLogTag(ZhihuStrRequest.class);

    public ZhihuStrRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(url,listener,errorListener);
    }

    public ZhihuStrRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method,url,listener,errorListener);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//        response.setContentType("text/html; charset=utf-8");
//        super.parseNetworkResponse(response);

//        String parsed;
//        try {
//            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            LogUtils.LOGD(TAG,HttpHeaderParser.parseCharset(response.headers));
//        } catch (UnsupportedEncodingException var4) {
//            try {
//                parsed = new String(response.data,"utf-8");
//            }catch (Exception e){
//                parsed = new String(response.data);
//                e.printStackTrace();
//            }
//
//        }
//
//        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

        /*
        * 返回数据字符集为null,而HttpHeaderParser.parseCharset默认返回"ISO-8859-1"
        * */
        try {
            String type = response.headers.get(HTTP.CONTENT_TYPE);
            if (type == null) {
                LogUtils.LOGD(TAG,"type is null");
                type = "utf-8";
                response.headers.put(HTTP.CONTENT_TYPE, type);
            } else if (!type.contains("UTF-8")) {
                LogUtils.LOGD(TAG,"type is not null,but not contain UTF-8");
                type += ";" + "utf-8";
                response.headers.put(HTTP.CONTENT_TYPE, type);
            }
        } catch (Exception e) {
            //print stacktrace e.g.
            e.printStackTrace();
        }
        return super.parseNetworkResponse(response);

    }

}
