package com.yangyongwen.zhihudailypaper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yangyongwen on 16/3/19.
 */
public class StickyLayout extends LinearLayout{

    int lastX=0,lastY=0;

    public StickyLayout(Context context) {
        super(context, (AttributeSet)null, 0);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){



        View view=getChildAt(1);

//
//        switch (event.getAction()){
//            case MotionEvent.ACTION_MOVE:
//                int x=(int)event.getX();
//                int y=(int)event.getY();
//                int deltaX=x-lastX;
//                int deltaY=y-lastY;
//                view.scrollBy(0,-deltaY);
////                ViewGroup.LayoutParams layoutParams=(ViewGroup.LayoutParams)view.getLayoutParams();
////                layoutParams.height-=deltaY;
////                view.requestLayout();
////
////                requestLayout();
//
//                lastX=x;
//                lastY=y;
//                break;
//            case MotionEvent.ACTION_DOWN:
//                lastX=(int)event.getX();
//                lastY=(int)event.getY();
//        }
//
//
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return true;
    }

}
