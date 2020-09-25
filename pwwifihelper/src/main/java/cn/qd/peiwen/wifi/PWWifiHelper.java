package cn.qd.peiwen.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import cn.qd.peiwen.wifi.listener.IPWConnectiveListener;
import cn.qd.peiwen.wifi.listener.IPWNetworkListener;
import cn.qd.peiwen.wifi.listener.IPWWifiScanListener;
import cn.qd.peiwen.wifi.listener.IPWWifiSignalListener;
import cn.qd.peiwen.wifi.listener.IPWWifiStateListener;
import cn.qd.peiwen.wifi.receiver.ConnectiveReceiver;
import cn.qd.peiwen.wifi.receiver.NetworkReceiver;
import cn.qd.peiwen.wifi.receiver.WifiScanReceiver;
import cn.qd.peiwen.wifi.receiver.WifiSignalReceiver;
import cn.qd.peiwen.wifi.receiver.WifiStateReceiver;
import cn.qd.peiwen.wifi.tools.WifiTools;

public class PWWifiHelper {
    private Context context;
    private WifiManager wifiManager;
    private NetworkReceiver networkReceiver;
    private WifiScanReceiver wifiScanReceiver;
    private WifiStateReceiver wifiStateReceiver;
    private WifiSignalReceiver wifiSignalReceiver;
    private ConnectiveReceiver connectiveReceiver;
    private ConnectivityManager connectivityManager;

    public PWWifiHelper(Context context) {
        this.context = context.getApplicationContext();
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    // 打开WIFI
    public void enable() {
        if (isDisabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void disable() {
        if (isEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public boolean isEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public boolean isDisabled() {
        return (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED);
    }

    public void startScan() {
        wifiManager.startScan();
    }

    public int addNetwork(WifiConfiguration configuration) {
        int network = wifiManager.addNetwork(configuration);
        return network;
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

    /**
     * 根据 NetworkId 获取 WifiConfiguration 信息
     */
    public WifiConfiguration getWifiConfigurationBySsid(String ssid) {
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals(ssid)) {
                return config;
            }
        }
        return null;
    }

    public WifiConfiguration generateWifiConfiguration(String ssid, String password, int security) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = ssid;
        switch (security) {
            case PWWifiDefine.SECURITY_WEP:
                configuration.preSharedKey = "\"" + password + "\"";
                configuration.hiddenSSID = true;
                configuration.wepTxKeyIndex = 0;
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                break;
            case PWWifiDefine.SECURITY_WPA:
            case PWWifiDefine.SECURITY_WPA2:
                configuration.hiddenSSID = true;
                configuration.preSharedKey = "\"" + password + "\"";
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                break;
            default:
                configuration.wepKeys[0] = "\"" + "\"";
                configuration.wepTxKeyIndex = 0;
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }
        return configuration;
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

    public void unregisterNetworkReceiver() {
        if (this.networkReceiver != null) {
            this.networkReceiver.unregistNetworkCallback();
            context.unregisterReceiver(this.networkReceiver);
            this.networkReceiver = null;
        }
    }

    public void registerNetworkReceiver(IPWNetworkListener listener) {
        if (this.networkReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            this.networkReceiver = new NetworkReceiver(this.connectivityManager, listener);
            this.networkReceiver.registNetworkCallback();
            context.registerReceiver(this.networkReceiver, filter);
        }
    }

    public void unregisterWifiScanReceiver() {
        if (this.wifiScanReceiver != null) {
            context.unregisterReceiver(this.wifiScanReceiver);
            this.wifiScanReceiver = null;
        }
    }

    public void registerWifiScanReceiver(IPWWifiScanListener listener) {
        if (this.wifiScanReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            this.wifiScanReceiver = new WifiScanReceiver(listener);
            context.registerReceiver(this.wifiScanReceiver, filter);
        }
    }

    public void unregisterWifiStateReceiver() {
        if (this.wifiStateReceiver != null) {
            context.unregisterReceiver(this.wifiStateReceiver);
            this.wifiStateReceiver = null;
        }
    }

    public void registerWifiStateReceiver(IPWWifiStateListener listener) {
        if (this.wifiStateReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            this.wifiStateReceiver = new WifiStateReceiver(listener);
            context.registerReceiver(this.wifiStateReceiver, filter);
        }
    }

    public void unregisterWifiSignalReceiver() {
        if (this.wifiSignalReceiver != null) {
            context.unregisterReceiver(this.wifiSignalReceiver);
            this.wifiSignalReceiver = null;
        }
    }

    public void registerWifiSignalReceiver(IPWWifiSignalListener listener) {
        if (this.wifiSignalReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            this.wifiSignalReceiver = new WifiSignalReceiver(listener);
            context.registerReceiver(this.wifiSignalReceiver, filter);
        }
    }

    public void unregisterConnectiveReceiver() {
        if (this.connectiveReceiver != null) {
            context.unregisterReceiver(this.connectiveReceiver);
            this.connectiveReceiver = null;
        }
    }

    public void registerConnectiveReceiver(IPWConnectiveListener listener) {
        if (this.connectiveReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.connectiveReceiver = new ConnectiveReceiver(this.connectivityManager, listener);
            context.registerReceiver(this.connectiveReceiver, filter);
        }
    }
}