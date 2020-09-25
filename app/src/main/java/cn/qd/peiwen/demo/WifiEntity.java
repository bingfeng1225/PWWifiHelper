package cn.qd.peiwen.demo;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import cn.qd.peiwen.wifi.PWWifiDefine;

public class WifiEntity {
    private String password;
    private ScanResult result;

    public WifiEntity(ScanResult result) {
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
        return (PWWifiDefine.SECURITY_NONE != this.getSecurity());
    }

    public String getQuotedSSID() {
        return "\"" + result.SSID + "\"";
    }

    public int getSecurity() {
        if (result.capabilities.contains("WEP")) {
            return PWWifiDefine.SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return PWWifiDefine.SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return PWWifiDefine.SECURITY_EAP;
        }
        return PWWifiDefine.SECURITY_NONE;
    }

    private int getPSKType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PWWifiDefine.PSK_WPA_WPA2;
        } else if (wpa2) {
            return PWWifiDefine.PSK_WPA2;
        } else if (wpa) {
            return PWWifiDefine.PSK_WPA;
        } else {
            return PWWifiDefine.PSK_UNKNOWN;
        }
    }

    public WifiConfiguration generateNetworkConfig(){
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = getQuotedSSID();
        int security = this.getSecurity();
        switch (security) {
            case PWWifiDefine.SECURITY_NONE:
                configuration.wepKeys[0] = "\"" + "\"";
                configuration.wepTxKeyIndex = 0;
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case PWWifiDefine.SECURITY_WEP:
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
            case PWWifiDefine.SECURITY_PSK:
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    configuration.preSharedKey = password;
                } else {
                    configuration.preSharedKey = '"' + password + '"';
                }
                break;
            case PWWifiDefine.SECURITY_EAP:
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
        if (obj instanceof WifiEntity) {
            return this.getSsid().equals(((WifiEntity) obj).getSsid());
        }
        return false;
    }
}
