package com.example.gooseapp.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gooseapp.R;
import com.example.gooseapp.activity.HomeActivity;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;
import com.example.gooseapp.sensors.ScannerBLE;
import com.example.gooseapp.sensors.ScannerWIFI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static NotificationCompat.Builder builderSound;
    private static NotificationCompat.Builder builderLight;
    private static CameraManager cameraManager;

    private static final String CHANNEL_ID = "GOOSE";
    private static final String CHANNEL_SOUND_ID = "GOOSE_SOUND";
    private static final String CHANNEL_LIGHT_ID = "GOOSE_LIGHT";

    private static ScannerWIFI scannerWIFI;
    private ScannerBLE scannerBLE;
    private GooseRequest gooseRequest;
    private android.os.Handler handler;
    private static final int SCAN_INTERVAL = 5000; // 3 seconds in milliseconds
    private Runnable scanRunnable;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //handle notifications
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setAction("Alert users");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        
        //basic notification
        builderGeneral = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.goose_transparent)
                .setContentTitle("Quack")
                .setContentText("Starting security localization check")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //sound notification
        builderSound = new NotificationCompat.Builder(this, CHANNEL_SOUND_ID)
                .setSmallIcon(R.drawable.goose_transparent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        //light notification
        builderLight = new NotificationCompat.Builder(this, CHANNEL_LIGHT_ID)
                .setSmallIcon(R.drawable.goose_transparent)
                
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(1, builderGeneral.build());


        //inizializza variabili
        scannerWIFI = new ScannerWIFI(this, this);
        scannerBLE = new ScannerBLE(this, this);
        gooseRequest = new GooseRequest(this, this);
        
        // Initialize handler and runnable for periodic scanning
        /*handler = new android.os.Handler();
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                scannerWIFI.backgroundMeasureWifi();
                  handler.postDelayed(this, SCAN_INTERVAL);
            }
        };*/
        
        // Start periodic scanning
        //handler.post(scanRunnable);
        
        return Service.START_STICKY;
    }

    private void createNotificationChannel() {
        //check permission
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            
            // Default channel
            CharSequence name = getString(R.string.notif_title);
            String description = getString(R.string.notif_text);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            // Sound channel
            NotificationChannel soundChannel = new NotificationChannel(CHANNEL_SOUND_ID, 
                "Important Alerts", NotificationManager.IMPORTANCE_HIGH);
            soundChannel.setDescription("Notifications with mandatory sound");
            soundChannel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, 
                new android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            soundChannel.enableVibration(true);
            
            // Light channel
            NotificationChannel lightChannel = new NotificationChannel(CHANNEL_LIGHT_ID,
                "Emergency Alerts", NotificationManager.IMPORTANCE_HIGH);
            lightChannel.setDescription("Notifications with light signal");
            lightChannel.enableLights(true);
            lightChannel.setLightColor(android.graphics.Color.RED);
            
            // Register all channels
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(soundChannel);
            notificationManager.createNotificationChannel(lightChannel);
        }
    }

    @SuppressLint("MissingPermission")
    public static void sendBasicNotification(String title, String text){
        notificationManager.notify(1, builderGeneral.setContentText(text).setContentTitle(title).build());
    }

    @SuppressLint("MissingPermission")
    public static void sendSoundNotification(String title, String text) {
        notificationManager.notify(2, builderSound.setContentText(text)
                .setContentTitle(title)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .build());
    }

    @SuppressLint("MissingPermission")
    public static void sendLightNotification(String title, String text) {
        notificationManager.notify(3, builderLight.setContentText(text)
                .setContentTitle(title)
                .build());
        turnOnFlashlight();
        // Turn off the flashlight after 3 seconds
        new Handler().postDelayed(() -> turnOffFlashlight(), 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(scanRunnable);
        }
        turnOffFlashlight();
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int userId = sharedPreferences.getInt(String.valueOf(R.string.session), -1);
            int roomId = sharedPreferences.getInt("room_in", -1);
            Log.i("GOOSE SIGNAL BLE RESULTS", "Sending: "+roomId + " + user: "+userId);
            gooseRequest.sendBLEScan(results, userId, roomId);
        }
    }

    //salva le informazioni ritornate nelle sharedPreferences in modo che siano accessibili altrove
    public void saveData(int roomId, String roomName){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("room_name", roomName).putInt("room_in", roomId).apply();
        String roomname = sharedPreferences.getString("room_name", "pippo");
        int roomid = sharedPreferences.getInt("room_in", -1);
        Log.i("GOOSE SIGNAL BLE RESULTS", "Room found: "+roomid +"-"+ roomname);
    }


    private static void turnOnFlashlight() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            Log.e("GOOSE LIGHT", "Failed to access camera", e);
        }
    }
    
    private static void turnOffFlashlight() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            Log.e("GOOSE LIGHT", "Failed to access camera", e);
        }
    }







    /// da cancellare
    private static String room;
    public static void submitWifiScan(String selectedRoom) {
        room = selectedRoom;
        Log.i("GOOSE SCANS", "inside submitWifi, with " + room);
        scannerWIFI.wifi();
    }


    public void manageWifiScans2(List<ScanResult> results){
        Log.i("GOOSE SCANS", "inside manageWifiScans2");
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
            sendBasicNotification("Sending to Goose Request", "sending data +"+this.room);
            gooseRequest.sendWIFIscanFingerprint(scannedList, this.room);
        } else {
            System.out.println("No Wi-Fi networks found.");
        }
    }
}
