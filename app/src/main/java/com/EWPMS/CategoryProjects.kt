package com.EWPMS

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.adapter.MyWorksAdapter
import com.EWPMS.data_response.DashboardWorkResponse
import com.EWPMS.data_response.SEresponse
import com.EWPMS.databinding.ActivityCategoryProjectsBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.finowizx.CallBackInterface.CallBackData
import org.json.JSONArray
import org.json.JSONException

class CategoryProjects : AppCompatActivity(),CallBackData {
    private lateinit var binding: ActivityCategoryProjectsBinding
    //api call
    lateinit var progressDialog: Dialog

    var se_list=ArrayList<SEresponse>()
    var division_list=ArrayList<SEresponse>()
    var dee_list=ArrayList<SEresponse>()
    var aee_list=ArrayList<SEresponse>()

    var status_api="true"

    var user_type=""

    var user_name=""

    private lateinit var my_works_list: ArrayList<DashboardWorkResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityCategoryProjectsBinding.inflate(layoutInflater);
        setContentView(binding.root)

        callCommonClass()

        spinner_selection()

        onClickListners()

        binding.backIconLayout.setOnClickListener{
            startActivity(Intent(this@CategoryProjects, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CategoryProjects, MainActivity::class.java))
        finish()
    }

    private fun onClickListners() {
        binding.completedProjectLayout.setOnClickListener {
            call_work_details_api("104",user_type,user_name)
        }
        binding.beyondLayout.setOnClickListener {
            call_work_details_api("102",user_type,user_name)
        }
        binding.asPerLayout.setOnClickListener {
            call_work_details_api("103",user_type,user_name)
        }

        binding.mileStonecompletedLayout.setOnClickListener {
            call_work_details_api("108",user_type,user_name)
        }
        binding.mileStoneongoingLayout.setOnClickListener {
            call_work_details_api("109",user_type,user_name)
        }
        binding.mileStonedelayLayout.setOnClickListener {
            call_work_details_api("110",user_type,user_name)
        }

        binding.amtCommitedLayout.setOnClickListener {
            call_work_details_api("105",user_type,user_name)
        }
        binding.amtReleasedLayout.setOnClickListener {
            call_work_details_api("106",user_type,user_name)
        }
        binding.amtPendingLayout.setOnClickListener {
            call_work_details_api("107",user_type,user_name)
        }

    }

