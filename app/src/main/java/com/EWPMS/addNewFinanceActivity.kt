package com.EWPMS

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.Interface.DatePickerCallBack
import com.EWPMS.databinding.ActivityAddNewFinanceBinding
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class addNewFinanceActivity : AppCompatActivity(),DatePickerCallBack{
    private lateinit var binding: ActivityAddNewFinanceBinding
    //date picker
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var progressDialog: Dialog

    lateinit var project_id:String
    lateinit var milestone_id:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityAddNewFinanceBinding.inflate(layoutInflater);
        setContentView(binding.root)

        project_id=intent.getStringExtra("project_id").toString()
        milestone_id=intent.getStringExtra("milestone_id").toString()
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
    }

    private fun call_add_api() {
        if (Common.isInternetAvailable(this@addNewFinanceActivity)) {
            try {
                progressDialog.show()
                val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_Ins_ProjectFinancials/"

                // Create the JSON payload
                val jsonBody = JSONObject()
                jsonBody.put("Id", project_id)
                jsonBody.put("MileStoneID",milestone_id)
                jsonBody.put("BillNo",binding.billNoTv.text.toString().trim())
                jsonBody.put("BillAmount",binding.billAmtTv.text.toString().trim())
                jsonBody.put("PaidOn",binding.paidOnTv.text.toString().trim())
                jsonBody.put("Remarks",binding.financialRemarkTv.text.toString().trim())
                jsonBody.put("Status",binding.spinnerStatus.selectedItem.toString().trim())


                // Initialize Volley RequestQueue
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)

                // Create a JsonObjectRequest
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    Response.Listener { response ->
                        Log.e("API Error", response.toString())
                            progressDialog.dismiss()
                            // Handle the successful response
                            Toast.makeText(
                                this,
                                getString(R.string.finance_added_success),
                                Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@addNewFinanceActivity,FinanceDetailsActivity::class.java).putExtra("project_id",project_id).putExtra("milestone_id",milestone_id))
                            finish()
                    },
                    Response.ErrorListener { error ->
                        // Handle error
                        Log.e("API Error", error.toString())
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                getString(R.string.upload_failed) + ": ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                )

                // Add the request to the RequestQueue
                requestQueue.add(jsonObjectRequest)
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
}