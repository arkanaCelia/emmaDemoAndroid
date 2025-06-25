package com.example.emma_test_android.managers

import android.util.Log
import io.emma.android.EMMA
import io.emma.android.interfaces.EMMARequestListener
import io.emma.android.model.EMMAEventRequest

/**
 * Object que encapsula la creación y el envío del evento,
 * y permite pasar los parámetros desde fuera
 */

object EmmaEventManager {

    /**
     * trackCustomerEvent = envía un evento personalizado a EMMA
     *
     * @param eventToken Token del evento configurado en EMMA.
     * @param attributes Mapa opcional de atributos personalizados.
     * @param customId Identificador personalizado opcional para la petición.
     */

    fun trackCustomEvent(
        eventToken: String,
        attributes: Map<String, String>? = null,
        customId: String? = null) {

        val eventRequest = EMMAEventRequest(eventToken)
        attributes?.let {
            eventRequest.attributes = it // Optional: custom attributes
        }
        customId?.let {
            eventRequest.customId = it // Optional: custom id for request delegate
        }

        // Optional: request status listener
        eventRequest.requestListener = object : EMMARequestListener {
            override fun onStarted(requestId: String) {
                Log.d("EMMA_request", "Request started with id: $requestId")
            }

            override fun onSuccess(requestId: String, result: Boolean) {
                Log.d("EMMA_request", "Request $requestId succeeded: $result")
            }

            override fun onFailed(requestId: String) {
                Log.e("EMMA_request", "Request $requestId failed")
            }
        }

        EMMA.getInstance().trackEvent(eventRequest)
    }
}