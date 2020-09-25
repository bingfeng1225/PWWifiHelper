package cn.qd.peiwen.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.wifi.listener.IPWWifiSignalListener;

public class WifiSignalReceiver extends BroadcastReceiver {
    private WeakReference<IPWWifiSignalListener> listener;

    public WifiSignalReceiver(IPWWifiSignalListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
            if (null != listener && null != listener.get()) {
                listener.get().onWifiSignalChanged(rssi);
            }
        }
    }
}
