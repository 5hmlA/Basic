package com.blueprint.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;

import com.blueprint.LibApp;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import static com.blueprint.error.ErrorMsg.DEFAULTSTR;

public class PhoneHelper {
    private static final String TAG = "PhoneHelper";

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

    private static final String LOG_TAG = "PhoneHelper";


    private String getPhoneInfo(){
        String phoneInfo = "手机型号:"+Build.MODEL+",SDK版本:"+Build.VERSION.SDK_INT+",系统版本:"+Build.VERSION.RELEASE+"制造商："+Build.BRAND+"产品名："+Build.PRODUCT;
        return phoneInfo;
    }


        /**
         * 手机MAC地址
         */
        public static String getMacAddressInfo(Context context) {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
     *
     * @return boolean
     */
    public static boolean isMIUI(){
        boolean isMIUI = false;
        //手机型号             制造商
        if(Build.MODEL.startsWith("MI") && Build.BRAND.startsWith("Xiaomi")) {
            isMIUI = true;
        }
        return isMIUI;
    }

    public static String getDeviceID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
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

    /** 获取手机剩余内存，单位为byte */
    public static long getAvailableMemory(Context context){
        if(context == null) {
            return -1;
        }
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.availMem;
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
    public static String getVersion(){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(LibApp.getContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
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
    public static String getVersion(String packageName){
        try {
            PackageManager manager = LibApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            String version = info.versionName;
            return version;
        }catch(Exception e) {
            e.printStackTrace();
            return DEFAULTSTR;
        }
    }

    /**
     * @param context
     * @param apkPath
     *         要安装的APK
     * @param rootMode
     *         是否是Root模式
     */
    public static void install(Context context, String apkPath, boolean rootMode){
        if(rootMode) {
            installRoot(context, apkPath);
        }else {
            installNormal(context, apkPath);
        }
    }

    /**
     * 通过非Root模式安装
     *
     * @param context
     * @param apkPath
     */
    public static void install(Context context, String apkPath){
        install(context, apkPath, false);
    }

    //普通安装
    private static void installNormal(Context context, String apkPath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
            File file = ( new File(apkPath) );
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.chenfengyao.installapkdemo", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    //通过Root方式安装
    private static void installRoot(Context context, String apkPath){
//        Observable.just(apkPath).map(mApkPath->"pm install -r "+mApkPath)
//                //                .map(SystemManager::RootCommand)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(integer->{
//            if(integer == 0) {
//                Toast.makeText(context, "安装成功", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(context, "root权限获取失败,尝试普通安装", Toast.LENGTH_SHORT).show();
//                install(context, apkPath);
//            }
//        });
    }
}
