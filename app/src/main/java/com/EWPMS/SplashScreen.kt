package com.EWPMS

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.databinding.ActivitySplashScreenBinding
import com.EWPMS.databinding.FragmentReportsBinding
import com.EWPMS.utilities.AppSharedPreferences
import com.goodworks.app.views.util.AppConstants

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivitySplashScreenBinding.inflate(layoutInflater);
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

        try {
            if (AppSharedPreferences.getStringSharedPreference(
                    baseContext,
                    AppConstants.REMEMBER_ME
                ) != null && (!AppSharedPreferences.getStringSharedPreference(
                    baseContext, AppConstants.REMEMBER_ME
                ).equals(""))
            ) {
                if (AppSharedPreferences.getStringSharedPreference(
                        baseContext,
                        AppConstants.REMEMBER_ME
                    ).equals("true")
                ) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }, 1800)
                }else{
                    binding.startBtn.visibility=View.VISIBLE
                }
            }else{
                binding.startBtn.visibility=View.VISIBLE
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        binding.startBtn.setOnClickListener {
            startActivity(Intent(this@SplashScreen, LoginScreen::class.java))
            finish()
        }
    }
}