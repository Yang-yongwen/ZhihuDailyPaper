package com.yangyongwen.zhihudailypaper.homePage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.network.NetworkRequest;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.QueryOperate;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;
import com.yangyongwen.zhihudailypaper.utils.DateUtils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samsung on 2016/2/3.
 */
public class HomePageModel implements Model,QueryOperate{

    private final static String TAG=LogUtils.makeLogTag(HomePageModel.class);

    public final static int QUERY_DATA=1;
    public final static int READ_DATA_FROM_CURSOR=2;

    private boolean isLoadLatest;

    public final static String QUERY_ID="query_id";
    public final static String DATE="date";

    private final Context mContext;
    private Handler mHandler;

    private HandlerThread mHandlerThread;


    private ArrayList<TopStory> mTopStories;
    private HashMap<String,ArrayList<Story>> mStories;


    private ModelUpdatedListener mModelUpdateListener;





    public HomePageModel(Context context){
        mContext=context;
        isLoadLatest=false;
        mHandlerThread=new HandlerThread("HomePageWorkerThread");
        mHandlerThread.start();
        mHandler=new DataProcessHandler(mHandlerThread.getLooper());
        mStories=new HashMap<String,ArrayList<Story>>();
        mTopStories=new ArrayList<TopStory>();
        //requestModelUpdate(HomePageActionEnum.INIT, null, null);
        //testFunction();




    }



    public ArrayList<TopStory> getTopStories(){
        return mTopStories;
    }

    public ArrayList<Story> getDailyStories(String date){
        return mStories.get(date);
    }




    /*
    * 1.查询top_stories     从TopStory表中检索出所有的topstory
    * 2.查询day_stories     依据date从story table中查找对应的day_stories
    * */
    @Override
    public QueryEnum[] getQueries(){
        return HomePageQueryEnum.values();
    }



    /*
    * 根据上述的query分别对Model进行更新
    * */
    @Override
    public boolean readDataFromCursor(Cursor cursor,QueryEnum query,Bundle args){
        if(cursor==null){
            LogUtils.LOGD(TAG,"cursor is null,read data from cursor failed");
            return false;
        }

        if(query==HomePageQueryEnum.TOPSTORY){
            TopStory topStory;
            if (cursor.moveToFirst()){
                do{
                    topStory=populateTopStoryFromCursorRow(cursor);
                    mTopStories.add(topStory);
                }while(cursor.moveToNext());
            }
            cursor.close();

            isLoadLatest=false;

            //TODO read all Top stories ,you can sort topstories by the rule you want


            if(mTopStories.size()!=0){
                return true;
            }else {
                return false;
            }



        }else if(query==HomePageQueryEnum.DAILYSTORY){
            ArrayList<Story> dailyStories=new ArrayList<Story>();
            if (cursor.moveToFirst()){
                do{
                    Story story=populateStoryFromCursorRow(cursor);
                    dailyStories.add(story);
                }while(cursor.moveToNext());
            }
            cursor.close();
            //TODO sort DailyStoies by the rule you want

            if(dailyStories.size()!=0) {
                String date=dailyStories.get(0).getDate();
                mStories.put(date,dailyStories);
                return true;
            }else{
                return false;
            }
        }else{
            LogUtils.LOGD(TAG,"query is invalid");
            return false;
        }


    }

