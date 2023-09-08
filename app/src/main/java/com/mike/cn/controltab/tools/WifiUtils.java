package com.mike.cn.controltab.tools;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;

public class WifiUtils {
    private final Context context;
    private final WifiManager wifiManager;

    public WifiUtils(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // 检查是否支持Wi-Fi
    public boolean isWifiSupported() {
        return wifiManager != null;
    }

    // 检查Wi-Fi是否已启用
    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    // 启用Wi-Fi
    public boolean enableWifi() {
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        }
        return false;
    }

    // 禁用Wi-Fi
    public boolean disableWifi() {
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(false);
        }
        return false;
    }

    // 扫描Wi-Fi网络并返回扫描结果列表
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<ScanResult> scanWifiNetworks() {
        if (wifiManager != null) {
            // 开始扫描
            wifiManager.startScan();
            // 获取扫描结果
            return wifiManager.getScanResults();
        }
        return null;
    }

    // 连接到指定的Wi-Fi网络
    public boolean connectToWifi(String ssid, String password, WifiSecurityType securityType) {
        if (wifiManager != null) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + ssid + "\"";

            switch (securityType) {
                case OPEN:
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    break;
                case WEP:
                    wifiConfig.wepKeys[0] = "\"" + password + "\"";
                    wifiConfig.wepTxKeyIndex = 0;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    break;
                case WPA:
                case WPA2:
                    wifiConfig.preSharedKey = "\"" + password + "\"";
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    break;
            }

            int networkId = wifiManager.addNetwork(wifiConfig);
            if (networkId != -1) {
                // 断开当前连接
                wifiManager.disconnect();
                // 连接到新配置的Wi-Fi网络
                wifiManager.enableNetwork(networkId, true);
                return wifiManager.reconnect();
            }
        }
        return false;
    }

    // 获取当前连接的Wi-Fi信息
    public WifiInfo getConnectedWifiInfo() {
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }
        return null;
    }

    // 枚举Wi-Fi网络的安全性类型
    public enum WifiSecurityType {
        OPEN, // 开放网络
        WEP,  // WEP加密
        WPA,  // WPA加密
        WPA2  // WPA2加密
    }
}
