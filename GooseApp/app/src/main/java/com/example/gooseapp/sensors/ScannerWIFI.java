package com.example.gooseapp.sensors;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.gooseapp.service.BackgroundService;

import java.util.List;


public class ScannerWIFI {

    private Context context;
    private BackgroundService backgroundService;
    //WIFI
    private boolean isWiFiScanning = false;
    private WifiManager wifiManager;

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Controlla se l'intento ricevuto è per i risultati della scansione Wi-Fi
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    isWiFiScanning = false;
                    handleWifiSuccess();
                } else {
                    handleWifiFailure();
                }
            }
            isWiFiScanning = false;
        }
    };


    public ScannerWIFI(Context context, BackgroundService backgroundService){
        //Inizializzazione contesto e canale comunicazione
        this.context = context;
        this.backgroundService = backgroundService;
        // Inizializzazione manager
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.i("GOOSE SIGNAL WIFI", "Scanner WIFI initialized");
    }

    // misura wifi
    public void backgroundMeasureWifi(){
        if(!isWiFiScanning){
            // Check if WiFi is enabled
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                // Wait a bit for WiFi to initialize
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("GOOSE SIGNAL WIFI", "Sleep interrupted", e);
                }
            }

            // Check all required permissions
            if (checkWifiPermissions()) {
                wifiManager.startScan();
                isWiFiScanning = true;
                Log.i("GOOSE SIGNAL WIFI", "start scanning Wi-Fi");
            } else {
                Log.e("GOOSE SIGNAL WIFI", "Missing required permissions");
            }
        }
    }

    private boolean checkWifiPermissions() {
        return PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED &&
               PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED &&
               PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
               PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
    }

    private void handleWifiSuccess() {
        if (checkWifiPermissions()) {
            Log.i("GOOSE SIGNAL WIFI", "successfully scanned Wi-Fi");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("GOOSE SIGNAL WIFI", "I don' have wifi permission");
                return;
            }
            List<ScanResult> results = wifiManager.getScanResults();
            if (results != null && !results.isEmpty()) {
                backgroundService.manageWifiScans(results);
            } else {
                Log.w("GOOSE SIGNAL WIFI", "Scan successful but no results found");
            }
        } else {
            Log.e("GOOSE SIGNAL WIFI", "WIFI Permissions not granted");
        }
    }

    private void handleWifiFailure() {
        Log.e("GOOSE SIGNAL WIFI", "Wi-Fi scan failed. Possible reasons:");
        if (!wifiManager.isWifiEnabled()) {
            Log.e("GOOSE SIGNAL WIFI", "- WiFi is disabled");
        }
        if (!checkWifiPermissions()) {
            Log.e("GOOSE SIGNAL WIFI", "- Missing permissions");
        }
        Log.e("GOOSE SIGNAL WIFI", "- Scan throttling (too frequent scans)");
        Log.e("GOOSE SIGNAL WIFI", "- Hardware/system issues");
    }

    public void stopWifi(){
        context.unregisterReceiver(wifiReceiver); // Deregistra il ricevitore Wi-Fi
    }


    // da cancellare
    private BroadcastReceiver wifiReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Controlla se l'intento ricevuto è per i risultati della scansione Wi-Fi
            Log.i("GOOSE SCANS", "inside wifiReceiver2");

            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    isWiFiScanning = false;
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("GOOSE SIGNAL WIFI", "I don' have wifi permission");
                        return;
                    }
                    List<ScanResult> results = wifiManager.getScanResults();
                    backgroundService.manageWifiScans2(results);
                } else {
                    handleWifiFailure();
                }
            }
            isWiFiScanning = false;
        }
    };

    public void wifi(){
        Log.i("GOOSE SCANS", "inside wifi()");
       // this.context.registerReceiver(wifiReceiver2, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       // wifiManager.startScan();
    }
}
