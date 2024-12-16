package com.EWPMS

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.adapter.ProjectDataAdapter
import com.EWPMS.data_response.ProjectDataResponse
import com.EWPMS.data_response.UpdateProjectResponse
import com.EWPMS.databinding.UpdateProjectProgressLayoutBinding
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.finowizx.CallBackInterface.CallBackData
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class UpdateProjectActivity : AppCompatActivity(), CallBackData {

    private lateinit var binding: UpdateProjectProgressLayoutBinding
    //api call
    lateinit var progressDialog: Dialog
    lateinit var project_id: String

    var project_data_list=ArrayList<ProjectDataResponse>()

    var update_project_data_list=ArrayList<UpdateProjectResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = UpdateProjectProgressLayoutBinding.inflate(layoutInflater);
        setContentView(binding.root)

        project_id=intent.getStringExtra("project_id").toString()

        callCommonClass()

        binding.backIconLayout.setOnClickListener {
            startActivity(Intent(this@UpdateProjectActivity,WorkDetailActivity::class.java).putExtra("project_id",project_id))
            finish()
        }

        binding.updateBtn.setOnClickListener {
            if(update_project_data_list.size>0) {
                update_progress_api()
            }else{
                Toast.makeText(this, getString(R.string.updates_not_found),Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this@UpdateProjectActivity)

        call_project_Details_api(project_id)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@UpdateProjectActivity,WorkDetailActivity::class.java).putExtra("project_id",project_id))
        finish()
    }

    private fun call_project_Details_api(projectId: String) {
        if (Common.isInternetAvailable(this@UpdateProjectActivity)) {
            progressDialog.show()
            val url =
                "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_MileStones_Details/?id=" + project_id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@UpdateProjectActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        project_data_list = ArrayList<ProjectDataResponse>()
                        update_project_data_list = ArrayList<UpdateProjectResponse>()

                        val jsonArray = JSONArray(response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val CompletedAmount = jsonObject.optString("CompletedAmount")
                            val EndDate = jsonObject.optString("EndDate")
                            val ItemDetailed = jsonObject.optString("ItemDetailed")
                            val MileStoneID = jsonObject.optString("MileStoneID")
                            val MileStone_Amount = jsonObject.optString("MileStone_Amount")
                            val Remarks = jsonObject.optString("Remarks")
                            val StartDate = jsonObject.optString("StartDate")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = ProjectDataResponse(
                                CompletedAmount,
                                EndDate,
                                ItemDetailed,
                                MileStoneID,
                                MileStone_Amount,
                                Remarks,
                                StartDate
                            )

                            project_data_list.add(workItem)
                        }
                        Log.d("Responsee", project_data_list.size.toString())
                        if (project_data_list.size > 0) {
                            binding.noDataLayout.visibility = View.GONE
                            binding.myWorksRv.visibility = View.VISIBLE
                            binding.myWorksRv.adapter = ProjectDataAdapter(
                                this@UpdateProjectActivity,
                                project_data_list,
                                this
                            )
                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                            binding.myWorksRv.visibility = View.GONE
                            binding.noDataLayout.visibility = View.VISIBLE
                            Toast.makeText(
                                this@UpdateProjectActivity,
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        binding.myWorksRv.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        Toast.makeText(
                            this@UpdateProjectActivity,
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    binding.myWorksRv.visibility = View.GONE
                    binding.noDataLayout.visibility = View.VISIBLE
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(
                        this@UpdateProjectActivity,
                        getString(R.string.response_failure_please_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            queue.add(stringRequest)
        }
    }


    private fun update_progress_api() {
        for (i in update_project_data_list.indices) {
            if (Common.isInternetAvailable(this@UpdateProjectActivity)) {
                try {
                    progressDialog.show()

                    val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Update_ItemsDetailed2"

                // Create the JSON payload
                    val jsonBody = JSONObject().apply {
                        put("Id", "70684")
                        put("ItemId", "137")
                        put("ComAmt", "15.02")
                        put("Remarks", "")
                    }

                    val requestQueue: RequestQueue = Volley.newRequestQueue(this)

                    val jsonArrayRequest = object : JsonArrayRequest(
                        Request.Method.POST,
                        url,
                        null, // Passing null here as the body will be sent in getBody
                        { response ->
                            try {
                                // Handle the JSONArray response
                                for (j in 0 until response.length()) {
                                    val jsonObject = response.getJSONObject(j)
                                    val retVal = jsonObject.getString("RetVal")
                                    Log.d("Response Value", "RetVal: $retVal")
                                    if (retVal == "Success") {
                                        if((update_project_data_list.size-1)==i) {
                                            progressDialog.dismiss()
                                            // Handle the successful response
                                            Toast.makeText(this, getString(R.string.project_progress_updated_successfully), Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@UpdateProjectActivity,WorkDetailActivity::class.java).putExtra("project_id",project_id))
                                            finish()
                                        }
                                    } else {
                                        if((update_project_data_list.size-1)==i) {
                                            progressDialog.dismiss()
                                            Toast.makeText(this,
                                                getString(R.string.upload_failed) + ": ${retVal}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("API Error", "JSON Parsing error: ${e.message}")
                            }
                        },
                        { error ->
                            // Handle errors
                            Log.e("API Error", "Volley Error: ${error.message}")
                            Toast.makeText(this, "API Request Failed: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        // Override getHeaders to set Content-Type
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-Type"] = "application/json"
                            return headers
                        }

                        // Override getBody to send the JSON payload
                        override fun getBody(): ByteArray {
                            return jsonBody.toString().toByteArray(Charsets.UTF_8)
                        }
                    }

                // Add the request to the Volley queue
                    requestQueue.add(jsonArrayRequest)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.upload_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getTaskStatus(position: String, remarks: String) {
        try {
            val parts = position.split(",")

            var firstPart = ""
            var secondPart = ""
            if (parts.size == 2) {
                firstPart = parts[0].trim()  // Remove any extra spaces
                secondPart = parts[1].trim()

                println("First part: $firstPart")
                println("Second part: $secondPart")
            } else {
                println("Input string does not contain exactly one comma!")
            }

            val projectStatus = UpdateProjectResponse(
                project_id,
                project_data_list[firstPart.toInt()].MileStoneID,
                remarks,
                secondPart.toString()
            )

            if (update_project_data_list.size > 0) {
                for (i in update_project_data_list.indices) {
                    if (update_project_data_list[i].Itemid == project_data_list[firstPart.toInt()].MileStoneID) {
                        update_project_data_list[i] = projectStatus
                    } else {
                        if (i == (update_project_data_list.size - 1)) {
                            update_project_data_list.add(projectStatus)
                        }
                    }
                }
            } else {
                update_project_data_list.add(projectStatus)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}