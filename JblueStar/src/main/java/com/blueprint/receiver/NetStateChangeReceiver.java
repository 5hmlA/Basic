package com.blueprint.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.blueprint.LibApp;
import com.blueprint.helper.NetHelper;
import com.blueprint.rx.RxBus;

/**
 * @another 江祖赟
 * @date 2017/9/2 0002.
 */
public class NetStateChangeReceiver extends BroadcastReceiver {

    public static NetStateChangeReceiver regestReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        NetStateChangeReceiver broadcastReceiver = new NetStateChangeReceiver();
        LibApp.getContext().registerReceiver(broadcastReceiver, filter);
        return broadcastReceiver;
    }

    private boolean currentWifi;

    @Override
    public void onReceive(Context context, Intent intent){
        if(NetHelper.isConnected()) {
            if(!NetHelper.isWifionnected()) {
                if(currentWifi) {
                    currentWifi = false;
                    RxBus.getInstance().post(new NetStateEvent());
                }
            }else {
                if(!currentWifi) {
                    currentWifi = NetHelper.isWifionnected();
                }
            }
        }
    }

    public static class NetStateEvent {
        public boolean isMobile = true;
    }
}