package com.yangyongwen.zhihudailypaper.common;

import android.content.Context;

/**
 * Created by samsung on 2016/2/16.
 */
public interface Presenter {

    public void setModel(Model model);

    public void setUpdatableView(UpdatableView view);

    public Context getContext();

}
