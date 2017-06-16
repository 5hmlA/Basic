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

import com.blueprint.LibApp;

import java.io.File;
import java.util.List;

import static com.blueprint.LibApp.getContext;
import static com.blueprint.helper.CheckHelper.checkLists;
import static com.blueprint.helper.ShellHelper.checkRootPermission;
import static com.blueprint.helper.ShellHelper.execCommand;

/**
 * PackageHelper
 * <ul>
 * <strong>Install package</strong>
 * </ul>
 * <ul>
 * <strong>Uninstall package</strong>
 * </ul>
 * <ul>
 * <strong>Is system application</strong>
 * <li>{@link PackageHelper#isSystemApplication(PackageManager, String)}</li>
 * </ul>
 * <ul>
 * <strong>Others</strong>
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
        install(apkPath, checkRootPermission());
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
        installNormal(new File(apkPath));
    }

    //普通安装
    public static void installNormal(File file){
        if(file != null && file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //版本在7.0以上是不能直接通过uri访问的
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(getContext(), LibApp.getPackageName()+".fileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            getContext().startActivity(intent);
        }
    }

    //通过Root方式安装
    private static void installRoot(String apkPath){
        File file = new File(apkPath);
        if(apkPath == null || apkPath.length() == 0 || ( file = new File(apkPath) ) == null || file.length()<=0 || !file
                .exists() || !file.isFile()) {
            LogHelper.Log_e("安装失败");
        }
        String[] args = {"pm", "install", "-r", apkPath};
        execCommand(args, checkRootPermission());
    }


    public static void installSilent(String apkPath, String pmParams){
    }

    /**
     * uninstall package normal by system intent
     *
     * @param packageName
     *         package name of app
     * @return whether package name is empty
     */
    public static boolean uninstallNormal(String packageName){
        if(packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE,
                Uri.parse(new StringBuilder(32).append("package:").append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LibApp.getContext().startActivity(i);
        return true;
    }


    public static void uninstallSilent(String packageName){
    }


    public static void uninstallSilent(String packageName, boolean isKeepData){
    }

    /**
     * whether context is system application
     *
     * @return
     */
    public static boolean isSystemApplication(){
        return isSystemApplication(getContext().getPackageName());
    }

    /**
     * whether packageName is system application
     *
     * @param packageName
     * @return
     */
    public static boolean isSystemApplication(String packageName){
        return isSystemApplication(getContext().getPackageManager(), packageName);
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
     * @param packageName
     * @return if params error or task stack is null, return null, otherwise retun whether the app is on the top of
     * stack
     */
    public static Boolean isTopActivity(String packageName){
        if(TextUtils.isEmpty(packageName)) {
            return null;
        }

        ActivityManager activityManager = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
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
     * @return
     */
    public static int getAppVersionCode(){
        PackageManager pm = getContext().getPackageManager();
        if(pm != null) {
            PackageInfo pi;
            try {
                pi = pm.getPackageInfo(getContext().getPackageName(), 0);
                if(pi != null) {
                    return pi.versionCode;
                }
            }catch(NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * start InstalledAppDetails Activity
     *
     * @param packageName
     */
    public static void startInstalledAppDetails(String packageName){
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
        getContext().startActivity(intent);
    }
}
