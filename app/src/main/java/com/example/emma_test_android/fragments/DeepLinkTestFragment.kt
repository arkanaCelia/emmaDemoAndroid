package com.example.emma_test_android.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.emma_test_android.R

/**
 * Pantalla para probar los deeplinks / powlinks
 */

class DeepLinkTestFragment : Fragment(R.layout.fragment_deeplink_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnVolver = view.findViewById<Button>(R.id.btn_volver)
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
}