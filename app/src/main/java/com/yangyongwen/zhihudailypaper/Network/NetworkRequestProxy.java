package com.yangyongwen.zhihudailypaper.network;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Created by samsung on 2016/2/3.
 */
public class NetworkRequestProxy implements NetworkRequest{

    private static NetworkRequest mInstance;

    /**
     * 在此处可以不同的网络封装库来实例化mInstance
     */

    public static NetworkRequest getInstance(Context context){
        if(mInstance==null){
            synchronized (NetworkRequestProxy.class){
                if(mInstance==null){
                    mInstance=new VolleyRequest(context);
                }
            }
        }
        return mInstance;
    }

    private NetworkRequestProxy(Context context){

    }

    @Override
    public  void requestLatestStory(RequestCallback callback){

    }
    @Override
    public  void requestThemeList(RequestCallback callback){

    }

    @Override
    public  void requestDailyStory(String date,RequestCallback callback){

    }
    @Override
    public void requestStoryDetail(String storyId,RequestCallback callback){

    }
    @Override
    public void requestStoryExtraInfo(String storyId,RequestCallback callback){

    }



    //给ThemeDataProvider使用，后面要删除，，，

    public static RequestQueue getQueue(){
        return ((VolleyRequest)mInstance).getQueue();
    }


}
