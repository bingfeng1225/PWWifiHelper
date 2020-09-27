package cn.qd.peiwen.wifi.entity;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;

import cn.qd.peiwen.wifi.PWWifiDefine;

public class WifiEntity {
    private ScanResult result;

    private String address;
    private boolean saved = false;
    private boolean active = false;
    private SupplicantState status = null;


    public WifiEntity(ScanResult result) {
        this.result = result;
    }

    public int getLevel() {
        return result.level;
    }

    public boolean isSecurity() {
        return (PWWifiDefine.SECURITY_NONE != this.getSecurity());
    }

    public String getSsid() {
        return result.SSID;
    }

    public String getQuotedSSID() {
        return "\"" + result.SSID + "\"";
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SupplicantState getStatus() {
        return status;
    }

    public void setStatus(SupplicantState status) {
        this.status = status;
    }

    public int getSecurity() {
        if (result.capabilities.toUpperCase().contains("EAP")) {
            return PWWifiDefine.SECURITY_EAP;
        } else if (result.capabilities.toUpperCase().contains("WEP")) {
            return PWWifiDefine.SECURITY_WEP;
        } else if (result.capabilities.toUpperCase().contains("WPA2")) {
            return PWWifiDefine.SECURITY_WPA2;
        } else if (result.capabilities.toUpperCase().contains("WPA")) {
            return PWWifiDefine.SECURITY_WPA;
        }
        return PWWifiDefine.SECURITY_NONE;
    }

    public String getEncryption() {
        String capabilities = this.result.capabilities.toUpperCase();
        if (capabilities.contains("WPA2-") && capabilities.contains("WPA-")) {
            return "WPA/WPA2";
        } else if (capabilities.contains("WPA2-")) {
            return "WPA2";
        } else if (capabilities.contains("WPA-")) {
            return "WPA";
        }
        return null;
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
