package com.EWPMS

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.EWPMS.adapter.FinanceListAdapter
import com.EWPMS.adapter.MyWorksListAdapter
import com.EWPMS.data_response.FinanceDataResponse
import com.EWPMS.data_response.MyWorksResponse
import com.EWPMS.databinding.ActivityFinanceDetailsBinding
import com.EWPMS.databinding.UpdateProjectProgressLayoutBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.lang.reflect.Array

class FinanceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceDetailsBinding
    //api call
    lateinit var progressDialog: Dialog

    lateinit var project_id:String
    lateinit var milestone_id:String
    var milestone_name=""

    var finace_list=ArrayList<FinanceDataResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityFinanceDetailsBinding.inflate(layoutInflater);
        setContentView(binding.root)

        try {
            project_id = intent.getStringExtra("project_id").toString()
            milestone_id = intent.getStringExtra("milestone_id").toString()
            milestone_name = intent.getStringExtra("milestone_name").toString()
        }catch (e:Exception){
            e.printStackTrace()
        }

        callCommonClass()

        binding.backIconLayout.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            startActivity(Intent(this, addNewFinanceActivity::class.java)
                .putExtra("project_id",project_id)
                .putExtra("milestone_name",milestone_name)
                .putExtra("milestone_id",milestone_id))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this@FinanceDetailsActivity)

        call_finance_list()
    }

    private fun call_finance_list() {
        if (Common.isInternetAvailable(this@FinanceDetailsActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_ProjectFinancials/?id="+project_id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@FinanceDetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        finace_list=ArrayList<FinanceDataResponse>()

                        val jsonArray = JSONArray(response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val BillAmount = jsonObject.optString("BillAmount")
                            val BillNo = jsonObject.optString("BillNo")
                            val PaidOn1 = jsonObject.optString("PaidOn1")
                            val Remarks = jsonObject.optString("Remarks")
                            val Status = jsonObject.optString("Status")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = FinanceDataResponse(
                                BillAmount,
                                BillNo,
                                PaidOn1,
                                Remarks,
                                Status)

                            finace_list.add(workItem)
                        }
                        Log.d("Responsee", finace_list.size.toString())
                        if(finace_list.size>0){
                            binding.noDataLayout.visibility= View.GONE
                            binding.myWorksRv.visibility= View.VISIBLE
                            binding.myWorksRv.adapter = FinanceListAdapter(this@FinanceDetailsActivity, project_id,milestone_id,milestone_name,finace_list)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.myWorksRv.visibility= View.GONE
                            binding.noDataLayout.visibility= View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        binding.myWorksRv.visibility= View.GONE
                        binding.noDataLayout.visibility= View.VISIBLE
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    binding.myWorksRv.visibility= View.GONE
                    binding.noDataLayout.visibility= View.VISIBLE
                    Log.e("VolleyError", "Request failed", error)
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this@FinanceDetailsActivity, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}