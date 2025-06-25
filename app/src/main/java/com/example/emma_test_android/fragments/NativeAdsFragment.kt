package com.example.emma_test_android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.emma_test_android.R
import io.emma.android.EMMA
import io.emma.android.enums.CommunicationTypes
import io.emma.android.interfaces.EMMABatchNativeAdInterface
import io.emma.android.interfaces.EMMAInAppMessageInterface
import io.emma.android.interfaces.EMMANativeAdInterface
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMANativeAd
import io.emma.android.model.EMMANativeAdRequest
import androidx.core.net.toUri
import android.util.Log

class NativeAdsFragment :
    Fragment(R.layout.fragment_native_ads),
    EMMANativeAdInterface,
    EMMAInAppMessageInterface,
    EMMABatchNativeAdInterface {

    //variables
    private lateinit var txtNativeAd: TextView
    private lateinit var txtNativeAdBody: TextView
    private lateinit var imgNativeAd: ImageView
    private lateinit var btnNativeAd: Button

    private var currentNativeAd: EMMANativeAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtNativeAd = view.findViewById(R.id.txt_nativeAd)
        txtNativeAdBody = view.findViewById(R.id.txt_nativeAdBody)
        imgNativeAd = view.findViewById(R.id.img_nativeAd)
        btnNativeAd = view.findViewById(R.id.btn_nativeAd)

        // se pide anuncio desde aquí
        val templateId = "plantilla-prueba-celia"
        val nativeAdRequest = EMMANativeAdRequest().apply { this.templateId = templateId }
        nativeAdRequest.isBatch = true // batch activado (varios ads con esta plantilla)
        EMMA.getInstance().getInAppMessage(nativeAdRequest, this)

        btnNativeAd.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // trackear un clic en un AD
        imgNativeAd.setOnClickListener{
            if (currentNativeAd == null) {
                Toast.makeText(context, "Aún no hay anuncio cargado", Toast.LENGTH_SHORT).show()
            }
            else {
                // enviar el clic
                EMMA.getInstance().sendInAppClick(CommunicationTypes.NATIVE_AD, currentNativeAd!!)

                // abrir una web, por ejemplo
                val url = "https://emma.io"  // o la URL que quieras abrir
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
            }
        }

    }

    // callback cuando EMMA entrega el anuncio
    override fun onReceived(nativeAd: EMMANativeAd) {
        activity?.runOnUiThread { // para que se ejecute desde el hilo principal
            Log.d("EMMA_nativead", "Ejecutando onRecieved")
            currentNativeAd = nativeAd

            val content = nativeAd.nativeAdContent
            txtNativeAd.text = content["Title"]?.fieldValue
            txtNativeAdBody.text = content["Body"]?.fieldValue
            val imageUrl = content["Main picture"]?.fieldValue
            Glide.with(requireContext())
                .load(imageUrl)
                .into(imgNativeAd)

            // envía impresión cuando se muestra el anuncio
            EMMA.getInstance().sendInAppImpression(CommunicationTypes.NATIVE_AD, nativeAd)

            // se envía la apertura
            EMMA.getInstance().openNativeAd(nativeAd)

        }
    }

    override fun onBatchReceived(nativeAds: MutableList<EMMANativeAd>) {
        activity?.runOnUiThread {
            Log.d("EMMA_batch", "Anuncios recibidos en batch: ${nativeAds.size}")
            nativeAds.forEachIndexed { index, ad ->
                val title = ad.nativeAdContent["Title"]?.fieldValue
                Log.d("EMMA_batch", "Ad $index: $title")
            }


            if (nativeAds.isNotEmpty()) {
                val nativeAd = nativeAds.random()  // eleccion de nativead
                currentNativeAd = nativeAd  // se guarda para clics

                val content = nativeAd.nativeAdContent

                txtNativeAd.text = content["Title"]?.fieldValue
                txtNativeAdBody.text = content["Body"]?.fieldValue
                val imageUrl = content["Main picture"]?.fieldValue
                Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imgNativeAd)

                EMMA.getInstance().sendInAppImpression(CommunicationTypes.NATIVE_AD, nativeAd)

                // se envía la apertura
                EMMA.getInstance().openNativeAd(nativeAd)
            }
        }
    }

    override fun onShown(campaign: EMMACampaign?) {}
    override fun onHide(campaign: EMMACampaign?) {}
    override fun onClose(campaign: EMMACampaign?) {}

}



