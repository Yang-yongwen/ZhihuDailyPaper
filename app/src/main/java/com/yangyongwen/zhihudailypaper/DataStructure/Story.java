package com.yangyongwen.zhihudailypaper.dataStructure;

/**
 * Created by samsung on 2016/2/3.
 */
public class Story {
    private String title;                                   //标题
    private String[] images;                                //图片的URL，为数组类型，但是目前基本只有一张图片
    private boolean multipic;                               //多图预警，如果是true则在图片缩略图中加入一个多图的标志
    private String ga_prefix;                               //用于Google Analysis，不懂。。。
    private int type;                                       //用处不明
    private int id;
    private boolean readed;
    private int themeId;
    private String themeName;
    private String date;

    public String getTitle(){
        return title;
    }
    public String[] getImages(){
        return images;
    }
    public boolean isMultipic(){
        return multipic;
    }
    public String getGaPrefix(){
        return ga_prefix;
    }
    public int getType(){
        return type;
    }
    public int getId() {
        return id;
    }
    public int getThemeId(){
        return themeId;
    }
    public String getThemeName(){
        return themeName;
    }
    public boolean isReaded(){
        return readed;
    }
    public String getDate(){
        return date;
    }

    public void setTitle(String title){
        this.title=title;
    }
    public void setImages(String[] images){
        this.images=images;
    }
    public void setMultipic(boolean multipic){
        this.multipic=multipic;
    }
    public void setGa_prefix(String ga_prefix){
        this.ga_prefix=ga_prefix;
    }
    public void setType(int type){
        this.type=type;
    }
    public void setId(int id){
        this.id=id;
    }
    public void setReaded(boolean readed){
        this.readed=readed;
    }
    public void setThemeId(int themeId){
        this.themeId=themeId;
    }
    public void setThemeName(String themeName){
        this.themeName=themeName;
    }
    public void setDate(String date){
        this.date=date;
    }

}
