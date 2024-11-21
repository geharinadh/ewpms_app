package com.EWPMS

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
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
import com.EWPMS.utilities.MultipartRequest
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.finowizx.CallBackInterface.CallBackData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.yalantis.ucrop.UCrop
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Random

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


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude_txt: String? = ""
    private var longitude_txt: String? = ""

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
                                        Toast.makeText(this,
                                            getString(R.string.no_maps_app_found), Toast.LENGTH_SHORT).show()
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

                        // Assuming `Password` or `UserType` exists
                        val TotalPercentage = obj.getString("TotalPercentage")
                        val TotalMilestones = obj.getString("TotalMilestones")

                        progressDialog.dismiss()
                        binding.totalWorkTv.text = TotalMilestones.toString()+" Milestones"
                        binding.totalWorkPercentage.text = TotalPercentage.toString()+"%"
                        binding.totalWorkProgress.progress=TotalPercentage.toFloat()

                        call_milestones_api(project_id)

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

                        if(mile_stone_list.size>0){
                            binding.noDataLayout.visibility=View.GONE
                            binding.mileStonesRv.visibility=View.VISIBLE
                            binding.mileStonesRv.adapter = MilestonesListAdapter(
                                this@WorkDetailActivity,
                                live_photo_position,
                                mile_stone_list,
                                live_photo_list,
                                this
                            )
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.mileStonesRv.visibility=View.GONE
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

    override fun getTaskStatus(milestone_id: String, position: String) {
        if(live_photo_position.isNotEmpty()) {
            if(live_photo_position != position) {
                live_photo_list = ArrayList<String>()
            }
        }else{
            live_photo_list = ArrayList<String>()
        }

        live_photo_position=position
        milestone_id_live=milestone_id

        if(ContextCompat.checkSelfPermission(this@WorkDetailActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (hasPermissions(this@WorkDetailActivity, *arrayOf<String>(Manifest.permission.CAMERA))) {
                checkcamera = "1"
                callCam()
                getUserLocation()
            } else {
                checkcamera = ""
                callPer()
            }
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@WorkDetailActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }else{
            if (hasPermissions(this@WorkDetailActivity, *arrayOf<String>(Manifest.permission.CAMERA))) {
                checkcamera = "1"
                callCam()
            } else {
                checkcamera = ""
                callPer()
            }
        }
    }

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

                        if (hasPermissions(this@WorkDetailActivity, *arrayOf<String>(Manifest.permission.CAMERA))) {
                            checkcamera = "1"
                            callCam()
                        } else {
                            checkcamera = ""
                            callPer()
                        }
                    } else {
                        Toast.makeText(this,
                            getString(R.string.unable_to_fetch_location), Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                        getString(R.string.failed_to_fetch_location, it.message), Toast.LENGTH_SHORT).show()
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun callCam() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        Log.d("camuri",""+imageUri)

        cameraResult.launch(intent)
    }

    var cameraResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {

                filepath=imageUri
                Log.d("imageUri",imageUri.toString())
                val imageFile1: File =
                    File(FileUtils.getPath(this, filepath))
                if (imageFile1 != null) {
                    imageFile = imageFile1.absolutePath
                    Log.d("imageUri",imageFile.toString())

                    // Step 1: Get file size
                    val fileSizeInBytes = imageFile1.length() // Get size in bytes
                    val fileSizeInKB = fileSizeInBytes / 1024 // Convert to KB
                    val fileSizeInMB = fileSizeInKB / 1024 // Convert to MB

                   live_photo_list.add(fileSizeInKB.toString()+"KB")
                   binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)

                    call_live_photo_api(imageFile1)
                    println("File size: $fileSizeInKB KB ($fileSizeInMB MB)"+" "+live_photo_list.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun call_live_photo_api(imageFile: File) {
        if (Common.isInternetAvailable(this@WorkDetailActivity)) {
            try {
                progressDialog.show()
                val url = "http://www.vmrda.gov.in/ewpms_api//api/Usp_Upload_LivePhoto/?id=${project_id}&img=$milestone_id_live&mid=${milestone_id_live}&lat=${latitude_txt!!.toDouble()}&lon=${longitude_txt!!.toDouble()}"

                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.live_photo_uploaded_successfully), Toast.LENGTH_LONG).show()
                        binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)
                    },
                    { error ->
                        progressDialog.dismiss()
                        live_photo_position=""
                        live_photo_list.removeAt((live_photo_list.size - 1))
                        binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)
                        Toast.makeText(this, getString(R.string.upload_failed)+": ${error.message}", Toast.LENGTH_LONG).show()

                        // Handle error
                        error.printStackTrace()
                        if (error.networkResponse != null) {
                            Log.e("API_ERROR", "Status Code: ${error.networkResponse.statusCode}")
                            Log.e("API_ERROR", "Response: ${String(error.networkResponse.data)}")
                        }
                    }
                )
                Volley.newRequestQueue(this).add(stringRequest)

                /* val url =
                     "http://www.vmrda.gov.in/ewpms_api//api/Usp_Upload_LivePhoto/?id=$project_id&img=$milestone_id_live.jpg&mid=$milestone_id_live&lat=$latitude_txt&lon=$longitude_txt"

                 val queue = Volley.newRequestQueue(this)

                 Log.d("Response", url+" "+milestone_id_live+" "+project_id)

                 val fileData = imageFile.readBytes()

                 val multipartRequest = MultipartRequest(
                     url,
                     headers = null, // or provide headers if needed
                     params = mutableMapOf(), // No params needed here; they are part of the URL
                     fileKey = "img",
                     fileData = fileData,
                     listener = Response.Listener { response ->
                         progressDialog.dismiss()
                         Toast.makeText(this, getString(R.string.live_photo_uploaded_successfully), Toast.LENGTH_LONG).show()
                         live_photo_position=""
                         live_photo_list=ArrayList<String>()
                         binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)
                     },
                     errorListener = Response.ErrorListener { error ->
                         progressDialog.dismiss()
                         live_photo_position=""
                         live_photo_list=ArrayList<String>()
                         binding.mileStonesRv.adapter = MilestonesListAdapter(this@WorkDetailActivity,live_photo_position, mile_stone_list,live_photo_list,this)
                         Toast.makeText(this, getString(R.string.upload_failed)+": ${error.message}", Toast.LENGTH_LONG).show()
                     }
                 )

                 queue.add(multipartRequest)
 */            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.upload_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun callPer() {
        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
            if (!hasPermissions(this@WorkDetailActivity, *PERMISSIONS)) {
                Log.d("Start", "FeedCamer1")
                ActivityCompat.requestPermissions(
                    (this@WorkDetailActivity as Activity?)!!,
                    PERMISSIONS,
                    111
                )
            } else {
                callCam()
            }
        }
    }

    fun gen(): Int {
        val r = Random(System.currentTimeMillis())
        return 10000 + r.nextInt(20000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                checkcamera="1"
                callCam()
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