    private TopStory populateTopStoryFromCursorRow(Cursor cursor){
        TopStory topStory=new TopStory();
        topStory.setId(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID)));
        topStory.setGa_prefix(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_GA_PREFIX)));
        topStory.setImage(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_IMAGE)));
        topStory.setTitle(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_TITLE)));
        topStory.setType(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_TYPE)));
        topStory.setDate(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_DATE)));
        topStory.setMultipic(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableTopStory.COLUMN_NAME_ISMULTIPIC))==1);          //数据库中无 boolean 字段，用int表示，1：true，0：false；

        return topStory;
    }

    private Story populateStoryFromCursorRow(Cursor cursor){
        Story story=new Story();
        story.setId(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_STORY_ID)));
        story.setTitle(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_TITLE)));
        story.setType(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_TYPE)));
        story.setThemeId(
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_THEME_ID)));
        story.setThemeName(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_THEME_NAME)));
        story.setGa_prefix(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_GA_PREFIX)));
        story.setDate(
                cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_DATE)));
        story.setMultipic(                                             //数据库中无 boolean 字段，用int表示，1：true，0：false；
                cursor.getInt(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_MULTIPIC)) == 1);
        story.setImages(                                              //从服务器获得的images是一个数组，但存储时候只存储第一个元素，此次取出也应一致
                new String[]{cursor.getString(cursor.getColumnIndex(
                        ZhihuContract.TableStory.COLUMN_NAME_IMAGE_URL))});
        return story;
    }



    /*
    * 根据User的操作对Model进行更新
    * 1.下拉刷新：网络请求latest，将latest写入SP、数据库，从SP、数据库中读取top stories、today stories、yesterday stories
    * 2.上拉加载：跟进date从story table 读取stories，如果返回null，则网络请求，然后在从story table中读取。
    * */
    @Override
    public boolean requestModelUpdate(UserActionEnum actionEnum,Bundle args, final Runnable callback){

        Cursor cursor;

        if(actionEnum==HomePageActionEnum.LOADLATEST){      //先进行网络加载,然后直接网络查找TopStory和两次DailyStory  直接使用发送相应的message，Handler的消息队列默认是FIFO，发送顺序即为处理顺序；
            isLoadLatest=false;
            NetworkRequestProxy.getInstance(mContext).requestLatestStory(new NetworkRequest.RequestCallback() {
                @Override
                public void onSuccess() {
                    isLoadLatest=true;
                    mStories.clear();
                    mTopStories.clear();
                    Message message=mHandler.obtainMessage(QUERY_DATA);
                    Bundle bundle=new Bundle();
                    bundle.putInt(QUERY_ID,HomePageQueryEnum.TOPSTORY.getId());
                    bundle.putString(DATE, "");
                    message.setData(bundle);
                    message.sendToTarget();

                    Message message1=mHandler.obtainMessage(QUERY_DATA);
                    Bundle bundle1=new Bundle();
                    String today= DateUtils.getToday();
                    bundle1.putInt(QUERY_ID,HomePageQueryEnum.DAILYSTORY.getId());
                    bundle1.putString(DATE, today);
                    message1.setData(bundle1);
                    message1.sendToTarget();

                    Message message2=mHandler.obtainMessage(QUERY_DATA);
                    Bundle bundle2=new Bundle();
                    String yesterday=DateUtils.getDay(-1);
                    bundle2.putInt(QUERY_ID,HomePageQueryEnum.DAILYSTORY.getId());
                    bundle2.putString(DATE, yesterday);
                    message2.setData(bundle2);
                    message2.sendToTarget();
                }

                @Override
                public void onFailure() {
                    LogUtils.LOGD(TAG, "network request failed");
                    mModelUpdateListener.onModelUpdateError(HomePageQueryEnum.TOPSTORY);
                    //Toast.makeText(mContext,"网络请求失败，请检查网络或重试",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(actionEnum==HomePageActionEnum.LOADDAILY){   //查找一次DailyStory
            Message message=mHandler.obtainMessage(QUERY_DATA);
            Bundle bundle=new Bundle();
            String date=args.getString(DATE);
            bundle.putInt(QUERY_ID,HomePageQueryEnum.DAILYSTORY.getId());
            bundle.putString(DATE, date);
            message.setData(bundle);
            message.sendToTarget();

        }else if(actionEnum==HomePageActionEnum.INIT){
            Cursor cursor1=query(HomePageQueryEnum.TOPSTORY,null);
            if(readDataFromCursor(cursor1,HomePageQueryEnum.TOPSTORY,null)){
                mModelUpdateListener.onModelUpdateSuccess(HomePageQueryEnum.TOPSTORY, new Bundle());

                Message message1=mHandler.obtainMessage(QUERY_DATA);
                Bundle bundle1=new Bundle();
                String today= mTopStories.get(0).getDate();
                bundle1.putInt(QUERY_ID,HomePageQueryEnum.DAILYSTORY.getId());
                bundle1.putString(DATE, today);
                message1.setData(bundle1);
                message1.sendToTarget();

                Message message2=mHandler.obtainMessage(QUERY_DATA);
                Bundle bundle2=new Bundle();
                String yesterday=DateUtils.lastDay(today);
                bundle2.putInt(QUERY_ID,HomePageQueryEnum.DAILYSTORY.getId());
                bundle2.putString(DATE, yesterday);
                message2.setData(bundle2);
                message2.sendToTarget();
            }
        } else{
            LogUtils.LOGD(TAG,"invalid UserAction");
            return false;
        }

        return true;
    }


    /*
    * 调用NetworkRequest请求网络时，要传递一个RequestCallback的匿名类，重写onSuccess，在其内先query，然后readdataFromCursor更新数据,重写onFailure,输出错误信息
    * */


    /*
    * query 系列操作仅负责从数据库中检索，即使返回null也不负责请求网络加载，网络加载由presenter发起。
    * （如果能将网络加载封装在query中更为理想，但是网络加载模型是监听者模式，无法再callback中回到原来得query函数在检索，返回cursor，除非使用阻塞-notify模式）
    * */

    @Override
    public Cursor queryTopStories(){

//        String[] mProjection={
//                ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID,
//                ZhihuContract.TableTopStory.COLUMN_NAME_TITLE,
//                ZhihuContract.TableTopStory.COLUMN_NAME_IMAGE,
//                ZhihuContract.TableTopStory.COLUMN_NAME_TYPE,
//                ZhihuContract.TableTopStory.COLUMN_NAME_GA_PREFIX,
//                ZhihuContract.TableTopStory.COLUMN_NAME_ISMULTIPIC
//        };


        Cursor cursor=mContext.getContentResolver().query(ZhihuContentProvider.STORY_TOP_STORY_CONTENT_URI, null, null,
                null, ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID + " ASC");
        return cursor;

    }


    @Override
    public Cursor queryDailyStories(String date){

        String[] mProjection={
                ZhihuContract.TableStory.COLUMN_NAME_STORY_ID,
                ZhihuContract.TableStory.COLUMN_NAME_TITLE,
                ZhihuContract.TableStory.COLUMN_NAME_IMAGE_URL,
                ZhihuContract.TableStory.COLUMN_NAME_DATE,
                ZhihuContract.TableStory.COLUMN_NAME_ISREAD,
                ZhihuContract.TableStory.COLUMN_NAME_MULTIPIC,
                ZhihuContract.TableStory.COLUMN_NAME_GA_PREFIX,
                ZhihuContract.TableStory.COLUMN_NAME_TYPE,
                ZhihuContract.TableStory.COLUMN_NAME_THEME_ID,
                ZhihuContract.TableStory.COLUMN_NAME_THEME_NAME
        };

        String mSelectionClause=ZhihuContract.TableStory.COLUMN_NAME_DATE+" = ?";
        String[] mSelectionArgs={date};

        Cursor cursor=mContext.getContentResolver().query(ZhihuContentProvider.STORY_CONTENT_URI, mProjection, mSelectionClause,
                mSelectionArgs, ZhihuContract.TableStory.COLUMN_NAME_STORY_ID + " ASC");
        return cursor;

    }


    public Cursor query(QueryEnum queryEnum,Bundle bundle){

        if(queryEnum==HomePageQueryEnum.TOPSTORY){
            Uri uri=ZhihuContentProvider.STORY_TOP_STORY_CONTENT_URI;
            String[] mProjection=queryEnum.getProjection();
            String mSelectionClause=null;
            String[]  mSelecttionArgs=null;
            return mContext.getContentResolver().query(uri,mProjection,mSelectionClause,mSelecttionArgs,
                    ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID + " ASC");
        }else if(queryEnum==HomePageQueryEnum.DAILYSTORY){
            Uri uri=ZhihuContentProvider.STORY_CONTENT_URI;
            String[] mProjection=queryEnum.getProjection();
            String mSelectionClause=ZhihuContract.TableStory.COLUMN_NAME_DATE+" = ?";
            String[] mSelecttionArgs={bundle.getString("date")};
            return mContext.getContentResolver().query(uri,mProjection,mSelectionClause,mSelecttionArgs,
                    ZhihuContract.TableStory.COLUMN_NAME_STORY_ID + " ASC");
        }else{
            LogUtils.LOGD(TAG,"QueryEnum is invalid");
            return null;
        }
    }





    /*
    *Function:
    * 1.query data from sql db(if data not in local, it will request data from server automatically and carry on)，
    * after query successfully, it will read cursor data to update model
    * 2.read data from cursor to update model
    *Run on worker thread to avoiding ANR
     */

    private class DataProcessHandler extends Handler{



        public DataProcessHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(final Message message){
            Cursor cursor=null;
            final Bundle bundle=message.getData();

            if(bundle==null){
                LogUtils.LOGD(TAG,"Bundle is null,you must put query id and extract data(ex: date) in it");
                return;
            }

            int queryId=bundle.getInt(QUERY_ID);
            final QueryEnum queryEnum=getQueryEnum(queryId);
            switch (message.what){
                case QUERY_DATA:
                    cursor=query(queryEnum,bundle);
                    if(cursor==null||(!cursor.moveToFirst()||cursor.getCount()==0)){           //not have data in database, must request data from server, request type depend on queryEnum
                        if(queryEnum==HomePageQueryEnum.TOPSTORY){
                            NetworkRequestProxy.getInstance(mContext).requestLatestStory(new NetworkRequest.RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    Message message1=mHandler.obtainMessage(QUERY_DATA);
                                    message1.setData(bundle);
                                    message1.sendToTarget();
                                }
                                @Override
                                public void onFailure() {
                                    LogUtils.LOGD(TAG,"network request failed");
                                    Toast.makeText(mContext,"网络请求失败，请检查网络或重试",Toast.LENGTH_SHORT).show();
                                    mModelUpdateListener.onModelUpdateError(queryEnum);
                                }
                            });


                        }else if(queryEnum==HomePageQueryEnum.DAILYSTORY){
                            String date=bundle.getString(DATE);
                            /*
                            * 刚过凌晨时候，返回的story是昨天的，所以即使加载latest成功了，数据库还是查找不到今天的story，此时直接返回，不加载今天的，直接加载昨天的
                            * */
                            if(date==DateUtils.getToday()&&isLoadLatest){
                                return;
                            }
                            NetworkRequestProxy.getInstance(mContext).requestDailyStory(date, new NetworkRequest.RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    Message message1=mHandler.obtainMessage(QUERY_DATA);
                                    message1.setData(bundle);
                                    message1.sendToTarget();
                                }

                                @Override
                                public void onFailure() {
                                    LogUtils.LOGD(TAG,"network request failed");
                                    Toast.makeText(mContext,"网络请求失败，请检查网络或重试",Toast.LENGTH_SHORT).show();
                                    mModelUpdateListener.onModelUpdateError(queryEnum);

                                }
                            });
                        }
                        return;
                    }
                case READ_DATA_FROM_CURSOR:
                    if(cursor==null){
                        cursor=(Cursor)message.obj;
                    }

                    //TODO post event to notify presenter that model has been update,

                    if(readDataFromCursor(cursor,queryEnum,bundle)){
                        mModelUpdateListener.onModelUpdateSuccess(queryEnum,bundle);
                    }else {
                        mModelUpdateListener.onModelUpdateError(queryEnum);
                    }

                    break;
                default:
                    break;
            }
        }

    }





    /*
    * 网络请求示范函数
    * */
    private void NetworkReuqest(){
        NetworkRequestProxy.getInstance(mContext.getApplicationContext()).requestDailyStory("20160101", new NetworkRequest.RequestCallback() {
            @Override
            public void onSuccess() {
                Cursor cursor=queryDailyStories("20160101");
                readDataFromCursor(cursor,null,null);
            }

            @Override
            public void onFailure() {

            }
        });

        NetworkRequestProxy.getInstance(mContext.getApplicationContext()).requestLatestStory(new NetworkRequest.RequestCallback() {
            @Override
            public void onSuccess() {
                Cursor cursor=queryTopStories();
                readDataFromCursor(cursor,null,null);
            }

            @Override
            public void onFailure() {

            }
        });
    }





    public static enum HomePageActionEnum implements UserActionEnum {
        LOADLATEST(1),
        LOADDAILY(2),
        INIT(3);
        private int id;
        HomePageActionEnum(int id) {
            this.id = id;
        }
        @Override
        public int getId(){
            return id;
        }
    }

    public static enum HomePageQueryEnum implements QueryEnum{

        TOPSTORY(1,new String[]{
                ZhihuContract.TableTopStory.COLUMN_NAME_STORY_ID,
                ZhihuContract.TableTopStory.COLUMN_NAME_IMAGE,
                ZhihuContract.TableTopStory.COLUMN_NAME_TITLE,
                ZhihuContract.TableTopStory.COLUMN_NAME_ISMULTIPIC,
                ZhihuContract.TableTopStory.COLUMN_NAME_GA_PREFIX,
                ZhihuContract.TableTopStory.COLUMN_NAME_TYPE,
                ZhihuContract.TableTopStory.COLUMN_NAME_DATE
        }),
        DAILYSTORY(2,new String[]{
                ZhihuContract.TableStory.COLUMN_NAME_STORY_ID,
                ZhihuContract.TableStory.COLUMN_NAME_DATE,
                ZhihuContract.TableStory.COLUMN_NAME_IMAGE_URL,
                ZhihuContract.TableStory.COLUMN_NAME_TYPE,
                ZhihuContract.TableStory.COLUMN_NAME_GA_PREFIX,
                ZhihuContract.TableStory.COLUMN_NAME_TITLE,
                ZhihuContract.TableStory.COLUMN_NAME_MULTIPIC,
                ZhihuContract.TableStory.COLUMN_NAME_ISREAD,
                ZhihuContract.TableStory.COLUMN_NAME_THEME_ID,
                ZhihuContract.TableStory.COLUMN_NAME_THEME_NAME
        });


        private int id;
        private String[] projection;
        HomePageQueryEnum(int id,String[] projection){
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

    private QueryEnum getQueryEnum(int id){
        for(QueryEnum queryEnum:HomePageQueryEnum.values()){
            if(queryEnum.getId()==id){
                return queryEnum;
            }
        }
        return null;
    }





    private void testFunction(){
//        NetworkRequestProxy.getInstance(mContext.getApplicationContext()).requestLatestStory(new NetworkRequest.RequestCallback() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(mContext,"Latest网络测试成功",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure() {
//                Toast.makeText(mContext,"Latest网络测试失败",Toast.LENGTH_SHORT).show();
//            }
//        });
        NetworkRequestProxy.getInstance(mContext.getApplicationContext()).requestDailyStory("20160206", new NetworkRequest.RequestCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "Daily网络测试成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(mContext, "Daily网络测试失败", Toast.LENGTH_SHORT).show();
            }
        });
    }




    public void setModelUpdateListener(ModelUpdatedListener mModelUpdateListener){
        this.mModelUpdateListener=mModelUpdateListener;
    }





}
