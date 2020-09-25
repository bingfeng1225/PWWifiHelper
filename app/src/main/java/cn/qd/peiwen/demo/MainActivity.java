package cn.qd.peiwen.demo;


import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import cn.qd.peiwen.logger.PWLogger;
import cn.qd.peiwen.wifi.listener.IPWConnectiveListener;
import cn.qd.peiwen.wifi.listener.IPWNetworkListener;
import cn.qd.peiwen.wifi.listener.IPWWifiSignalListener;
import cn.qd.peiwen.wifi.listener.IPWWifiScanListener;
import cn.qd.peiwen.wifi.listener.IPWWifiStateListener;
import cn.qd.peiwen.wifi.PWWifiHelper;
import cn.qd.peiwen.wifi.tools.WifiTools;

public class MainActivity extends AppCompatActivity implements IPWWifiSignalListener, IPWWifiStateListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, IPWWifiScanListener, IPWNetworkListener, IPWConnectiveListener {
    private PWWifiHelper helper;
    private Switch wifi_switch;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.helper = new PWWifiHelper(this);
        this.helper.registerNetworkReceiver(this);
        this.helper.registerWifiScanReceiver(this);
        this.helper.registerWifiStateReceiver(this);
        this.helper.registerWifiSignalReceiver(this);
        this.helper.registerConnectiveReceiver(this);

        this.wifi_switch = findViewById(R.id.wifi_switch);
        this.wifi_switch.setChecked(this.helper.isEnabled());
        this.wifi_switch.setOnCheckedChangeListener(this);
        this.recyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_switch:

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            this.helper.enable();
        } else {
            this.helper.disable();
        }
    }

    @Override
    public void onWIFIEnabled() {
        PWLogger.error("onWIFIEnabled");
        this.helper.startScan();
    }

    @Override
    public void onWIFIDisabled() {
        PWLogger.error("onWIFIDisabled");
    }

    @Override
    public void onScanResultsAvailabled() {
        PWLogger.error("onScanResultsAvailabled");
    }

    @Override
    public void onNetworkStateChanged(NetworkInfo info) {
        PWLogger.error("onNetworkStateChanged:" + info.getExtraInfo() + ",State:" + info.getDetailedState());
    }

    @Override
    public void onSupplicantStateChanged(SupplicantState state, int error) {
        WifiInfo info = this.helper.getConnectionInfo();
        PWLogger.error("onSupplicantStateChanged SSID:" + info.getSSID() + "State:" + state + ",error:" + error);
    }

    @Override
    public void onNetworkLost(Network network) {
        PWLogger.error("onNetworkLost");
    }

    @Override
    public void onNetworkAvailabled(Network network) {
        PWLogger.error("onNetworkAvailabled");
    }

    @Override
    public void onWifiSignalChanged(int rssi) {
        int level = WifiTools.calculateWifiLevel(rssi,5);
        PWLogger.error("onWifiSignalChanged:" + level);
    }

    @Override
    public void onNetworkConnected() {
        PWLogger.error("onNetworkConnected");
    }

    @Override
    public void onNetworkDisconnected() {
        PWLogger.error("onNetworkDisconnected");
    }
}
