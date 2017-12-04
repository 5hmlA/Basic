package com.blueprint.helper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.blueprint.Consistent;
import com.blueprint.LibApp;
import com.blueprint.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;
import static com.blueprint.LibApp.getContext;

public class IntentHelper {

    public static void shareText(String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        safeStartIntent(Intent.createChooser(sendIntent, "分享至"));
    }


    public static void shareImage(Context context, Uri uri, String title){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }


    public static void share(Context context, String title, String extraText){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void shareImage(Uri uri){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/jpeg");
        safeStartIntent(Intent.createChooser(sendIntent, LibApp.findString(R.string.jlib_share_title)));
    }

    public static void shareImageList(ArrayList<Uri> uris){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uris);
        sendIntent.setType("image/*");
        safeStartIntent(Intent.createChooser(sendIntent, LibApp.findString(R.string.jlib_share_title)));
    }

    /**
     * 意图跳转  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * <p>先判断 intent是否能正常跳转
     */
    public static void startActivityWFlag(Class dest){
        Intent intent = new Intent(getContext(), dest);
        safeStartIntent(intent);
    }

    /**
     * 意图跳转
     * <p>先判断 intent是否能正常跳转
     */
    public static boolean safeStartIntent(Intent toIntent){
        if(toIntent == null) {
            return false;
        }
        //判断 改intent跳转的目标是否存在
        if(toIntent.resolveActivityInfo(getContext().getPackageManager(), 0) != null) {
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        List<ResolveInfo> resolveInfos = getContext().getPackageManager().queryIntentActivities(toIntent, 0);
        if(resolveInfos.size()>0) {
            getContext().startActivity(toIntent);
        }
        return resolveInfos.size()>0;
    }


    //1.拨打电话
    public static void call(int num){
        Uri uri = Uri.parse("tel:"+num);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        safeStartIntent(intent);
    }

    /**
     * 获取拨打电话意图
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CALL_PHONE"/>}</p>
     *
     * @param phoneNumber
     *         电话号码
     */
    public static void callDirect(int phoneNumber){
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:"+phoneNumber));
        safeStartIntent(intent);
    }

    //2.发送短信
    public static void sendSms(int num, @NonNull String msg){
        Uri uri = Uri.parse("smsto:"+num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        safeStartIntent(intent);
    }


    //4.打开浏览器:
    public static void openUrl(@NonNull String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        safeStartIntent(intent);
    }

    /**
     * @param path
     *         "sdcard/foo.mp3"
     * @param type
     *         "audio/mp3"
     */
    public static void playMedia(String path, String type){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file:///"+path);
        intent.setDataAndType(uri, type);
        safeStartIntent(intent);
    }


    /**
     * 获取SD卡下所有音频文件,然后播放第一首=-=
     */
    public static void openAllaudios(@NonNull Context cx){
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        safeStartIntent(intent);
    }


    /**
     * 打开拍照程序
     *
     * @param requestCode
     *         <pre>
     *                           取出照片数据
     *                          Bundle extras = intent.getExtras();
     *                          Bitmap bitmap = (Bitmap) extras.get("data");
     *                           安卓7以上返回 照片地址
     *                           </pre>
     */
    public static String takePicForResult(@NonNull Activity cx, int requestCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(LibApp.getContext().getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(new Date())+"_j_.png";
            File file = new File(LibApp.getContext().getExternalCacheDir(), filename);

            Uri fileUri = FileProvider7.getUriForFile(file);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cx.startActivityForResult(takePictureIntent, requestCode);
            return file.getAbsolutePath();
        }
        return Consistent.DEFAULTSTR;
    }

    /**
     * 调用系统相册裁剪图片
     */
    private void cropImage(Activity ac, int requrescode, File file){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(FileProvider7.getUriForFile(file), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider7.getUriForFile(file));
        ac.startActivityForResult(intent, requrescode);
    }

    /**
     * 进入系统相册
     *
     * @param activity
     * @param requestCode
     */
    public static void toAlbum(Activity activity, int requestCode){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);//调用相册照片
    }

    /**
     * 进入手机设置界面:android.provider.Settings.ACTION_WIRELESS_SETTINGS
     *
     * @param page
     *         see {@link android.provider.Settings}
     *         <p>android.provider.Settings.ACTION_WIRELESS_SETTINGS</p>
     * @param requestCode
     *         <p>
     *         <li>com.android.settings.AccessibilitySettings 辅助功能设置
     *         　<li>com.android.settings.ActivityPicker 选择活动
     *         　<li>com.android.settings.ApnSettings APN设置
     *         　<li>com.android.settings.ApplicationSettings 应用程序设置
     *         　<li>com.android.settings.BandMode 设置GSM/UMTS波段
     *         　<li>com.android.settings.BatteryInfo 电池信息
     *         　<li>com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
     *         　<li>com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
     *         　<li>com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
     *         　<li>com.android.settings.DeviceAdminSettings 设备管理器
     *         　<li>com.android.settings.DeviceInfoSettings 关于手机
     *         　<li>com.android.settings.Display 显示——设置显示字体大小及预览
     *         　<li>com.android.settings.DisplaySettings 显示设置
     *         　<li>com.android.settings.DockSettings 底座设置
     *         　<li>com.android.settings.IccLockSettings SIM卡锁定设置
     *         　<li>com.android.settings.InstalledAppDetails 语言和键盘设置
     *         　<li>com.android.settings.LanguageSettings 语言和键盘设置
     *         　<li>com.android.settings.LocalePicker 选择手机语言
     *         　<li>com.android.settings.LocalePickerInSetupWizard 选择手机语言
     *         　<li>com.android.settings.ManageApplications 已下载（安装）软件列表
     *         　<li>com.android.settings.MasterClear 恢复出厂设置
     *         　<li>com.android.settings.MediaFormat 格式化手机闪存
     *         　<li>com.android.settings.PhysicalKeyboardSettings 设置键盘
     *         　<li>com.android.settings.PrivacySettings 隐私设置
     *         　<li>com.android.settings.ProxySelector 代理设置
     *         　<li>com.android.settings.RadioInfo 手机信息
     *         　<li>com.android.settings.RunningServices 正在运行的程序（服务）
     *         　<li>com.android.settings.SecuritySettings 位置和安全设置
     *         　<li>com.android.settings.Settings 系统设置
     *         　<li>com.android.settings.SettingsSafetyLegalActivity 安全信息
     *         　<li>com.android.settings.SoundSettings 声音设置
     *         　<li>com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
     *         　<li>com.android.settings.TetherSettings 绑定与便携式热点
     *         　<li>com.android.settings.TextToSpeechSettings 文字转语音设置
     *         　<li>com.android.settings.UsageStats 使用情况统计
     *         　<li>com.android.settings.UserDictionarySettings 用户词典
     *         　<li>com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
     *         　<li>com.android.settings.WirelessSettings 无线和网络设置
     *         </p>
     */
    public static void toSettingPage(@NonNull Activity cx, String page, int requestCode){
        Intent intent = new Intent(page);
        cx.startActivityForResult(intent, requestCode);
    }

    public static void toAppDetailPage(@NonNull Activity cx, String page){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", page, null));
        cx.startActivity(intent);
        //        gotoMiuiPermission(cx);
    }

    /**
     * 跳转到miui的权限管理页面
     */
    public static void gotoMiuiPermission(Context ex){
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", ex.getPackageName());
        try {
            ex.startActivity(i);
        }catch(Exception e) {
            e.printStackTrace();
            gotoMeizuPermission(ex);
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    public static void gotoMeizuPermission(Context ex){
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", ex.getPackageName());
        try {
            ex.startActivity(intent);
        }catch(Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission(ex);
        }
    }

    /**
     * 华为的权限管理页面
     */
    public static void gotoHuaweiPermission(Context ex){
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            ex.startActivity(intent);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 安装apk:
     */
    public static void installApk(String apkPath){
        PackageHelper.installNormal(apkPath);
    }


    /**
     * 卸载apk:
     */
    public static void unInstallApk(Context cx, String packagename){
        Uri uri = Uri.fromParts("package", packagename, null);
        Intent it = new Intent(Intent.ACTION_DELETE, uri);

        safeStartIntent(it);
    }

    /**
     * 进入联系人页面
     */
    public static void toContantPage(Context cx, String packagename){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Contacts.People.CONTENT_URI);
        safeStartIntent(intent);
    }

    public static boolean restartApp(){
        Intent intent = LibApp.getContext().getPackageManager().getLaunchIntentForPackage(LibApp.getPackageName());
        return safeStartIntent(intent);
    }

    public static boolean launchApp(String packageName){
        return jump2App(packageName);
    }

    public static boolean jump2App(String packageName){
        Intent intent = LibApp.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        return safeStartIntent(intent);
    }

    public static boolean jump2QQ(String qq){
        String url = "mqqwpa://im/chat?chat_type=wpa&uin="+qq;//uin是发送过去的qq号码
        return safeStartIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static boolean jump2QQgroup(String qqgroup){
        //代码可参考        http://qun.qq.com/join.html网站获取
        String url = "mqqwpa://im/chat?chat_type=group&uin=+"+qqgroup+"+&version=1";
        return safeStartIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static void showPic(Activity activity, String path){
        Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
        intent.setDataAndType(Uri.parse("file://"+path), "image/*");// 设置intent数据和图片格式
        activity.startActivity(intent);
    }

    /**
     * 无障碍设置界面
     * @param activity
     * @param path
     */
    public static void toAccessibility(Activity activity, String path){
        Intent intent = new Intent(ACTION_ACCESSIBILITY_SETTINGS);    //无障碍设置界面
        activity.startActivity(intent);
    }
}
