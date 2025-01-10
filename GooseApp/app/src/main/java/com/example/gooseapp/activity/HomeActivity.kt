package com.example.gooseapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gooseapp.R
import com.example.gooseapp.sensors.SensorHelper
import com.example.gooseapp.service.BackgroundService

class HomeActivity : AppCompatActivity() {

    private val sensorHelper = SensorHelper()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var roomSpinner: Spinner
    private lateinit var submitScanButton: Button

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Toast.makeText(this, "Background measuring is not used right now", Toast.LENGTH_SHORT).show()

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
            /* ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            94
        )*/
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
        val newText = "Welcome "+ sharedPreferences.getString(R.string.username.toString(), "")
        userText.setText(newText)

        //prendi riferimento bottone
        val getLocationButton = findViewById<Button>(R.id.getLocationButton)
        //definisci codice bottone
        getLocationButton.setOnClickListener {
            scan()
        }

        // Room Spinner Setup
        roomSpinner = findViewById(R.id.roomSpinner)
        val rooms = arrayOf("Select Room", "E1", "E2", "Uffici", "Garden", "Dipartimento")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rooms)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roomSpinner.adapter = adapter

        // Submit Scan Button
        submitScanButton = findViewById(R.id.submitScanButton)
        submitScanButton.setOnClickListener {
            submitWifiScan()
        }
    }

    private fun submitWifiScan(){
        val selectedRoom = roomSpinner.selectedItem.toString()
        if ("Select Room" == selectedRoom) {
            Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Sending scan command", Toast.LENGTH_SHORT).show()
        BackgroundService.submitWifiScan(selectedRoom)

    }

    //TODO: metti observable object
    @RequiresApi(Build.VERSION_CODES.R)
    private fun scan() {
        //shared preferences
        val roomText = findViewById<TextView>(R.id.room_text)
        val value = sharedPreferences.getString("room_name", null)
        Log.i("GOOSE ACTIVITY ROOM", "sei nella stanza con valore "+ value)
        var newText = "I'm measuring"
        if(value != null){
            newText = "I've located you in room " + value.toString()
        }
        roomText.setText(newText)
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
