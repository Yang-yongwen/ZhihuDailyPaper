package com.yangyongwen.zhihudailypaper.homePage;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/2/26.
 */
public class HomepageRecyclerView extends RecyclerView{

    private final static String TAG= LogUtils.makeLogTag(HomepageRecyclerView.class);


    private Context mContext;
    private HomePageAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public HomepageRecyclerView(Context context){
        this(context, null);
    }

    public HomepageRecyclerView(Context context,AttributeSet attrs){
        super(context,attrs);
        mAdapter=new HomePageAdapter(mContext,mFragmentManager);
        mContext=context;
    }

    public void initHomePageView(){
        if(mAdapter==null){
            mAdapter=new HomePageAdapter(mContext,mFragmentManager);
            this.setAdapter(mAdapter);
        }else{
            mAdapter.clearAll();
        }
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        mSwipeRefreshLayout=swipeRefreshLayout;
        mAdapter.setSwipeRefreshLayout(mSwipeRefreshLayout);
    }

    public void setFragmentManager(FragmentManager fragmentManager){
        mFragmentManager=fragmentManager;
    }

    public void addStory(Story story){

        mAdapter.notifyDataSetChanged();
    }

    public void addDailyStories(ArrayList<Story> stories){

        mAdapter.notifyDataSetChanged();
    }

    public void setTopStories(ArrayList<TopStory> topStories){
        mAdapter.setTopStories(topStories);
        mAdapter.notifyDataSetChanged();
    }





}
