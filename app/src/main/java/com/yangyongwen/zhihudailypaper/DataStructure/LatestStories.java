package com.yangyongwen.zhihudailypaper.DataStructure;

import java.security.PrivateKey;

/**
 * Created by samsung on 2016/2/3.
 */
public class LatestStories {
    private String date;
    private Story[] stories;
    private TopStory[] top_stories;

    public String getDate(){
        return date;
    }
    public Story[] getStories(){
        return stories;
    }
    public TopStory[] getTopStories(){
        return top_stories;
    }

}
