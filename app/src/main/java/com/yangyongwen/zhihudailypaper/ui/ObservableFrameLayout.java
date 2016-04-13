package com.yangyongwen.zhihudailypaper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by samsung on 2016/4/13.
 */
public class ObservableFrameLayout extends FrameLayout{
    private OnScrollChangedCallback mOnScrollChangedCallback;
    private OnTouchCallback mOnTouchCallback;

    public ObservableFrameLayout(final Context context)
    {
        super(context);
    }

    public ObservableFrameLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ObservableFrameLayout(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t,oldl,oldt);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){

        if(mOnTouchCallback!=null){
            mOnTouchCallback.onTouch(event);
        }

        return super.onInterceptTouchEvent(event);
    }


    public OnScrollChangedCallback getOnScrollChangedCallback()
    {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
    {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public void setOnTouchCallback(final OnTouchCallback callback){
        mOnTouchCallback=callback;
    }


    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback
    {
        public void onScroll(int l, int t,int oldl,int oldt);
    }

    public static interface OnTouchCallback{
        void onTouch(MotionEvent event);
    }

}
