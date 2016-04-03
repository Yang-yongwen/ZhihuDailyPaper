package com.yangyongwen.zhihudailypaper.common;


import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by samsung on 2016/2/3.
 */
public interface Model {

    public interface ModelUpdatedListener{
        void onModelUpdateSuccess(QueryEnum queryEnum,Bundle bundle);
        void onModelUpdateError(QueryEnum queryEnum);
    }

    public QueryEnum[] getQueries();

    public boolean readDataFromCursor(Cursor cursor,QueryEnum query,Bundle args);

    public boolean requestModelUpdate(UserActionEnum actionEnum,Bundle args,Runnable callback);

    public void setModelUpdateListener(ModelUpdatedListener mModelUpdateListener);

    public Cursor query(QueryEnum queryEnum,Bundle bundle);

}
