package com.yangyongwen.zhihudailypaper.common;


import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by samsung on 2016/2/3.
 */
public interface Model {

    public QueryEnum[] getQueries();

    public boolean readDataFromCursor(Cursor cursor,QueryEnum query,Bundle args);

    public boolean requestModelUpdate(UserActionEnum actionEnum,Bundle args);


}
