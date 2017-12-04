package com.blueprint.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import com.blueprint.LibApp;
import com.blueprint.rx.RxUtill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.blueprint.Consistent.DEFAULTERROR;
import static com.blueprint.Consistent.DEFAULTSTR;

/**
 * 在adb shell模式下输入 getprop 就能获取系统属性值
 */
public class PhoneHelper {
    private static final String TAG = "PhoneHelper";
    public static final int SCANING_APP_START = 111;
    public static final int SCANING_APP_NAME = 110;
    public static final int SCANING_APP_FINISH = 119;

    /**
     * 通过android.os.Build 类，可以直接获得一些 Build 提供的系统信息。Build类包含了系统编译时，--编译时哦--的大量设备、配置信息，下面 列举一些常用的信息：
     * Build.BOARD : 主板 :BalongV9R1
     * Build.BRAND : 系统定制商： Huawei
     * Build.DEVICE : 设备参数：hw7d501l
     * Build.DISPLAY : 显示屏参数：7D-503LV100R002C598B003
     * Build.FINGERPRINT : 唯一编号：Huawei/MediaPad/hw7d501l:4.4.2/HuaweiMediaPad/7D503LV1R2C598B003:user/release-keys
     * Build.SERIAL : 硬件序列号：T7K6R14727000194
     * Build.ID : 修订版本列表：HuaweiMediaPad
     * Build.MANUFACTURER : 硬件制造商：HUAWEI
     * Build.MODEL : 版本 ：X1 7.0
     * Build.HARDWARE : 硬件名 ：hw7d501l
     * Build.PRODUCT : 手机产品名 ：MediaPad
     * Build.TAGS : 描述Build的标签 ：release-keys
     * Build.TYPE : Builder 类型 ：user
     * Build.CODENAME : 当前开发版本号 ：REL
     * Build.INCREMENTAL : 源码控制版本号 ：C598B003
     * Build.RELEASE : 版本字符串 ：4.4.2
     * Build.SDK_INT : 版本号 ：19
     * Build.HOST : Host值 ：screen2
     * Build.USER : User名 ：jslave
     * Build.TIME : 编译时间 ：1419969480000
     * SystemProperty 包含了许多系统配置属性值和参数，很多信息与上面通过android.os.Build 获取的值是相同的，下面同样列举了一些常用的信息：
     * System.getProperty("os.version") : OS版本 ：3.0.8-g0f59686
     * System.getProperty("os.name") : OS名称 ：Linux
     * System.getProperty("os.arch") : OS架构 ：armv7l
     * System.getProperty("user.home") : Home属性 ：/
     * System.getProperty("user.name") : Name属性 ：root
     * System.getProperty("user.dir") : Dir属性 ：/
     * System.getProperty("user.timezone") : 时区 ：null
     * System.getProperty("path.separator") : 路径分隔符 ：:
     * System.getProperty("line.separator") : 行分隔符 ：
     * System.getProperty("file.separator") : 文件分隔符 ：/
     * System.getProperty("java.vendor.url") : Java vender URL 属性：http://www.android.com/
     * System.getProperty("java.class.path") : Java Class 路径 ：.
     * System.getProperty("java.class.version") : Java Class 版本 ：50.0
     * System.getProperty("java.vendor") : Java Vender属性 ：The Android Project
     * System.getProperty("java.version") : Java 版本 ：0
     * System.getProperty("java.home") : Java Home属性 ：/system
     */

    @SuppressLint("DefaultLocale")
    private String getPhoneInfo(){
        return String.format("手机型号:%s,SDK版本:%d,系统版本:%s制造商：%s产品名：%s", Build.MODEL, Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE, Build.BRAND, Build.PRODUCT);
    }

