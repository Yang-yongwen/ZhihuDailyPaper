package com.yangyongwen.zhihudailypaper.HomePage;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.Network.NetworkRequest;
import com.yangyongwen.zhihudailypaper.Network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.QueryOperate;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;

/**
 * Created by samsung on 2016/2/3.
 */
public class HomePageModel implements Model,QueryOperate{

    private final Context mContext;





    public HomePageModel(Context context){
        mContext=context;
        testFunction();
    }




    /*
    * 1.查询top_storiesList  依据关键字，从sharepreference中读取top story id 的列表
    * 2.查询top_stories     依据top story list，从story table中查找对应的top_stories（获取全面信息）
    * 3.查询day_stories     依据date从story table中查找对应的day_stories
    * */
    @Override
    public QueryEnum[] getQueries(){


        return null;
    }



    /*
    * 根据上述的query分别对Model进行更新
    * */
    @Override
    public boolean readDataFromCursor(Cursor cursor,QueryEnum query,Bundle args){




        return true;
    }

    /*
    * 根据User的操作对Model进行更新
    * 1.下拉刷新：网络请求latest，将latest写入SP、数据库，从SP、数据库中读取top stories、today stories、yesterday stories
    * 2.上拉加载：跟进date从story table 读取stories，如果返回null，则网络请求，然后在从story table中读取。
    * */
    @Override
    public boolean requestModelUpdate(UserActionEnum actionEnum,Bundle args){


        return true;
    }


    /*
    * 调用NetworkRequest请求网络时，要传递一个RequestCallback的匿名类，重写onSuccess，在其内先query，然后readdataFromCursor更新数据,重写onFailure,输出错误信息
    * */



    @Override
    public Cursor queryTopStories(){
        return null;
    }


    @Override
    public Cursor queryDailyStories(String date){
        return null;
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





}
