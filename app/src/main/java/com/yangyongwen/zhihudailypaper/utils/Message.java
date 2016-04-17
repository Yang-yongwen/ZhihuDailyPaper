package com.yangyongwen.zhihudailypaper.utils;

/**
 * Created by samsung on 2016/4/14.
 */
public class Message {

    public static class StartCommActMsg{
        public final String message;
        public StartCommActMsg(String message) {
            this.message = message;
        }
    }

    public static class LoadShortComment{
        public final String message;
        public LoadShortComment(String message) {
            this.message = message;
        }
    }


}
