package com.blueprint.helper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.List;

import static com.blueprint.LibApp.getContext;

/**
 * Created by zhanghongyang01 on 17/5/31.
 */

public class FileProvider7 {
    public static Uri getUriForFile(File file){
        Uri fileUri = null;
        if(Build.VERSION.SDK_INT>=24) {
            fileUri = getUriForFile24(file);
        }else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(File file){
        Uri fileUri = android.support.v4.content.FileProvider
                .getUriForFile(getContext(), getContext().getPackageName()+".fileprovider", file);
        return fileUri;
    }

    public static void grantPermissions(Intent intent, Uri uri, boolean writeAble){

        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if(writeAble) {
            flag |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        intent.addFlags(flag);
        List<ResolveInfo> resInfoList = getContext().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getContext().grantUriPermission(packageName, uri, flag);
        }
    }


    public static void setIntentData(Intent intent, File file, boolean writeAble){
        if(Build.VERSION.SDK_INT>=24) {
            intent.setData(getUriForFile(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }else {
            intent.setData(Uri.fromFile(file));
        }
    }

//    public static void setIntentDataAndType(Intent intent, String type, File file){
//        setIntentDataAndType(intent, type, file);
//    }

    public static void setIntentDataAndType(Intent intent, String type, File file, boolean writeAble){
        if(Build.VERSION.SDK_INT>=24) {
            intent.setDataAndType(getUriForFile(file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
