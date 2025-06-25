package com.example.emma_test_android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.emma.android.EMMA
import android.content.Intent
import android.util.Log
import com.example.emma_test_android.R
import com.example.emma_test_android.fragments.DeepLinkTestFragment
import com.example.emma_test_android.fragments.HomeFragment
import io.emma.android.interfaces.EMMANotificationInterface
import io.emma.android.interfaces.EMMAUserInfoInterface
import io.emma.android.model.EMMAPushCampaign
import org.json.JSONObject


/**
 * Activity principal de la app.
 * Gestiona configuracion inicial, etc...
 */

class MainActivity :
    AppCompatActivity(),
    EMMANotificationInterface,
    EMMAUserInfoInterface{

    // region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        // cuando CustomDeepLinkActivity lance MainActivity con putExtra sabrá qué fragment cargar
        handleNavigationIntent(intent)

    }
    // endregion

    // region Otras funciones

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        EMMA.getInstance().onNewNotification(intent, true)
        handleNavigationIntent(intent)

    }

    // navegacion entre pantallas
    private fun handleNavigationIntent(intent: Intent?) {
        val navigateTo = intent?.getStringExtra("navigateTo")
        if (navigateTo != null) {
            when (navigateTo) {
                "home" -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
                "test" -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DeepLinkTestFragment())
                    .commit()
            }
        }
    }

    //EMMANotificationInterface
    override fun onPushOpen(pushCampaign: EMMAPushCampaign) {
        Log.d("EMMA_noti", "Notificación recibida")
    }

    //EMMAGetUserInfoInterface
    override fun OnGetUserInfo(userInfo: JSONObject?) {
        userInfo?.let {
            Log.d("USER_DATA", "User data: $it")
        }
    }

    override fun OnGetUserID(id: Int) {
        Log.d("USER_ID", "User ID: $id")
    }

    //endregion
}