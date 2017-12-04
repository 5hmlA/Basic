package com.blueprint.crash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.blueprint.LibApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class CrashLog {
    private static final String TAG = "CrashLog";

    /**
     * 保存异常信息到sdcard中
     *
     * @param pContext
     * @param ex
     *         异常信息对象
     */
    public static void saveCrashToSdcard(Context pContext, Throwable ex){
        String fileName = null;
        StringBuffer sBuffer = new StringBuffer();
        // 添加异常信息
        sBuffer.append(getExceptionInfo(ex));
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalCacheDir = pContext.getExternalCacheDir();
            if(externalCacheDir == null) {
                externalCacheDir = pContext.getCacheDir();
            }
            String logDir = externalCacheDir.getAbsolutePath()+File.separator+"Crashs";

            File file1 = new File(logDir);
            if(!file1.exists()) {
                file1.mkdir();
            }
            fileName = file1.toString()+File.separator+paserTime(System.currentTimeMillis())+".log";
            File file2 = new File(fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file2);
                fos.write(sBuffer.toString().getBytes());
                fos.flush();
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                if(fos != null) {
                    try {
                        fos.close();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取并且转化异常信息
     * 同时可以进行投递相关的设备，用户信息
     *
     * @param ex
     * @return 异常信息的字符串形式
     */
    public static String getExceptionInfo(Throwable ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("---------Crash Log Begin---------\n");
        stringBuffer.append(getDeviceDetails()+"\n");
        stringBuffer.append(sw.toString()+"\n");
        stringBuffer.append("---------Crash Log End---------\n");
        return stringBuffer.toString();
    }

    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param milliseconds
     * @return
     */
    public static String paserTime(long milliseconds){
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String times = format.format(new Date(milliseconds));

        return times;
    }

    private static String getCurrentLauncherApp() {
        String str = "";
        PackageManager localPackageManager = LibApp.getContext().getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        try {
            ResolveInfo resolveInfo = localPackageManager.resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                str = resolveInfo.activityInfo.packageName;
            }
        } catch (Exception e) {
            Log.e("AppUtils", "Exception : " + e.getMessage());
        }
        return str;
    }

    public static String getDeviceDetails() {

        return "Device Information\n"
                + "\n手机型号 : " + Build.BRAND
                + "\n设备ID : " + getDeviceId()
                + "\n应用版本号 : " + getAppVersion()
                + "\n设备厂商 : " + Build.MANUFACTURER
                + "\nLAUNCHER.APP : " + getCurrentLauncherApp()
                + "\nVERSION.RELEASE : " + Build.VERSION.RELEASE
                + "\nVERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                + "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
                + "\nBOARD : " + Build.BOARD
                + "\nBOOTLOADER : " + Build.BOOTLOADER
                + "\nCPU_ABI : " + Build.CPU_ABI
                + "\nCPU_ABI2 : " + Build.CPU_ABI2
                + "\nDISPLAY : " + Build.DISPLAY
                + "\nFINGERPRINT : " + Build.FINGERPRINT
                + "\nHARDWARE : " + Build.HARDWARE
                + "\nHOST : " + Build.HOST
                + "\nID : " + Build.ID
                + "\nMODEL : " + Build.MODEL
                + "\nPRODUCT : " + Build.PRODUCT
                + "\nSERIAL : " + Build.SERIAL
                + "\nTAGS : " + Build.TAGS
                + "\nTIME : " + Build.TIME
                + "\nTYPE : " + Build.TYPE
                + "\nUNKNOWN : " + Build.UNKNOWN
                + "\nUSER : " + Build.USER;
    }

    private static String getDeviceId() {
        String androidDeviceId = getAndroidDeviceId();
        if (androidDeviceId == null)
            androidDeviceId = UUID.randomUUID().toString();
        return androidDeviceId;

    }

    private static String getAndroidDeviceId() {
        final String INVALID_ANDROID_ID = "9774d56d682e549c";
        final String androidId = android.provider.Settings.Secure.getString(
                LibApp.getContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        if (androidId == null
                || androidId.toLowerCase().equals(INVALID_ANDROID_ID)) {
            return null;
        }
        return androidId;
    }

    private static String getAppVersion() {
        try {
            PackageInfo packageInfo = LibApp.getContext().getPackageManager()
                    .getPackageInfo(LibApp.getContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
