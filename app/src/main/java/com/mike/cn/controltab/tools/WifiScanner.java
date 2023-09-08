package com.mike.cn.controltab.tools;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.RequiresApi;

import java.util.List;

public class WifiScanner {
    private Context context;
    private WifiManager wifiManager;
    private WifiScanListener wifiScanListener;

    public WifiScanner(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public interface WifiScanListener {
        void onScanResultsAvailable(List<ScanResult> scanResults);
    }

    public void setWifiScanListener(WifiScanListener listener) {
        this.wifiScanListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestWifiScan() {
        if (wifiManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Android 9及更高版本需要ACCESS_FINE_LOCATION权限
                return;
            }

            // 检查是否已启用Wi-Fi
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            // 开始扫描WiFi
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            final Handler handler = new Handler();
            new Thread(() -> {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                wifiManager.startScan();
                handler.post(() -> {
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    if (wifiScanListener != null) {
                        wifiScanListener.onScanResultsAvailable(scanResults);
                    }
                });
            }).start();
        }
    }
}
