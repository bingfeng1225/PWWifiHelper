package cn.qd.peiwen.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.wifi.listener.IPWConnectiveListener;
import cn.qd.peiwen.wifi.listener.IPWNetworkListener;

public class ConnectiveReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private WeakReference<IPWConnectiveListener> listener;

    public ConnectiveReceiver(ConnectivityManager connectivityManager, IPWConnectiveListener listener) {
        this.listener = new WeakReference<>(listener);
        this.connectivityManager = connectivityManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo network = this.connectivityManager.getActiveNetworkInfo();
            if(network != null && network.isAvailable()) {
                if(listener != null && listener.get() != null) {
                    listener.get().onNetworkConnected();
                }
            } else {
                if(listener != null && listener.get() != null) {
                    listener.get().onNetworkDisconnected();
                }
            }
        }
    }
}
