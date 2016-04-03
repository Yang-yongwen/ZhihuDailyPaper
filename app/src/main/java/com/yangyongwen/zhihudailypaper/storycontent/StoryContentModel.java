package com.yangyongwen.zhihudailypaper.storycontent;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryExtraInfo;
import com.yangyongwen.zhihudailypaper.network.NetworkRequest;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samsung on 2016/3/8.
 */
public class StoryContentModel implements Model{

    private final static int QUERY_DATA=0;

    public final static String QUERY_ID="query_id";
    public final static String STORY_ID="story_id";

    private String STORY_DETAIL_STATE="story_detail_state";


    private final static String STORY_ADAPTER_INDEX="story_adapter_index";

    private static final String TAG= LogUtils.makeLogTag(StoryContentModel.class);

    private Context mContext;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private ModelUpdatedListener mModelUpdateListener;


    private HashMap<String,StoryDetail> mStoryDetails;
    private HashMap<String,StoryExtraInfo>mStoryExtraInfos;

    private String mStoryCurrentId;

    private ArrayList<String> mStoryIdList;

    private int mIndexOfStoryId;
    private int mIndexOfAdapter;


    StoryContentModel(Context context,ArrayList<String> ids,String stroyId){
        mContext=context;
        mStoryDetails=new HashMap<>();
        mStoryExtraInfos=new HashMap<>();
        mHandlerThread=new HandlerThread("StoryContentWorkerThread");
        mHandlerThread.start();
        mHandler=new DataProcessHandler(mHandlerThread.getLooper());

        mStoryIdList=ids;
        mStoryCurrentId=stroyId;

    }


    public StoryDetail getStoryDetail(String storyId){
        return mStoryDetails.get(storyId);
    }

    public StoryExtraInfo getStoryExtraInfo(String storyId){
        return mStoryExtraInfos.get(storyId);
    }


    public ArrayList<String> getStoryIdList(){
        return mStoryIdList;
    }

    public String getStoryCurrentId(){
        return mStoryCurrentId;
    }



    /*
    * 切换ViewPager页面的时候调用
    * 返回-1，model不需要更新，mStoryDetails和mStoryExtraInfos就是ViewPager的Adapter
    * 返回非负数，model需要更新，返回值为当前ViewPager的Adapter的CurrentItem
    * 假设mStoryIds的size大于等于5（实际上也一直是大于5的）
    * */

//    public int pageChange(int index){
//
//        if(index==mIndexOfAdapter){
//            return -1;
//        }
//
//        mIndexOfAdapter=index;
//        if(index!=0&&index!=mStoryDetails.size()-1){
//            return -1;
//        }else if(index==0){                            //switch to left page
//            mIndexOfStoryId--;
//            int left=(mIndexOfStoryId-2)>=0?(mIndexOfStoryId-2):0;
//            int right=left+4;
//            int i=mIndexOfStoryId-left;
//
//            mIndexOfAdapter+=i;
//
//            for(int j=4;j>=i;--j){
//                mStoryDetails.put(j,mStoryDetails.get(j-i));
//                mStoryExtraInfos.set(j,mStoryExtraInfos.get(j-i));
//            }
//
//            for(int j=0;j<i;++j){
//                //get Story detail and extraInfo from Db or network;
//            }
//
//            return mIndexOfAdapter;
//
//        }else if(index==mStoryDetails.size()-1){       //switch to right page
//            mIndexOfStoryId++;
//            int right=(mIndexOfStoryId+2)<mStoryIdList.size()?mIndexOfStoryId+2:mStoryIdList.size()-1;
//            int left=right-4;
//            int i=right-mIndexOfStoryId;
//
//            mIndexOfAdapter-=i;
//
//            for(int j=0;j<5-i;++j){
//                mStoryDetails.put(j,mStoryDetails.get(j+i));
//                mStoryExtraInfos.set(j,mStoryExtraInfos.get(j+i));
//            }
//
//            for(int j=5-i;j<5;++j){
//                //get Story detail and extraInfo from Db or network;
//            }
//
//            return mIndexOfAdapter;
//        }else {
//            return -1;
//        }
//    }
//
//    public void initModel(ArrayList<String> storyIds,String storyId){
//        mStoryIdList=storyIds;
//        int index=0;
//        for(String id:storyIds){
//            if(id==storyId){
//                break;
//            }else{
//                ++index;
//            }
//        }
//        int left,right;
//
//        if(index<2){
//            left=0;
//            right=4;
//            mIndexOfAdapter=index;
//        }else if(index>=mStoryIdList.size()-2){
//            right=mStoryIdList.size()-1;
//            left=right-4;
//            mIndexOfAdapter=5-(mStoryIdList.size()-index);
//        }else{
//            left=index-2;
//            right=index+2;
//            mIndexOfAdapter=2;
//        }
//
//        int j=0;                         //get story detail and extrainfo from db or network
//        for(int i=left;i<=right;++i){
//            final String id=mStoryIdList.get(i);
//
//            final int indexOfAdapter=j;
//
//            final Bundle bundle=new Bundle();
//            bundle.putInt(STORY_ADAPTER_INDEX, indexOfAdapter);
//            bundle.putString(STORY_ID, id);
//            bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYDETAIL.getId());
//            Message message=mHandler.obtainMessage(QUERY_DATA);
//            message.setData(bundle);
//            message.sendToTarget();
//
//            NetworkRequestProxy.getInstance(mContext).requestStoryExtraInfo(id, new NetworkRequest.RequestCallback() {
//                @Override
//                public void onSuccess() {
//                    Bundle bundle1=new Bundle();
//                    bundle1.putInt(STORY_ADAPTER_INDEX, indexOfAdapter);
//                    bundle1.putString(STORY_ID, id);
//                    bundle1.putInt(QUERY_ID, StoryContentQueryEnum.STORYEXTRAINFO.getId());
//                    Message message1=mHandler.obtainMessage(QUERY_DATA);
//                    message1.setData(bundle1);
//                    message1.sendToTarget();
//                }
//
//                @Override
//                public void onFailure() {
//                    LogUtils.LOGD(TAG,"request story detail from network failed.storyid: "+id);
//                    Toast.makeText(mContext,"网络请求失败，请检查网络或重试",Toast.LENGTH_SHORT).show();
//                    mModelUpdateListener.onModelUpdateError(StoryContentQueryEnum.STORYEXTRAINFO);
//                }
//            });
//
//            ++j;
//        }
//
//    }





