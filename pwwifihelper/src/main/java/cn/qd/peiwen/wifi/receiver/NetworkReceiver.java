package cn.qd.peiwen.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.wifi.listener.IPWNetworkListener;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private WeakReference<IPWNetworkListener> listener;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkReceiver(ConnectivityManager connectivityManager, IPWNetworkListener listener) {
        this.listener = new WeakReference<>(listener);
        this.connectivityManager = connectivityManager;
    }

    public void registNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != this.networkCallback) {
                return;
            }
            this.networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    if (null != listener && null != listener.get()) {
                        listener.get().onNetworkAvailabled(network);
                    }
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    if (null != listener && null != listener.get()) {
                        listener.get().onNetworkLost(network);
                    }
                }
            };
            NetworkRequest request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            this.connectivityManager.registerNetworkCallback(request, this.networkCallback);
        }
    }

    public void unregistNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != this.networkCallback) {
                this.connectivityManager.unregisterNetworkCallback(this.networkCallback);
                this.networkCallback = null;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != listener && null != listener.get()) {
                listener.get().onNetworkStateChanged(info);
            }
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            SupplicantState supplicant = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (null != listener && null != listener.get()) {
                listener.get().onSupplicantStateChanged(supplicant, error);
            }
        }
    }
}
