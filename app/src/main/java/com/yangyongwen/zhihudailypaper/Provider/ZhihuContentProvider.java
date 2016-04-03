package com.yangyongwen.zhihudailypaper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by samsung on 2016/2/2.
 */
public class ZhihuContentProvider extends ContentProvider{

    private static final String TAG= LogUtils.makeLogTag(ZhihuContentProvider.class);

    public static final String AUTHORITY="com.yangyongwen.zhihudailypaper.provider";

    public static final Uri STORY_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableStory.TABLE_NAME);
    public static final Uri STORY_DETAIL_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableStoryDetail.TABLE_NAME);
    public static final Uri STORY_THEME_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableStoryTheme.TABLE_NAME);
    public static final Uri STORY_THEME_DETAIL_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableStoryThemeDetail.TABLE_NAME);
    public static final Uri STORY_TOP_STORY_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableTopStory.TABLE_NAME);
    public static final Uri STORY_EXTRA_INFO_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+ZhihuContract.TableStoryExtraInfo.TABLE_NAME);


    private static final int STORY_URI_CODE=0;
    private static final int STORY_DETAIL_URI_CODE=1;
    private static final int STORY_THEME_URI_CODE=2;
    private static final int STORY_THEME_DETAIL_URI_CODE=3;
    private static final int TOP_STORY__URI_CODE=4;
    private static final int STORY_EXTRA_INFO_URI_CODE=5;




    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableStory.TABLE_NAME,STORY_URI_CODE);
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableStoryDetail.TABLE_NAME,STORY_DETAIL_URI_CODE);
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableStoryTheme.TABLE_NAME,STORY_THEME_URI_CODE);
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableStoryThemeDetail.TABLE_NAME,STORY_THEME_DETAIL_URI_CODE);
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableTopStory.TABLE_NAME,TOP_STORY__URI_CODE);
        mUriMatcher.addURI(AUTHORITY,ZhihuContract.TableStoryExtraInfo.TABLE_NAME,STORY_EXTRA_INFO_URI_CODE);
    }


    private DbOpenHelper mDbOpenHelper;

    private Context mContext;

    @Override
    public boolean onCreate(){
        LogUtils.LOGD(TAG,"onCreate");
        mDbOpenHelper=new DbOpenHelper(getContext());
        mContext=getContext();
        return true;
    }




    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        LogUtils.LOGD(TAG,"query");
        String table=getTableName(uri);
        if(table==null){
            LogUtils.LOGD(TAG,"table is null");
            throw new IllegalArgumentException("Unsupport Uri"+uri);
        }
        return mDbOpenHelper.getReadableDatabase().query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }


    @Override
    public Uri insert(Uri uri,ContentValues contentValues){
        SQLiteDatabase db=mDbOpenHelper.getWritableDatabase();
        LogUtils.LOGD(TAG, "insert");
        String table=getTableName(uri);
        if(table==null){
            LogUtils.LOGD(TAG,"table is null");
            throw new IllegalArgumentException("Unsupport Uri"+uri);
        }
        db.insertWithOnConflict(table, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);    //如果有相同的story id，将已有的删除，插入新的
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        LogUtils.LOGD(TAG,"update");
        String table=getTableName(uri);
        if(table==null){
            LogUtils.LOGD(TAG, "table is null");
            throw new IllegalArgumentException("Unsupport Uri"+uri);
        }
        SQLiteDatabase db=mDbOpenHelper.getWritableDatabase();
        int row=db.update(table, values, selection, selectionArgs);
        if(row>0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return row;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        LogUtils.LOGD(TAG,"delete");
        String table=getTableName(uri);
        if(table==null){
            LogUtils.LOGD(TAG,"table is null");
            throw new IllegalArgumentException("Unsupport Uri"+uri);
        }
        SQLiteDatabase db=mDbOpenHelper.getWritableDatabase();
        int row=db.delete(table, selection, selectionArgs);
        if(row>0){
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }


    public void deleteAll(Uri uri){
        String table=getTableName(uri);
        if(table==null){
            LogUtils.LOGD(TAG,"table is null");
            throw new IllegalArgumentException("Unsupport Uri"+uri);
        }
        SQLiteDatabase db=mDbOpenHelper.getWritableDatabase();
        String[] selectionArgs={" * "};
        String selection=null;

        switch (table){
            case ZhihuContract.TableStory.TABLE_NAME:
                selection=ZhihuContract.TableStory.COLUMN_NAME_STORY_ID+ " =?";
                break;
            case ZhihuContract.TableStoryDetail.TABLE_NAME:
                selection=ZhihuContract.TableStoryDetail.COLUMN_NAME_STORY_ID+ " =?";
                break;
            default:
                break;
        }
        db.delete(table,selection,selectionArgs);
    }



    @Override
    public String getType(Uri uri){
        return null;
    }


    private String getTableName(Uri uri){
        String table="";
        switch (mUriMatcher.match(uri)){
            case STORY_URI_CODE:
                table=ZhihuContract.TableStory.TABLE_NAME;
                break;
            case STORY_DETAIL_URI_CODE:
                table=ZhihuContract.TableStoryDetail.TABLE_NAME;
                break;
            case STORY_THEME_DETAIL_URI_CODE:
                table=ZhihuContract.TableStoryThemeDetail.TABLE_NAME;
                break;
            case STORY_THEME_URI_CODE:
                table=ZhihuContract.TableStoryTheme.TABLE_NAME;
                break;
            case TOP_STORY__URI_CODE:
                table=ZhihuContract.TableTopStory.TABLE_NAME;
                break;
            case STORY_EXTRA_INFO_URI_CODE:
                table=ZhihuContract.TableStoryExtraInfo.TABLE_NAME;
                break;
            default:
                break;
        }
        return table;
    }


}
