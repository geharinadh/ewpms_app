package com.EWPMS

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.EWPMS.databinding.ActivityProfileBinding
import com.EWPMS.databinding.ActivityWorkDetailBinding
import com.EWPMS.databinding.FragmentDashBoardBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(binding.root)

        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
            binding.userNameTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                    .toString()
        }
        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
            binding.userTypeTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                    .toString()
        }
        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERMOBILE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERMOBILE).isNotEmpty()) {
            binding.phoneNoTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERMOBILE)
                    .toString()
        }
        onclick_listeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
        finish()
    }

    private fun onclick_listeners() {

        binding.addNewProject.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, MainActivity::class.java).putExtra("screen","profile"))
            finish()
        }

        binding.signOutBtn.setOnClickListener {
            AppSharedPreferences.setStringPreference(
                this, AppConstants.REMEMBER_ME, "false"
            )

            AppSharedPreferences.setStringPreference(
                this,
                AppConstants.USERTYPE,
                null
            )
            AppSharedPreferences.setStringPreference(
                this,
                AppConstants.USERID,
                null
            )
            AppSharedPreferences.setStringPreference(
                this,
                AppConstants.USERNAME,
                null
            )
            AppSharedPreferences.setStringPreference(
                this,
                AppConstants.USERMOBILE,
                null
            )

            startActivity(Intent(this@ProfileActivity, LoginScreen::class.java))
            finish()
        }
    }
}