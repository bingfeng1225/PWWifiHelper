package cn.qd.peiwen.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.wifi.listener.IPWWifiStateListener;

public class WifiStateReceiver extends BroadcastReceiver {
    private WeakReference<IPWWifiStateListener> listener;

    public WifiStateReceiver(IPWWifiStateListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (state) {
                case WifiManager.WIFI_STATE_ENABLED: {
                    if (null != listener && null != listener.get()) {
                        listener.get().onWIFIEnabled();
                    }
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLED: {
                    if (null != listener && null != listener.get()) {
                        listener.get().onWIFIDisabled();
                    }
                    break;
                }
            }
        }
    }
}
