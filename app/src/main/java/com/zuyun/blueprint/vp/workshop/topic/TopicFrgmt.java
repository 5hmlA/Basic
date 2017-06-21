package com.zuyun.blueprint.vp.workshop.topic;


import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blueprint.du.DownloadCell;
import com.blueprint.du.okh.MultipartHelper2;
import com.blueprint.du.okh.ProgressListener;
import com.blueprint.du.sys.DownloadManagerPro;
import com.blueprint.service.JDownloadService;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zuyun.blueprint.R;
import com.zuyun.blueprint.vp.basic.JBaseTitleFrgmt;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [专题]
 */
public class TopicFrgmt extends JBaseTitleFrgmt {

    private DownloadManager mSysDownload;
    private long mDownloadId;
    //
    private static final String apktesturl = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
    //    private static final String apktesturl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private DownloadChangeObserver mObserver;
    private MultipartHelper2 mDownloadHelper;
    private JDownloadService.DownloadBinder mDownloadBinder;

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
        getContext().bindService(new Intent(getContext(), JDownloadService.class), downloadConnection,
                Context.BIND_AUTO_CREATE);
    }

    private boolean pause = true;


    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootview = inflater.inflate(R.layout.sec_work_topic, container);
        mMultiStateLayout.showStateSucceed();
        RxView.clicks(rootview.findViewById(R.id.btn_start)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception{
                checkPermission();
            }
        });
        RxView.clicks(rootview.findViewById(R.id.btn_pause)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception{
                mDownloadBinder.pauseDownload(mDownloadId);
                if(mDownloadHelper != null) {
//                    mDownloadHelper.pause();
                }
            }
        });
        RxView.clicks(rootview.findViewById(R.id.btn_resume)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception{
                mDownloadBinder.resumeDownload(mDownloadId);
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        getContext().getContentResolver()
                .registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mObserver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getContext().getContentResolver().unregisterContentObserver(mObserver);

        getContext().unbindService(downloadConnection);
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

            //            doDownload();
            doDownload2();
        }
    }

    private void doDownload2(){
        mDownloadHelper = new MultipartHelper2(
                new DownloadCell.DownloadCellBuilder().downUrl(apktesturl).saveName("aa"+""+".apk").build(),
                new ProgressListener() {
                    @Override
                    public void onProgress(long bytesWritten, long contentLength, boolean done){
                        System.out
                                .println(bytesWritten*1f/contentLength+"---------+++++++++++++++++++++"+contentLength);
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
//        mDownloadHelper.download();
    }

    private void doDownload(){

        mDownloadId = mDownloadBinder.config(false).check2download_install(apktesturl);
        System.out.println(DownloadManagerPro.URL.hashCode());
        System.out.println(DownloadManagerPro.URL1.hashCode());
        System.out.println(DownloadManagerPro.URL2.hashCode());
    }

    private ServiceConnection downloadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            mDownloadBinder = (JDownloadService.DownloadBinder)service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    };

}
