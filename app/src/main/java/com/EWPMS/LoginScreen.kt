package com.EWPMS

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.databinding.ActivityLoginScreenBinding
import com.EWPMS.utilities.AppSharedPreferences
import com.goodworks.app.views.util.AppConstants

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

        binding.loginBtn.setOnClickListener {
            try {
                if (binding.userNameEt.text.toString().isNotEmpty()) {
                    if (binding.passwordEt.text.toString().isNotEmpty()) {
                        if (binding.rememberMeCheckBox.isChecked) {
                            AppSharedPreferences.setStringPreference(
                                this, AppConstants.REMEMBER_ME, "true")
                        } else {
                            AppSharedPreferences.setStringPreference(
                                this, AppConstants.REMEMBER_ME, "false")
                        }

                        startActivity(Intent(this@LoginScreen, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginScreen, getString(R.string.valid_password_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginScreen, getString(R.string.valid_username_error), Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }
}