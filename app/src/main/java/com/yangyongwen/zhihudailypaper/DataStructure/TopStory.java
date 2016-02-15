package com.yangyongwen.zhihudailypaper.DataStructure;

/**
 * Created by samsung on 2016/2/3.
 */
public class TopStory {
    private String title;                                   //标题
    private String image;                            //图片的URL，为数组类型，但是目前基本只有一张图片
    private boolean multipic;                               //多图预警，如果是true则在图片缩略图中加入一个多图的标志
    private String ga_prefix;                               //用于Google Analysis，不懂。。。
    private int type;                                       //用处不明
    private int id;

    public String getTitle(){
        return title;
    }
    public String getImage(){
        return image;
    }
    public String getGaPrefix(){
        return ga_prefix;
    }
    public boolean isMultipic(){
        return multipic;
    }
    public int getType(){
        return type;
    }
    public int getId(){
        return id;
    }

}
