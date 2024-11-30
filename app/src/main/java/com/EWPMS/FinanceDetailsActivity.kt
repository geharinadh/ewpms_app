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
import com.EWPMS.adapter.MyWorksListAdapter
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

class FinanceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceDetailsBinding
    //api call
    lateinit var progressDialog: Dialog

    lateinit var project_id:String
    lateinit var milestone_id:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityFinanceDetailsBinding.inflate(layoutInflater);
        setContentView(binding.root)

        project_id=intent.getStringExtra("project_id").toString()
        milestone_id=intent.getStringExtra("milestone_id").toString()
        callCommonClass()

        binding.backIconLayout.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            startActivity(Intent(this, addNewFinanceActivity::class.java)
                .putExtra("project_id",project_id)
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
       /* if (Common.isInternetAvailable(this@FinanceDetailsActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_ProjectFinancials/?id="+project_id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@FinanceDetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        my_works_list=ArrayList<MyWorksResponse>()

                        val jsonArray = JSONArray(response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val categoryName = jsonObject.optString("CategoryName")
                            val completedPercentage = jsonObject.optString("CompletedPercentage")
                            val currentProjectsID = jsonObject.optString("CurrentProjectsID")
                            val deadline = jsonObject.optString("DeadLine")
                            val noOfMileStones = jsonObject.optString("NoOfMileStones")
                            val projectName = jsonObject.optString("ProjectName")

                            // Create a new MyWorksResponse object and add it to the list
                            val workItem = MyWorksResponse(
                                categoryName,
                                completedPercentage,
                                currentProjectsID,
                                deadline,
                                noOfMileStones,
                                projectName
                            )

                            my_works_list.add(workItem)
                        }
                        Log.d("Responsee", my_works_list.size.toString())
                        if(my_works_list.size>0){
                            binding.noDataLayout.visibility= View.GONE
                            binding.myWorksRv.visibility= View.VISIBLE
                            binding.myWorksRv.adapter = MyWorksListAdapter(this@FinanceDetailsActivity, my_works_list)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.myWorksRv.visibility= View.GONE
                            binding.noDataLayout.visibility= View.VISIBLE
                            Toast.makeText(this@FinanceDetailsActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        binding.myWorksRv.visibility= View.GONE
                        binding.noDataLayout.visibility= View.VISIBLE
                        Toast.makeText(this@FinanceDetailsActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    binding.myWorksRv.visibility= View.GONE
                    binding.noDataLayout.visibility= View.VISIBLE
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this@FinanceDetailsActivity, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this@FinanceDetailsActivity, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}