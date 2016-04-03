package com.yangyongwen.zhihudailypaper.homePage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;

import java.util.ArrayList;

/**
 * Created by yangyongwen on 16/2/29.
 */
public class TopStoryAdapter extends FragmentStatePagerAdapter{

    private ArrayList<TopStory> mTopStories;

    public final static String TITLE="topstory_title";
    public final static String IMAGE="topstory_image";


    public TopStoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new TopStoryFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
//        args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
        args.putString(TITLE, mTopStories.get(i).getTitle());
        args.putString(IMAGE,mTopStories.get(i).getImage());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTopStories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }


    public void setTopStories(ArrayList<TopStory> mTopStories){
        this.mTopStories=mTopStories;
    }


}
