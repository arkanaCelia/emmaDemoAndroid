package com.example.emma_test_android.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.emma_test_android.BuildConfig
import com.example.emma_test_android.activities.MainActivity
import com.example.emma_test_android.R
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import io.emma.android.EMMA
import io.emma.android.model.EMMAPushOptions

/**
 * Activity Application que inicializa el SDK de EMMA
 */

class EmmaTestApplication : Application() {

    companion object {
        fun startSession(context: Context) {
            val configuration = EMMA.Configuration.Builder(context)
                .setSessionKey("3DBF55A0B7BC550874edfbac6d5dc49f8")
                .setDebugActive(BuildConfig.DEBUG)
                .build()
            // añadir .trackScreenEvents(false) para desactivar el envío de pantallas
            // añadir .setShortPowlinkDomains si se usa un tracker que no es dominio de emma
            // añadir .setPowlinkDomains ...
            // añadir .setWebServiceUrl("https://www.your_proxy_url") para cambiar la URL de API EMMA (p.e. proxies),

            EMMA.getInstance().startSession(configuration)
        }
    }

    override fun onCreate() {
        super.onCreate()

        // --- startSession
        startSession(this)

        // --- inicio del sistema de PUSH
        val pushOpt = EMMAPushOptions.Builder(
            MainActivity::class.java, // actividad que desea que se abra cuando reciba el push
            R.drawable.notification_icon, // opcional: icono que se muestra
        )
            .setNotificationColor(ContextCompat.getColor(this, R.color.yellow))
            .setNotificationChannelName("Mi custom channel")
            .build()

        EMMA.getInstance().startPushSystem(pushOpt)

    }
}