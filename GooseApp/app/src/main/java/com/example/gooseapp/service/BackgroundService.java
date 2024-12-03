package com.example.gooseapp.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gooseapp.R;
import com.example.gooseapp.activity.HomeActivity;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;
import com.example.gooseapp.sensors.ScannerBLE;
import com.example.gooseapp.sensors.ScannerWIFI;

import java.util.ArrayList;
import java.util.List;

/*
* Ogni 3 secondi effettua richiesta backend
* Invia notifiche se:
* - entrato in un area senza permesso
* - entrato ma non ha i dispositivi necessari
* - uscito ma non ha i dispositivi necessari !!
* */
public class BackgroundService extends Service {

    private static NotificationManagerCompat notificationManager;
    private static NotificationCompat.Builder builderGeneral;
    private static final String CHANNEL_ID = "GOOSE";

    private ScannerWIFI scannerWIFI;
    private ScannerBLE scannerBLE;
    private GooseRequest gooseRequest;
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
        notificationIntent.setAction("Alert users");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builderGeneral = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.goose_transparent)
                .setContentTitle("Quack")
                .setContentText("Starting security localization check")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, builderGeneral.build());


        //inizializza variabili
        scannerWIFI = new ScannerWIFI(this, this);
        scannerBLE = new ScannerBLE(this, this);
        gooseRequest = new GooseRequest(this, this);
        scannerWIFI.backgroundMeasureWifi();
        return Service.START_STICKY;
    }

    private void createNotificationChannel() {
        //check permission
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    @SuppressLint("MissingPermission")
    public static void sendBasicNotification(String title, String text){
        notificationManager.notify(1, builderGeneral.setContentText(text).setContentTitle(title).build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerWIFI.stopWifi();
    }



    //handle WIFI
    public void manageWifiScans(List<ScanResult> results){
        if (!results.isEmpty()) {
            List<ScannedWifiEntity> scannedList = new ArrayList<ScannedWifiEntity>();
            for (ScanResult scanResult : results) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ScannedWifiEntity swe = new ScannedWifiEntity(
                            scanResult.getWifiSsid(),
                            scanResult.getApMldMacAddress(),
                            scanResult.level
                    );
                    scannedList.add(swe);
                }
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int userid = sharedPreferences.getInt(String.valueOf(R.string.session), -1);
            gooseRequest.sendWifiScan(scannedList, userid);
        } else {
            System.out.println("No Wi-Fi networks found.");
        }
    }

    //handle BLE
    public void neededBLE(){
        scannerBLE.backgroundMeasureBLE();
    }

    public void manageBLEScans(List<ScannedBLEEntity> results) {
        if (results == null || results.isEmpty()) {
            Log.i("GOOSE SIGNAL BLE RESULTS", "non ho ottenuto risultati ble");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int userId = sharedPreferences.getInt(String.valueOf(R.string.session), -1);
            int roomId = sharedPreferences.getInt("room_in", -1);
            Log.i("GOOSE SIGNAL BLE RESULTS", "Room found: "+roomId);
            gooseRequest.sendBLEScan(null, userId, roomId);
        } else {
            Log.i("GOOSE SIGNAL BLE RESULTS", "Trovati " + results.size() + " dispositivi BLE");
            for (ScannedBLEEntity device : results) {
                Log.i("GOOSE SIGNAL BLE RESULTS", device.toString());
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int userId = sharedPreferences.getInt(String.valueOf(R.string.session), -1);
            int roomId = sharedPreferences.getInt(String.valueOf(R.string.room_in), -1);
            Log.i("GOOSE SIGNAL BLE RESULTS", "Room found: "+roomId);
            gooseRequest.sendBLEScan(results, userId, roomId);
        }
    }

    //salva le informazioni ritornate nelle sharedPreferences in modo che siano accessibili altrove
    public void saveData(int roomId, String roomName){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("room_name", roomName).putInt("room_in", roomId).apply();
        String roomname = sharedPreferences.getString("room_name", "pippo");
        int roomid = sharedPreferences.getInt(String.valueOf(R.string.room_in), -1);
        Log.i("GOOSE SIGNAL BLE RESULTS", "Room found: "+roomid +"-"+ roomname);


    }
}
