package cn.qd.peiwen.wifi.listener;

import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;

public interface IPWNetworkListener {
    void onNetworkLost(Network network);

    void onNetworkAvailabled(Network network);

    void onNetworkStateChanged(NetworkInfo info);

    void onSupplicantStateChanged(SupplicantState state, int error);
}
