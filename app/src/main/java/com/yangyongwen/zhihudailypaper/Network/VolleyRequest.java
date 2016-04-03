package com.yangyongwen.zhihudailypaper.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yangyongwen.zhihudailypaper.dataStructure.DailyStory;
import com.yangyongwen.zhihudailypaper.dataStructure.LatestStories;
import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryExtraInfo;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeList;
import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.utils.DateUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;


/**
 * Created by samsung on 2016/2/3.
 */
public class VolleyRequest implements NetworkRequest{
    
    private static final String TAG= LogUtils.makeLogTag(VolleyRequest.class);

    private static final int REQUEST_LATEST_STORY=0;
    private static final int REQUEST_DAILY_STORY=1;
    private static final int REQUEST_THEME_LIST=2;
    private static final int REQUEST_STORY_DETAIL=3;
    private static final int REQUEST_STORY_EXTRA_INFO=4;


    private HandlerThread mHandlerThread;

    private Context mContext;

    private final RequestQueue mRequestQueue;

    private final Gson mGson;

    Handler mHandler;


    public RequestQueue getQueue(){
        return mRequestQueue;
    }


    /*
    * 以下两个Request函数是使用Volley发出一个StringRequest，然后在工作线程中parse，并写入到数据库中
    * 代码冗余比较多，后期需要重构
    * */

