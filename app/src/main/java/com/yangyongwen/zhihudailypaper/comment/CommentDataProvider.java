package com.yangyongwen.zhihudailypaper.comment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yangyongwen.zhihudailypaper.dataStructure.Comment;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.network.ZhihuStrRequest;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by samsung on 2016/4/14.
 */
public class CommentDataProvider {

    private static final String TAG=LogUtils.makeLogTag(CommentDataProvider.class);
    private static final String URL_PREFIX="http://news-at.zhihu.com/api/4/story/";
    private static final int LONG_COMMENT=0;
    private static final int SHORT_COMMENT=1;


    private static CommentDataProvider mInstance;
    private Context mContext;

    private static Handler mHandler;
    private static HandlerThread mHandlerThread;

    private RequestQueue mQueue;
    private Gson mGson;




    private CommentDataProvider(Context context){
        mHandlerThread=new HandlerThread("ThemeDataProcesser");
        mHandlerThread.start();
        mHandler=new Handler(mHandlerThread.getLooper());
        mContext=context;
        mQueue= NetworkRequestProxy.getQueue();
        mGson=new Gson();
    }

    public static CommentDataProvider getInstance(Context context){
        if(mInstance==null){
            synchronized (CommentDataProvider.class){
                if(mInstance==null){
                    mInstance=new CommentDataProvider(context);
                }
            }
        }
        return mInstance;
    }


    public void getCommentList(int type,String id,final DataRequestCallback callback){
        String url;
        if(type==LONG_COMMENT){
            url=getLongCommentUrl(id);
        }else{
            url=getShortCommentUrl(id);
        }
        LogUtils.LOGD(TAG, "url: " + url);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Comments comments=mGson.fromJson(response,Comments.class);
                        LogUtils.LOGD(TAG,"haha");
                        callback.onSuccess(comments,null);
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




    private String getShortCommentUrl(String id){
        String url=URL_PREFIX+id+"/short-comments";
        return url;
    }

    private String getLongCommentUrl(String id){
        String url=URL_PREFIX+id+"/long-comments";
        return url;
    }


    interface DataRequestCallback{
        void onSuccess(Object object,Bundle bundle);
        void onFailure(Bundle bundle);
    }

    public static class Comments{
        public Comment[] comments;
    }

}
