package com.yangyongwen.zhihudailypaper.Provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.yangyongwen.zhihudailypaper.Utils.LogUtils;

/**
 * Created by samsung on 2016/2/2.
 */
public class ZhihuContract {

    public static final String TAG= LogUtils.makeLogTag(ZhihuContract.class);

    public ZhihuContract(){}

    public static abstract class TableStory implements BaseColumns {
        public static final String TABLE_NAME="story";
        public static final String COLUMN_NAME_STORY_ID="story_id";
        public static final String COLUMN_NAME_DATE="date";
        public static final String COLUMN_NAME_IMAGE_URL="imageurl";
        public static final String COLUMN_NAME_TYPE="type";
        public static final String COLUMN_NAME_GA_PREFIX="gaprefix";
        public static final String COLUMN_NAME_TITLE="title";
        public static final String COLUMN_NAME_MULTIPIC="multipic";
        public static final String COLUMN_NAME_ISREAD="isread";
        public static final String COLUMN_NAME_THEME_ID="theme_id";
        public static final String COLUMN_NAME_THEME_NAME="theme_name";
    }


    public static final String SQL_CREATE_STORY_TABLE="CREATE TABLE "+
            TableStory.TABLE_NAME+
            " ("+TableStory._ID+" INTEGER PRIMARY KEY,"+
            TableStory.COLUMN_NAME_STORY_ID+" INTEGER UNIQUE,"+
            TableStory.COLUMN_NAME_DATE+" TEXT,"+
            TableStory.COLUMN_NAME_IMAGE_URL+" TEXT,"+
            TableStory.COLUMN_NAME_TYPE+" INTEGER,"+
            TableStory.COLUMN_NAME_GA_PREFIX+" TEXT,"+
            TableStory.COLUMN_NAME_TITLE+" TEXT,"+
            TableStory.COLUMN_NAME_MULTIPIC+" INTEGER, "+
            TableStory.COLUMN_NAME_ISREAD+" INTEGER, "+
            TableStory.COLUMN_NAME_THEME_ID+" INTEGER " +
            TableStory.COLUMN_NAME_THEME_NAME+" TEXT "+")";
    
    
    public static final String SQL_DELETE_STORY_TABLE="DROP TABLE IF EXISTS "+TableStory.TABLE_NAME+";";


