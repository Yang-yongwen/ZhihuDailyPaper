package com.yangyongwen.zhihudailypaper.Network;

import android.content.Context;

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


    public  void requestLatestStory(RequestCallback callback){

    }


    public  void requestDailyStory(String date,RequestCallback callback){

    }


}