    public  void requestLatestStory(final RequestCallback callback){

        String url= ZhihuApiContract.LATEST_URL;

        Listener<String> listener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                mHandler.post(new DataProcessRunnable(response, REQUEST_LATEST_STORY, callback));
            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onFailure();
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);
        this.add(stringRequest);

    }


    public  void requestDailyStory(String date, final RequestCallback callback){

        String url=ZhihuApiContract.DAILY_STORY_URL_PREFIX+DateUtils.nextDay(date);

        Listener<String> listener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                mHandler.post(new DataProcessRunnable(response, REQUEST_DAILY_STORY, callback));
            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onFailure();
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);
        this.add(stringRequest);

    }

    @Override
    public void requestThemeList(final RequestCallback callback){
        String url=ZhihuApiContract.THEME_LIST;

        Listener<String> listener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                mHandler.post(new DataProcessRunnable(response, REQUEST_THEME_LIST, callback));
            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onFailure();
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);
        this.add(stringRequest);
    }

    @Override
    public void requestStoryDetail(String storyId, final RequestCallback callback){
        String url=ZhihuApiContract.STORY_DETAIL_URL_PREFIX+storyId;

        Listener<String> listener=new Listener<String>() {
            @Override
            public void onResponse(String response) {
                mHandler.post(new DataProcessRunnable(response,REQUEST_STORY_DETAIL,callback));
            }
        };

        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onFailure();
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);
        this.add(stringRequest);
    }

    @Override
    public void requestStoryExtraInfo(final String storyId, final RequestCallback callback){
        String url=ZhihuApiContract.STORY_EXTRA_INFO_URL_PREFIX+storyId;

        Listener<String> listener=new Listener<String>() {
            @Override
            public void onResponse(String response) {
                mHandler.post(new DataProcessRunnable(storyId+response,REQUEST_STORY_EXTRA_INFO,callback));
            }
        };

        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onFailure();
            }
        };

        ZhihuStrRequest stringRequest=new ZhihuStrRequest(url,listener,errorListener);
        this.add(stringRequest);
    }




    public VolleyRequest(Context context){
        mContext=context;
        mGson=new Gson();
        mRequestQueue= Volley.newRequestQueue(mContext);
        mHandlerThread=new HandlerThread("DataProcessThread");
        mHandlerThread.start();
        mHandler=new DataProcessHandler(mHandlerThread.getLooper());

    }





    private class DataProcessHandler extends Handler{

        public DataProcessHandler(Looper looper){
            super(looper);
        }


        @Override
        public void handleMessage(Message message){

        }


    }


    private class DataProcessRunnable implements Runnable{

        private RequestCallback mRequestCallback;
        private int mType;
        private String mData;


        public DataProcessRunnable(String data,int type,RequestCallback callback){
            mData=data;
            mType=type;
            mRequestCallback=callback;
        }


        public void run(){
            try {
                switch (mType){
                    case REQUEST_LATEST_STORY:
                        processLatestStory(mData);
                        break;
                    case REQUEST_DAILY_STORY:
                        processDailyStory(mData);
                        break;
                    case REQUEST_THEME_LIST:
                        processThemeList(mData);
                        break;
                    case REQUEST_STORY_DETAIL:
                        processStoryDetail(mData);
                        break;
                    case REQUEST_STORY_EXTRA_INFO:
                        processStoryExtraInfo(mData);
                    default:
                        break;
                }
                mRequestCallback.onSuccess();
                LogUtils.LOGD(TAG, "process data (parse and insert) success. data type: "+mType);
            }catch (Exception e){
                LogUtils.LOGD(TAG,"process data (parse and insert) failed");
                mRequestCallback.onFailure();
                e.printStackTrace();
            }
        }
    }


    public void add(Request request){
        mRequestQueue.add(request);
    }


    private void processLatestStory(String data){
        LatestStories latestStories=mGson.fromJson(data, LatestStories.class);
        LogUtils.LOGD(TAG,latestStories.toString());

        Story[] stories=latestStories.getStories();
        String date=latestStories.getDate();
        TopStory[] topStories=latestStories.getTopStories();

        /*
        * theme_id theme_type 还没有加进去
        * */
        for(Story story:stories){
            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_STORY_ID,story.getId());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_DATE,date);
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_GA_PREFIX,story.getGaPrefix());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_IMAGE_URL,story.getImages()[0]);   //目前所读的照片都只有一张,所以直接选择index0
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_ISREAD,story.isReaded());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_MULTIPIC,story.isMultipic());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_TITLE,story.getTitle());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_TYPE,story.getType());
            mContext.getContentResolver().insert(ZhihuContentProvider.STORY_CONTENT_URI,contentValues);
        }

        String[] selectionArgs=null;
        String selection=null;
        mContext.getContentResolver().delete(ZhihuContentProvider.STORY_TOP_STORY_CONTENT_URI, selection, selectionArgs);

        for (TopStory topStory:topStories){
            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_DATE,date);
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID,topStory.getId());
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_IMAGE,topStory.getImage());
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_ISMULTIPIC,topStory.isMultipic());
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_GA_PREFIX,topStory.getGaPrefix());
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_TITLE,topStory.getTitle());
            contentValues.put(ZhihuContract.TableTopStory.COLUMN_NAME_TYPE,topStory.getType());
            mContext.getContentResolver().insert(ZhihuContentProvider.STORY_TOP_STORY_CONTENT_URI,contentValues);
        }

    }

    private void processDailyStory(String data){
        DailyStory dailyStory=mGson.fromJson(data,DailyStory.class);
        LogUtils.LOGD(TAG,dailyStory.toString());
        String date=dailyStory.getDate();
        Story[] stories=dailyStory.getStories();

/*
        * theme_id theme_type 还没有加进去
        * */
        for(Story story:stories){
            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_STORY_ID,story.getId());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_DATE,date);
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_GA_PREFIX,story.getGaPrefix());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_IMAGE_URL,story.getImages()[0]);   //目前所读的照片都只有一张,所以直接选择index0
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_ISREAD,story.isReaded());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_MULTIPIC,story.isMultipic());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_TITLE,story.getTitle());
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_TYPE,story.getType());
            mContext.getContentResolver().insert(ZhihuContentProvider.STORY_CONTENT_URI,contentValues);
        }
    }

    private void processThemeList(String data){
        ThemeList themeList=mGson.fromJson(data,ThemeList.class);
        LogUtils.LOGD(TAG,themeList.toString());
        ThemeList.ThemeItem[] subcribed=themeList.getSubscribed();
        ThemeList.ThemeItem[] others=themeList.getOthers();

        for(ThemeList.ThemeItem item:subcribed){
            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID,item.getId());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_COLOR,item.getColor());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_DESCRIPTION,item.getDescription());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_NAME,item.getName());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THUMBNAIL,item.getThumbnail());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED,true);
            mContext.getContentResolver().insert(ZhihuContentProvider.STORY_THEME_CONTENT_URI,contentValues);
        }

        for(ThemeList.ThemeItem item:others){

            Boolean isSubcribed=false;
            String[] mProjection={ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID,ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED};
            String mSelectionClause=ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID+" =?";
            String[] mSelectionArgs={Integer.toString(item.getId())};
            Cursor cursor=mContext.getContentResolver().query(ZhihuContentProvider.STORY_THEME_CONTENT_URI,mProjection,mSelectionClause,mSelectionArgs,
                    ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID + " ASC");
            if (cursor.moveToFirst()){
                int i=cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED));
                isSubcribed=i==1?true:false;
            }
            cursor.close();


            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID,item.getId());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_COLOR,item.getColor());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_DESCRIPTION,item.getDescription());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_NAME,item.getName());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_THUMBNAIL,item.getThumbnail());
            contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED,isSubcribed);     // 实际应该先判断本地的数据,此theme是否被订阅,后续再改进
            mContext.getContentResolver().insert(ZhihuContentProvider.STORY_THEME_CONTENT_URI,contentValues);
        }





    }

    private void processStoryDetail(String data){
        StoryDetail storyDetail=mGson.fromJson(data,StoryDetail.class);
        LogUtils.LOGD(TAG, storyDetail.toString());

        ContentValues contentValues=new ContentValues();
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID,storyDetail.getId());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE_SOURCE,storyDetail.getImage_source());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_BODY,storyDetail.getBody());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_TITLE,storyDetail.getTitle());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE,storyDetail.getImage());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_SHARE_URL,storyDetail.getShare_url());
//        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_JS,storyDetail.getJs()[0]);         //js[] 仅仅取首元素
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_GA_PREFIX,storyDetail.getGa_prefix());
        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_TYPE,storyDetail.getType());
//        contentValues.put(ZhihuContract.TableStoryDetail.COLUMN_NAME_CSS,storyDetail.getCss()[0]);       //css[] 仅仅取首元素

        mContext.getContentResolver().insert(ZhihuContentProvider.STORY_DETAIL_CONTENT_URI,contentValues);


    }

    private void processStoryExtraInfo(String data){
        String storyId=data.substring(0, 7);
        data=data.substring(7);
        StoryExtraInfo storyExtraInfo=mGson.fromJson(data,StoryExtraInfo.class);
        LogUtils.LOGD(TAG, storyExtraInfo.toString());

        ContentValues contentValues=new ContentValues();
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_STORY_ID,storyId);
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_COMMENTS,storyExtraInfo.getComments());
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_NORMAL_COMMENTS,storyExtraInfo.getNormal_comments());
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POPULARITY,storyExtraInfo.getPopularity());
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_LONG_COMMENTS,storyExtraInfo.getLong_comments());
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_SHORT_COMMENTS,storyExtraInfo.getShort_comments());
        contentValues.put(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POST_REASONS,storyExtraInfo.getPost_reasons());

        mContext.getContentResolver().insert(ZhihuContentProvider.STORY_EXTRA_INFO_CONTENT_URI,contentValues);

    }
    




}
