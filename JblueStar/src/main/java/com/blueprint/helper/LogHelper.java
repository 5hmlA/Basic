package com.blueprint.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Log;

import com.blueprint.Consistent;
import com.orhanobut.logger.Logger;

import static com.blueprint.LibApp.isInDebug;

/**
 * Created by 4399-1500 on 2017/6/12.
 */

public class LogHelper {

    private static final String TOP_BORDER = "╔═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER = "╚═══════════════════════════════════════════════════════════════════════════════════════════════════";

    public static String bale(Object... msg){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object s : msg) {
            stringBuilder.append(s).append(Consistent.SPLIT_DOS);
        }
        return stringBuilder.subSequence(0, stringBuilder.length()-Consistent.SPLIT_DOS.length()).toString();
    }

    public static String concat(Object... msg){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object s : msg) {
            stringBuilder.append(s);
        }
        return stringBuilder.subSequence(0, stringBuilder.length()).toString();
    }

    public static void Log_d(@NonNull @Size(min = 1) Object... msg){
        Logger.d(concat(msg));
    }

    public static void Log_e(Throwable throwable){
        if(throwable != null) {
            Logger.e(Log.getStackTraceString(throwable));
        }
    }

    public static void Log_e(@NonNull @Size(min = 1) Object... msg){
        Logger.e(concat(msg));
    }

    public static void Log_w(@NonNull @Size(min = 1) Object... msg){
        Logger.w(bale(msg));
    }

    public static void Log_json(@NonNull @Size(min = 1) String msg){
        Logger.json(msg);
    }

    public static void Log_w2f(@NonNull @Size(min = 1) Object... msg){
        Logger.wtf(concat(msg));
    }

    public static void slog_d(String tag, @NonNull @Size(min = 1) Object... msg){
        if(isInDebug()) {
            Log.d(tag, TOP_BORDER);
            Log.d(tag, LEFT_BORDER+concat(msg));
            Log.d(tag, BOTTOM_BORDER);
        }
    }

    public static void slog_e(String tag, @NonNull @Size(min = 1) Object... msg){
        if(isInDebug()) {
            Log.e(tag, TOP_BORDER);
            Log.e(tag, LEFT_BORDER+concat(msg));
            Log.e(tag, BOTTOM_BORDER);
        }
    }
}
