package com.yangyongwen.zhihudailypaper.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by samsung on 2016/2/2.
 */
public class DbOpenHelper extends SQLiteOpenHelper{

    private static final String TAG= LogUtils.makeLogTag(DbOpenHelper.class);
    private static final String DB_NAME="ZhihuDb";
    private Context mContext=null;

    private static final int DB_VERSION=1;

    public DbOpenHelper(Context context){
        super(context,DB_NAME,null, DB_VERSION);
        mContext=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        LogUtils.LOGD(TAG,"DataBase onCreate");

        db.beginTransaction();

        ZhihuContract.createStoryTable(db);
        ZhihuContract.createStoryDetailTable(db);
        ZhihuContract.createStoryThemeTable(db);
        ZhihuContract.createStoryThemeDetailTable(db);
        ZhihuContract.createTopStoryTable(db);
        ZhihuContract.createStoryExtraInfoTable(db);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }



}
