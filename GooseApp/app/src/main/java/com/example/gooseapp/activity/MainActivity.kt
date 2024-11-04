package com.example.gooseapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gooseapp.LoginError
import com.example.gooseapp.R
import com.example.gooseapp.service.BackgroundService
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MainActivity : AppCompatActivity() {

    // LiveData per controllare se l'utente è autenticato
    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    val supabase = createSupabaseClient(
        supabaseUrl = "https://cdutvkhtyqcsorzvmuzg.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNkdXR2a2h0eXFjc29yenZtdXpnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjM1NDMwOTYsImV4cCI6MjAzOTExOTA5Nn0.rE0B4Uvj6083r_QgGJy_KtizUfjY2bO8SMcmZFf3LRI"
    ) {
        this.install(Postgrest)
    }

    public final suspend fun login(username: String, password: String) {
        // Verifica se username o password sono vuoti
        if (username.isBlank() || password.isBlank()) {
            throw LoginError.EMPTY_FIELDS
        }
            try {
                val credentials =  buildJsonObject {
                    put("_username", username)
                    put("_password", password)
                }

                val result = supabase.postgrest.rpc("login", credentials)
                println("ritorna: ${result.data}")
                println("----------------------------------------------")


                if(result.data.toBoolean()){

                    Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
                    val startServiceIntent = Intent(
                        this,
                        BackgroundService::class.java
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(startServiceIntent)
                    }

                    val goToHome = Intent(this, HomeActivity::class.java)
                    startActivity(goToHome)
                }

            } catch (e: Exception) {
                // Gestione degli errori
                println("Errore durante il login: ${e.message}")
                showAlert("Errore di Login", e.message.toString())
            }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val title: TextView = findViewById(R.id.title)
        val gooseImage: ImageView = findViewById(R.id.gooseImage)
        val usernameField: EditText = findViewById(R.id.username)
        val passwordField: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)

        // Imposta il listener per il bottone di login
        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            // Chiama la funzione di login del ViewModel
            runBlocking {
                try{
                    login(username, password)
                } catch (e: LoginError) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Osserva lo stato di autenticazione dal ViewModel
       isAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                // Passa alla Home se autenticato
                Toast.makeText(this, "Accesso eseguito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