    /**
     * 手机MAC地址
     */
    public static String getMacAddressInfo(Context context){
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    /** 获得设备ip地址 */
    public static String getLocalAddress(){
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while(enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }catch(SocketException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * 检查是否是华为
     *
     * @return boolean
     */
    public static boolean isHUAWEI(){
        boolean ishuawei = false;
        //手机型号             制造商
        if(Build.MODEL.startsWith("HUAWEI") && Build.BRAND.startsWith("HUAWEI")) {
            ishuawei = true;
        }
        return ishuawei;
    }

    /**
     * 检查是否是MIUI
     * <li>
     * [net.hostname]: [RedmiNote5A-hongmish]
     * [ro.bootimage.build.fingerprint]: [xiaomi/ugg/ugg:7.1.2/N2G47H/V9.1.4.0.NDKCNEI:user/release-keys]
     * [ro.build.fingerprint]: [xiaomi/ugg/ugg:7.1.2/N2G47H/V9.1.4.0.NDKCNEI:user/release-keys]
     * [ro.build.version.incremental]: [V9.1.4.0.NDKCNEI] miui版本|稳定版
     * [ro.build.version.release]: [7.1.2] 安卓版本
     * [ro.product.model]: [Redmi Note 5A] 手机型号
     * [ro.build.version.security_patch]: [2017-10-01]安全补丁级别
     * [ro.com.google.clientidbase]: [android-xiaomi]
     * [ro.product.brand]: [xiaomi]
     * [ro.product.manufacturer]: [Xiaomi]
     * [ro.miui.ui.version.code]: [7]
     * [ro.miui.ui.version.name]: [V9]
     * </li>
     *
     * @return boolean
     */
    public static boolean isMIUI(){
        boolean isMIUI = false;
        //手机型号             制造商
        //        if(Build.MODEL.toLowerCase().contains("mi") && Build.BRAND.equalsIgnoreCase("Xiaomi")) {
        if(Build.BRAND.equalsIgnoreCase("xiaomi")) {
            LogHelper.Log_d("MIUI版本："+Build.VERSION.INCREMENTAL);
            LogHelper.Log_d("手机型号："+Build.MODEL);
            isMIUI = true;
        }
        return isMIUI;
    }

    public static String getMIUISystemVersionCode(){
        return getMIUISystemProperty("ro.miui.ui.version.code");
    }

    public static String getMIUISystemVersionName(){
        return getMIUISystemProperty("ro.miui.ui.version.name");
    }

    public static String getMIUISystemProperty(String propName){
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop "+propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        }catch(IOException ex) {
            Log.e(TAG, "Unable to read sysprop "+propName, ex);
            return null;
        }finally {
            if(input != null) {
                try {
                    input.close();
                }catch(IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public static String getDeviceID(){
        return Settings.Secure.getString(LibApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /** 获取手机外部可用空间大小，单位为byte */
    @SuppressWarnings("deprecation")
    public static long getExternalTotalSpace(){
        long totalSpace = -1L;
        try {
            String path = Environment.getExternalStorageDirectory().getPath();// 获取外部存储目录即 SDCard
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalSpace = totalBlocks*blockSize;
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return totalSpace;
    }

    /** 获取外部存储可用空间，单位为byte */
    @SuppressWarnings("deprecation")
    public static long getExternalSpace(){
        long availableSpace = -1L;
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
            StatFs stat = new StatFs(path);
            availableSpace = stat.getAvailableBlocks()*(long)stat.getBlockSize();
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return availableSpace;
    }

    /** 获取手机内部空间大小，单位为byte */
    @SuppressWarnings("deprecation")
    public static long getTotalInternalSpace(){
        long totalSpace = -1L;
        try {
            String path = Environment.getDataDirectory().getPath();
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();// 获取该区域可用的文件系统数
            totalSpace = totalBlocks*blockSize;
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return totalSpace;
    }

    /** 获取手机内部可用空间大小，单位为byte */
    @SuppressWarnings("deprecation")
    public static long getAvailableInternalMemorySize(){
        long availableSpace = -1l;
        try {
            String path = Environment.getDataDirectory().getPath();// 获取 Android 数据目录
            StatFs stat = new StatFs(path);// 一个模拟linux的df命令的一个类,获得SD卡和手机内存的使用情况
            long blockSize = stat.getBlockSize();// 返回 Int ，大小，以字节为单位，一个文件系统
            long availableBlocks = stat.getAvailableBlocks();// 返回 Int ，获取当前可用的存储空间
            availableSpace = availableBlocks*blockSize;
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return availableSpace;
    }


    /**
     * 获取ActionBar高度
     *
     * @param activity
     *         activity
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Activity activity){
        TypedValue tv = new TypedValue();
        if(activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * 通過res下的圖片資源名字獲取圖片
     *
     * @param context
     * @param resName
     * @return
     */
    public static Drawable getResDrawable(Context context, String resName){
        int drawableId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        return context.getResources().getDrawable(drawableId);
    }

    public static Drawable getResString(Context context, String resName){
        int drawableId = context.getResources().getIdentifier(resName, "string", context.getPackageName());
        return context.getResources().getDrawable(drawableId);
    }

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID(){
        String serial = null;

        String m_szDevIDShort = "35"+Build.BOARD.length()%10+Build.BRAND.length()%10+

                Build.CPU_ABI.length()%10+Build.DEVICE.length()%10+

                Build.DISPLAY.length()%10+Build.HOST.length()%10+

                Build.ID.length()%10+Build.MANUFACTURER.length()%10+

                Build.MODEL.length()%10+Build.PRODUCT.length()%10+

                Build.TAGS.length()%10+Build.TYPE.length()%10+

                Build.USER.length()%10; //13 位

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }catch(Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(LibApp.getContext().getPackageName(), 0);
            return info.versionName;
        }catch(Exception e) {
            e.printStackTrace();
            return DEFAULTSTR;
        }
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(String packageName){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            return info.versionName;
        }catch(Exception e) {
            e.printStackTrace();
            return DEFAULTSTR;
        }
    }

    public static String getCurrentChannel(String packageName, String channelName){
        String channel = "";
        try {
            ApplicationInfo appInfo = LibApp.getContext().getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString(channelName);
        }catch(PackageManager.NameNotFoundException e) {
            channel = "blue";
        }
        return channel;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(LibApp.getContext().getPackageName(), 0);
            return info.versionCode;
        }catch(Exception e) {
            e.printStackTrace();
            return DEFAULTERROR;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(String packageName){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            return info.versionCode;
        }catch(Exception e) {
            e.printStackTrace();
            return DEFAULTERROR;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int checkPermision(String ops){
        AppOpsManager appOpsManager = (AppOpsManager)LibApp.getContext().getSystemService(Context.APP_OPS_SERVICE);
        return appOpsManager.checkOpNoThrow(ops, Binder.getCallingUid(), LibApp.getContext().getPackageName());
        //        if(checkResult == AppOpsManager.MODE_ALLOWED) {
        //        }else if(checkResult == AppOpsManager.MODE_IGNORED) {
        //        }else if(checkResult == AppOpsManager.MODE_ERRORED) {
        //        }else if(checkResult == 4) {
        //        }
    }

    /**
     * Check whether the app is installed
     *
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(final String packageName){
        try {
            final PackageManager pm = LibApp.getContext().getPackageManager();
            final PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info != null;
        }catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断service是否运行
     *
     * @param cls
     * @return
     */
    public static boolean isServiceRunning(Class<?> cls){
        final ActivityManager am = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        final String className = cls.getName();
        for(ActivityManager.RunningServiceInfo service : services) {
            if(className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断app是否运行
     *
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(final String packageName){
        final ActivityManager am = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if(apps == null || apps.isEmpty()) {
            return false;
        }
        for(ActivityManager.RunningAppProcessInfo app : apps) {
            if(packageName.equals(app.processName)) {
                return true;
            }
        }
        return false;
    }


    /** 获取app使用的最大内存，单位为 M*/
    public static long getAppTotalMemory(){
        ActivityManager activityManager = (ActivityManager)LibApp.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass();
    }

    /** 获取app使用的最大内存，单位为 M*/
    public static long getAppLargeMemory(){
        ActivityManager activityManager = (ActivityManager)LibApp.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getLargeMemoryClass();
    }

    /**
     * 获取手机的剩余可用内存(RAM)空间
     *
     * @return 可用内存的大小 单位 byte
     */
    public static long getAvailRam(){
        ActivityManager am = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availram = outInfo.availMem;
        System.out.println(String.format("获取剩余可用内存："+Formatter.formatFileSize(LibApp.getContext(), availram)));
        return availram;
    }

    /**
     * 获取手机的总的内存(RAM)空间大小
     *
     * @return 可用内存的大小 单位 byte
     */
    public static long getTotalRam(){
        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            //MemTotal:         513000 kB
            StringBuffer sb = new StringBuffer();
            for(char c : line.toCharArray()) {
                if(c>='0' && c<='9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString())*1024;
        }catch(Exception e) {
            ActivityManager am = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(outInfo);
            return outInfo.totalMem;
        }

    }

    public static boolean isEmulator(){
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL
                .contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL
                .contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || ( Build.BRAND
                .startsWith("generic") && Build.DEVICE.startsWith("generic") ) || "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * 检查是否为模拟器
     *
     * @return
     */
    public static boolean checkIsEmulator(){

        try {
            boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean emu = getSystemProperty("ro.kernel.qemu").length()>0;
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            if(emu || goldfish || sdk || android.os.Build.MODEL.contains("google_sdk") || android.os.Build.MODEL
                    .contains("Emulator")) {
                return true;
            }
        }catch(Exception e) {
        }
        return false;
    }

    private static String getSystemProperty(String name) throws Exception{
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String)systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, new Object[]{name});
    }

    /**
     * 检查 摄像头是否可用
     *
     * @return github上一个关于权限的讨论链接：https://github.com/android-cn/android-discuss/issues/174#issuecomment-289990995
     */
    public static boolean isCameraUseable(){
        boolean canUse = true;
        Camera mCamera = null;

        try {
            mCamera = Camera.open();

            // setParameters 是针对魅族MX5。MX5通过Camera.open()拿到的Camera对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        }catch(Exception e) {
            canUse = false;

        }
        if(mCamera != null) {
            mCamera.release();

        }
        return canUse;
    }

    public static String getDiviceName(){
        return android.os.Build.MODEL;
    }

    /**
     * 计算缓存大小
     * <ul>
     * Message 包括
     * <li>开始[扫描的app数量]{@link #SCANING_APP_START}</li>
     * <li>进度[当前扫描的app名字]{@link #SCANING_APP_NAME}</li>
     * <li>结束[扫描完毕所有的app缓存数据集合{@link CacheInfo}]{@link #SCANING_APP_FINISH}</li>
     * </ul>
     */
    public static Observable<Message> scanAppsCacheObserver(){
        return Observable.create(new ObservableOnSubscribe<Message>() {
            @Override
            public void subscribe(ObservableEmitter<Message> e) throws Exception{
                scanCache(e);
            }
        }).compose(RxUtill.<Message>defaultSchedulers_obser());
    }

    /**
     * 扫描缓存
     *
     * @param e
     */
    private static void scanCache(final ObservableEmitter<Message> e){
        final ArrayList<CacheInfo> appCacheListInfos = new ArrayList<CacheInfo>();
        final PackageManager pm = LibApp.getContext().getPackageManager();
        // 1.扫描全部应用程序的包名
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        int size = infos.size();
        Message msgStart = Message.obtain();
        msgStart.what = SCANING_APP_START;
        msgStart.obj = size;
        e.onNext(msgStart);
        int progress = 0;
        for(PackageInfo info : infos) {
            String packname = info.packageName;
            try {
                Method method = PackageManager.class
                        .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.invoke(pm, packname, new MyObserver(e, appCacheListInfos));
                Thread.sleep(50);
                progress++;
                Message msgProgress = Message.obtain();
                msgProgress.what = SCANING_APP_NAME;
                msgProgress.obj = progress*1f/size;
                e.onNext(msgProgress);
            }catch(Exception err) {
                e.onError(err);
            }
        }
        //集合的数据就准备好了. 通知界面更新
        Message msgFinish = Message.obtain();
        msgFinish.what = SCANING_APP_FINISH;
        msgFinish.obj = appCacheListInfos;
        e.onNext(msgFinish);
        e.onComplete();
    }

    /**
     * 清理全部应用程序缓存的点击事件
     * <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
     *
     * @param view
     */
    @RequiresPermission("android.permission.CLEAR_APP_CACHE")
    public static void cleanAllappCaches(View view){
        //freeStorageAndNotify
        PackageManager pm = LibApp.getContext().getPackageManager();
        Method[] methods = PackageManager.class.getMethods();
        try {
            Method method = pm.getClass().getMethod("freeStorageAndNotify", Long.TYPE, IPackageDataObserver.class);
            method.invoke(pm, getTotalInternalSpace(), new ClearCacheObserver());
        }catch(NoSuchMethodException e) {
            e.printStackTrace();
        }catch(IllegalAccessException e) {
            e.printStackTrace();
        }catch(InvocationTargetException e) {
            e.printStackTrace();
        }
        //        for(Method method : methods) {
        //            if("freeStorageAndNotify".equals(method.getName())) {
        //                try {
        ////                    method.invoke(pm, Integer.MAX_VALUE, new ClearCacheObserver());
        //                    //Integer.MAX_VALUE=2147483647 这个值要大于你手机的可用内存。真机一般比Integer.MAX_VALUE大
        //                    method.invoke(pm, getTotalInternalSpace(), new ClearCacheObserver());
        //                }catch(Exception e) {
        //                    e.printStackTrace();
        //                }
        //                return;
        //            }
        //        }
    }

    /**
     * 清除 指定包名 缓存数据
     * <p>类似app详情页的清除缓存，清除后会被清除进程app被强制关闭</p>
     *
     * @param packageName
     */
    public static void strongClearCache(String packageName){
        try {
            ActivityManager am = (ActivityManager)LibApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> amClass = Class.forName(am.getClass().getName());
            Method clearApp = amClass.getMethod("clearApplicationUserData", String.class, IPackageDataObserver.class);
            Log.d("MainActivity", "clearApp: "+clearApp.getName());
            clearApp.invoke(am, packageName, new PhoneHelper.ClearCacheObserver());
        }catch(Exception e) {
            LogHelper.Log_e("Exception: ", e.toString());
            e.printStackTrace();
        }
    }

    public static class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded){
            ToastHelper.showShort("清除状态:", succeeded);
        }
    }

    public static class MyObserver extends IPackageStatsObserver.Stub {
        PackageManager pm;
        private ObservableEmitter<Message> mE;
        private ArrayList mAppCacheListInfos;

        public MyObserver(ObservableEmitter<Message> e, ArrayList appCacheListInfos){
            mE = e;
            mAppCacheListInfos = appCacheListInfos;
            pm = LibApp.getContext().getPackageManager();
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException{
            try {
                Message msg = Message.obtain();
                msg.what = SCANING_APP_NAME;
                String appname = pm.getPackageInfo(pStats.packageName, 0).applicationInfo.loadLabel(pm).toString();
                msg.obj = appname;
                mE.onNext(msg);
                if(pStats.cacheSize>0) {
                    CacheInfo cacheinfo = new CacheInfo();
                    cacheinfo.cache = pStats.cacheSize;
                    cacheinfo.packname = pStats.packageName;
                    cacheinfo.icon = pm.getPackageInfo(cacheinfo.packname, 0).applicationInfo.loadIcon(pm);
                    cacheinfo.appname = appname;
                    mAppCacheListInfos.add(cacheinfo);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean isSystemUILayoutFullScreen(Window window){
        return ( window.getDecorView()
                .getSystemUiVisibility()&View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    }

    static class CacheInfo {
        long cache;
        String packname;
        Drawable icon;
        String appname;
    }
}
