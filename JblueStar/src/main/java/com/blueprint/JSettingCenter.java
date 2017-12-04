package com.blueprint;

import com.blueprint.helper.SpHelper;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;

import static com.blueprint.helper.FileHelper.clearFile;
import static com.blueprint.helper.FileHelper.getDirSize;
import static com.blueprint.helper.PhoneHelper.strongClearCache;

public class JSettingCenter {

    public static final Set<String> sMsaveDataUrls = new HashSet<>();

    public static void needSaveDataUrl(String url){
        sMsaveDataUrls.add(url);
    }

    public static boolean isNeedSaveData(String url){
        return sMsaveDataUrls.contains(url);
    }

    public static boolean getOnlyWifiLoadImage(){
        return (boolean)SpHelper.sget("getOnlyWifiLoadImage", false);
    }

    public static void setOnlyWifiLoadImage(boolean isEnable){
        SpHelper.sput("getOnlyWifiLoadImage", isEnable);
    }

    /**
     * 计算缓存大小
     */
    public static Single<Long> cacheSizeObserver(){
        return getDirSize(LibApp.getContext().getCacheDir(), LibApp.getContext().getExternalCacheDir());
    }

    /**
     * 清除缓存
     */
    public static Single<Boolean> clearAppCache(){
        return clearFile(LibApp.getContext().getCacheDir(), LibApp.getContext().getExternalCacheDir());
    }

//    /**
//     * 计算缓存大小
//     */
//    public static Observable<Long> strongCacheSizeObserver(){
//        //        return getDirSize(LibApp.getContext().getCacheDir(), LibApp.getContext().getExternalCacheDir());
//        return null;
//    }

    /**
     * 强力清除app缓存缓存
     * <p>类似app详情页的清除缓存，清除后会被清除进程app被强制关闭</p>
     */
    public static void strongClearAppCache(){
        strongClearCache(LibApp.getPackageName());
    }
}