    private void test(){
        Bundle bundle=new Bundle();
        bundle.putString(STORY_ID,"8013943");
        requestModelUpdate(StoryContentActionEnum.INIT, bundle,null);
    }




    @Override
    public QueryEnum[] getQueries(){
        return StoryContentQueryEnum.values();
    }

    @Override
    public boolean readDataFromCursor(Cursor cursor,QueryEnum query,Bundle args){
//        int index=args.getInt(STORY_ADAPTER_INDEX);

        if(cursor==null){
            LogUtils.LOGD(TAG,"cursor is null,read data from cursor failed");
            return false;
        }

        if(query==StoryContentQueryEnum.STORYDETAIL){    //每次查询都只返回一行，model也只持有一个storyDetail 和storyExtraInfo
            StoryDetail storyDetail=null;
            if (cursor.moveToFirst()){
                do{
                    storyDetail=populateStoryDetailFromCursorRow(cursor);
                    mStoryDetails.put(args.getString(STORY_ID),storyDetail);
                }while(cursor.moveToNext());
            }
            cursor.close();

            mStoryCurrentId=args.getString(STORY_ID);

            if(storyDetail!=null){
                return true;
            }else {
                return false;
            }


        }else if (query==StoryContentQueryEnum.STORYEXTRAINFO){
            StoryExtraInfo storyExtraInfo=null;
            if (cursor.moveToFirst()){
                do{
                    storyExtraInfo=populateStoryExtraInfoFromCursorRow(cursor);
                    mStoryExtraInfos.put(args.getString(STORY_ID),storyExtraInfo);
                }while(cursor.moveToNext());
            }
            cursor.close();

            if(storyExtraInfo!=null){
                return true;
            }else {
                return false;
            }


        }else{
            LogUtils.LOGD(TAG,"query invalid");
            return false;
        }


    }

