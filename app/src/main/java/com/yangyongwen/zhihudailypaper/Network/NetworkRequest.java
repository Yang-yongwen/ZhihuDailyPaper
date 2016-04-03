package com.yangyongwen.zhihudailypaper.network;

/**
 * Created by samsung on 2016/2/3.
 */
public interface NetworkRequest {


    void requestLatestStory(RequestCallback callback);


    void requestDailyStory(String date,RequestCallback callback);

    void requestThemeList(RequestCallback callback);

    void requestStoryDetail(String storyId,RequestCallback callback);

    void requestStoryExtraInfo(String storyId,RequestCallback callback);



    interface RequestCallback{
        void onSuccess();
        void onFailure();
    }

}
