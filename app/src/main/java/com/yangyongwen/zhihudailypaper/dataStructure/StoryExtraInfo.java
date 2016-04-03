package com.yangyongwen.zhihudailypaper.dataStructure;

/**
 * Created by samsung on 2016/3/7.
 */
public class StoryExtraInfo {
    private int long_comments;
    private int popularity;
    private int short_comments;
    private int comments;
    private int post_reasons;
    private int normal_comments;
    private String id;

    public void setLong_comments(int long_comments){
        this.long_comments=long_comments;
    }
    public void setPopularity(int popularity){
        this.popularity=popularity;
    }
    public void setShort_comments(int short_comments){
        this.short_comments=short_comments;
    }
    public void setComments(int comments){
        this.comments=comments;
    }
    public void setPost_reasons(int post_reasons){
        this.post_reasons=post_reasons;
    }
    public void setNormal_comments(int normal_comments){
        this.normal_comments=normal_comments;
    }
    public void setId(String id){
        this.id=id;
    }

    public int getLong_comments(){
        return long_comments;
    }
    public int getPopularity(){
        return popularity;
    }
    public int getShort_comments(){
        return short_comments;
    }
    public int getComments(){
        return comments;
    }
    public int getPost_reasons(){
        return post_reasons;
    }
    public int getNormal_comments(){
        return normal_comments;
    }
    public String getId(){
        return id;
    }


}
