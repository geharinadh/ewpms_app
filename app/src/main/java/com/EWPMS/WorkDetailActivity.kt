package com.EWPMS

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import com.EWPMS.databinding.ActivityLoginScreenBinding
import com.EWPMS.databinding.ActivityWorkDetailBinding
import com.EWPMS.databinding.FragmentMyWorksBinding
import com.EWPMS.databinding.FragmentReportsBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityWorkDetailBinding

    //api call
    lateinit var progressDialog: Dialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityWorkDetailBinding.inflate(layoutInflater);
        setContentView(binding.root)

        mapView = findViewById(R.id.small_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        callCommonClass()

        tab_click_listeners()

        onclick_listeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this@WorkDetailActivity)

        var project_id=intent.getStringExtra("project_id").toString()
        call_project_Details_api(project_id)
    }


    private fun onclick_listeners() {
        binding.backIconLayout.setOnClickListener{
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun tab_click_listeners() {
        binding.workDetailCardview.setOnClickListener {
            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.workDetailTv.setTextColor(resources.getColor(R.color.white))

            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.milestoneTextview.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.black))

            binding.milestonesLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.GONE
            binding.workDetailLayout.visibility= View.VISIBLE

        }

        binding.milestoneCardview.setOnClickListener {
            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.milestoneTextview.setTextColor(resources.getColor(R.color.white))

            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.workDetailTv.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.black))

            binding.workDetailLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.GONE
            binding.milestonesLayout.visibility= View.VISIBLE
        }

        binding.presentPhotosCardview.setOnClickListener {
            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.white))

            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.workDetailTv.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.milestoneTextview.setTextColor(resources.getColor(R.color.black))

            binding.workDetailLayout.visibility= View.GONE
            binding.milestonesLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.VISIBLE
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap=googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.7749, -122.4194), 10f))
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun call_project_Details_api(project_id:String) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_WorkDetails/?id="+ project_id.toString()
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@WorkDetailActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        val obj = jsonArray.getJSONObject(0)
                        Log.d("Response", response)

                        // Assuming `Password` or `UserType` exists
                        val ProjectName = obj.getString("ProjectName")
                        val AgreementNo = obj.getString("AgreementNo")
                        val ProjectStatus = obj.getString("Stat")

                        val StartDate = obj.getString("StartDate")
                        val EndDate = obj.getString("EndDate")

                        val AdministrativeSanction = obj.getString("AdministrativeSanction")
                        val AdministrativeSanctionAmount = obj.getString("AdministrativeSanctionAmount")

                        val TechnicalSanction = obj.getString("TechnicalSanction")
                        val TechnicalSanctionAmount = obj.getString("TechnicalSanctionAmount")

                        val TimePeriod = obj.getString("TimePeriod")
                        val tcv = obj.getString("tcv")
                        val ECV = obj.getString("ECV")

                        val Latitude = obj.getString("Latitude")
                        val Longitude = obj.getString("Longitude")

                        if(ProjectName.toString().isNotEmpty()){
                            progressDialog.dismiss()
                            binding.projectTitleTv.text=ProjectName.toString()
                            binding.projectNameTv.text=ProjectName.toString()
                            binding.agreementNoTv.text=AgreementNo.toString()
                            binding.projectStatus.text=ProjectStatus.toString()
                            if(ProjectStatus.equals("Ongoing")){
                                val color_selected = ContextCompat.getColor(this@WorkDetailActivity, R.color.green)
                                ImageViewCompat.setImageTintList(binding.dotImg, ColorStateList.valueOf(color_selected))
                            }else{
                                val color_selected = ContextCompat.getColor(this@WorkDetailActivity, R.color.red)
                                ImageViewCompat.setImageTintList(binding.dotImg, ColorStateList.valueOf(color_selected))
                            }
                            date_conversion(StartDate,EndDate)

                            binding.adminSanctionTv.text=AdministrativeSanction.toString()
                            binding.adminSanctionAmtTv.text=AdministrativeSanctionAmount.toString()+" Lakhs"

                            binding.technicalSanctionTv.text=TechnicalSanction.toString()
                            binding.technicalSanctionAmtTv.text=TechnicalSanctionAmount.toString()+" Lakhs"

                            binding.timePeriodTv.text=TimePeriod.toString()
                            binding.ecvTv.text=ECV.toString()
                            binding.tcvTv.text=tcv.toString()

                            if(Latitude.toString().isNotEmpty()) {
                                gMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(Latitude.toDouble(), Longitude.toDouble()), 12f))

                                binding.smallMap.isEnabled=false
                                binding.smallMap.isClickable = false
                                binding.smallMap.onPause()
                                binding.smallMap.getMapAsync { googleMap ->
                                    googleMap.uiSettings.setAllGesturesEnabled(false)  // Disable all gestures
                                }

                                binding.viewLargerMap.setOnClickListener {
                                    val geoUri = "geo:$Latitude,$Longitude"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                                    intent.setPackage("com.google.android.apps.maps")  // Optional, to open specifically in Google Maps

                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "No Maps app found", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this@WorkDetailActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this@WorkDetailActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this@WorkDetailActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this@WorkDetailActivity, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun date_conversion(start_date:String, end_date:String) {
        //date convertion
        val inputDate_1 = start_date.toString()
        val inputDate_2 = end_date.toString()

        // Step 1: Define the input format
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        // Step 2: Parse the input date
        val date1 = LocalDate.parse(inputDate_1, inputFormatter)
        val date2 = LocalDate.parse(inputDate_2, inputFormatter)

        // Step 3: Define the output format
        val outputFormatter = DateTimeFormatter.ofPattern("MMM, dd yyyy")

        // Step 4: Format the date
        val formattedDate1 = date1.format(outputFormatter)
        val formattedDate2 = date2.format(outputFormatter)

        binding.startDateTv.text=formattedDate1.toString()
        binding.endDateTv.text=formattedDate2.toString()
    }
}