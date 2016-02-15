package com.yangyongwen.zhihudailypaper.Utils;

import android.util.Log;

/**
 * Created by samsung on 2016/2/2.
 */
public class LogUtils {

    private static String LOG_PREFIX="Zhihu_";
    private static int LOG_PREFIX_LENGTH=LOG_PREFIX.length();
    private static int MAX_LOG_TAG_LENGTH=30;
    public static boolean LOGGING_ENABLED=true;

    public static String makeLogTag(String str){
        if(str.length()>MAX_LOG_TAG_LENGTH-LOG_PREFIX_LENGTH){
            return LOG_PREFIX+str.substring(0,MAX_LOG_TAG_LENGTH-LOG_PREFIX_LENGTH-1);
        }
        return LOG_PREFIX+str;
    }

    public static String makeLogTag(Class cls){
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGV(final String tag,String msg){
        if(LOGGING_ENABLED){
            Log.v(tag, msg);
        }
    }

    public static void LOGV(final String tag,String msg,Throwable cause){
        if(LOGGING_ENABLED){
            Log.v(tag,msg,cause);

        }
    }

    public static void LOGI(final String tag,String msg){
        if(LOGGING_ENABLED){
            Log.i(tag, msg);
        }
    }

    public static void LOGI(final String tag,String msg,Throwable cause){
        if(LOGGING_ENABLED){
            Log.i(tag, msg, cause);
        }
    }
    public static void LOGW(final String tag,String msg){
        if(LOGGING_ENABLED){
            Log.w(tag, msg);
        }
    }

    public static void LOGW(final String tag,String msg,Throwable cause){
        if(LOGGING_ENABLED){
            Log.w(tag, msg, cause);
        }
    }
    public static void LOGE(final String tag,String msg){
        if(LOGGING_ENABLED){
            Log.e(tag, msg);
        }
    }

    public static void LOGE(final String tag,String msg,Throwable cause){
        if(LOGGING_ENABLED){
            Log.e(tag, msg, cause);
        }
    }

    public static void LOGD(final String tag,String msg){
        if(LOGGING_ENABLED){
            Log.d(tag,msg);
        }
    }

    public static void LOGD(final String tag,String msg,Throwable cause){
        if(LOGGING_ENABLED){
            Log.d(tag,msg,cause);
        }
    }

}
