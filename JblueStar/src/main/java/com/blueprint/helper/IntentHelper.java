package com.blueprint.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.blueprint.LibApp;

import java.util.List;

public class IntentHelper {

    /**
     * 意图跳转  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * <p>先判断 intent是否能正常跳转
     */
    public static void startActivityWFlag(Class dest) {
        Intent intent = new Intent(LibApp.getContext(), dest);
        safeStartIntent(intent);
    }

    /**
     * 意图跳转
     * <p>先判断 intent是否能正常跳转
     */
    public static boolean safeStartIntent(Intent toIntent) {
        //判断 改intent跳转的目标是否存在
        if (toIntent.resolveActivityInfo(LibApp.getContext().getPackageManager(), 0) != null) {
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        List<ResolveInfo> resolveInfos = LibApp.getContext()
                                             .getPackageManager()
                                             .queryIntentActivities(toIntent, 0);
        if (resolveInfos.size() > 0) {
            LibApp.getContext().startActivity(toIntent);
        }
        return resolveInfos.size() > 0;
    }


    //1.拨打电话
    public static void call(@NonNull Context cx, int num) {
        Uri uri = Uri.parse("tel:" + num);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        cx.startActivity(intent);
    }


    //2.发送短信
    public static void sendSms(@NonNull Context cx, int num, @NonNull String msg) {
        Uri uri = Uri.parse("smsto:" + num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        cx.startActivity(intent);
    }


    //4.打开浏览器:
    public static void openWebView(@NonNull Context cx, @NonNull String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        cx.startActivity(intent);
    }

    /**
     * @param path "sdcard/foo.mp3"
     * @param type "audio/mp3"
     */
    public static void playMedia(@NonNull Context cx, String path, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file:///" + path);
        intent.setDataAndType(uri, type);
        cx.startActivity(intent);
    }


    /**
     * 获取SD卡下所有音频文件,然后播放第一首=-=
     */
    public static void openAllaudios(@NonNull Context cx) {
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        cx.startActivity(intent);
    }


    /**
     * 打开拍照程序
     *
     * @param requestCode <pre>
     *  取出照片数据
     * Bundle extras = intent.getExtras();
     * Bitmap bitmap = (Bitmap) extras.get("data");
     *  </pre>
     */
    public static void takePicForResult(@NonNull Activity cx, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cx.startActivityForResult(intent, requestCode);
    }


    /**
     * 进入手机设置界面:android.provider.Settings.ACTION_WIRELESS_SETTINGS
     *
     * @param page see {@link android.provider.Settings}
     * <p>android.provider.Settings.ACTION_WIRELESS_SETTINGS</p>
     * @param requestCode <p>
     * <li>com.android.settings.AccessibilitySettings 辅助功能设置
     * 　<li>com.android.settings.ActivityPicker 选择活动
     * 　<li>com.android.settings.ApnSettings APN设置
     * 　<li>com.android.settings.ApplicationSettings 应用程序设置
     * 　<li>com.android.settings.BandMode 设置GSM/UMTS波段
     * 　<li>com.android.settings.BatteryInfo 电池信息
     * 　<li>com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
     * 　<li>com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
     * 　<li>com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
     * 　<li>com.android.settings.DeviceAdminSettings 设备管理器
     * 　<li>com.android.settings.DeviceInfoSettings 关于手机
     * 　<li>com.android.settings.Display 显示——设置显示字体大小及预览
     * 　<li>com.android.settings.DisplaySettings 显示设置
     * 　<li>com.android.settings.DockSettings 底座设置
     * 　<li>com.android.settings.IccLockSettings SIM卡锁定设置
     * 　<li>com.android.settings.InstalledAppDetails 语言和键盘设置
     * 　<li>com.android.settings.LanguageSettings 语言和键盘设置
     * 　<li>com.android.settings.LocalePicker 选择手机语言
     * 　<li>com.android.settings.LocalePickerInSetupWizard 选择手机语言
     * 　<li>com.android.settings.ManageApplications 已下载（安装）软件列表
     * 　<li>com.android.settings.MasterClear 恢复出厂设置
     * 　<li>com.android.settings.MediaFormat 格式化手机闪存
     * 　<li>com.android.settings.PhysicalKeyboardSettings 设置键盘
     * 　<li>com.android.settings.PrivacySettings 隐私设置
     * 　<li>com.android.settings.ProxySelector 代理设置
     * 　<li>com.android.settings.RadioInfo 手机信息
     * 　<li>com.android.settings.RunningServices 正在运行的程序（服务）
     * 　<li>com.android.settings.SecuritySettings 位置和安全设置
     * 　<li>com.android.settings.Settings 系统设置
     * 　<li>com.android.settings.SettingsSafetyLegalActivity 安全信息
     * 　<li>com.android.settings.SoundSettings 声音设置
     * 　<li>com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
     * 　<li>com.android.settings.TetherSettings 绑定与便携式热点
     * 　<li>com.android.settings.TextToSpeechSettings 文字转语音设置
     * 　<li>com.android.settings.UsageStats 使用情况统计
     * 　<li>com.android.settings.UserDictionarySettings 用户词典
     * 　<li>com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
     * 　<li>com.android.settings.WirelessSettings 无线和网络设置
     * </p>
     */
    public static void toSettingPage(@NonNull Activity cx, String page, int requestCode) {
        Intent intent = new Intent(page);
        cx.startActivityForResult(intent, requestCode);
    }


    /**
     * 安装apk:
     */
    public static void installApk(Context cx, String packagename) {
        Uri installUri = Uri.fromParts("package", packagename, null);
        cx.startActivity(new Intent(Intent.ACTION_PACKAGE_ADDED, installUri));
    }


    /**
     * 卸载apk:
     */
    public static void unInstallApk(Context cx, String packagename) {
        Uri uri = Uri.fromParts("package", packagename, null);
        Intent it = new Intent(Intent.ACTION_DELETE, uri);

        cx.startActivity(it);
    }

    /**
     * 进入联系人页面
     */
    public static void toContantPage(Context cx, String packagename) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Contacts.People.CONTENT_URI);
        cx.startActivity(intent);
    }
}
