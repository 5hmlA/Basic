package com.blueprint.service;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.du.DownloadCell;
import com.blueprint.du.sys.DownloadManagerPro;
import com.blueprint.error.ErrorMsg;
import com.blueprint.helper.SpHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.blueprint.error.ErrorMsg.DEFAULTERROR;
import static com.blueprint.helper.FileHelper.doClearFile;
import static com.blueprint.helper.FileHelper.getFileDownloadPath;
import static com.blueprint.helper.FileHelper.getFileDownloadPath_file;
import static com.blueprint.helper.FileHelper.getNewAppFile;
import static com.blueprint.helper.FileHelper.getNewAppName;
import static com.blueprint.helper.LogHelper.Log_d;
import static com.blueprint.helper.PackageHelper.getAppVersionCode;
import static com.blueprint.helper.PackageHelper.installNormal;
import static java.lang.String.valueOf;

/**
 * @another 江祖赟
 * @date 2017/6/15.
 */
public class JDownloadService extends Service {

    public static final String SP_DOWNLOADID = "up_download_id";
    public static final int HAVE_NEWAPP = -11;
    private DownloadBinder mDownloadBinder = new DownloadBinder();
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

    public class DownloadBinder extends Binder {

        public DownloadBinder config(boolean downloadwify_only, boolean download_only, String new_version){
            mDownload_wifi_only = downloadwify_only;
            mDownload_only = download_only;
            mNewversion = new_version;
            return this;
        }

        public DownloadBinder config(String new_version){
            mNewversion = new_version;
            return this;
        }

        public DownloadBinder config(boolean download_only){
            mDownload_only = download_only;
            return this;
        }

        /**
         * @param notifyVisibility
         * @return
         */
        public DownloadBinder configNotify(int notifyVisibility){
            mNotifyVisibility = notifyVisibility;
            return this;
        }

        /**
         * @return
         */
        public Observable getDownloadObserver(DownloadCell downloadCell){
            mDownloadCell = downloadCell;
            return Observable.create(new ObservableOnSubscribe<DownloadCell>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<DownloadCell> downloadEmitter) throws Exception{
                    mDownloadEmitter = downloadEmitter;
                }
            }).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(@NonNull Disposable disposable) throws Exception{
                    startDownload(mDownloadCell.getDownUrl());
                }
            });
        }


        /**
         * 下载
         * -11表示本地存在新版本
         *
         * @param apkUrl
         *         下载的url
         */
        public long check2download_install(String apkUrl){
            long downloadId = -10;
            //点击下载
            //原有app版本和当前版本一致 删除原有的APK
            doClearFile(getFileDownloadPath_file(getNewAppName(valueOf(getAppVersionCode()))));

            //判断文件是否存在
            if(getFileDownloadPath_file(getNewAppName(mNewversion)).exists()) {
                Log_d("更新的文件 ****** 文件已经存在");
                if(!mDownload_only) {
                    autoInstallNewApp();
                }
                destroySelf();
                return HAVE_NEWAPP;
            }
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //具备读写权限
                downloadId = doDownload(apkUrl);
            }else {
                // 请求失败回收当前服务
                destroySelf();
            }

            return downloadId;
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


        public JDownloadService getService(){
            return JDownloadService.this;
        }

    }

    public void startDownload(String downUrl){
        if(String.valueOf(ErrorMsg.DEFAULTERROR).equals(mDownloadCell.getDownloadID())) {
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

    @Override
    public void onCreate(){
        super.onCreate();
        mDownloadManagerPro = DownloadManagerPro.getInstance();
        mDownloadSuccessReceiver = new DownloadSuccessReceiver();

        registerReceiver(mDownloadSuccessReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mDownloadBinder;
    }

    private void autoInstallNewApp(){
        Log_d("安装app ****** ");
        //是否需要自动安装/只下载
        installNormal(getNewAppFile(mNewversion));
    }

    private long doDownload(String downloadUrl){
        long downloadid = 0;

        Log_d("开始下载apk ****** ");
        downloadid = mDownloadManagerPro
                .downloadApp(downloadUrl, getNewAppName(mNewversion), LibApp.findString(R.string.j_d_newversion),
                        mDownload_wifi_only, mNotifyVisibility);
        SpHelper.sput(SP_DOWNLOADID, downloadid);
        mDownloadCell.setDownloadID(String.valueOf(downloadid));
        mApkPaths.append(downloadid, getFileDownloadPath(getNewAppName(mNewversion)));
        mDownloadCell.setSavePath(getFileDownloadPath(getNewAppName(mNewversion)));
        mDownloadCell.setSaveName(getNewAppName(mNewversion));

        return downloadid;
    }

    public class DownloadSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            SpHelper.sremove(SP_DOWNLOADID);
            Log_d("更新下载完成 ****** "+completeDownloadId+" 存储地址："+mApkPaths.get(completeDownloadId));

            if(mDownloadEmitter != null) {
                mDownloadEmitter.onComplete();
            }
            if(!mDownload_only) {
                autoInstallNewApp();
            }
            destroySelf();
        }
    }

    private void destroySelf(){
        Log_d("关闭更新服务 ****** ");
        //销毁当前的Service
        stopSelf();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mDownloadSuccessReceiver);
    }
}
