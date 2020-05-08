package cn.qd.peiwen.pwwifihelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import cn.qd.peiwen.pwtools.ByteUtils;
import cn.qd.peiwen.pwtools.EmptyUtils;

public class WIFIHelper {
    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private WeakReference<IWIFIListener> listener;
    private ConnectivityManager.NetworkCallback networkCallback;

    public WIFIHelper(Context context, IWIFIListener listener) {
        this.context = context.getApplicationContext();
        this.listener = new WeakReference<>(listener);
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isEnable() {
        return wifiManager.isWifiEnabled();
    }

    // 打开WIFI
    public void enable() {
        if (!isEnable()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void disable() {
        if (isEnable()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public void startScan() {
        wifiManager.startScan();
    }

    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        filter.addAction(WIFIDefine.CONFIGURED_NETWORKS_CHANGED_ACTION);
        filter.addAction(WIFIDefine.LINK_CONFIGURATION_CHANGED_ACTION);
        context.registerReceiver(this.broadcastReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        this.context.unregisterReceiver(broadcastReceiver);
    }

    public int addNetwork(WifiConfiguration configuration) {
        return wifiManager.addNetwork(configuration);
    }

    public void disconnect() {
        wifiManager.disconnect();
    }

    public void enableNetwork(int networkId) {
        wifiManager.enableNetwork(networkId, true);
    }

    public void disableNetwork(int networkId) {
        wifiManager.disableNetwork(networkId);
    }

    public boolean removeNetwork(int networkId) {
        return wifiManager.removeNetwork(networkId);
    }

    public List<ScanResult> getScanResults() {
        return this.wifiManager.getScanResults();
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        return this.wifiManager.getConfiguredNetworks();
    }

    public WifiInfo getConnectionInfo() {
        return this.wifiManager.getConnectionInfo();
    }

    /**
     * 获取当前网络信息
     */
    public NetworkInfo getActiveNetworkInfo() {
        return this.connectivityManager.getActiveNetworkInfo();
    }

    /**
     * 获取设备的mac地址（android4.4以上可用）
     *
     * @return
     */
    public String getMachineHardwareAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface element = interfaces.nextElement();
                if ("wlan0".equals(element.getName())) {
                    return ByteUtils.bytes2HexString(element.getHardwareAddress(), false, ":");
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据 NetworkId 获取 WifiConfiguration 信息
     */
    public WifiConfiguration getWifiConfigurationBySsid(String ssid) {
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (EmptyUtils.isEmpty(configs)) {
            return null;
        }
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals(ssid)) {
                return config;
            }
        }
        return null;
    }

    /**
     * 根据 NetworkId 获取 WifiConfiguration 信息
     */
    public WifiConfiguration getWifiConfigurationByNetworkId(int networkId) {
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (networkId == config.networkId) {
                    return config;
                }
            }
        }
        return null;
    }

    public void registNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EmptyUtils.isNotEmpty(this.networkCallback)) {
                return;
            }
            this.networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.e("WIFI", "onAvailable: " + network);
                    if (EmptyUtils.isNotEmpty(listener)) {
                        listener.get().onNetworkAvailabled(network);
                    }
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.e("WIFI", "onLost: " + network);
                    if (EmptyUtils.isNotEmpty(listener)) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EmptyUtils.isNotEmpty(this.networkCallback)) {
                this.connectivityManager.unregisterNetworkCallback(this.networkCallback);
                this.networkCallback = null;
            }
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    if (EmptyUtils.isNotEmpty(listener)) {
                        listener.get().onScanResultsAvailabled();
                    }
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (state) {
                        case WifiManager.WIFI_STATE_ENABLED: {
                            if (EmptyUtils.isNotEmpty(listener)) {
                                listener.get().onWIFIEnabled();
                            }
                            break;
                        }
                        case WifiManager.WIFI_STATE_DISABLED: {
                            if (EmptyUtils.isNotEmpty(listener)) {
                                listener.get().onWIFIDisabled();
                            }
                            break;
                        }
                    }
                    break;

                case WifiManager.NETWORK_STATE_CHANGED_ACTION: {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (EmptyUtils.isNotEmpty(listener)) {
                        listener.get().onNetworkStateChanged(info);
                    }
                    break;
                }
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION: {
                    int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    if (error == WifiManager.ERROR_AUTHENTICATING) {
                        if (EmptyUtils.isNotEmpty(listener)) {
                            listener.get().onAuthenticatingError();
                        }
                    }
                    break;
                }
                case WIFIDefine.LINK_CONFIGURATION_CHANGED_ACTION:
                case WIFIDefine.CONFIGURED_NETWORKS_CHANGED_ACTION:
                    if (EmptyUtils.isNotEmpty(listener)) {
                        listener.get().onConfiguredNetworksChanged();
                    }
                    break;
            }
        }
    };
}