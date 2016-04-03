package com.yangyongwen.zhihudailypaper.common;

import android.net.Uri;
import android.os.Bundle;

/**
 * Created by samsung on 2016/2/16.
 */
public interface UpdatableView<M> {

    public void displayData(M model,QueryEnum query,Bundle bundle);

    public void displayErrorMessage(QueryEnum query);

    public Uri getDataUri(QueryEnum query);

    public void addListener(UserActionListener listener);


    interface UserActionListener{
        public void onUserAction(UserActionEnum action,Bundle args);
    }


}
