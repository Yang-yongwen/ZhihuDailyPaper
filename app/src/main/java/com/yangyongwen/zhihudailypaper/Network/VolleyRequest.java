package com.yangyongwen.zhihudailypaper.Network;

import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yangyongwen.zhihudailypaper.DataStructure.DailyStory;
import com.yangyongwen.zhihudailypaper.DataStructure.LatestStories;
import com.yangyongwen.zhihudailypaper.DataStructure.Story;
import com.yangyongwen.zhihudailypaper.DataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.Provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.Provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.Utils.LogUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.yangyongwen.zhihudailypaper.Provider.ZhihuContract;


/**
 * Created by samsung on 2016/2/3.
 */
public class VolleyRequest implements NetworkRequest{
    
    private static final String TAG= LogUtils.makeLogTag(VolleyRequest.class);

    private static final int REQUEST_LATEST_STORY=0;
    private static final int REQUEST_DAILY_STORY=1;


    private HandlerThread mHandlerThread;

    private Context mContext;

    private final RequestQueue mRequestQueue;

    private final Gson mGson;

    Handler mHandler;


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

        String url=ZhihuApiContract.DAILY_STORY_URL_PREFIX+date;

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
                    default:
                        break;
                }
                mRequestCallback.onSuccess();
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

    




}