    private fun spinner_selection() {
        binding.spinnerSE.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    user_type="se"
                    user_name=se_list[position-1].LoginName.toString()
                    call_reports_api(se_list[position-1].LoginName.toString())
                }
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle the case when no item is selected, if needed
                }
            }

        binding.spinnerAee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    user_type="ae"
                    user_name=aee_list[position-1].LoginName.toString()
                    call_reports_api(aee_list[position-1].LoginName.toString())
                }
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle the case when no item is selected, if needed
                }
            }

        binding.spinnerDivision.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    user_type="div"
                    user_name=division_list[position-1].LoginName.toString()
                    call_reports_api(division_list[position-1].LoginName.toString())
                }
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle the case when no item is selected, if needed
                }
            }

        binding.spinnerDee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    user_type="de"
                    user_name=dee_list[position-1].LoginName.toString()
                    call_reports_api(dee_list[position-1].LoginName.toString())
                }
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle the case when no item is selected, if needed
                }
            }
    }

    private fun callCommonClass() {
        if(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString().substring(0,2).toString() == "se") {
            user_type ="se"
            user_name =AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString()
        }else if(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString().substring(0,2).toString() == "ae") {
            user_type ="ae"
            user_name =AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString()
        }else if(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString().substring(0,2).toString() == "de") {
            user_type ="de"
            user_name =AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString()
        }else if(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString().substring(0,2).toString() == "div") {
            user_type ="div"
            user_name =AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString()
        }else if(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString().substring(0,2).toString() == "ee") {
            user_type ="div"
            user_name =AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString()
        }
        progressDialog = Common.progressDialog(this@CategoryProjects)

        call_reports_api(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString())
    }

    private fun call_reports_api(id:String) {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_WorkReports/?id="+ id
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
                        val AheadPercentage = obj.getString("CompletedPercentage")
                        val AheadofShedule = obj.getString("Completedprojects")

                        val BeyondPercentage = obj.getString("BeyondPercentage")
                        val BeyondShedule = obj.getString("BeyondShedule")

                        val AsperPercentage = obj.getString("AsperPercentage")
                        val AsperShedule = obj.getString("AsperShedule")

                        val TotalWork = obj.getString("TotalCount")

                        val CompletedMileStone = obj.getString("CompletedM")
                        val CompletedMileStonePercentage = obj.getString("CompletedMPercentage")

                        val OngoingMileStone = obj.getString("OngoingM")
                        val OnGoingMileStonePercentage = obj.getString("OnGoingMPercentage")

                        val DelayMileStone = obj.getString("DelayM")
                        val DelayMileStonePercentage = obj.getString("DelaymPercentage")

                        val TotalMileStones = obj.getString("TotalMileStones")

                        val AmountsCommitted = obj.getString("AmountsCommitted")
                        val AmountsCommittedPercentage = obj.getString("CommittedPercentage")

                        val AmountsReleased = obj.getString("AmountsReleased")
                        val AmountsReleasedPercentage = obj.getString("ReleasedPercentage")

                        val AmountsPending = obj.getString("AmountsPending")
                        val AmountsPendingPercentage= obj.getString("PendingPercentage")

                        val TotalAmount= obj.getString("TotalFinance")

                        if(AheadPercentage.toString().isNotEmpty()){
                            progressDialog.dismiss()
                            binding.aheadScheduleTv.text=AheadofShedule.toString()
                            binding.aheadScheduleProgress.progress=AheadPercentage.toFloat()

                            binding.beyondScheduleProgress.progress=BeyondPercentage.toFloat()
                            binding.beyondScheduleTv.text=BeyondShedule.toString()

                            binding.asPerScheduleProgress.progress=AsperPercentage.toFloat()
                            binding.asPerScheduleTv.text=AsperShedule.toString()

                            binding.totalWorkTv.text= "Total Works: $TotalWork"

                            binding.completedProgress.progress=CompletedMileStonePercentage.toFloat()
                            binding.completedMsTv.text=CompletedMileStone.toString()

                            binding.ongoingProgress.progress=OnGoingMileStonePercentage.toFloat()
                            binding.ongoingMsTv.text=OngoingMileStone.toString()

                            binding.delayProgress.progress=DelayMileStonePercentage.toFloat()
                            binding.delayMsTv.text=DelayMileStone.toString()

                            binding.totalMilestoneTv.text="Total Milestones: "+TotalMileStones.toString()

                            binding.committedProgress.progress=AmountsCommittedPercentage.toFloat()
                            binding.committedAmtTv.text="Rs."+AmountsCommitted.toString()

                            binding.releasedProgress.progress=AmountsReleasedPercentage.toFloat()
                            binding.releasedAmtTv.text="Rs."+AmountsReleased.toString()

                            binding.pendingProgress.progress=AmountsPendingPercentage.toFloat()
                            binding.pendingAmtTv.text="Rs."+AmountsPending.toString()

                            binding.totalAmountTv.text="Total Amounts(In Lakhs): Rs."+TotalAmount.toString()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }

                        if(status_api=="true") {
                            call_se_list_api()
                            status_api="false"
                        }else{
                            call_work_details_api("104",user_type,user_name)
                        }

                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_se_list_api() {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=se"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        se_list=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("DisplayName")
                            val LoginName = jsonObject.optString("LoginName")
                            // Create a new MyWorksResponse object and add it to the list
                            val seItem = SEresponse(
                                DisplayName, LoginName)

                            se_list.add(seItem)
                        }

                        var se_list_name=ArrayList<String>()
                        se_list_name.add("All")

                        for(i in se_list.indices){
                           se_list_name.add(se_list[i].DisplayName)
                        }

                        if(se_list.size>0){
                            lateinit var se_adapter: ArrayAdapter<String>
                            se_adapter = ArrayAdapter(this, R.layout.spinner_item, se_list_name)
                            binding.spinnerSE.adapter = se_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_div_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_div_list_api() {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=div"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        division_list=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("DisplayName")
                            val LoginName = jsonObject.optString("LoginName")
                            // Create a new MyWorksResponse object and add it to the list
                            val divItem = SEresponse(
                                DisplayName, LoginName)

                            division_list.add(divItem)
                        }

                        var div_list_name=ArrayList<String>()
                        div_list_name.add("All")
                        for(i in division_list.indices){
                           div_list_name.add(division_list[i].DisplayName)
                        }

                        if(division_list.size>0){
                            lateinit var div_adapter: ArrayAdapter<String>
                            div_adapter = ArrayAdapter(this, R.layout.spinner_item, div_list_name)
                            binding.spinnerDivision.adapter = div_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_dee_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_dee_list_api() {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=de"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        dee_list=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("DisplayName")
                            val LoginName = jsonObject.optString("LoginName")
                            // Create a new MyWorksResponse object and add it to the list
                            val deeItem = SEresponse(
                                DisplayName, LoginName)

                            dee_list.add(deeItem)
                        }

                        var dee_list_name=ArrayList<String>()
                        dee_list_name.add("All")
                        for(i in dee_list.indices){
                            dee_list_name.add(dee_list[i].DisplayName)
                        }

                        if(dee_list.size>0){
                            lateinit var de_adapter: ArrayAdapter<String>
                            de_adapter = ArrayAdapter(this, R.layout.spinner_item, dee_list_name)
                            binding.spinnerDee.adapter = de_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_aee_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_aee_list_api() {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=ae"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        aee_list=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("DisplayName")
                            val LoginName = jsonObject.optString("LoginName")
                            // Create a new MyWorksResponse object and add it to the list
                            val aeeItem = SEresponse(
                                DisplayName, LoginName)

                            aee_list.add(aeeItem)
                        }

                        var aee_list_name=ArrayList<String>()
                        aee_list_name.add("All")
                        for(i in aee_list.indices){
                            aee_list_name.add(aee_list[i].DisplayName)
                        }

                        if(aee_list.size>0){
                            lateinit var ae_adapter: ArrayAdapter<String>
                            ae_adapter = ArrayAdapter(this, R.layout.spinner_item, aee_list_name)
                            binding.spinnerAee.adapter = ae_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_my_works_api(AppSharedPreferences.getStringSharedPreference(this, AppConstants.USERID).toString())
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_my_works_api(id: String) {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_DashboardMyWorks/?id="+id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    try {
                        my_works_list=ArrayList<DashboardWorkResponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val categoryName = jsonObject.optString("CategoryName")
                            val completedPercentage = jsonObject.optString("CompletedPercentage")
                            val currentProjectsID = jsonObject.optString("CurrentProjectsID")
                            val daysLeft = jsonObject.optString("DaysLeft")
                            val noOfMileStones = jsonObject.optString("NoOfMileStones")
                            val projectName = jsonObject.optString("ProjectName")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = DashboardWorkResponse(
                                categoryName,
                                completedPercentage,
                                currentProjectsID,
                                daysLeft,
                                noOfMileStones,
                                projectName)
                            my_works_list.add(workItem)
                        }

                        if(my_works_list.size>0){
                            binding.noDataLayout.visibility=View.GONE
                            binding.myWorksRv.visibility=View.VISIBLE
                            binding.myWorksRv.adapter = MyWorksAdapter(this, my_works_list,this)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.myWorksRv.visibility=View.GONE
                            Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_work_details_api(id: String,type: String,name: String) {
        if (Common.isInternetAvailable(this)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Get_ProjectsList2/?id="+id+"&type="+type+"&name="+name
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    try {
                        my_works_list=ArrayList<DashboardWorkResponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val categoryName = jsonObject.optString("CategoryName")
                            val completedPercentage = jsonObject.optString("CompletedPercentage")
                            val currentProjectsID = jsonObject.optString("CurrentProjectsID")
                            val daysLeft = jsonObject.optString("DaysLeft")
                            val noOfMileStones = jsonObject.optString("NoOfMileStones")
                            val projectName = jsonObject.optString("ProjectName")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = DashboardWorkResponse(
                                categoryName,
                                completedPercentage,
                                currentProjectsID,
                                daysLeft,
                                noOfMileStones,
                                projectName)
                            my_works_list.add(workItem)
                        }

                        if(my_works_list.size>0){
                            binding.noDataLayout.visibility=View.GONE
                            binding.myWorksRv.visibility=View.VISIBLE
                            binding.myWorksRv.adapter = MyWorksAdapter(this, my_works_list,this)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.myWorksRv.visibility=View.GONE
                            Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getTaskStatus(id: String, position: String) {
        startActivity(Intent(this@CategoryProjects, WorkDetailActivity::class.java).putExtra("project_id",id))
    }

}