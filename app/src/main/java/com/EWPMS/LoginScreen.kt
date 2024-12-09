package com.EWPMS

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.databinding.ActivityLoginScreenBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException


class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    var check: Boolean = false

    //api call
    lateinit var progressDialog: Dialog

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

        callCommonClass()

        onclick_listerners()
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this)
    }

    private fun onclick_listerners() {
        binding.forgotPasswordTv.setOnClickListener {
            Toast.makeText(this@LoginScreen, getString(R.string.comming_soon), Toast.LENGTH_SHORT).show()
        }

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
                        call_login_api(binding.userNameEt.text.toString())
                    } else {
                        Toast.makeText(this@LoginScreen, getString(R.string.valid_password_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginScreen, getString(R.string.valid_username_error), Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }


    private fun call_login_api(userId: String) {
        if (Common.isInternetAvailable(this@LoginScreen)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Check_Login/?id=$userId"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        val obj = jsonArray.getJSONObject(0)
                        Log.d("Response", response)

                        // Assuming `Password` or `UserType` exists
                        val user_name = obj.getString("Name")
                        val user_password = obj.getString("Password")
                        val loginId = obj.getString("LoginId")
                        val userType = obj.getString("UserType")
                        val userMobile = obj.getString("MobileNo")

                        if(user_password.equals(binding.passwordEt.text.toString().trim())) {
                            Toast.makeText(this, "Welcome $user_name!", Toast.LENGTH_LONG).show()

                            if (binding.rememberMeCheckBox.isChecked) {
                                AppSharedPreferences.setStringPreference(
                                    this, AppConstants.REMEMBER_ME, "true"
                                )
                            } else {
                                AppSharedPreferences.setStringPreference(
                                    this, AppConstants.REMEMBER_ME, "false"
                                )
                            }

                            if (AppSharedPreferences.getStringSharedPreference(
                                    baseContext, AppConstants.THEME) != null && (!AppSharedPreferences.getStringSharedPreference(
                                    baseContext, AppConstants.THEME).equals(""))) {
                                AppSharedPreferences.setStringPreference(
                                    this, AppConstants.THEME, "white")
                            }

                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERTYPE,
                                userType
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERID,
                                loginId
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERNAME,
                                user_name
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERMOBILE,
                                userMobile
                            )

                            startActivity(Intent(this@LoginScreen, MainActivity::class.java))
                            finish()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(
                                this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(
                            this, getString(R.string.user_details_not_found), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(
                this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

}