    private StoryDetail populateStoryDetailFromCursorRow(Cursor cursor){
        StoryDetail storyDetail=new StoryDetail();
        storyDetail.setBody(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_BODY)));
        storyDetail.setGa_prefix(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_GA_PREFIX)));
        storyDetail.setId(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID)));
        storyDetail.setImage(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE)));
        storyDetail.setImage_source(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE_SOURCE)));
        storyDetail.setTitle(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_TITLE)));
        storyDetail.setType(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_TYPE)));
        storyDetail.setShare_url(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_SHARE_URL)));
        storyDetail.setJs(new String[]{cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_JS))});
        storyDetail.setCss(new String[]{cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryDetail.COLUMN_NAME_CSS))}); //从服务器获得的images是一个数组，但存储时候只存储第一个元素，此次取出也应一致
        LogUtils.LOGD(TAG,"populate Story detail, id: "+storyDetail.getId());
        return storyDetail;
    }

    private StoryExtraInfo populateStoryExtraInfoFromCursorRow(Cursor cursor){
        StoryExtraInfo storyExtraInfo=new StoryExtraInfo();
        storyExtraInfo.setId(cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_STORY_ID)));
        storyExtraInfo.setLong_comments(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_LONG_COMMENTS)));
        storyExtraInfo.setComments(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_COMMENTS)));
        storyExtraInfo.setNormal_comments(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_NORMAL_COMMENTS)));
        storyExtraInfo.setPopularity(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POPULARITY)));
        storyExtraInfo.setPost_reasons(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POST_REASONS)));
        storyExtraInfo.setShort_comments(cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_SHORT_COMMENTS)));
        return storyExtraInfo;
    }







    @Override
    public boolean requestModelUpdate(UserActionEnum actionEnum,Bundle args,Runnable callback){
        Cursor cursor;
        if(args==null){
            LogUtils.LOGD(TAG,"args must be not null");
            return false;
        }
        final String storyId=args.getString(STORY_ID);
        if(actionEnum==StoryContentActionEnum.INIT){
//            NetworkRequestProxy.getInstance(mContext).requestStoryExtraInfo(storyId, new NetworkRequest.RequestCallback() {
//                @Override
//                public void onSuccess() {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(STORY_ID, storyId);
//                    bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYEXTRAINFO.getId());
//                    Message message = mHandler.obtainMessage(QUERY_DATA);
//                    message.setData(bundle);
//                    message.sendToTarget();
//                }
//
//                @Override
//                public void onFailure() {
//                    LogUtils.LOGD(TAG, "request story extra info from network failed.storyid: " + storyId);
//                    Toast.makeText(mContext, "网络请求失败，请检查网络或重试", Toast.LENGTH_SHORT).show();
//                    mModelUpdateListener.onModelUpdateError(StoryContentQueryEnum.STORYEXTRAINFO);
//                }
//            });

            mModelUpdateListener.onModelUpdateSuccess(StoryContentQueryEnum.STORYIDLIST,args);

//            if(storyId.equals(mStoryIdList.get(0))){
//                requestModelUpdate(StoryContentActionEnum.PAGECHANGE,args,null);
//            }

            Bundle bundle=new Bundle();
            bundle.putString(STORY_ID,storyId);
            bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYDETAIL.getId());
            Message message=mHandler.obtainMessage(QUERY_DATA);
            message.setData(bundle);
            message.sendToTarget();

            NetworkRequestProxy.getInstance(mContext).requestStoryDetail(storyId, new NetworkRequest.RequestCallback() {
                @Override
                public void onSuccess() {
                    Bundle bundle = new Bundle();
                    bundle.putString(STORY_ID, storyId);
                    bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYEXTRAINFO.getId());
                    Message message1 = mHandler.obtainMessage(QUERY_DATA);
                    message1.setData(bundle);
                    message1.sendToTarget();
                }

                @Override
                public void onFailure() {
                    LogUtils.LOGD(TAG, "get extra info from server failed");
                }
            });



        }else if(actionEnum==StoryContentActionEnum.PAGECHANGE){

            if(args.getString(STORY_DETAIL_STATE)!=null&&args.getString(STORY_DETAIL_STATE).equals("exist")){
                //story detail already exist in view adapter, don't need to update it
            }else if(getStoryDetail(storyId)!=null){
                mModelUpdateListener.onModelUpdateSuccess(StoryContentQueryEnum.STORYDETAIL,args);
            }else{
                Bundle bundle=new Bundle();
                bundle.putString(STORY_ID,storyId);
                bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYDETAIL.getId());
                Message message=mHandler.obtainMessage(QUERY_DATA);
                message.setData(bundle);
                message.sendToTarget();
            }

            if(getStoryExtraInfo(storyId)!=null){
                mStoryExtraInfos.remove(storyId);
            }

            NetworkRequestProxy.getInstance(mContext).requestStoryExtraInfo(storyId, new NetworkRequest.RequestCallback() {
                @Override
                public void onSuccess() {
                    Bundle bundle=new Bundle();
                    bundle.putString(STORY_ID,storyId);
                    bundle.putInt(QUERY_ID, StoryContentQueryEnum.STORYEXTRAINFO.getId());
                    Message message=mHandler.obtainMessage(QUERY_DATA);
                    message.setData(bundle);
                    message.sendToTarget();
                }
                @Override
                public void onFailure() {
                    LogUtils.LOGD(TAG,"get extra info from server failed");
                }
            });


        } else{
            LogUtils.LOGD(TAG,"user action invalid");
            return false;
        }

        return true;
    }


    @Override
    public void setModelUpdateListener(ModelUpdatedListener mModelUpdateListener){
        this.mModelUpdateListener=mModelUpdateListener;
    }

    private class DataProcessHandler extends Handler {


        public DataProcessHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message message) {
            Cursor cursor = null;
            final Bundle bundle = message.getData();

            if (bundle == null) {
                LogUtils.LOGD(TAG, "Bundle is null,you must put query id and extract data(ex: date) in it");
                return;
            }

            final String storyId=bundle.getString(STORY_ID);
            int queryId=bundle.getInt(QUERY_ID);
            final QueryEnum queryEnum=getQueryEnum(queryId);

            switch (message.what){
                case QUERY_DATA:
                    cursor=query(queryEnum,bundle);
                    if(cursor==null||(!cursor.moveToFirst()||cursor.getCount()==0)){   //not have data in database, must request data from server, request type depend on queryEnum
                        if(queryEnum==StoryContentQueryEnum.STORYDETAIL){
                            NetworkRequestProxy.getInstance(mContext).requestStoryDetail(storyId, new NetworkRequest.RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    LogUtils.LOGD(TAG,"story content request from server success");
                                    Message message1=mHandler.obtainMessage(QUERY_DATA);
                                    message1.setData(bundle);
                                    message1.sendToTarget();
                                }

                                @Override
                                public void onFailure() {
                                    LogUtils.LOGD(TAG,"request story detail from network failed.storyid: "+storyId);
                                    Toast.makeText(mContext,"网络请求失败，请检查网络或重试",Toast.LENGTH_SHORT).show();
                                    mModelUpdateListener.onModelUpdateError(queryEnum);

                                }
                            });
                        }else if(queryEnum==StoryContentQueryEnum.STORYEXTRAINFO){    //story extra info will be updated from network before here

                        }
                    }

                    break;
                default:
                    break;
            }

            if(readDataFromCursor(cursor,queryEnum,bundle)){
                mModelUpdateListener.onModelUpdateSuccess(queryEnum,bundle);
            }else {
                mModelUpdateListener.onModelUpdateError(queryEnum);
            }


        }
    }



    @Override
    public Cursor query(QueryEnum queryEnum,Bundle bundle){

        String id=bundle.getString(STORY_ID);
        if (queryEnum==StoryContentQueryEnum.STORYDETAIL){
            Uri uri= ZhihuContentProvider.STORY_DETAIL_CONTENT_URI;
            String[] mProjection=queryEnum.getProjection();
            String mSelectionClause=ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID+" = ?";
            String[]  mSelecttionArgs={id};
             return mContext.getContentResolver().query(uri,mProjection,mSelectionClause,mSelecttionArgs,
                    ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID + " ASC");
        }else if(queryEnum==StoryContentQueryEnum.STORYEXTRAINFO){
            Uri uri= ZhihuContentProvider.STORY_EXTRA_INFO_CONTENT_URI;
            String[] mProjection=queryEnum.getProjection();
            String mSelectionClause=ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_STORY_ID+" = ?";
            String[]  mSelecttionArgs={id};
            return mContext.getContentResolver().query(uri,mProjection,mSelectionClause,mSelecttionArgs,
                    ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_STORY_ID + " ASC");
        }

        return null;
    }



    private QueryEnum getQueryEnum(int id){
        for(QueryEnum queryEnum:StoryContentQueryEnum.values()){
            if(queryEnum.getId()==id){
                return queryEnum;
            }
        }
        return null;
    }


    public static enum StoryContentActionEnum implements UserActionEnum {
        INIT(1),
        PAGECHANGE(2);
        private int id;
        StoryContentActionEnum(int id) {
            this.id = id;
        }
        @Override
        public int getId(){
            return id;
        }
    }

    public static enum StoryContentQueryEnum implements QueryEnum{
        STORYDETAIL(1,new String[]{
                ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_BODY,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE_SOURCE,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_TITLE,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_IMAGE,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_SHARE_URL,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_JS,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_GA_PREFIX,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_TYPE,
                ZhihuContract.TableStoryDetail.COLUMN_NAME_CSS
        }),
        STORYEXTRAINFO(2,new String[]{
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_STORY_ID,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POPULARITY,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_NORMAL_COMMENTS,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_COMMENTS,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_SHORT_COMMENTS,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_POST_REASONS,
                ZhihuContract.TableStoryExtraInfo.COLUMN_NAME_LONG_COMMENTS
        }),
        STORYIDLIST(3,new String[]{});


        private int id;
        private String[] projection;
        StoryContentQueryEnum(int id,String[] projection){
            this.id=id;
            this.projection=projection;
        }

        @Override
        public int getId(){
            return id;
        }
        @Override
        public String[] getProjection(){
            return projection;
        }
    }


}
