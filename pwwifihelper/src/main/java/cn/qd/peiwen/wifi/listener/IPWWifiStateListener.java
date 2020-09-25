package cn.qd.peiwen.wifi.listener;

import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;

public interface IPWWifiStateListener {
    void onWIFIEnabled();

    void onWIFIDisabled();
}
