package cn.qd.peiwen.wifi;

import android.net.Network;
import android.net.NetworkInfo;

public interface IPWWifiListener {
    void onWIFIEnabled();
    void onWIFIDisabled();
    void onAuthenticatingError();
    void onScanResultsAvailabled();
    void onConfiguredNetworksChanged();
    void onNetworkStateChanged(NetworkInfo info);

    void onNetworkLost(Network network);
    void onNetworkAvailabled(Network network);
}
