package com.EWPMS

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    var check: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityLoginScreenBinding.inflate(layoutInflater);
        setContentView(binding.root)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        onclick_listerners()
    }

    private fun onclick_listerners() {
        binding.eyeImg.setOnClickListener {
            if (check) {
                check = false
                binding.passwordEt.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordEt.setSelection(binding.passwordEt.text.length)
                binding.eyeImg.setImageResource(R.drawable.close_eye)
            } else {
                check = true
                binding.passwordEt.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.passwordEt.setSelection(binding.passwordEt.text.length)
                binding.eyeImg.setImageResource(R.drawable.open_eye)
            }
        }
    }
}