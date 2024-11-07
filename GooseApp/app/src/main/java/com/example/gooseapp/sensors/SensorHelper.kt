package com.example.gooseapp.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.gooseapp.activity.HomeActivity

@SuppressLint("NotConstructor")
class SensorHelper {

    public final val PERMISSION_FINE_LOCATION = 99
    public final val PERMISSION_COARSE_LOCATION = 98
    public final val PERMISSION_WIFI = 97
    public final val PERMISSION_CHANGE_WIFI = 96
    public final val PERMISSION_BLE = 95


    public fun hasWifiPermission(context: Context, activity: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissions(activity, arrayOf(Manifest.permission.CHANGE_WIFI_STATE), PERMISSION_CHANGE_WIFI)
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_WIFI_STATE), PERMISSION_WIFI)

            false
        } else {
            true
        }
    }

    public fun hasLocationPermission(context: Context, activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_COARSE_LOCATION)

            return false
        }
        return true
    }

    public fun hasBluetoothPermission(context: Context, activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN), PERMISSION_BLE)
            return false
        }
        return true
    }


}