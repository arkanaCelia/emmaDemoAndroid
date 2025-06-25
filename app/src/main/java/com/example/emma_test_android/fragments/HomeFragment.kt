package com.example.emma_test_android.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.emma_test_android.R
import com.example.emma_test_android.application.EmmaTestApplication
import com.example.emma_test_android.managers.EmmaCallbackManager
import com.example.emma_test_android.managers.EmmaEventManager
import io.emma.android.EMMA
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMAInAppRequest

/**
 * Pantalla principal con todos los botones de prueba
 */

class HomeFragment : Fragment(R.layout.fragment_home) {

    // region Variables

    //Session
    private lateinit var btnSession: Button
    private lateinit var txtSession: TextView

    //Notification permission
    private lateinit var btnNotificationPermission: Button
    private lateinit var txtNotificationPermission: TextView

    //Register user
    private lateinit var btnRegisterUser: Button
    private lateinit var txtInfoRegisterUser: TextView

    //Login user
    private lateinit var btnLoginUser: Button
    private lateinit var txtInfoLogin: TextView

    //Events and extras
    private lateinit var btnTrackEvent: Button
    private lateinit var btnAddUserTag: Button

    //In-App Communication
    private lateinit var btnShowNativeAd: Button
    private lateinit var btnShowStartView: Button
    private lateinit var btnShowAdBall: Button
    private lateinit var btnShowBanner: Button
    private lateinit var btnShowStrip: Button
    private lateinit var btnShowCoupons: Button

    //Dialog de dos editText
    private lateinit var edtxtUser: EditText
    private lateinit var edtxtEmail: EditText

    //Orders and products (start, add y track order)
    private lateinit var txtInfoOrders: TextView
    private lateinit var btnStartOrder: Button
    private lateinit var btnAddProduct: Button
    private lateinit var btnTrackOrder: Button
    private lateinit var btnCancelOrder: Button

    //Language
    private lateinit var spnLanguage: Spinner
    private lateinit var btnLanguage: Button

    //Other EMMA Methods
    private lateinit var callbackManager: EmmaCallbackManager // clase con las interfaces implementadas
    private lateinit var btnGetUserInfo: Button
    private lateinit var btnGetUserID: Button
    private lateinit var btnGetInstallAttribution: Button
    private lateinit var btnGetDeviceID: Button
    private lateinit var btnSetCustomerId: Button

    //endregion
    //region onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //--- Session
        btnSession = view.findViewById(R.id.btn_startSession)
        txtSession = view.findViewById(R.id.txt_dch_session)

        btnSession.setOnClickListener {
            EmmaTestApplication.startSession(requireContext())
        }

        //--- Notification Permission
        txtNotificationPermission = view.findViewById(R.id.txt_2NotificationsPermission)
        btnNotificationPermission = view.findViewById(R.id.btn_RequestNotificationsPermission)
        btnNotificationPermission.setOnClickListener {
            EMMA.getInstance().requestNotificationPermission()
        }

        //--- Register User
        btnRegisterUser = view.findViewById(R.id.btn_RegisterUser)
        txtInfoRegisterUser = view.findViewById(R.id.txt_dch_RegisterUser)

