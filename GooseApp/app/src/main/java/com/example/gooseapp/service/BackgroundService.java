package com.example.gooseapp.service;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;

import com.example.gooseapp.R;
import com.example.gooseapp.activity.HomeActivity;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import java.util.List;

/*
* Ogni 3 secondi effettua richiesta backend
* Invia notifiche se:
* - entrato in un area senza permesso
* - entrato ma non ha i dispositivi necessari
* - uscito ma non ha i dispositivi necessari !!
* */
public class BackgroundService extends Service {

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builderGeneral;
    private static final String CHANNEL_ID = "GOOSE";

    //check ogni 3 secondi
    int time = 3 * 1000 *60;

    //WIFI
    private boolean isWiFiScanning = false;
    private WifiManager wifiManager;
    private Handler wifiHandler = new Handler();
    private Runnable wifiScanRunnable = new Runnable() {
        @Override
        public void run() {
            // Avvia la scansione Wi-Fi
            backgroundMeasureWifi();
            // Pianifica la prossima scansione tra 3 secondi
            wifiHandler.postDelayed(this, time);
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


    //BLE
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean isBLEScanning;
    private Handler handler = new Handler();
    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.i("SIGNAL BLE RESULT", String.valueOf(result));
                    isBLEScanning = false;
                    isWiFiScanning = false;
                   // bluetoothLeScanner.stopScan(leScanCallback);
                }
            };

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("GOOSE RANDEVOUZ", "i've started on command");
        //handle notifications
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setAction("Measure Signal");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builderGeneral = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.goose_transparent)
                .setContentTitle("Quack")
                .setContentText("...")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, builderGeneral.build());

        // Inizializzazione manager
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        // Avvia loop di scansione
        wifiHandler.post(wifiScanRunnable);


        return Service.START_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notif_title);
            String description = getString(R.string.notif_text);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    //misura ble
    private void backgroundMeasureBLE(){

        if (!isBLEScanning) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                return;
            }
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isBLEScanning = false;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            isWiFiScanning = true;
            isBLEScanning = true;
            Log.i("BACKGROUND SIGNAL", "measuring BLE");
            bluetoothLeScanner.startScan(leScanCallback);
        }
    }

    // misura wifi
    private void backgroundMeasureWifi(){
        if(!isWiFiScanning){
            wifiManager.startScan();
            isWiFiScanning= true;
            Log.i("BACKGROUND SIGNAL", "start scanning Wi-Fi");
        }
    }

    private void handleWifiSuccess() {
        if (PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED) {
            List<ScanResult> results = wifiManager.getScanResults();
            if (!results.isEmpty()) {
                for (ScanResult scanResult : results) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ScannedWifiEntity swe = new ScannedWifiEntity(
                                scanResult.getWifiSsid(),
                                scanResult.getApMldMacAddress(),
                                scanResult.level
                        );
                        notificationManager.notify(1, builderGeneral.setContentText("Scanned Wi-Fi successfully").build());
                        System.out.println("----------------------------------------------");
                        backgroundMeasureBLE();
                    }
                }
            } else {
                System.out.println("No Wi-Fi networks found.");
            }
        } else {
            System.out.println("Permissions not granted.");
        }
    }

    private void handleWifiFailure() {
        System.out.println("Wi-Fi scan failed. Retrying or handling the failure.");
        System.out.println("----------------------------------------------");

        Log.e("BACKGROUND SIGNAL", "Wi-Fi scan failed. Please try again.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wifiHandler.removeCallbacks(wifiScanRunnable);  // Ferma il loop Wi-Fi
        unregisterReceiver(wifiReceiver); // Deregistra il ricevitore Wi-Fi
        Log.i("GOOSE QUACK", "background service destroyed");
    }

}
