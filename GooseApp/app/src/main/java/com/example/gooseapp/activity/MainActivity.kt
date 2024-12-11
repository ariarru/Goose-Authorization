package com.example.gooseapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gooseapp.LoginError
import com.example.gooseapp.R
import com.example.gooseapp.service.GooseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        with (sharedPreferences.edit()){
            putInt(R.string.session.toString(), -1)
            putString(R.string.username.toString(), "")
            putBoolean(R.string.logged.toString(), false)
            apply()
        }

        val title: TextView = findViewById(R.id.title)
        val gooseImage: ImageView = findViewById(R.id.gooseImage)
        val usernameField: EditText = findViewById(R.id.username)
        val passwordField: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)

        // Imposta il listener per il bottone di login
        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()
            usernameField.setText("")
            passwordField.setText("")
            // Chiama la funzione di login del ViewModel
            runBlocking {
                try{
                    login(username, password)
                } catch (e: LoginError) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun login(username: String, password: String) {
        // Check for empty fields
        if (username.isBlank() || password.isBlank()) {
            throw LoginError.EMPTY_FIELDS
        }
        // Create JSON request body
        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        // Send request
        val gooseRequest = GooseRequest(this, this)
        gooseRequest.login(jsonBody);

    }

    public fun handleLoginResults(jsonResult: JSONObject){
        if(jsonResult.getBoolean("success")){
            with (sharedPreferences.edit()) {
                putInt(R.string.session.toString(), jsonResult.getInt("user_id"))
                putString(R.string.username.toString(), jsonResult.getString("username"))
                putBoolean(R.string.logged.toString(), true)
                apply()
            }

            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
            val goToHome = Intent(this, HomeActivity::class.java)
            startActivity(goToHome)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Error logging in")
                .setMessage(jsonResult.getString("error"))
                .setPositiveButton("OK", null)
                .show()
        }
    }

}
