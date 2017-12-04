package com.blueprint.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.blueprint.Consistent;
import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.du.DownloadCell;
import com.blueprint.du.sys.DownloadManagerPro;
import com.blueprint.helper.PhoneHelper;
import com.blueprint.helper.SpHelper;

import io.reactivex.ObservableEmitter;

import static com.blueprint.Consistent.DEFAULTERROR;
import static com.blueprint.helper.FileHelper.doClearFile;
import static com.blueprint.helper.FileHelper.getFileDownloadPath;
import static com.blueprint.helper.FileHelper.getFileDownloadPath_file;
import static com.blueprint.helper.FileHelper.getNewAppFile;
import static com.blueprint.helper.FileHelper.getNewAppName;
import static com.blueprint.helper.LogHelper.Log_d;
import static com.blueprint.helper.PackageHelper.installNormal;

/**
 * @another 江祖赟
 * @date 2017/6/15.
 */
public class AppUpdateManager {

    public static final String SP_DOWNLOADID = "up_download_id_";
    public static final int ALREADY_HAVE_NEWAPP = -11;
    /**
     * 只在 wifi下下载
     */
    private boolean mDownload_wifi_only = true;
    /**
     * 只下载 不安装
     */
    private boolean mDownload_only = true;
    private ObservableEmitter<DownloadCell> mDownloadEmitter;
    private String mNewversion = "";
    private BroadcastReceiver mDownloadSuccessReceiver;
    private DownloadManagerPro mDownloadManagerPro;
    private LongSparseArray<String> mApkPaths = new LongSparseArray<>();
    private int mNotifyVisibility = DEFAULTERROR;
    private DownloadCell mDownloadCell;

    private AppUpdateManager(){
        mDownloadManagerPro = DownloadManagerPro.getInstance();
    }

    private static class Singleton {
        private static AppUpdateManager sAppUpdateManager = new AppUpdateManager();
    }

    public static AppUpdateManager getSingleton(){
        return Singleton.sAppUpdateManager;
    }

    /**
     * 下载
     * -11表示本地存在新版本
     *
     * @param apkUrl
     *         下载的url
     */
    public long check2download_install(String newversion, String apkUrl){
        mNewversion = newversion;
        long downloadId = -10;
        SpHelper.sremove(getUpdateKey(newversion));
        //点击下载
        //原有app版本和当前版本一致 删除原有的APK
        doClearFile(getFileDownloadPath_file(getNewAppName(PhoneHelper.getVersionName())));
        //判断文件是否存在
        if(getFileDownloadPath_file(getNewAppName(mNewversion)).exists()) {
            Log_d("更新的文件 ****** 文件已经存在");
            if(!mDownload_only) {
                autoInstallNewApp();
            }
            return ALREADY_HAVE_NEWAPP;
        }
        //具备读写权限
        downloadId = doDownload(apkUrl);
        return downloadId;
    }

    public AppUpdateManager setDownload_only(boolean download_only){
        mDownload_only = download_only;
        return this;
    }

    public void pauseDownload(long downloadId){
        if(mDownloadManagerPro != null) {
            mDownloadManagerPro.pauseDownload(downloadId);
        }
    }

    public void resumeDownload(long downloadId){
        if(mDownloadManagerPro != null) {
            mDownloadManagerPro.resumeDownload(downloadId);
        }
    }

    public void cancelDownload(long downloadId){
        if(mDownloadManagerPro != null) {
            mDownloadManagerPro.removeDownload(downloadId);
        }
    }

    /**
     * 获取进度信息
     *
     * @param downloadId
     *         要获取下载的id
     * @return 进度信息 max-100
     */
    public float getProgress(long downloadId){
        if(mDownloadManagerPro != null) {
            return mDownloadManagerPro.getDownloadProgress(downloadId);
        }
        return 0;
    }


    public void startDownload(String downUrl){
        if(String.valueOf(Consistent.DEFAULTERROR).equals(mDownloadCell.getDownloadID())) {
            //使用ok


        }else {
            //使用系统默认
            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downUrl));
            //通知栏设置 是否显示
            if(!TextUtils.isEmpty(mDownloadCell.getExtra1())) {
                downloadRequest.setNotificationVisibility(Integer.parseInt(mDownloadCell.getExtra1()));
            }

            downloadRequest.setDestinationUri(Uri.fromFile(mDownloadCell.getDestFile()));
            //是否只在WiFi环境下下载
            if(!TextUtils.isEmpty(mDownloadCell.getExtra2())) {
                downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            }
            mDownloadManagerPro.startDownload(downloadRequest);
        }

    }

    public void autoInstallNewApp(){
        autoInstallNewApp(mNewversion);
    }

    public void autoInstallNewApp(String mNewversion){
        Log_d("安装app ****** ");
        //是否需要自动安装/只下载
        installNormal(getNewAppFile(mNewversion));
    }

    private long doDownload(String downloadUrl){
        long downloadid = 0;
        mDownloadCell = new DownloadCell(downloadUrl);
        Log_d("开始下载apk ****** ");
        downloadid = mDownloadManagerPro
                .downloadApp(downloadUrl, getNewAppName(mNewversion), LibApp.findString(R.string.j_d_newversion),
                        mDownload_wifi_only, mNotifyVisibility);
        SpHelper.sput(getUpdateKey(mNewversion), downloadid);
        mDownloadCell.setDownloadID(String.valueOf(downloadid));
        mApkPaths.append(downloadid, getFileDownloadPath(getNewAppName(mNewversion)));
        mDownloadCell.setSavePath(getFileDownloadPath(getNewAppName(mNewversion)));
        mDownloadCell.setSaveName(getNewAppName(mNewversion));

        return downloadid;
    }

    public AppUpdateManager setNotifyVisibility(int notifyVisibility){
        mNotifyVisibility = notifyVisibility;
        return this;
    }

    //    public class DownloadSuccessReceiver extends BroadcastReceiver {
    //        @Override
    //        public void onReceive(Context context, Intent intent){
    //            // get complete download id
    //            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
    //            SpHelper.sremove(SP_DOWNLOADID);
    //            Log_d("更新下载完成 ****** "+completeDownloadId+" 存储地址："+mApkPaths.get(completeDownloadId));
    //
    //            if(mDownloadEmitter != null) {
    //                mDownloadEmitter.onComplete();
    //            }
    //            if(!mDownload_only) {
    //                autoInstallNewApp();
    //            }
    //            destroySelf();
    //        }
    //    }

    /**
     * 通过浏览器下载APK包
     *
     * @param context
     * @param url
     */
    public static void downloadForWebView(Context context, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getUpdateKey(String newversion){
        return SP_DOWNLOADID+newversion;
    }
}
