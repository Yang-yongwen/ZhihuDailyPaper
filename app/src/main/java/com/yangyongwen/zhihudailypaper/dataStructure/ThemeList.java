package com.yangyongwen.zhihudailypaper.dataStructure;

/**
 * Created by yangyongwen on 16/2/21.
 */
public class ThemeList {


    private int limit;
    private ThemeItem[] subscribed;
    private ThemeItem[] others;

    public class ThemeItem{
        private int color;
        private String thumbnail;
        private String description;
        private String name;
        private int id;
        private boolean isSubcribed;
        public int getColor(){
            return color;
        }
        public String getThumbnail(){
            return thumbnail;
        }
        public String getDescription(){
            return description;
        }
        public String getName(){
            return name;
        }
        public int getId(){
            return id;
        }
        public boolean isSubcribed(){
            return isSubcribed;
        }



    }

    public ThemeItem[] getSubscribed(){
        return subscribed;
    }
    public ThemeItem[] getOthers(){
        return others;
    }


}
