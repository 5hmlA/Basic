package com.blueprint.helper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.blueprint.LibApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.blueprint.helper.CheckHelper.checkLists;

/**
 * PackageHelper
 * <ul>
 * <strong>Install package</strong>
 * <li>{@link PackageHelper#installNormal(Context, String)}</li>
 * </ul>
 * <ul>
 * <strong>Uninstall package</strong>
 * <li>{@link PackageHelper#uninstallNormal(Context, String)}</li>
 * <li>{@link PackageHelper#uninstallSilent(Context, String)}</li>
 * </ul>
 * <ul>
 * <strong>Is system application</strong>
 * <li>{@link PackageHelper#isSystemApplication(Context)}</li>
 * <li>{@link PackageHelper#isSystemApplication(Context, String)}</li>
 * <li>{@link PackageHelper#isSystemApplication(PackageManager, String)}</li>
 * </ul>
 * <ul>
 * <strong>Others</strong>
 * <li>{@link PackageHelper#isTopActivity(Context, String)} whether the app whost package's name is packageName is on the
 * top of the stack</li>
 * <li>{@link PackageHelper#startInstalledAppDetails(Context, String)} start InstalledAppDetails Activity</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-15
 */
public class PackageHelper {

    public static final String TAG = "PackageHelper";

    private PackageHelper(){
        throw new AssertionError();
    }


    /**
     * 优先使用root模式
     *
     * @param apkPath
     */
    public static void install(String apkPath){
        install(apkPath, ShellHelper.checkRootPermission());
    }

    /**
     * @param apkPath
     *         要安装的APK
     * @param rootMode
     *         是否是Root模式
     */
    public static void install(String apkPath, boolean rootMode){
        if(rootMode) {
            installRoot(apkPath);
        }else {
            installNormal(apkPath);
        }
    }

    //普通安装
    public static void installNormal(String apkPath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            File file = ( new File(apkPath) );
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider
                    .getUriForFile(LibApp.getContext(), LibApp.getContext().getPackageName()+".fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        }
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LibApp.getContext().startActivity(intent);
    }

    //通过Root方式安装
    private static void installRoot(String apkPath){
        File file = new File(apkPath);
        if(apkPath == null || apkPath.length() == 0 || ( file = new File(apkPath) ) == null || file.length()<=0 || !file
                .exists() || !file.isFile()) {
            LogHelper.Log_e("安装失败");
        }
        String[] args = {"pm", "install", "-r", apkPath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while(( s = successResult.readLine() ) != null) {
                successMsg.append(s);
            }
            while(( s = errorResult.readLine() ) != null) {
                errorMsg.append(s);
            }
        }catch(IOException e) {
            e.printStackTrace();
            result = 2;
        }catch(Exception e) {
            e.printStackTrace();
            result = 2;
        }finally {
            try {
                if(successResult != null) {
                    successResult.close();
                }
                if(errorResult != null) {
                    errorResult.close();
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
            if(process != null) {
                process.destroy();
            }
        }    // TODO should add memory is not enough here
        if(successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        }else {
            result = 2;
        }
        Log.d("installSlient", "successMsg:"+successMsg+", ErrorMsg:"+errorMsg);
    }


    public static void installSilent(Context context, String apkPath, String pmParams){
    }

    /**
     * uninstall package normal by system intent
     *
     * @param context
     * @param packageName
     *         package name of app
     * @return whether package name is empty
     */
    public static boolean uninstallNormal(Context context, String packageName){
        if(packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE,
                Uri.parse(new StringBuilder(32).append("package:").append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }


    public static void uninstallSilent(Context context, String packageName){
    }


    public static void uninstallSilent(Context context, String packageName, boolean isKeepData){
    }

    /**
     * whether context is system application
     *
     * @param context
     * @return
     */
    public static boolean isSystemApplication(Context context){
        if(context == null) {
            return false;
        }

        return isSystemApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system application
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemApplication(Context context, String packageName){
        if(context == null) {
            return false;
        }

        return isSystemApplication(context.getPackageManager(), packageName);
    }

    /**
     * whether packageName is system application
     *
     * @param packageManager
     * @param packageName
     * @return <ul>
     * <li>if packageManager is null, return false</li>
     * <li>if package name is null or is empty, return false</li>
     * <li>if package name not exit, return false</li>
     * <li>if package name exit, but not system app, return false</li>
     * <li>else return true</li>
     * </ul>
     */
    public static boolean isSystemApplication(PackageManager packageManager, String packageName){
        if(packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return ( app != null && ( app.flags&ApplicationInfo.FLAG_SYSTEM )>0 );
        }catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * whether the app whost package's name is packageName is on the top of the stack
     * <ul>
     * <strong>Attentions:</strong>
     * <li>You should add <strong>android.permission.GET_TASKS</strong> in manifest</li>
     * </ul>
     *
     * @param context
     * @param packageName
     * @return if params error or task stack is null, return null, otherwise retun whether the app is on the top of
     * stack
     */
    public static Boolean isTopActivity(Context context, String packageName){
        if(context == null || TextUtils.isEmpty(packageName)) {
            return null;
        }

        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if(!checkLists(tasksInfo)) {
            return null;
        }
        try {
            return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get app version code
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context){
        if(context != null) {
            PackageManager pm = context.getPackageManager();
            if(pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if(pi != null) {
                        return pi.versionCode;
                    }
                }catch(NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * start InstalledAppDetails Activity
     *
     * @param context
     * @param packageName
     */
    public static void startInstalledAppDetails(Context context, String packageName){
        Intent intent = new Intent();
        int sdkVersion = Build.VERSION.SDK_INT;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));
        }else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(
                    ( sdkVersion == Build.VERSION_CODES.FROYO ? "pkg" : "com.android.settings.ApplicationPkgName" ),
                    packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
