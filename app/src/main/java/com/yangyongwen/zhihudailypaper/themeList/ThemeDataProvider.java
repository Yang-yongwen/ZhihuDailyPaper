package com.yangyongwen.zhihudailypaper.themeList;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.network.ZhihuApiContract;
import com.yangyongwen.zhihudailypaper.network.ZhihuStrRequest;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by samsung on 2016/3/31.
 */




/*
* 此模块不使用mvp模式，仅仅是为了现在尽快开发。后期需要可以重构
*
* 每次数据都根据ThemeId从网络加载
* */


public class ThemeDataProvider {

    private static final String TAG= LogUtils.makeLogTag(ThemeDataProvider.class);

    private static final String URL_PREFIX="http://news-at.zhihu.com/api/4/theme/";


    private static Handler mHandler;
    private static HandlerThread mHandlerThread;

    private static ThemeDataProvider mInstance;

    private Context mContext;

    private RequestQueue mQueue;
    private Gson mGson;




    private ThemeDataProvider(Context context){
        mHandlerThread=new HandlerThread("ThemeDataProcesser");
        mHandlerThread.start();
        mHandler=new Handler(mHandlerThread.getLooper());
        mContext=context;
        mQueue= NetworkRequestProxy.getQueue();
        mGson=new Gson();
    }


    public static ThemeDataProvider getInstance(Context context){
        if(mInstance==null){
            synchronized (ThemeDataProvider.class){
                if(mInstance==null){
                    mInstance=new ThemeDataProvider(context);
                }
            }
        }
        return mInstance;
    }



    public  void getThemeContent(int themeId,final DataRequestCallback callback){

        String url= URL_PREFIX+themeId;
        LogUtils.LOGD(TAG,"url: "+url);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ThemeContent themeContent=mGson.fromJson(response,ThemeContent.class);
                        callback.onSuccess(themeContent,null);
                    }
                });
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onFailure(null);
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);

        mQueue.add(stringRequest);
    }




    interface DataRequestCallback{
        void onSuccess(Object object,Bundle bundle);
        void onFailure(Bundle bundle);
    }







}
