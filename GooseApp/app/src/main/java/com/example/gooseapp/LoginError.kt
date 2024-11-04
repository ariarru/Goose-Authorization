package com.example.gooseapp

class LoginError(message: String) : Exception(message) {
    companion object {
        val EMPTY_FIELDS = LoginError("Inserisci i dati corretti")
    }
}
