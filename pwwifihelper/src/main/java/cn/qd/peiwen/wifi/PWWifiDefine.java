package cn.qd.peiwen.wifi;

public class PWWifiDefine {
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    public static final int PSK_UNKNOWN = 0;
    public static final int PSK_WPA = 1;
    public static final int PSK_WPA2 = 2;
    public static final int PSK_WPA_WPA2 = 3;

    public static final int MAX_RSSI = -55;
    public static final int MIN_RSSI = -100;
    public static final int RSSI_LEVELS = 4;

    public static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
    public static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
}
