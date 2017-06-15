package com.blueprint.service;

import android.Manifest;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.du.sys.DownloadManagerPro;
import com.blueprint.helper.FileHelper;
import com.blueprint.helper.PackageHelper;
import com.blueprint.helper.SpHelper;

import java.io.File;

import static com.blueprint.helper.LogHelper.Log_d;

/**
 * @another 江祖赟
 * @date 2017/6/15.
 */
public class JUpdateService extends IntentService {

    public static final String UPDATEURL = "updateurl";
    public static final String NEWVERSION_CODE = "version_code";
    public static final String ONLY_DOWNLOAD = "onlydownload";
    public static final String FORCE_DOWNLOAD = "force_download";
    public static final String SP_DOWNLOADID = "up_download_id";
    private String mDownloadUrl;
    /**
     * 只在 wifi下下载
     */
    private boolean mDownload_wifi_only;
    /**
     * 只下载 不安装
     */
    private boolean mDownload_only;
    private String mNewversion;
    private BroadcastReceiver mDownloadSuccessReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public JUpdateService(){
        super("JUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        mDownloadUrl = intent.getStringExtra(UPDATEURL);
        mDownload_wifi_only = intent.getBooleanExtra(FORCE_DOWNLOAD, true);
        mDownload_only = intent.getBooleanExtra(ONLY_DOWNLOAD, false);
        mNewversion = intent.getStringExtra(NEWVERSION_CODE);
        mDownloadSuccessReceiver = new DownloadSuccessReceiver();
        registerReceiver(mDownloadSuccessReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //具备读写权限
            startDownload(mDownloadUrl);
        }else {
            // 请求失败回收当前服务
            destroySelf();

        }
    }

    private void autoInstallNewApp(){
        Log_d("安装app ****** ");
        //是否需要自动安装/只下载
        PackageHelper.installNormal(getSPathFileName());
    }

    private void startDownload(String downloadUrl){
        //判断文件是否存在
        if(!FileHelper.fileExists(getSPathFileName())) {
            Log_d("开始下载apk ****** ");
            long downloadin = DownloadManagerPro.getInstance()
                    .downloadApp(downloadUrl, getSaveName(), LibApp.findString(R.string.j_d_newversion),
                            mDownload_wifi_only);
            SpHelper.sput(SP_DOWNLOADID, downloadin);
        }else {
            Log_d("更新的文件 ****** 文件已经存在");
            if(!mDownload_only) {
                autoInstallNewApp();
            }
            destroySelf();
        }

    }

    @NonNull
    private String getSPathFileName(){
        return FileHelper.getDownloadPath()+File.separator+getSaveName();
    }

    @NonNull
    private String getSaveName(){
        return LibApp.getPackageName()+mNewversion+".apk";
    }

    public class DownloadSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            SpHelper.sremove(SP_DOWNLOADID);
            Log_d("更新下载完成 ****** "+completeDownloadId);
            if(!mDownload_only) {
                autoInstallNewApp();
            }
            destroySelf();
        }
    }

    private void destroySelf(){
        Log_d("关闭更新服务 ****** ");
        unregisterReceiver(mDownloadSuccessReceiver);
        //销毁当前的Service
        stopSelf();
    }

}
