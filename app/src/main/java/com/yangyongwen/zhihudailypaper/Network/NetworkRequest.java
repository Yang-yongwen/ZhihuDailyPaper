package com.yangyongwen.zhihudailypaper.Network;

/**
 * Created by samsung on 2016/2/3.
 */
public interface NetworkRequest {


    void requestLatestStory(RequestCallback callback);


    void requestDailyStory(String date,RequestCallback callback);



    interface RequestCallback{
        void onSuccess();
        void onFailure();
    }

}
