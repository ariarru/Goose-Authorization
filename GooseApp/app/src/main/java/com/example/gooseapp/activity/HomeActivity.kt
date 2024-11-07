package com.example.gooseapp.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.example.gooseapp.R
import com.example.gooseapp.sensors.ScannedWifiEntity
import com.example.gooseapp.sensors.SensorHelper
import com.example.gooseapp.service.BackgroundService


class HomeActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private var size = 0
    private lateinit var results: List<ScanResult>
    private val arrayList = ArrayList<ScannedWifiEntity>()
    private var isReceiverRegistered = false

    private val sensorHelper = SensorHelper()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //chiedi permessi
        if(!sensorHelper.hasWifiPermission(applicationContext, this)) {
            Toast.makeText(this, "I don't have any wifi permission, try again", Toast.LENGTH_SHORT).show()
        }
        if(!sensorHelper.hasLocationPermission(applicationContext, this)){
            Toast.makeText(applicationContext, "I don't have any location permission, try again", Toast.LENGTH_LONG).show()
        }
        if(!sensorHelper.hasBluetoothPermission(applicationContext, this)){
            Toast.makeText(applicationContext, "I don't have any bluetooth permission, try again", Toast.LENGTH_LONG).show()
        }

        val startServiceIntent = Intent(this, BackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startServiceIntent)
            println("Inviato intent servizio background")
            println("----------------------------------------------")

        }

        //definisci receiver
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        //definisci wifi
        wifiManager = getSystemService(WIFI_SERVICE) as WifiManager

        //prendi riferimento bottone
        val getLocationButton = findViewById<Button>(R.id.getLocationButton)
        //definisci codice bottone
        getLocationButton.setOnClickListener {
            scan()
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun scan() {
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "wifi is disabled.. You need to enable it in order to use the application", Toast.LENGTH_LONG).show()
            // richiedi permesso attivazione wifi
        }
        if (!isReceiverRegistered) {
            isReceiverRegistered = true
            wifiManager.startScan()
            println("scanning started")
            println("----------------------------------------------")

            Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show()
        }
    }


    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Controlla se l'intento ricevuto è per i risultati della scansione Wi-Fi
            println("sono qui dentro")
            println("----------------------------------------------")

            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    handleScanSuccess()
                } else {
                    handleScanFailure()
                }
            }
            isReceiverRegistered = false
        }
    }

    private fun handleScanSuccess() {
        if (PermissionChecker.checkCallingOrSelfPermission(applicationContext, Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED) {
            results = wifiManager.scanResults
            if (results.isNotEmpty()) {
                for (scanResult in results) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        val swe = ScannedWifiEntity(
                            scanResult.wifiSsid,
                            scanResult.apMldMacAddress,
                            scanResult.level
                        )
                        println("Scan successful: $swe")
                    }
                }
            } else {
                println("No Wi-Fi networks found.")
                println("----------------------------------------------")
            }
        } else {
            println("Permissions not granted.")
            println("----------------------------------------------")

        }
    }

    private fun handleScanFailure() {
        println("Wi-Fi scan failed. Retrying or handling the failure.")
        println("----------------------------------------------")

        // Puoi decidere di rilanciare la scansione o gestire il fallimento diversamente
        Toast.makeText(this, "Wi-Fi scan failed. Please try again.", Toast.LENGTH_SHORT).show()
    }





}
