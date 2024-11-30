package com.example.gooseapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gooseapp.R
import com.example.gooseapp.sensors.ScannerWIFI
import com.example.gooseapp.sensors.SensorHelper
import com.example.gooseapp.service.BackgroundService


class HomeActivity : AppCompatActivity() {

    //TODO:
    // - metti ScannerWifi come istanza
    // - richiama gestione notifiche?


    private val sensorHelper = SensorHelper()

    private lateinit var sharedPreferences : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Abilita il pulsante "Up" nella barra superiore
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_logout_24)

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
        if(!sensorHelper.hasNotificationPermission(applicationContext, this)){
            Toast.makeText(applicationContext, "I need notification permit to continue", Toast.LENGTH_LONG).show()
        }

        val startServiceIntent = Intent(this, BackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startServiceIntent)
            println("Inviato intent servizio background")
            println("----------------------------------------------")

        }



        //shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val userText = findViewById<TextView>(R.id.user_text)
        println(sharedPreferences.getString(R.string.username.toString(), ""))
        val newText = "Benvenuta/o "+ sharedPreferences.getString(R.string.username.toString(), "")
        userText.setText(newText)

        //prendi riferimento bottone
        val getLocationButton = findViewById<Button>(R.id.getLocationButton)
        //definisci codice bottone
        getLocationButton.setOnClickListener {
            scan()
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun scan() {
       //prendi solo le sharedPreferences, se non ci sono metti avviso

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                with( sharedPreferences.edit()){
                    putInt(R.string.session.toString(), -1)
                    putString(R.string.username.toString(), "")
                    putBoolean(R.string.logged.toString(), false)
                    apply()
                }
                onBackPressed() // Torna alla schermata precedente
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





}
