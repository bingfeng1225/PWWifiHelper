package cn.qd.peiwen.wifi;

class WifiTools {
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


    public static String bytes2HexString(byte[] data) {
        return bytes2HexString(data, false);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag) {
        return bytes2HexString(data, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        return bytes2HexString(data, 0, data.length, hexFlag, separator);
    }

    public static String bytes2HexString(byte[] data, int offset, int len) {
        return bytes2HexString(data, offset, len, false);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag) {
        return bytes2HexString(data, offset, len, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        if (offset < 0 || offset > data.length - 1) {
            throw new IllegalArgumentException("The offset index out of bounds");
        }
        if (len < 0 || offset + len > data.length) {
            throw new IllegalArgumentException("The len can not be < 0 or (offset + len) index out of bounds");
        }
        String format = "%02X";
        if (hexFlag) {
            format = "0x%02X";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = offset; i < offset + len; i++) {
            buffer.append(String.format(format, data[i]));
            if (separator == null) {
                continue;
            }
            if (i != (offset + len - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }
}
