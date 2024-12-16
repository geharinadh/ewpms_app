package com.EWPMS

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.EWPMS.adapter.MilestonesListAdapter
import com.EWPMS.adapter.PresentPhotosAdapter
import com.EWPMS.data_response.MileStoneResponse
import com.EWPMS.data_response.PresentPhotosResponse
import com.EWPMS.databinding.ActivityWorkDetailBinding
import com.EWPMS.utilities.Common
import com.EWPMS.utilities.FileUtils
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.finowizx.CallBackInterface.CallBackData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkDetailActivity : AppCompatActivity(), OnMapReadyCallback,CallBackData {
    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityWorkDetailBinding

    //api call
    lateinit var progressDialog: Dialog
    lateinit var project_id: String

    private lateinit var mile_stone_list: ArrayList<MileStoneResponse>

    private lateinit var present_photos_list: ArrayList<PresentPhotosResponse>

    private lateinit var live_photo_list: ArrayList<String>

    private  var live_photo_position=""
    private  var milestone_id_live=""

    //profile
    var imageUri: Uri? = null
    private var filepath: Uri? = null
    private var imageFile: String? = ""
    private var checkcamera: String? = ""
    private var checkCameraOpen: String? = ""

    private var check_location_enabled: String? = ""

    //turn on location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude_txt: String? = ""
    private var longitude_txt: String? = ""

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationSettingsLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityWorkDetailBinding.inflate(layoutInflater);
        setContentView(binding.root)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this@WorkDetailActivity)

        mapView = findViewById(R.id.small_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        //location data
        //turn on location
        locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // User enabled location services
                Log.d("WorkDetail", "Location services are enabled.")
                // Proceed with location-based actions (e.g., get current location)
                check_location_enabled="yes"
                requestLocationPermission()
            } else {
                // User did not enable location services
                Log.d("WorkDetail", "Location services were not enabled.")
                check_location_enabled="no"
                enableLocationSettings()
                // Toast.makeText(this@WorkDetailActivity,getString(R.string.grant_permisison), Toast.LENGTH_SHORT).show()
            }
        }

        callCommonClass()

        tab_click_listeners()

        onclick_listeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this@WorkDetailActivity)

        project_id=intent.getStringExtra("project_id").toString()
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

        binding.updateProgressCard.setOnClickListener {
            startActivity(Intent(this@WorkDetailActivity,UpdateProjectActivity::class.java).putExtra("project_id",project_id))
            finish()
        }

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

            live_photo_list=ArrayList<String>()
            call_milestones_total_Work_api(project_id)
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

            call_present_photos_api(project_id)
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
                            binding.ecvTv.text="\u20B9"+ECV.toString()
                            binding.tcvTv.text="\u20B9"+tcv.toString()

                            if(Latitude.toString().isNotEmpty()) {

                                binding.mapLayout.visibility=View.VISIBLE

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
                                        Toast.makeText(this,
                                            getString(R.string.no_maps_app_found), Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }else{
                                binding.mapLayout.visibility=View.GONE
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


    private fun call_milestones_total_Work_api(project_id:String) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_TotalWorkProgress/?id="+ project_id.toString()
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@WorkDetailActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        val obj = jsonArray.getJSONObject(0)
                        Log.d("Response", response)

                        try {
                            // Assuming `Password` or `UserType` exists
                            val totalMilestones = obj.getString("TotalMilestones")
                            val financePercentage = obj.getString("FinancePercentage")
                            val totalPercentage = obj.getString("TotalPercentage")
                            val daysPercentage = obj.getString("DaysPercentage")

                            progressDialog.dismiss()
                            binding.totalWorkTv.text = totalMilestones.toString() + " Milestones"
                            binding.totalWorkPercentage.text = totalPercentage.toString() + "%"

                            binding.totalWorkProgress.progress = totalPercentage.toFloat()
                            binding.amountPaidProgress.progress = financePercentage.toFloat()
                            binding.milestoneProgress.progress = daysPercentage.toFloat()

                            binding.totalWorkProgress.labelText = totalPercentage.toString() + "%"
                            binding.amountPaidProgress.labelText =
                                financePercentage.toString() + "%"
                            binding.milestoneProgress.labelText = daysPercentage.toString() + "%"
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }
                        call_milestones_api(project_id)

                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this@WorkDetailActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this@WorkDetailActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this@WorkDetailActivity, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_present_photos_api(project_id: String) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Get_PresentPhotos/?id="+project_id.toString()
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@WorkDetailActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        present_photos_list=ArrayList<PresentPhotosResponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val PhotoDate = jsonObject.optString("PhotoDate")
                            val fn = jsonObject.optString("fn")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = PresentPhotosResponse(
                                PhotoDate,
                                fn
                            )

                            present_photos_list.add(workItem)
                        }

                        if(present_photos_list.size>0){
                            binding.noDataLayout.visibility=View.GONE
                            binding.presentPhotosRv.visibility=View.VISIBLE
                            binding.presentPhotosRv.adapter = PresentPhotosAdapter(this@WorkDetailActivity, present_photos_list)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.presentPhotosRv.visibility=View.GONE
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

    private fun call_milestones_api(project_id: String) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_MileStones_WorkProgress/?id="+project_id.toString()
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@WorkDetailActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        mile_stone_list=ArrayList<MileStoneResponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        try {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val EndDate = jsonObject.optString("EndDate")
                                val ItemDetailed = jsonObject.optString("ItemDetailed")
                                val MileStoneID = jsonObject.optString("MileStoneID")
                                val Persentage = jsonObject.optString("Persentage")
                                val StartDate = jsonObject.optString("StartDate")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = MileStoneResponse(
                                    MileStoneID,
                                    EndDate,
                                    ItemDetailed,
                                    Persentage,
                                    StartDate
                                )

                                mile_stone_list.add(workItem)
                            }

                            if (mile_stone_list.size > 0) {
                                binding.noDataLayout.visibility = View.GONE
                                binding.mileStonesRv.visibility = View.VISIBLE
                                binding.mileStonesRv.adapter = MilestonesListAdapter(
                                    this@WorkDetailActivity,
                                    project_id,
                                    live_photo_position,
                                    mile_stone_list,
                                    live_photo_list,
                                    this
                                )
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.mileStonesRv.visibility = View.GONE
                                Toast.makeText(
                                    this@WorkDetailActivity,
                                    getString(R.string.no_data),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this@WorkDetailActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getTaskStatus(milestone_id: String, position: String) {
            if (live_photo_position.isNotEmpty()) {
                if (live_photo_position != position) {
                    live_photo_list = ArrayList<String>()
                }
            } else {
                live_photo_list = ArrayList<String>()
            }

            live_photo_position = position
            milestone_id_live = milestone_id

            if (check_location_enabled.equals("yes")) {
                if (ContextCompat.checkSelfPermission(
                        this@WorkDetailActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        checkcamera = ""
                        callPer()
                    } else {
                        checkcamera = "1"
                        callCam()
                        getUserLocation()
                    }
                } else {
                    requestLocationPermission()
                }
            } else {
                enableLocationSettings()
            }
    }

    //turn on location
    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableLocationSettings() {
        // Create a location request
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Create a LocationSettingsRequest
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()

        // Check if location settings are satisfied
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                // All location settings are satisfied, proceed with location-based actions
                Log.d("WorkDetails", "Location settings are satisfied.")
                if (ContextCompat.checkSelfPermission(
                        this@WorkDetailActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        checkcamera = ""
                        callPer()
                    } else {
                        checkcamera = "1"
                        callCam()
                        getUserLocation()
                    }
                }else{
                    requestLocationPermission()
                }
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // Prompt user to enable location settings with a dialog
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        locationSettingsLauncher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e("WorkDetails", "Error resolving location settings: ${sendEx.message}")
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@WorkDetailActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }else{
            if (ContextCompat.checkSelfPermission(
                    this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                checkcamera = ""
                callPer()
            } else {
                checkcamera = "1"
                callCam()
                getUserLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserLocation() {
        Handler(Looper.getMainLooper()).postDelayed({
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        latitude_txt=latitude.toString()
                        longitude_txt=longitude.toString()

                        println("status_txt "+latitude_txt+" "+longitude_txt)

                        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            checkcamera = ""
                            callPer()
                        } else {
                            checkcamera = "1"
                            callCam()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.unable_to_fetch_location), Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                        getString(R.string.failed_to_fetch_location, it.message), Toast.LENGTH_SHORT).show()
                }
        } else {
            requestLocationPermission()
        }
        }, 2000)
    }

    private fun callCam() {
        if(checkCameraOpen!="true") {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            Log.d("camuri", "" + imageUri)
            checkCameraOpen = "true"
            cameraResult.launch(intent)
        }
    }

    var cameraResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                filepath=imageUri
                Log.d("imageUri1",imageUri.toString())
                val imageFile1: File =
                    File(FileUtils.getPath(this, filepath))
                if (imageFile1 != null) {
                    imageFile = imageFile1.absolutePath
                    Log.d("imageUri2", imageFile.toString())

                    // Copy the image to a temporary file for further use
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val tempFile = File(cacheDir, "captured_image.jpg")
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Compress the image
                    val compressedFile = compressImage(tempFile)

                    // Check file size after compression
                    val fileSizeInBytes = compressedFile.length()
                    val fileSizeInKB = fileSizeInBytes / 1024
                    val fileSizeInMB = fileSizeInKB / 1024

                    live_photo_list.add("$fileSizeInKB KB")
                    binding.mileStonesRv.adapter = MilestonesListAdapter(
                        this@WorkDetailActivity,
                        project_id,
                        live_photo_position,
                        mile_stone_list,
                        live_photo_list,
                        this
                    )
                    checkCameraOpen == "false"

                    // Call your API with the compressed file
                    call_live_photo_api(compressedFile)
                    println("File size: $fileSizeInKB KB ($fileSizeInMB MB) ${live_photo_list.size}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            checkCameraOpen="false"
            println("File size: result not found ")
        }
    }


    fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        // Resize the bitmap if necessary
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)

        // Save the compressed image to a temporary file
        val compressedFile = File(file.parent, "compressed_image.jpg")
        FileOutputStream(compressedFile).use { outputStream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Adjust quality (80) as needed
        }

        return compressedFile
    }

    private fun call_live_photo_api(imageFile: File) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            try {
                progressDialog.show()

                class MultipartRequest(
                    url: String,
                    private val headers: Map<String, String>?,
                    private val params: Map<String, String>,
                    private val filePartName: String,
                    private val file: File,
                    private val listener: Response.Listener<String>,
                    errorListener: Response.ErrorListener
                ) : StringRequest(Method.POST, url, listener, errorListener) {

                    private val boundary = "volleyBoundary" + System.currentTimeMillis()

                    override fun getBodyContentType(): String {
                        return "multipart/form-data;boundary=$boundary"
                    }

                    override fun getHeaders(): MutableMap<String, String> {
                        return headers?.toMutableMap() ?: super.getHeaders()
                    }

                    override fun getBody(): ByteArray {
                        val bos = ByteArrayOutputStream()
                        val dos = DataOutputStream(bos)
                        try {
                            // Add form fields
                            for ((key, value) in params) {
                                buildTextPart(dos, key, value)
                            }

                            // Add file field
                            buildFilePart(dos, filePartName, file)

                            // End of multipart
                            dos.writeBytes("--$boundary--\r\n")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        return bos.toByteArray()
                    }

                    private fun buildTextPart(dos: DataOutputStream, name: String, value: String) {
                        dos.writeBytes("--$boundary\r\n")
                        dos.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
                        dos.writeBytes("$value\r\n")
                    }

                    private fun buildFilePart(dos: DataOutputStream, fieldName: String, file: File) {
                        dos.writeBytes("--$boundary\r\n")
                        dos.writeBytes("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"${file.name}\"\r\n")
                        dos.writeBytes("Content-Type: ${getMimeType(file)}\r\n\r\n")

                        val fileInputStream = FileInputStream(file)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                            dos.write(buffer, 0, bytesRead)
                        }
                        dos.writeBytes("\r\n")
                        fileInputStream.close()
                    }

                    private fun getMimeType(file: File): String {
                        return "image/jpeg" // Change this based on file type if needed
                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                        return try {
                            val result = String(response.data)
                            Response.success(result, HttpHeaderParser.parseCacheHeaders(response))
                        } catch (e: Exception) {
                            Response.error(AuthFailureError())
                        }
                    }
                }

                val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Upload_LivePhoto2"

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

                val params = mapOf(
                    "id" to project_id,
                    "img" to timeStamp.toString()+".jpg",
                    "mid" to milestone_id_live,
                    "lat" to latitude_txt.toString(),
                    "lon" to longitude_txt.toString()
                )

                val file = imageFile

                val request = MultipartRequest(
                    url = url,
                    headers = null, // Add headers if needed
                    params = params,
                    filePartName = "image", // This should match the server's expected field name for the file
                    file = file,
                    listener = Response.Listener { response ->
                        println("Response: $response")
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.live_photo_uploaded_successfully), Toast.LENGTH_LONG).show()
                        binding.mileStonesRv.adapter = MilestonesListAdapter(
                            this@WorkDetailActivity,
                            project_id,
                            live_photo_position,
                            mile_stone_list,
                            live_photo_list,
                            this
                        )
                    },
                    errorListener = Response.ErrorListener { error ->
                        error.printStackTrace()
                        println("Error: ${error.message}")
                        progressDialog.dismiss()
                        live_photo_position=""
                        live_photo_list.removeAt((live_photo_list.size - 1))
                        binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,project_id,live_photo_position, mile_stone_list,live_photo_list,this)
                        Toast.makeText(this, getString(R.string.upload_failed)+": ${error.message}", Toast.LENGTH_LONG).show()
                    }
                )

                val requestQueue = Volley.newRequestQueue(this@WorkDetailActivity)
                requestQueue.add(request)
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.upload_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Toast.makeText(
                applicationContext,
                getString(R.string.please_check_with_the_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(checkcamera.equals("1"))
            {
                filepath = imageUri
                Log.d("filepath", filepath.toString())
                val imageFile1: File =
                    File(FileUtils.getPath(this, filepath))
                if (imageFile1 != null) {
                    imageFile = imageFile1.absolutePath// Step 1: Get file size
                    val file = File(filepath!!.path ?: "")
                    val fileSizeInBytes = file.length() // Get size in bytes
                    val fileSizeInKB = fileSizeInBytes / 1024 // Convert to KB
                    val fileSizeInMB = fileSizeInKB / 1024 // Convert to MB
                    println("File sizeee: $fileSizeInKB KB ($fileSizeInMB MB)")

                   // live_photo_list.add(fileSizeInMB.toString())
                    //binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun callPer() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)  {
            Log.d("Start", "FeedCamer1")
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 111)
        } else {
            callCam()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                checkcamera="1"
                callCam()
                getUserLocation()
                return
            }
            100 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getUserLocation()
                } else {
                    Toast.makeText(this,
                        getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}