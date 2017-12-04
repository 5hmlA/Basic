package com.blueprint.basic.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.helper.Damping;
import com.blueprint.helper.KeyboardHelper;
import com.blueprint.helper.LogHelper;
import com.blueprint.helper.NetHelper;
import com.blueprint.helper.ToastHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.blueprint.LibApp.toLaunch;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public class JBaseActivity extends AppCompatActivity {

    protected Toast mDoubleFinish;
    public static boolean currentWifi;
    protected NetworkConnectChangedReceiver mConnectChangedReceiver;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected boolean mNeedWatchNetState;
    protected boolean mNeedLoopTransition = true;
    protected Intent mFromIntent;

    protected void collectDisposables(Disposable disposable){
        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposables(){
        LogHelper.Log_d("before-clearDisposables()-: "+mCompositeDisposable.size());
        mCompositeDisposable.clear();
        LogHelper.Log_d("after-clearDisposables()-: "+mCompositeDisposable.size());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            //状态栏透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//布局全屏
        }
        super.onCreate(savedInstanceState);
        if(mNeedLoopTransition && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(setExitTransition());
            getWindow().setEnterTransition(setEnterTransition());
        }
        mDoubleFinish = Toast.makeText(this, LibApp.findString(R.string.jdouble_exit), Toast.LENGTH_SHORT);
        if(mNeedWatchNetState = setNeedWatchNetState()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mConnectChangedReceiver = new NetworkConnectChangedReceiver();
            registerReceiver(mConnectChangedReceiver, filter);
            currentWifi = NetHelper.isWifionnected();
        }
        if(( mFromIntent = getIntent() ) != null) {
            parseIntent(mFromIntent);
        }
    }

    /**
     * getIntent不为空
     *
     * @param fromIntent
     */
    protected void parseIntent(Intent fromIntent){

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Transition setEnterTransition(){
        return new Explode();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Transition setExitTransition(){
        return new Explode();
    }

    protected boolean setNeedWatchNetState(){
        return false;
    }

    public void doubleExit(){
        if(mDoubleFinish.getView() != null && mDoubleFinish.getView().isShown()) {
            finish();
            Single.just(0).delay(400, TimeUnit.MILLISECONDS).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(@NonNull Integer integer) throws Exception{
                    if(LibApp.findBoolen(R.bool.double_exit_2_launch)) {
                        toLaunch();
                    }else {
                        System.exit(0);
                    }
                }
            });
        }else {
            mDoubleFinish.show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(setToggleKeyboardAble()) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    KeyboardHelper.hideKeyboard(this);
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean setToggleKeyboardAble(){
        return false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mNeedWatchNetState && mConnectChangedReceiver != null) {
            unregisterReceiver(mConnectChangedReceiver);
        }
        clearDisposables();
    }

    public static class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            if(NetHelper.isConnected()) {
                if(!NetHelper.isWifionnected()) {
                    if(currentWifi) {
                        currentWifi = false;
                        ToastHelper.showShort("切换到手机流量");
                    }
                }else {
                    if(!currentWifi) {
                        currentWifi = NetHelper.isWifionnected();
                        ToastHelper.showShort("切换到wifi");
                    }
                }
            }
        }
    }

    public void wrapperView(View view){
        if(LibApp.JELLYLIST) {
            Damping.wrapper(view).configDirection(LinearLayout.VERTICAL);
        }
    }
}
