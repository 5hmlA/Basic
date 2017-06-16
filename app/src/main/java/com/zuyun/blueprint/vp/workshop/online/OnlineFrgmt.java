package com.zuyun.blueprint.vp.workshop.online;


import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zuyun.blueprint.R;
import com.zuyun.blueprint.vp.basic.JBaseTitleFrgmt;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [联机]
 */
public class OnlineFrgmt extends JBaseTitleFrgmt {

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
            }
        });
        RxView.clicks(rootview.findViewById(R.id.btn_resume)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception{
            }
        });
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
                    dosthing();
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
            dosthing();
        }
    }

    private void dosthing(){

    }

}
