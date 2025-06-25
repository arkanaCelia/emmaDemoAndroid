package com.example.emma_test_android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.example.emma_test_android.R
import io.emma.android.interfaces.EMMACouponsInterface
import io.emma.android.EMMA
import io.emma.android.enums.CommunicationTypes
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMACoupon
import io.emma.android.model.EMMAInAppRequest
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

/**
 * Fragment para probar los cupones
 * Muestra la imagen del cupón, las veces que ha sido canjeado, y un botón para canjear y volver a home
 */

class CouponsFragment :
    Fragment(R.layout.fragment_coupons),
    EMMACouponsInterface {

    // variables
    private lateinit var btnVolverCoupons: Button
    private lateinit var btnCanjearCoupon: Button
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgCoupon: ImageView
    private lateinit var txtCouponsLeft: TextView

    private var currentCoupon: EMMACoupon? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTitle = view.findViewById(R.id.txt_couponTitle)
        txtDescription = view.findViewById(R.id.txt_couponDescription)
        imgCoupon = view.findViewById(R.id.img_coupon)
        txtCouponsLeft = view.findViewById(R.id.txt_couponsLeft)

        // callback de coupons
        EMMA.getInstance().addCouponsCallback(this)
        EMMA.getInstance().getInAppMessage(EMMAInAppRequest(EMMACampaign.Type.COUPON)) // getCoupons()

        // botón volver
        btnVolverCoupons = view.findViewById(R.id.btn_volverCoupons)
        btnVolverCoupons.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // botón canjear
        btnCanjearCoupon = view.findViewById(R.id.btn_canjearCoupon)
        btnCanjearCoupon.setOnClickListener{
            EMMA.getInstance().addCouponsCallback(this)
            val redeemCouponRequest = EMMAInAppRequest(EMMACampaign.Type.REDEEM_COUPON)
            redeemCouponRequest.inAppMessageId = "8316" // id de la campaña del mensaje
            EMMA.getInstance().getInAppMessage(redeemCouponRequest)

            // impresiones y notificaciones
            currentCoupon?.let {
                EMMA.getInstance().sendInAppClick(CommunicationTypes.COUPON, it) // registrar clic
                Toast.makeText(requireContext(), "Cupon canjeado", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(requireContext(), "No hay cupón disponible", Toast.LENGTH_SHORT).show()

            // volver a home al canjear
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

        }

    }

    // EMMACouponsInterface
    override fun onCouponsReceived(coupons: List<EMMACoupon>) {
        activity?.runOnUiThread {
            coupons.forEach {
                Log.d("EMMA_Coupon", "Cupón: ${it.title}, ID interno: ${it.couponId}, Código promo: ${it.offerCode}")
            }
            if (coupons.isNotEmpty()) {
                // rellenar los componentes del fragment
                val coupon = coupons.first()
                txtTitle.text = coupon.title
                txtDescription.text = coupon.description
                txtCouponsLeft.text = coupon.currentRedemms.toString()

                Glide.with(requireContext())
                    .load(coupon.imageUrl)
                    .into(imgCoupon)

                // enviar impresión a EMMA
                EMMA.getInstance().sendInAppImpression(CommunicationTypes.COUPON, coupon)

                currentCoupon = coupon
            } else {
                txtDescription.text = "No hay cupones disponibles"
            }

        }

    }

    /*
    //En el caso en que se quisiera obtener un solo cupon con un ID
    private fun getSingleCoupon() {
        val couponsRequest = EMMAInAppRequest(EMMACampaign.Type.COUPON)
        couponsRequest.inAppMessageId = "<COUPON_ID>"
        EMMA.getInstance().getInAppMessage(couponsRequest)
    }
     */

    // parecen no funcionar, ningún toast ni log aparece
    override fun onCouponsFailure() {
        Log.d("EMMA_Coupon","An error has occurred obtaining coupons")
        Toast.makeText(requireContext(), "An error has occurred obtaining coupons", Toast.LENGTH_SHORT).show()
    }

    override fun onCouponRedemption(success: Boolean) {
        Log.d("EMMA_Coupon","Coupon redemption success: $success")
        Toast.makeText(requireContext(), "Coupon redemption success: $success", Toast.LENGTH_LONG).show()
    }

    override fun onCouponCancelled(success: Boolean) {
        Log.d("EMMA_Coupon","Coupon cancelled success: $success")
        Toast.makeText(requireContext(), "Coupon cancelled success: $success", Toast.LENGTH_LONG).show()
    }

    override fun onCouponValidRedeemsReceived(numRedeems: Int) {
        Log.d("EMMA_Coupon","Coupon redeems: $numRedeems")
        Toast.makeText(requireContext(), "Coupon redeems: $numRedeems", Toast.LENGTH_LONG).show()
    }
}