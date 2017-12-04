package com.blueprint.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.blueprint.LibApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import io.reactivex.annotations.NonNull;

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
        //        install(apkPath, checkRootPermission());
        installNormal(apkPath);
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
    private static void installRoot(@NonNull String apkPath){
        File file = null;
        if(TextUtils.isEmpty(apkPath) || ( file = new File(apkPath) ) == null || file.length()<=0 || !file
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
            return false;
        }

        ActivityManager activityManager = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if(!checkLists(tasksInfo)) {
            return false;
        }
        try {
            return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ComponentName getTopActivity(){

        ActivityManager activityManager = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT>20) {
            return activityManager.getRunningAppProcesses().get(0).importanceReasonComponent;
        }else {
            return activityManager.getRunningTasks(1).get(0).topActivity;
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
     * 获取App图标
     */
    public static Drawable getAppIcon(){
        return getAppIcon(getContext().getPackageName());
    }

    /**
     * 获取App图标
     *
     * @param packageName
     *         包名
     * @return App图标
     */
    public static Drawable getAppIcon(final String packageName){
        if(TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageManager pm = LibApp.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 跳转到应用管理中的应用详情界面
     */
    public static void toInstalledAppDetails(){
        toInstalledAppDetails(getContext().getPackageName());
    }

    /**
     * 跳转到应用管理中的应用详情界面
     *
     * @param packageName
     */
    public static void toInstalledAppDetails(String packageName){
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

    public static boolean isProcessRunning(String processSuffix){
        ActivityManager manager = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infoList = manager.getRunningAppProcesses();
        for(RunningAppProcessInfo info : infoList) {
            Log.e("~~~processName", info.processName);
            if(info.processName.endsWith(processSuffix)) {
                Log.e(processSuffix+"~~~Running", "true");
                return true;
            }else {
                Log.e(processSuffix+"~~~Running", "false");
            }
        }
        return false;
    }

    /**
     * 判断App是否是Debug版本
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(){
        return isAppDebug(getContext().getPackageName());
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param packageName
     *         包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(final String packageName){
        if(TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            PackageManager pm = getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && ( ai.flags&ApplicationInfo.FLAG_DEBUGGABLE ) != 0;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取App签名
     *
     * @return App签名
     */
    public static Signature[] getAppSignature(){
        return getAppSignature(getContext().getPackageName());
    }

    /**
     * 获取App签名
     *
     * @param packageName
     *         包名
     * @return App签名
     */
    public static Signature[] getAppSignature(final String packageName){
        if(TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageManager pm = getContext().getPackageManager();
            @SuppressLint("PackageManagerGetSignatures") PackageInfo pi = pm
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取应用签名的的SHA1值
     * <p>可据此判断高德，百度地图key是否正确</p>
     *
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    public static String getAppSignatureSHA1(){
        return getAppSignatureSHA1(getContext().getPackageName());
    }

    /**
     * 获取应用签名的的SHA1值
     * <p>可据此判断高德，百度地图key是否正确</p>
     *
     * @param packageName
     *         包名
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    public static String getAppSignatureSHA1(final String packageName){
        Signature[] signature = getAppSignature(packageName);
        if(signature == null) {
            return null;
        }
        return EncryptUtils.encryptSHA1ToString(signature[0].toByteArray()).
                replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
    }

    /**
     * 判断App是否处于前台
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(){
        ActivityManager manager = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> info = manager.getRunningAppProcesses();
        if(info == null || info.size() == 0) {
            return false;
        }
        for(RunningAppProcessInfo aInfo : info) {
            if(aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName.equals(getContext().getPackageName());
            }
        }
        return false;
    }

    /**
     * 判断App是否处于前台
     * <p>当不是查看当前App，且SDK大于21时，
     * 需添加权限 {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>}</p>
     *
     * @param packageName
     *         包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(final String packageName){
        return false;
    }


    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //todo   动态还appicon  http://blog.csdn.net/eclipsexys/article/details/53791818
    public static void enableComponent(ComponentName componentName) {
        PackageManager packageManager = LibApp.getContext().getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableComponent(ComponentName componentName) {
        PackageManager packageManager = LibApp.getContext().getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