    public static void createStoryTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_STORY_TABLE);
        LogUtils.LOGD(TAG,"create Story  table");
    }

    public static void deleteStoryTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_STORY_TABLE);
        LogUtils.LOGD(TAG, "delete Story  table");
    }



    public static abstract class TableStoryDetail implements BaseColumns {
        public static final String TABLE_NAME="story_detail";
        public static final String COLUMN_NAME_STORY_ID="story_id";
        public static final String COLUMN_NAME_BODY="body";
        public static final String COLUMN_NAME_IMAGE_SOURCE="image_source";
        public static final String COLUMN_NAME_RECOMMENDERS="recommenders";            //数组,且仅包含推荐者的头像的url，读取之后要以“，”进行分割
        public static final String COLUMN_NAME_TITLE="title";
        public static final String COLUMN_NAME_IMAGE="image";
        public static final String COLUMN_NAME_SHARE_URL="share_url";
        public static final String COLUMN_NAME_JS="js";                                //数组，读取之后要进行分割
        public static final String COLUMN_NAME_GA_PREFIX="ga_prefix";
        public static final String COLUMN_NAME_SECTION_THUMBNAIL="section_thumbnail";
        public static final String COLUMN_NAME_SECTION_ID="section_id";
        public static final String COLUMN_NAME_SECTION_NAME="section_name";
        public static final String COLUMN_NAME_TYPE="type";
        public static final String COLUMN_NAME_CSS="css";                              //数组，读取之后要进行分割
        public static final String COLUMN_NAME_THEME_ID="theme_id";
        public static final String COLUMN_NAME_THEME_NAME="theme_name";
        public static final String COLUMN_NAME_THEME_THUMBNAIL="theme_thumbnail";

    }



    public static final String SQL_CREATE_STORY_DETAIL_TABLE="CREATE TABLE "+
            TableStoryDetail.TABLE_NAME+
            " ("+TableStoryDetail._ID+" INTEGER PRIMARY KEY,"+
            TableStoryDetail.COLUMN_NAME_STORY_ID+" INTEGER UNIQUE,"+
            TableStoryDetail.COLUMN_NAME_BODY+" TEXT,"+
            TableStoryDetail.COLUMN_NAME_IMAGE_SOURCE+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_RECOMMENDERS+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_TITLE+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_IMAGE+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_SHARE_URL+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_JS+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_GA_PREFIX+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_SECTION_THUMBNAIL+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_SECTION_ID+ " INTEGER,"+
            TableStoryDetail.COLUMN_NAME_SECTION_NAME+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_TYPE+ " INTEGER,"+
            TableStoryDetail.COLUMN_NAME_CSS+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_THEME_ID+ " INTEGER,"+
            TableStoryDetail.COLUMN_NAME_THEME_NAME+ " TEXT,"+
            TableStoryDetail.COLUMN_NAME_THEME_THUMBNAIL+ " TEXT"+" )";


    public static void createStoryDetailTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_STORY_DETAIL_TABLE);
        LogUtils.LOGD(TAG,"create Story  table");
    }


    public static final String SQL_DELETE_STORY_DETAIL_TABLE="DROP TABLE IF EXISTS "+TableStoryDetail.TABLE_NAME+";";

    public static void deleteStoryDetailTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_STORY_DETAIL_TABLE);
        LogUtils.LOGD(TAG, "delete Story  table");
    }

    public static abstract class TableStoryTheme implements BaseColumns {
        public static final String TABLE_NAME="theme";
        public static final String COLUMN_NAME_THEME_ID="theme_id";
        public static final String COLUMN_NAME_THEME_NAME="theme_name";
        public static final String COLUMN_NAME_DESCRIPTION="description";
        public static final String COLUMN_NAME_THUMBNAIL="thumbnail";
        public static final String COLUMN_NAME_COLOR="color";
    }

    public static final String SQL_CREATE_STORY_THEME_TABLE="CREATE TABLE "+
            TableStoryTheme.TABLE_NAME+
            " ("+TableStoryTheme._ID+" INTEGER PRIMARY KEY,"+
            TableStoryTheme.COLUMN_NAME_THEME_ID+" INTEGER UNIQUE,"+
            TableStoryTheme.COLUMN_NAME_THEME_NAME+" TEXT,"+
            TableStoryTheme.COLUMN_NAME_DESCRIPTION+" TEXT,"+
            TableStoryTheme.COLUMN_NAME_THUMBNAIL+" TEXT"+
            TableStoryTheme.COLUMN_NAME_COLOR+" INTEGER"+" )";

    public static void createStoryThemeTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_STORY_THEME_TABLE);
        LogUtils.LOGD(TAG,"create Story theme table");
    }


    public static void initStoryThemeTable(SQLiteDatabase db){

    }


    public static final String SQL_DELETE_STORY_THEME_TABLE="DROP TABLE IF EXISTS "+TableStoryTheme.TABLE_NAME+";";


    public static void deleteStoryThemeTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_STORY_THEME_TABLE);
        LogUtils.LOGD(TAG,"delete Story theme table");
    }


    public static abstract class TableStoryThemeDetail implements BaseColumns {
        public static final String TABLE_NAME="theme_detail";
        public static final String COLUMN_NAME_THEME_ID="theme_id";
        public static final String COLUMN_NAME_THEME_NAME="theme_name";
        public static final String COLUMN_NAME_DESCRIPTION="description";
        public static final String COLUMN_NAME_COLOR="color";
        public static final String COLUMN_NAME_EDITORS="editors";
        public static final String COLUMN_NAME_BACKGROUND="background";
        public static final String COLUMN_NAME_IMAGE="image";
        public static final String COLUMN_NAME_IMAGE_SOURCE="image_source";
    }


    public static final String SQL_CREATE_STORY_THEME_DETAIL_TABLE="CREATE TABLE "+
            TableStoryThemeDetail.TABLE_NAME+
            " ("+TableStoryThemeDetail._ID+" INTEGER PRIMARY KEY,"+
            TableStoryThemeDetail.COLUMN_NAME_THEME_ID+" INTEGER UNIQUE,"+
            TableStoryThemeDetail.COLUMN_NAME_THEME_NAME+" TEXT,"+
            TableStoryThemeDetail.COLUMN_NAME_DESCRIPTION+" TEXT,"+
            TableStoryThemeDetail.COLUMN_NAME_COLOR+" INTEGER,"+
            TableStoryThemeDetail.COLUMN_NAME_EDITORS+" TEXT,"+
            TableStoryThemeDetail.COLUMN_NAME_BACKGROUND+" TEXT,"+
            TableStoryThemeDetail.COLUMN_NAME_IMAGE+" TEXT,"+
            TableStoryThemeDetail.COLUMN_NAME_IMAGE_SOURCE+" TEXT"+")";

    public static void createStoryThemeDetailTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_STORY_THEME_DETAIL_TABLE);
        LogUtils.LOGD(TAG,"create Story theme detail table");
    }


    public static final String SQL_DELETE_STORY_THEME_DETAIL_TABLE="DROP TABLE IF EXISTS "+TableStoryThemeDetail.TABLE_NAME+";";


    public static void deleteStoryThemeDetailTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_STORY_THEME_DETAIL_TABLE);
        LogUtils.LOGD(TAG,"delete Story theme detail table");
    }


    private class TopStory {
        private String title;                                   //标题
        private String image;                            //图片的URL，为数组类型，但是目前基本只有一张图片
        private boolean multipic;                               //多图预警，如果是true则在图片缩略图中加入一个多图的标志
        private String ga_prefix;                               //用于Google Analysis，不懂。。。
        private int type;                                       //用处不明
        private int id;
    }

    public static abstract class TableTopStory implements BaseColumns {
        public static final String TABLE_NAME = "top_story";
        public static final String COLUMN_NAME_STORY_ID="top_story_id";
        public static final String COLUMN_NAME_IMAGE="image";
        public static final String COLUMN_NAME_TITLE="title";
        public static final String COLUMN_NAME_ISMULTIPIC="ismultipic";
        public static final String COLUMN_NAME_GA_PREFIX="ga_prefix";
        public static final String COLUMN_NAME_TYPE="type";
    }

    public static final String SQL_CREATE_TOP_STORY_TABLE="CREATE TABLE "+
            TableTopStory.TABLE_NAME+
            " ("+TableTopStory._ID+" INTEGER PRIMARY KEY,"+
            TableTopStory.COLUMN_NAME_STORY_ID+" INTEGER UNIQUE,"+
            TableTopStory.COLUMN_NAME_IMAGE+" TEXT,"+
            TableTopStory.COLUMN_NAME_TITLE+" TEXT,"+
            TableTopStory.COLUMN_NAME_ISMULTIPIC+" INTEGER,"+
            TableTopStory.COLUMN_NAME_GA_PREFIX+" INTEGER,"+
            TableTopStory.COLUMN_NAME_TYPE+" INTEGER"+")";


    public static void createTopStoryTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TOP_STORY_TABLE);
        LogUtils.LOGD(TAG,"create top story table");
    }

    public static final String SQL_DELETE_TOP_STORY_TABLE="DROP TABLE IF EXISTS "+TableTopStory.TABLE_NAME+";";

    public static void deleteTopStoryTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_TOP_STORY_TABLE);
        LogUtils.LOGD(TAG,"delete top story table");
    }

}
