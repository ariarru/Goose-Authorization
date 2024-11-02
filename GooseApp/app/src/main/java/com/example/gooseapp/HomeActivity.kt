package com.example.gooseapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val getLocationButton = findViewById<Button>(R.id.getLocationButton)

        getLocationButton.setOnClickListener {
            // Avvio di una coroutine per eseguire la funzione `getPosition()`
            Toast.makeText(this, "Scanning wifi", Toast.LENGTH_SHORT).show()
        }
    }
}
