package cn.qd.peiwen.pwwifihelper;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public class WIFIEntity {
    private String password;
    private ScanResult result;

    public WIFIEntity(ScanResult result) {
        this.result = result;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRssi(){
        return result.level;
    }

    public String getSsid() {
        return result.SSID;
    }

    public boolean isSecured() {
        return (WIFIDefine.SECURITY_NONE != this.getSecurity());
    }

    public String getQuotedSSID() {
        return "\"" + result.SSID + "\"";
    }

    public int getSecurity() {
        if (result.capabilities.contains("WEP")) {
            return WIFIDefine.SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return WIFIDefine.SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return WIFIDefine.SECURITY_EAP;
        }
        return WIFIDefine.SECURITY_NONE;
    }

    private int getPSKType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return WIFIDefine.PSK_WPA_WPA2;
        } else if (wpa2) {
            return WIFIDefine.PSK_WPA2;
        } else if (wpa) {
            return WIFIDefine.PSK_WPA;
        } else {
            return WIFIDefine.PSK_UNKNOWN;
        }
    }

    public int getSignalLevel() {
        if (result.level <= WIFIDefine.MIN_RSSI) {
            return 0;
        } else if (result.level >= WIFIDefine.MAX_RSSI) {
            return WIFIDefine.RSSI_LEVELS - 1;
        } else {
            float inputRange = (WIFIDefine.MAX_RSSI - WIFIDefine.MIN_RSSI);
            float outputRange = (WIFIDefine.RSSI_LEVELS - 1);
            float level = (result.level - WIFIDefine.MIN_RSSI) * outputRange * 1.0f / inputRange;
            return Math.round(level);
        }
    }

    public WifiConfiguration generateNetworkConfig(){
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = getQuotedSSID();
        int security = this.getSecurity();
        switch (security) {
            case WIFIDefine.SECURITY_NONE:
                configuration.wepKeys[0] = "\"" + "\"";
                configuration.wepTxKeyIndex = 0;
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case WIFIDefine.SECURITY_WEP:
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int length = password.length();
                if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                    configuration.wepKeys[0] = password;
                } else {
                    configuration.wepKeys[0] = '"' + password + '"';
                }
                break;
            case WIFIDefine.SECURITY_PSK:
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    configuration.preSharedKey = password;
                } else {
                    configuration.preSharedKey = '"' + password + '"';
                }
                break;
            case WIFIDefine.SECURITY_EAP:
                // 暂时忽略
                break;
        }
        return configuration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof WIFIEntity) {
            return this.getSsid().equals(((WIFIEntity) obj).getSsid());
        }
        return false;
    }
}
