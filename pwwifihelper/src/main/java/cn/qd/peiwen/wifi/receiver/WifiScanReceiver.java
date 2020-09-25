package cn.qd.peiwen.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.wifi.listener.IPWWifiScanListener;

public class WifiScanReceiver extends BroadcastReceiver {
    private WeakReference<IPWWifiScanListener> listener;

    public WifiScanReceiver(IPWWifiScanListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            if (null != listener && null != listener.get()) {
                listener.get().onScanResultsAvailabled();
            }
        }
    }
}
