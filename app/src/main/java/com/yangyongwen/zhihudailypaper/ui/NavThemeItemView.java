package com.yangyongwen.zhihudailypaper.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by samsung on 2016/2/17.
 */
public class NavThemeItemView extends LinearLayout {

    private final static String TAG= LogUtils.makeLogTag(NavThemeItemView.class);

    private int themeId;

    private boolean followed;

    private SelectListener selectListener;
    private  FollowListener followListener;

    private LinearLayout themeItem;
    private ImageView followImg;

    private String title;

    public NavThemeItemView(Context context){
        this(context, null);
    }

    public NavThemeItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.nav_drawer_theme_item_view, this, true);
        themeItem=(LinearLayout)findViewById(R.id.theme_item);
        followImg=(ImageView)findViewById(R.id.follow);
    }


    public void setSelectListener(final SelectListener listener){
        selectListener=listener;

        themeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.LOGD(TAG, "theme: " + title + " is on select");
                if (listener != null) {
                    selectListener.onThemeSelect(getThemeId());
                }
                //selectTheme();  TODO do something that change the state of navthemeitemview

            }
        });

    }


    public void setFollowListener(final FollowListener listener){
        followListener=listener;
        followImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.LOGD(TAG, "theme: " + title + " is on follow");
                if (followed != true) {
                    followed = true;
                    if (listener != null) {
                        followListener.onThemeFollow(themeId);
                    }
                    //followTheme(); TODO do something that change the state of navthemeitemview
                }
            }
        });
    }








    public void selectTheme() {
        themeItem.setBackgroundColor(Color.parseColor("#e0e0e0"));
    }

    public void unSelectTheme(){
        themeItem.setBackgroundColor(Color.parseColor("#f5f5f5"));
    }

    public void followTheme(){
        followImg.setImageResource(R.drawable.menu_arrow);
        followed=true;
    }

    public void unFollowTheme() {
        followImg.setImageResource(R.drawable.menu_follow);
        followed=false;
    }


    public void setTitle(String title){
        this.title=title;
        ((TextView)findViewById(R.id.theme_title)).setText(title);
    }

    public String getTitle(){
        return title;
    }


    public void setThemeId(int id){
        themeId=id;
    }

    public int getThemeId(){
        return themeId;
    }

    public void setFollowed(boolean followed){
        this.followed=followed;
        if(followed){
            followImg.setImageResource(R.drawable.menu_arrow);
        }else{
            followImg.setImageResource(R.drawable.menu_follow);
        }
    }

    public boolean isFollowed(){
        return followed;
    }


    public interface SelectListener{
        void onThemeSelect(int themeId);
    }

    public interface FollowListener{
        void onThemeFollow(int themeId);
    }



}
