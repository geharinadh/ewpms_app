package com.EWPMS

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.Interface.DatePickerCallBack
import com.EWPMS.adapter.FinanceListAdapter
import com.EWPMS.data_response.FinanceDataResponse
import com.EWPMS.databinding.ActivityAddNewFinanceBinding
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class addNewFinanceActivity : AppCompatActivity(),DatePickerCallBack{
    private lateinit var binding: ActivityAddNewFinanceBinding
    //date picker
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var progressDialog: Dialog

    lateinit var project_id:String
    lateinit var milestone_id:String
    lateinit var position:String

    var finace_list=ArrayList<FinanceDataResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityAddNewFinanceBinding.inflate(layoutInflater);
        setContentView(binding.root)

        project_id=intent.getStringExtra("project_id").toString()
        milestone_id=intent.getStringExtra("milestone_id").toString()
        position=intent.getStringExtra("position").toString()

        println("stayst "+position)
        datePickerDialog = Common.getPreviousDatePicker(this,this)
        progressDialog = Common.progressDialog(this@addNewFinanceActivity)

        binding.mileStoneNameTv.text = milestone_id.toString()

        var status_list=ArrayList<String>()
        status_list.add("Choose..")
        status_list.add("Work In Progress")
        status_list.add("Completed")
        lateinit var statusAdapter: ArrayAdapter<String>
        statusAdapter = ArrayAdapter(this, R.layout.spinner_item, status_list)
        binding.spinnerStatus.adapter = statusAdapter

        binding.backIconLayout.setOnClickListener {
            startActivity(Intent(this, FinanceDetailsActivity::class.java))
            finish()
        }

        binding.paidOnTv.setOnClickListener {
            datePickerDialog.show()
        }

        binding.deleteBtn.setOnClickListener {
            binding.mileStoneNameTv.text=""
            binding.paidOnTv.text=""
            binding.billNoTv.setText("")
            binding.billAmtTv.setText("")
            binding.financialRemarkTv.setText("")
            binding.spinnerStatus.setSelection(0)
        }

        binding.saveBtn.setOnClickListener {
            if(binding.billNoTv.text.toString().isNotEmpty()){
                if(binding.billAmtTv.text.toString().isNotEmpty()) {
                    if(binding.paidOnTv.text.toString().isNotEmpty()) {
                        if(binding.financialRemarkTv.text.toString().isNotEmpty()) {
                            if(!binding.spinnerStatus.selectedItem.equals("Choose..")) {
                                call_add_api()
                            }else{
                                Toast.makeText(this, getString(R.string.select_status),Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this, getString(R.string.enter_financial_remark),Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, getString(R.string.select_paid_date),Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, getString(R.string.enter_bill_amt),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, getString(R.string.enter_bill_no),Toast.LENGTH_SHORT).show()
            }
        }

        if(position.isNotEmpty()) {
            if(position!="null") {
                call_finance_list()
            }
        }

    }

    private fun call_add_api() {
        if (Common.isInternetAvailable(this@addNewFinanceActivity)) {
            try {
                progressDialog.show()
                val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Ins_ProjectFinancials/"

                // Create the JSON payload
                val jsonBody = JSONObject()
                jsonBody.put("Id", project_id)
                jsonBody.put("ItemID",milestone_id)
                jsonBody.put("BillNo",binding.billNoTv.text.toString().trim())
                jsonBody.put("BillAmount",binding.billAmtTv.text.toString().trim())
                jsonBody.put("PaidOn",binding.paidOnTv.text.toString().trim())
                jsonBody.put("Remarks",binding.financialRemarkTv.text.toString().trim())
                jsonBody.put("Status",binding.spinnerStatus.selectedItem.toString().trim())

                val requestQueue: RequestQueue = Volley.newRequestQueue(this)

                val jsonArrayRequest = object : JsonArrayRequest(
                    Request.Method.POST,
                    url,
                    null, // Passing null here as the body will be sent in getBody
                    { response ->
                        try {
                            // Handle the JSONArray response
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val retVal = jsonObject.getString("RetVal")
                                Log.d("Response Value", "RetVal: $retVal")
                                if (retVal == "Success") {
                                    progressDialog.dismiss()
                                    // Handle the successful response
                                    Toast.makeText(
                                        this,
                                        getString(R.string.finance_added_success),
                                        Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@addNewFinanceActivity,FinanceDetailsActivity::class.java).putExtra("project_id",project_id).putExtra("milestone_id",milestone_id))
                                    finish()
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        getString(R.string.upload_failed),
                                        Toast.LENGTH_LONG
                                    ).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, FinanceDetailsActivity::class.java))
        finish()
    }

    override fun date_picker_data(value: String?, key: String?) {
        binding.paidOnTv.text=value.toString()
    }

    private fun call_finance_list() {
        if (Common.isInternetAvailable(this@addNewFinanceActivity)) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_ProjectFinancials/?id="+project_id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this@addNewFinanceActivity)
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
                            binding.billNoTv.setText(finace_list[position.toInt()].BillNo.toString())
                            binding.billAmtTv.setText("\u20B9"+finace_list[position.toInt()].BillAmount.toString())
                            binding.paidOnTv.text = finace_list[position.toInt()].PaidOn1.toString()
                            binding.financialRemarkTv.setText(finace_list[position.toInt()].Remarks.toString())
                            if(finace_list[position.toInt()].Status.toString() == "Completed") {
                                binding.spinnerStatus.setSelection(2)
                            }else{
                                binding.spinnerStatus.setSelection(1)
                            }

                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(this@addNewFinanceActivity, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

}