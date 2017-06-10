package com.baselib;

import com.baselib.helper.SpHelper;
import com.baselib.rx.RxUtill;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

public class SettingCenter {

    public static boolean getOnlyWifiLoadImage(){
        return (boolean)SpHelper.get(LibApp.getContext(), "getOnlyWifiLoadImage", false);
    }

    public static void setOnlyWifiLoadImage(boolean isEnable){
        SpHelper.get(LibApp.getContext(), "getOnlyWifiLoadImage", isEnable);
    }

    //region 缓存相关

    /**
     * 计算缓存大小
     *
     */
    public static Single<Long> dirSizeObserver(){
        return Single.create(new SingleOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Long> e) throws Exception{
                e.onSuccess(getDirSize(LibApp.getContext().getCacheDir()));
            }
        }).compose(RxUtill.<Long>defaultSchedulers_single());
    }

    /**
     * 清除缓存
     */
    public static Single<Boolean> clearAppCache(){
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> e) throws Exception{
                clearFile(LibApp.getContext().getCacheDir());
                e.onSuccess(true);
            }
        }).compose(RxUtill.<Boolean>defaultSchedulers_single());

    }

    private static long getDirSize(File dir){
        if(dir == null) {
            return 0;
        }
        if(!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for(File file : files) {
            if(file.isFile()) {
                dirSize += file.length();
            }else if(file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    private static void clearFile(File file){
        if(file == null || !file.exists()) {
            return;
        }
        if(file.isDirectory()) {
            for(File child : file.listFiles()) {
                clearFile(child);
            }
        }else {
            file.delete();
        }
    }

    public static String formatFileSize(long fileS){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if(fileS<1024) {
            fileSizeString = df.format((double)fileS)+"B";
        }else if(fileS<1048576) {
            fileSizeString = df.format((double)fileS/1024)+"KB";
        }else if(fileS<1073741824) {
            fileSizeString = df.format((double)fileS/1048576)+"MB";
        }else {
            fileSizeString = df.format((double)fileS/1073741824)+"G";
        }

        if(fileSizeString.startsWith(".")) {
            return "0B";
        }
        return fileSizeString;
    }

}
