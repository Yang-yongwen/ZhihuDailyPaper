package com.yangyongwen.zhihudailypaper.common;

import android.database.Cursor;

/**
 * Created by samsung on 2016/2/3.
 */
public interface QueryOperate {


    public Cursor queryTopStories();

    public Cursor queryDailyStories(String date);

}
