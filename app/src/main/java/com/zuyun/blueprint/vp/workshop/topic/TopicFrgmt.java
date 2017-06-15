package com.zuyun.blueprint.vp.workshop.topic;


import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blueprint.du.okh.DownLoadEntity;
import com.blueprint.du.okh.MultipartHelper;
import com.blueprint.du.okh.ProgressListener;
import com.blueprint.du.sys.DownloadManagerPro;
import com.blueprint.service.JUpdateService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zuyun.blueprint.R;
import com.zuyun.blueprint.vp.basic.JBaseFragment;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [专题]
 */
public class TopicFrgmt extends JBaseFragment {

    private DownloadManager mSysDownload;
    private long mDownloadId;
    //
    private static final String apktesturl = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
    //    private static final String apktesturl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private DownloadChangeObserver mObserver;
    private MultipartHelper mDownloadHelper;

    class DownloadChangeObserver extends ContentObserver {
        public DownloadChangeObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri){
            super.onChange(selfChange, uri);
            System.out.println(uri.getPath()+"-----------------");
            int[] bytesAndStatus = DownloadManagerPro.getInstance().getBytesAndStatus(mDownloadId);
            System.out.println(bytesAndStatus[0]*1f/bytesAndStatus[1]+"====="+bytesAndStatus[2]);
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSysDownload = (DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
        mObserver = new DownloadChangeObserver(null);
    }

    private boolean pause = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View inflate = inflater.inflate(R.layout.jmain_fm_content, null);
        TextView mTempTv = (TextView)inflate.findViewById(R.id.temp_tv);
        mTempTv.setText("2323232");
        mTempTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(pause) {
                    pause = false;
                    checkPermission();
                }else {
                    pause = true;
                    //                    mDownloadHelper.cancel();
                }
            }
        });
        return inflate;
    }

    @Override
    public void onResume(){
        super.onResume();
        getContext().getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mObserver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getContext().getContentResolver().unregisterContentObserver(mObserver);
    }

    private void checkPermission(){
        if(PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE)) {
            //            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},100);
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(@NonNull Disposable d){

                }

                @Override
                public void onNext(@NonNull Boolean aBoolean){
                    doDownload();
                    Toast.makeText(getContext(), "货到权限——"+aBoolean, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Throwable e){
                    Toast.makeText(getActivity(), "拒绝权限", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(){

                }
            });
        }else {

            doDownload();
            //            doDownload2();
        }
    }

    private long byw;

    private void doDownload2(){
        mDownloadHelper = new MultipartHelper(new DownLoadEntity(apktesturl, "aa"+".apk"),
                new ProgressListener() {
                    @Override
                    public void onProgress(long bytesWritten, long contentLength, boolean done){
                        System.out.println(bytesWritten*1f/contentLength+"---------+++++++++++++++++++++"+contentLength);
                        byw = bytesWritten;
                    }

                    @Override
                    public void onComplete(){

                    }

                    @Override
                    public void onFailure(){

                    }

                    @Override
                    public void onCancel(){

                    }
                });
        mDownloadHelper.download();
        //        mDownloadHelper.downloadFrom(byw);
    }

    private void doDownload(){
        Intent intent = new Intent(getContext(), JUpdateService.class);
        intent.putExtra(JUpdateService.UPDATEURL, apktesturl);
        getContext().startService(intent);
//        BuildConfig.APPLICATION_ID
//        downloadFile(apktesturl, "MeiLiShuo.apk");
        //        downloadFile("http://down.mumayi.com/41052/mbaidu", "baidu.apk");


    }

    private void downloadFile(String url, String name){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("通知下载测试");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        request.setTitle("下载");
        //表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        /**设置下载文件的类型*/
        request.setMimeType("application/vnd.android.package-archive");
        //表示允许MediaScanner扫描到这个文件，默认不允许。
        //        request.allowScanningByMediaScanner();
        //表示下载允许的网络类型，默认在任何网络下都允许下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        mDownloadId = mSysDownload.enqueue(request);
    }
}
