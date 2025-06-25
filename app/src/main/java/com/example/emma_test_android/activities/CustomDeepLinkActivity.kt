package com.example.emma_test_android.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class CustomDeepLinkActivity: Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //si llega un intent con datos
        Log.d("EMMA_deeplink", "CustomDeepLinkActivity lanzada con intent: $intent")
        val data: Uri? = intent?.data
        if (data != null) {
            processDeepLink(data)
        }

        //cerrar inmediatamente después de procesar
        finish()
    }

    private fun processDeepLink(uri: Uri) {
        Log.d("EMMA_deeplink", "URI recibido: $uri")
        Log.d("EMMA_deeplink", "Host: ${uri.host}, Path: ${uri.path}")
        //comparamos por path no por host
        when (uri.path) {
            "/home" -> {
                Toast.makeText(this, "Ejecutando /home", Toast.LENGTH_SHORT).show()
                goHome()
            }
            "/test" -> {
                Toast.makeText(this, "Ejecutando /test", Toast.LENGTH_SHORT).show()
                goTest()
            }
            else -> {
                Toast.makeText(this, "Deeplink no reconocido", Toast.LENGTH_SHORT).show()
                Log.d("EMMA_deeplink", "Deeplink no reconocido.")
                goHome()
            }
            //más casos
        }
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        val data: Uri? = intent?.data
        if (data != null) {
            processDeepLink(data)
        }
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateTo", "home")
        startActivity(intent)
    }

    private fun goTest() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateTo", "test")
        startActivity(intent)
    }

}