        btnRegisterUser.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_twoedittext, null)
            edtxtUser = dialogView.findViewById(R.id.edtxt_user)
            edtxtEmail = dialogView.findViewById(R.id.edtxt_email)

            AlertDialog.Builder(requireContext())
                .setTitle("Register User")
                .setView(dialogView)
                .setPositiveButton("Register") { dialog, _ ->
                    val userId = edtxtUser.text.toString()
                    val email = edtxtEmail.text.toString()

                    //EMMARegisterUser
                    EMMA.getInstance().registerUser(userId, email)

                    txtInfoRegisterUser.text = "User registered"
                    btnRegisterUser.isEnabled = false
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        //--- LogIn user
        btnLoginUser = view.findViewById(R.id.btn_LogInUser)
        txtInfoLogin = view.findViewById(R.id.txt_dch_LogInUser)
        btnLoginUser.setOnClickListener{
            val dialogView = layoutInflater.inflate(R.layout.dialog_twoedittext, null)

            val editTextUser = dialogView.findViewById<EditText>(R.id.edtxt_user)
            val editTextEmail = dialogView.findViewById<EditText>(R.id.edtxt_email)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("LogIn User")
                .setView(dialogView)
                .setPositiveButton("LogIn") { dialog, _ ->
                    val userId = editTextUser.text.toString()
                    val email = editTextEmail.text.toString()

                    //EMMALoginUser
                    EMMA.getInstance().loginUser(userId, email)

                    txtInfoLogin.text = "User logged in"
                    btnLoginUser.isEnabled = false

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        //--- Events and extras
        btnTrackEvent = view.findViewById(R.id.btn_TrackEvent)
        btnAddUserTag = view.findViewById(R.id.btn_AddUserTagTag)

        btnTrackEvent.setOnClickListener{
            EmmaEventManager.trackCustomEvent(
                "4137f3a184bb5de977da43f90546b1f8", // token de evento_prueba
                mapOf("attribute_example" to "attribute_valor"),
                "customID"
            )
            Toast.makeText(requireContext(), "Evento personalizado trackeado", Toast.LENGTH_LONG).show()
        }

        btnAddUserTag.setOnClickListener{
            val tags = ArrayMap<String, String>()
            tags["TAG"] = "TAG"
            tags["VIP_USER"] = "true"
            EMMA.getInstance().trackExtraUserInfo(tags)
            Toast.makeText(requireContext(), "Etiquetas añadidas a usuario: ${tags}", Toast.LENGTH_LONG).show()
        }

        //--- In-App Communication
        // IMPORTANTE: configurados hasta el 31 de julio de 2025

        // NativeAd
        btnShowNativeAd = view.findViewById(R.id.btn_ShowNativeAd)
        btnShowNativeAd.setOnClickListener{
            // se abre fragment dedicado
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NativeAdsFragment())
                .addToBackStack(null)
                .commit()
        }

        // StartView
        btnShowStartView = view.findViewById(R.id.btn_ShowStartView)
        btnShowStartView.setOnClickListener{
            val startViewRequest = EMMAInAppRequest(EMMACampaign.Type.STARTVIEW)
            EMMA.getInstance().getInAppMessage(startViewRequest)
        }

        // AdBall
        btnShowAdBall = view.findViewById(R.id.btn_ShowAdball)
        btnShowAdBall.setOnClickListener{
            val adBallRequest = EMMAInAppRequest(EMMACampaign.Type.ADBALL)
            EMMA.getInstance().getInAppMessage(adBallRequest)
        }

        // Banner
        btnShowBanner = view.findViewById(R.id.btn_ShowBanner)
        btnShowBanner.setOnClickListener{
            val bannerRequest = EMMAInAppRequest(EMMACampaign.Type.BANNER)
            EMMA.getInstance().getInAppMessage(bannerRequest)
        }

        // Strip
        btnShowStrip = view.findViewById(R.id.btn_ShowStrip)
        btnShowStrip.setOnClickListener{
            val stripRequest = EMMAInAppRequest(EMMACampaign.Type.STRIP)
            EMMA.getInstance().getInAppMessage(stripRequest)
        }

        // Coupons
        btnShowCoupons = view.findViewById(R.id.btn_ShowCoupons)
        btnShowCoupons.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponsFragment())
                .addToBackStack(null)
                .commit()
        }

        //--- Orders and products
        txtInfoOrders = view.findViewById(R.id.txt_dch_OrdersAndProducts)
        btnStartOrder = view.findViewById(R.id.btn_StartOrder)
        btnAddProduct = view.findViewById(R.id.btn_AddProduct)
        btnTrackOrder = view.findViewById(R.id.btn_TrackOrder)
        btnCancelOrder = view.findViewById(R.id.btn_CancelOrder)

        btnAddProduct.isEnabled = false
        btnTrackOrder.isEnabled = false
        btnCancelOrder.isEnabled = false

        val extras = emptyMap<String, String>() // para el ejemplo creamos una variable vacía
        btnStartOrder.setOnClickListener{
            EMMA.getInstance().startOrder(
                "<ORDER_ID>",
                "<CUSTOMER_ID>",
                10.0F,
                extras
            )
            txtInfoOrders.text = "Order started"
            btnStartOrder.isEnabled = false
            btnAddProduct.isEnabled = true
        }

        btnAddProduct.setOnClickListener{
            EMMA.getInstance().addProduct(
                "PRODUCT_ID",
                "PRODUCT_NAME",
                1.0F,
                10.0F
            )
            txtInfoOrders.text = "Product added"
            btnAddProduct.isEnabled = false
            btnTrackOrder.isEnabled = true
        }

        btnTrackOrder.setOnClickListener{
            EMMA.getInstance().trackOrder()
            txtInfoOrders.text = "Tracking order"
            btnTrackOrder.isEnabled = false
            btnCancelOrder.isEnabled = true
        }

        btnCancelOrder.setOnClickListener{
            EMMA.getInstance().cancelOrder("<ORDER_ID>")

            txtInfoOrders.text = ""
            btnStartOrder.isEnabled = true
            btnCancelOrder.isEnabled = false
        }

        //--- Language
        spnLanguage = view.findViewById(R.id.spn_language)
        btnLanguage = view.findViewById(R.id.btn_SetLanguage)
        //Config del spinner
        val languages = resources.getStringArray(R.array.language_list)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnLanguage.adapter = adapter
        //Clic del boton
        btnLanguage.setOnClickListener {
            val selectedLanguage = spnLanguage.selectedItem.toString()
            val languageCode = when (selectedLanguage) {
                "Spanish" -> "es"
                "English" -> "en"
                "French"  -> "fr"
                "German"  -> "de"
                else -> "en"
            }

            EMMA.getInstance().setUserLanguage(languageCode)
        }

        //--- Otras comprobaciones
        callbackManager = EmmaCallbackManager

        //Para obtener info de la notificación antes de llamar a onPushOpen
        EMMA.getInstance().getNotificationInfo()

        //Para comprobar cuando se abre la app desde la notificación
        EMMA.getInstance().checkForRichPushUrl()

        //Para obtener la info del perfil de usuario en la app (getUserInfo)
        btnGetUserInfo = view.findViewById(R.id.btn_getUserInfo)
        btnGetUserInfo.setOnClickListener{
            EMMA.getInstance().getUserInfo()
        }

        //Para obtener el ID de usuario (getUserID)
        btnGetUserID = view.findViewById(R.id.btn_getUserID)
        btnGetUserID.setOnClickListener{
            EMMA.getInstance().getUserID()
        }

        //Para obtener info de atribución de la instalación (getInstallAttributionInfo)
        btnGetInstallAttribution = view.findViewById(R.id.btn_getInstallAttributionInfo)
        btnGetInstallAttribution.setOnClickListener{
            EMMA.getInstance().getInstallAttributionInfo { attribution ->
                if (attribution != null) {
                    Log.d("EMMA_Attribution", "Attribution Info: ${attribution}")
                } else {
                    Log.d("EMMA_Attribution", "No attribution data available.")
                }
            }
        }

        //Para obtener la info del dispositivo
        btnGetDeviceID = view.findViewById(R.id.btn_DeviceID)
        btnGetDeviceID.setOnClickListener{
            EMMA.getInstance().getDeviceId(callbackManager)
        }

        //Para introducir un customer ID independientemente del login/registro
        btnSetCustomerId = view.findViewById(R.id.btn_setCustomerId)
        btnSetCustomerId.setOnClickListener{
            val dialogView = layoutInflater.inflate(R.layout.dialog_oneedittext, null)
            val editTextUser = dialogView.findViewById<EditText>(R.id.edtxt_customer)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Set Customer ID")
                .setView(dialogView)
                .setPositiveButton("Set") { dialog, _ ->
                    val customerId = editTextUser.text.toString()

                    //EMMASetCustomerID
                    EMMA.getInstance().setCustomerId(customerId)
                    Log.d("EMMA_CustomerID", "Customer ID set as ${customerId}")

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }


    }

    // endregion

    override fun onResume() {
        super.onResume()

        // Session
        if (EMMA.getInstance().isSdkStarted) {
            btnSession.isEnabled = false
            txtSession.text = "Session Started"
        } else {
            btnSession.isEnabled = true
            txtSession.text = "No session"
        }

        // NotificationPermission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            txtNotificationPermission.text = "Permission status: GRANTED"
        } else {
            txtNotificationPermission.text = "Permission status: DENIED"
        }
    }

}