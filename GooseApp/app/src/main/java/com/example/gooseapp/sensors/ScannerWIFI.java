package com.example.gooseapp.sensors;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import com.example.gooseapp.R;
import com.example.gooseapp.service.BackgroundService;
import com.example.gooseapp.service.GooseRequest;

import java.util.List;

public class ScannerWIFI {

    private Context context;
    private BackgroundService backgroundService;
    //WIFI
    private boolean isWiFiScanning = false;
    private WifiManager wifiManager;
    private Handler wifiHandler = new Handler();

    //check ogni 3 secondi
    private static final long RESTART_SCAN_PERIOD = 3 * 1000 *60;
    private Runnable wifiScanRunnable = new Runnable() {
        @Override
        public void run() {
            // Avvia la scansione Wi-Fi
            backgroundMeasureWifi();
            // Pianifica la prossima scansione tra 3 secondi
            wifiHandler.postDelayed(this, RESTART_SCAN_PERIOD);
        }
    };
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

        // Avvia loop di scansione
        wifiHandler.post(wifiScanRunnable);
    }

    // misura wifi
    public void backgroundMeasureWifi(){
        if(!isWiFiScanning){
            wifiManager.startScan();
            isWiFiScanning= true;
            Log.i("BACKGROUND GOOSE SIGNAL", "start scanning Wi-Fi");
        }
    }

    private void handleWifiSuccess() {
        if (PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED) {
            List<ScanResult> results = wifiManager.getScanResults();
            backgroundService.manageWifiScans(results);

        } else {
            System.out.println("Permissions not granted.");
        }
    }

    private void handleWifiFailure() {
        System.out.println("Wi-Fi scan failed. Retrying or handling the failure.");
        System.out.println("----------------------------------------------");

        Log.e("BACKGROUND GOOSE SIGNAL", "Wi-Fi scan failed. Please try again.");
    }

    private void stopWifi(){
        wifiHandler.removeCallbacks(wifiScanRunnable);  // Ferma il loop Wi-Fi
        context.unregisterReceiver(wifiReceiver); // Deregistra il ricevitore Wi-Fi
        Log.i("GOOSE QUACK", "background service destroyed");
    }
}
