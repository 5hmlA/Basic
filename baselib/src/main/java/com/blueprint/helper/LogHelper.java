package com.blueprint.helper;

import android.util.Log;

import com.orhanobut.logger.Logger;

import static com.blueprint.LibApp.isInDebug;

/**
 * Created by 4399-1500 on 2017/6/12.
 */

public class LogHelper {

    public static void Log_d(String msg){
        Logger.d(msg);
    }

    public static void Log_e(String msg){
        Logger.e(msg);
    }

    public static void Log_w(String msg){
        Logger.w(msg);
    }

    public static void Log_json(String msg){
        Logger.json(msg);
    }

    public static void Log_w2f(String msg){
        Logger.wtf(msg);
    }

    public static void slog_d(String tag, String msg){
        if(isInDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void slog_e(String tag, String msg){
        if(isInDebug()) {
            Log.e(tag, msg);
        }
    }
}
