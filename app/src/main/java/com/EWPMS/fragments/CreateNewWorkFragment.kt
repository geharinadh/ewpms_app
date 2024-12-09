package com.EWPMS.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.EWPMS.Interface.DatePickerCallBack
import com.EWPMS.R
import com.EWPMS.data_response.CategoryResponse
import com.EWPMS.data_response.SEresponse
import com.EWPMS.databinding.FragmentCreateNewWorkBinding
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CreateNewWorkFragment : Fragment(), DatePickerCallBack {

    private lateinit var binding: FragmentCreateNewWorkBinding

    //api call
    lateinit var progressDialog: Dialog
    lateinit var datePickerDialog: DatePickerDialog

    var date_from=""
    var tender_premium_status=""

    private lateinit var category_list: ArrayList<CategoryResponse>
    private lateinit var district_list: ArrayList<CategoryResponse>
    private lateinit var mandals_list: ArrayList<CategoryResponse>
    private lateinit var villege_list: ArrayList<CategoryResponse>

    var se_list=ArrayList<SEresponse>()
    var division_list=ArrayList<SEresponse>()
    var dee_list=ArrayList<SEresponse>()
    var aee_list=ArrayList<SEresponse>()
    var ee_list=ArrayList<SEresponse>()

    var contractorList=ArrayList<SEresponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCreateNewWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCommonClass()
        tab_click_listeners()

        onclick_listeners()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event here
                if (shouldInterceptBack()) {
                    // Perform any custom action, like showing a dialog
                    ExitFunctions()
                } else {
                    // Allow default back press behavior
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }

        // Add the callback to the OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun ExitFunctions() {
        if(binding.tabOneLayout.visibility==View.VISIBLE){
            requireActivity().onBackPressed()
        }else if(binding.tabTwoLayout.visibility==View.VISIBLE){
            binding.tabTwoLayout.visibility=View.GONE
            binding.tabOneLayout.visibility=View.VISIBLE
        }else if(binding.tabThreeLayout.visibility==View.VISIBLE){
            binding.tabThreeLayout.visibility=View.GONE
            binding.tabTwoLayout.visibility=View.VISIBLE
            binding.createProjectBtn.visibility=View.GONE
            binding.continueBtn.visibility=View.VISIBLE
        }
    }

    private fun shouldInterceptBack(): Boolean {
        // Logic to decide whether to intercept the back press
        return true // Example: always intercept
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())
        datePickerDialog = Common.getPreviousDatePicker(requireActivity(),this)

        call_category_api()
    }

    private fun onclick_listeners() {
        binding.agreementDateTv.setOnClickListener {
            date_from="agreement"
            datePickerDialog.show()
        }

        binding.adminSanctionDateTv.setOnClickListener {
            date_from="admin"
            datePickerDialog.show()
        }

        binding.technicalSanctionDateTv.setOnClickListener {
            date_from="tech"
            datePickerDialog.show()
        }

        binding.approvalDateTv.setOnClickListener {
            date_from="approval"
            datePickerDialog.show()
        }

        binding.grandedUptoEt.setOnClickListener {
            date_from="grand"
            datePickerDialog.show()
        }

        binding.startDateTv.setOnClickListener {
            date_from = "start"
            datePickerDialog.show()
        }

        binding.endDateTv.setOnClickListener {
            date_from = "end"
            datePickerDialog.show()
        }

        binding.plusLayout.setOnClickListener {
            if (binding.ecvEt.text.toString().trim().isNotEmpty()) {
                if (binding.tenderPremiumEt.text.toString().trim().isNotEmpty()) {
                    tender_premium_status="Excess"
                    var value = binding.ecvEt.text.toString().trim()
                    var percentage = binding.tenderPremiumEt.text.toString().trim()
                    var formula= percentage.toFloat()*value.toFloat()/100
                    var result_value=formula.toFloat()+value.toFloat()
                    binding.tenderCostEdt.text = result_value.toString()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.please_enter_tender_premium), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_ecv), Toast.LENGTH_SHORT).show()
            }
        }

        binding.minusLayout.setOnClickListener {
            if (binding.ecvEt.text.toString().trim().isNotEmpty()) {
                if (binding.tenderPremiumEt.text.toString().trim().isNotEmpty()) {
                    tender_premium_status="Less"
                    var value = binding.ecvEt.text.toString().trim()
                    var percentage = binding.tenderPremiumEt.text.toString().trim()
                    var formula= percentage.toFloat()*value.toFloat()/100
                    var result_value=value.toFloat()-formula.toFloat()
                    binding.tenderCostEdt.text = result_value.toString()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.please_enter_tender_premium), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_ecv), Toast.LENGTH_SHORT).show()
            }
        }

        binding.spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    call_mandal_api(district_list[position-1].id.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }

        binding.spinnerMandal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    call_villege_api(mandals_list[position-1].id.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }

        binding.spinnerContractor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    call_contractor_details_api(contractorList[position-1].LoginName.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }
    }

    private fun tab_click_listeners() {

        binding.continueBtn.setOnClickListener {
            if (binding.tabOneLayout.visibility == View.VISIBLE) {
                continue_btn_one()
            } else {
                continue_btn_two()
            }
        }

        binding.createProjectBtn.setOnClickListener {
            if (binding.spinnerDistrict.selectedItemPosition != 0) {
                if (binding.spinnerMandal.selectedItemPosition != 0) {
                    if (binding.spinnerVillege.selectedItemPosition != 0) {
                        if (binding.spinnerSE.selectedItemPosition != 0) {
                            if (binding.spinnerDivision.selectedItemPosition != 0) {
                                if (binding.spinnerEE.selectedItemPosition != 0) {
                                    if (binding.spinnerDEE.selectedItemPosition != 0) {
                                        if (binding.spinnerAEE.selectedItemPosition != 0) {
                                            if (binding.spinnerContractor.selectedItemPosition != 0) {
                                                if (binding.startDateTv.text.toString()
                                                        .isNotEmpty()
                                                ) {
                                                    if (binding.endDateTv.text.toString()
                                                            .isNotEmpty()
                                                    ) {
                                                        create_project_api()
                                                    } else {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            getString(R.string.please_select_end_date),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.please_enter_start_date),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.please_select_contractor),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.please_select_aee),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.please_select_dee),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.please_select_ee),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.please_select_dv),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.please_select_se),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_select_villege),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_select_madal),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_district),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun continue_btn_two() {
        if (binding.adminSactionAmtEt.text.toString().isNotEmpty()) {
            if (binding.adminSanctionDateTv.text.toString().isNotEmpty()) {
                if (binding.technicalSanctionAmtEt.text.toString().isNotEmpty()) {
                    if (binding.technicalSanctionDateTv.text.toString().isNotEmpty()) {

                        binding.tabOneLayout.visibility = View.GONE
                        binding.tabTwoLayout.visibility = View.GONE
                        binding.tabThreeLayout.visibility = View.VISIBLE

                        binding.continueBtn.visibility = View.GONE
                        binding.createProjectBtn.visibility = View.VISIBLE

                        binding.tabOneCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        binding.tabTwoCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        binding.tabThreeCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.orange
                            )
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_select_tech_sanction),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_enter_tech_amt),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_admin_date),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_administrative_sanction_amount),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun continue_btn_one() {
        if(binding.projectNameEt.text.toString().isNotEmpty()){
            if(binding.spinnerCategory.selectedItemPosition!=0){
              if(binding.estimatedCostEt.text.toString().isNotEmpty()){
                  if(binding.agreementNoEt.text.toString().isNotEmpty()) {
                      if(binding.agreementDateTv.text.toString().isNotEmpty()) {
                              if(binding.ecvEt.text.toString().isNotEmpty()) {
                                  if(binding.tenderPremiumEt.text.toString().isNotEmpty()) {
                                      if(binding.tenderCostEdt.text.toString().isNotEmpty()) {
                                          binding.tabOneLayout.visibility=View.GONE
                                          binding.tabThreeLayout.visibility=View.GONE
                                          binding.tabTwoLayout.visibility=View.VISIBLE

                                          binding.continueBtn.visibility=View.VISIBLE
                                          binding.createProjectBtn.visibility=View.GONE

                                          binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                                          binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
                                          binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
                                      }else{
                                          Toast.makeText(requireContext(), getString(R.string.please_enter_tender_ecv),Toast.LENGTH_SHORT).show()
                                      }
                                  }else{
                                      Toast.makeText(requireContext(), getString(R.string.please_enter_tender_premium),Toast.LENGTH_SHORT).show()
                                  }
                              }else{
                                  Toast.makeText(requireContext(), getString(R.string.please_enter_ecv),Toast.LENGTH_SHORT).show()
                              }
                      }else{
                          Toast.makeText(requireContext(), getString(R.string.please_select_agreement_date),Toast.LENGTH_SHORT).show()
                      }
                  }else{
                      Toast.makeText(requireContext(), getString(R.string.please_enter_agreement_no),Toast.LENGTH_SHORT).show()
                  }
              }else{
                  Toast.makeText(requireContext(), getString(R.string.please_enter_estimated_cost),Toast.LENGTH_SHORT).show()
              }
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_select_a_categoty),Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_enter_a_project_name),Toast.LENGTH_SHORT).show()
        }
    }

    private fun create_project_api() {
        if (Common.isInternetAvailable(requireContext())) {
            try {
                progressDialog.show()

                var se_name=se_list[binding.spinnerSE.selectedItemPosition-1].DisplayName.toString()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Ins_CurrentProjects"

                // JSON body
                val requestBody = JSONObject()
                requestBody.put("VillageMasterID", villege_list[binding.spinnerVillege.selectedItemPosition-1].id.toString())
                requestBody.put("ContractorMasterID", contractorList[binding.spinnerContractor.selectedItemPosition-1].LoginName.toString())
                requestBody.put("DivisionMasterID", (binding.spinnerDivision.selectedItemPosition-1).toString())
                requestBody.put("ProjectName", binding.projectNameEt.text.toString().trim())
                requestBody.put("Description", "")
                requestBody.put("EstimatedCostValue", binding.estimatedCostEt.text.toString().trim().toString())
                requestBody.put("ProjectCost", binding.estimatedCostEt.text.toString().trim().toString())
                requestBody.put("InceptionDate",binding.approvalDateTv.text.toString().trim())
                requestBody.put("ProjectCategoryID",  category_list[binding.spinnerCategory.selectedItemPosition-1].id.toString())
                requestBody.put("Latitude", binding.latitudeTxt.text.toString().trim().toString())
                requestBody.put("Longitude", binding.longitudeTxt.text.toString().trim().toString())
                requestBody.put("AdministrativeSanction",binding.adminSanctionDetailEt.text.toString().trim())
                requestBody.put("AdministrativeSanctionAmount", binding.adminSactionAmtEt.text.toString().trim().toString())
                requestBody.put("AdministrativeSanctionOn", binding.adminSanctionDateTv.text.toString().trim())
                requestBody.put("TechnicalSanction", binding.technicalSanctionDetailsEt.text.toString().trim())
                requestBody.put("TechnicalSanctionAmount", binding.technicalSanctionAmtEt.text.toString().trim().toString())
                requestBody.put("TechnicalSanctionOn", binding.technicalSanctionDateTv.text.toString().trim())
                requestBody.put("Status", "")
                requestBody.put("AgreementNo", binding.agreementNoEt.text.toString().trim())
                requestBody.put("AgreementDate", binding.agreementDateTv.text.toString().trim())
                requestBody.put("TimePeriod", binding.timePeriodEt.text.toString().trim())
                requestBody.put("ExtensionofTime","")
                requestBody.put("EOTOrderNo","")
                requestBody.put("RevisedCost", binding.revisedCostEt.text.toString().trim().toString())
                requestBody.put("WorkingCost", "")
                requestBody.put("ApprovalDate", binding.approvalDateTv.text.toString().trim())
                requestBody.put("Supplementaryorderno",binding.suppOrderEt.text.toString().trim())
                requestBody.put("EstimatedContractValue", binding.ecvEt.text.toString().trim())
                requestBody.put("tcv", binding.tenderCostEdt.text.toString().trim())
                requestBody.put("Mobile", binding.mobileEt.text.toString().trim())
                requestBody.put("Email", binding.emailEt.text.toString().trim())
                requestBody.put("Address", binding.emailEt.text.toString().trim())
                requestBody.put("Grantedupto",binding.grandedUptoEt.text.toString().trim())
                requestBody.put("Extendedupto","" )
                requestBody.put("WorkOrderNo", "")
                requestBody.put("WorkOrderDate", binding.approvalDateTv.text.toString().trim())
                requestBody.put("CurrentProjectsID","0")
                requestBody.put("DistrictMasterID",  district_list[binding.spinnerDistrict.selectedItemPosition-1].id.toString())
                requestBody.put("MandalMasterID",  mandals_list[binding.spinnerMandal.selectedItemPosition-1].id.toString())
                requestBody.put("PanchayatMasterID",villege_list[binding.spinnerVillege.selectedItemPosition-1].id.toString())
                requestBody.put("SE",  se_list[binding.spinnerSE.selectedItemPosition-1].DisplayName.toString())
                requestBody.put("DEE",  dee_list[binding.spinnerDEE.selectedItemPosition-1].DisplayName.toString())
                requestBody.put("AEE",  aee_list[binding.spinnerAEE.selectedItemPosition-1].DisplayName.toString())
                requestBody.put("EE", ee_list[binding.spinnerEE.selectedItemPosition-1].DisplayName.toString().trim())
                requestBody.put("StartDate", binding.startDateTv.text.toString().trim())
                requestBody.put("EndDate", binding.endDateTv.text.toString().trim())
                requestBody.put("Administrative_Sanction_by", binding.spinnerSanctionName.selectedItem.toString().trim())
                requestBody.put("Technical_Sanction_by", binding.spinnerTechSanctionName.selectedItem.toString().trim())
                requestBody.put("Tender_Premium", binding.tenderPremiumEt.text.toString().trim().toString())
                requestBody.put("Tender_PremiumType",tender_premium_status)

                println("Response_: $url"+requestBody.toString())

                val requestBodyString = requestBody.toString()

                // Create a POST request
                val stringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        Log.d("API Response", response)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.new_project_created_successfully),Toast.LENGTH_SHORT).show()
                        binding.tabThreeLayout.visibility = View.GONE
                        binding.tabTwoLayout.visibility = View.GONE
                        binding.tabOneLayout.visibility = View.VISIBLE

                        binding.createProjectBtn.visibility = View.GONE
                        binding.continueBtn.visibility = View.VISIBLE

                        binding.tabOneCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.orange
                            )
                        )
                        binding.tabTwoCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white_text
                            )
                        )
                        binding.tabThreeCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white_text
                            )
                        )
                    },
                    Response.ErrorListener { error ->
                        // Handle error response
                        Log.e("API Error", error.toString())
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    override fun getBody(): ByteArray {
                        return requestBodyString.toByteArray(Charsets.UTF_8)
                    }
                }

            // Add the request to the Volley request queue
                val requestQueue = Volley.newRequestQueue(context)
                requestQueue.add(stringRequest)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.response_failure_please_try_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_check_with_the_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun call_category_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_getCategories/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            category_list = ArrayList<CategoryResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("id")
                                val name = jsonObject.optString("name")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = CategoryResponse(
                                    id,
                                    name)

                                category_list.add(workItem)
                            }

                            if (category_list.size > 0) {
                                var category_name_list=ArrayList<String>()
                                category_name_list.add("Choose..")
                                for(i in category_list.indices){
                                  category_name_list.add(category_list[i].name.toString())
                                }
                                lateinit var category_adapter: ArrayAdapter<String>
                                category_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, category_name_list)
                                binding.spinnerCategory.adapter = category_adapter

                                var admin_sanction_list=ArrayList<String>()
                                admin_sanction_list.add("Choose..")
                                admin_sanction_list.add("Metropolitan Commissioner")
                                admin_sanction_list.add("VMRDA Authority")
                                admin_sanction_list.add("Govt of AP")

                                lateinit var admin_sanction_adapter: ArrayAdapter<String>
                                admin_sanction_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, admin_sanction_list)
                                binding.spinnerSanctionName.adapter = admin_sanction_adapter

                                var technical_sanction_list=ArrayList<String>()
                                technical_sanction_list.add("Choose..")
                                technical_sanction_list.add("Chief Engineer")
                                technical_sanction_list.add("Executive Engineer")
                                technical_sanction_list.add("Superintendent Engineer")

                                lateinit var technical_sanction_adapter: ArrayAdapter<String>
                                technical_sanction_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, technical_sanction_list)
                                binding.spinnerTechSanctionName.adapter = technical_sanction_adapter

                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            call_district_api()
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun call_district_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Districts/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            district_list = ArrayList<CategoryResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("DId")
                                val name = jsonObject.optString("DName")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = CategoryResponse(
                                    id,
                                    name)

                                district_list.add(workItem)
                            }

                            if (district_list.size > 0) {
                                var district_name_list=ArrayList<String>()
                                district_name_list.add("Choose..")
                                for(i in district_list.indices){
                                    district_name_list.add(district_list[i].name.toString())
                                }
                                lateinit var district_adapter: ArrayAdapter<String>
                                district_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, district_name_list)
                                binding.spinnerDistrict.adapter = district_adapter
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            call_se_api()
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun call_mandal_api(id:String) {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Mandals/?id="+id
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            mandals_list = ArrayList<CategoryResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("Mid")
                                val name = jsonObject.optString("Mname")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = CategoryResponse(
                                    id,
                                    name)

                                mandals_list.add(workItem)
                            }

                            if (mandals_list.size > 0) {
                                var mandal_name_list=ArrayList<String>()
                                mandal_name_list.add("Choose..")
                                for(i in mandals_list.indices){
                                    mandal_name_list.add(mandals_list[i].name.toString())
                                }
                                lateinit var mandals_adapter: ArrayAdapter<String>
                                mandals_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mandal_name_list)
                                binding.spinnerMandal.adapter = mandals_adapter
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun call_villege_api(id:String) {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Villagess/?id="+id
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            villege_list = ArrayList<CategoryResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("Vid")
                                val name = jsonObject.optString("Vname")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = CategoryResponse(
                                    id,
                                    name)

                                villege_list.add(workItem)
                            }

                            if (villege_list.size > 0) {
                                var villege_name_list=ArrayList<String>()
                                villege_name_list.add("Choose..")
                                for(i in villege_list.indices){
                                    villege_name_list.add(villege_list[i].name.toString())
                                }
                                lateinit var vilelge_adapter: ArrayAdapter<String>
                                vilelge_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, villege_name_list)
                                binding.spinnerVillege.adapter = vilelge_adapter
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun call_se_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=se"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
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
                        se_list_name.add("Choose..")

                        for(i in se_list.indices){
                            se_list_name.add(se_list[i].DisplayName)
                        }

                        if(se_list.size>0){
                            lateinit var se_adapter: ArrayAdapter<String>
                            se_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, se_list_name)
                            binding.spinnerSE.adapter = se_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_div_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_div_list_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=div"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
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
                        div_list_name.add("Choose..")
                        for(i in division_list.indices){
                            div_list_name.add(division_list[i].DisplayName)
                        }

                        if(division_list.size>0){
                            lateinit var div_adapter: ArrayAdapter<String>
                            div_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, div_list_name)
                            binding.spinnerDivision.adapter = div_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_dee_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_dee_list_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=de"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
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
                        dee_list_name.add("Choose..")
                        for(i in dee_list.indices){
                            dee_list_name.add(dee_list[i].DisplayName)
                        }

                        if(dee_list.size>0){
                            lateinit var de_adapter: ArrayAdapter<String>
                            de_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, dee_list_name)
                            binding.spinnerDEE.adapter = de_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                    call_aee_list_api()
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_aee_list_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=ae"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
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
                        aee_list_name.add("Choose..")
                        for(i in aee_list.indices){
                            aee_list_name.add(aee_list[i].DisplayName)
                        }

                        if(aee_list.size>0){
                            lateinit var ae_adapter: ArrayAdapter<String>
                            ae_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, aee_list_name)
                            binding.spinnerAEE.adapter = ae_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                        call_ee_list_api()
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_ee_list_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_bind_FilterUsers/?id=ee"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        ee_list=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("DisplayName")
                            val LoginName = jsonObject.optString("LoginName")
                            // Create a new MyWorksResponse object and add it to the list
                            val eeItem = SEresponse(
                                DisplayName, LoginName)

                            ee_list.add(eeItem)
                        }

                        var ee_list_name=ArrayList<String>()
                        ee_list_name.add("Choose..")
                        for(i in ee_list.indices){
                            ee_list_name.add(ee_list[i].DisplayName)
                        }

                        if(ee_list.size>0){
                            lateinit var ee_adapter: ArrayAdapter<String>
                            ee_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, ee_list_name)
                            binding.spinnerEE.adapter = ee_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                        call_contractor_list_api()
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_contractor_list_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Contractors/"
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        contractorList=ArrayList<SEresponse>()
                        val jsonArray = JSONArray(response)
                        Log.d("Response", response)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Extract the required fields from the JSON object
                            val DisplayName = jsonObject.optString("CName")
                            val LoginName = jsonObject.optString("Cid")
                            // Create a new MyWorksResponse object and add it to the list
                            val aeeItem = SEresponse(
                                DisplayName, LoginName)

                            contractorList.add(aeeItem)
                        }

                        var contractor_list_name=ArrayList<String>()
                        contractor_list_name.add("Choose..")
                        for(i in contractorList.indices){
                            contractor_list_name.add(contractorList[i].DisplayName)
                        }

                        if(contractorList.size>0){
                            lateinit var contractor_adapter: ArrayAdapter<String>
                            contractor_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, contractor_list_name)
                            binding.spinnerContractor.adapter = contractor_adapter
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()

                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun call_contractor_details_api(id:String) {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Get_Contractor_Details/?id="+id
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        val obj = jsonArray.getJSONObject(0)
                        Log.d("Response", response)

                        // Assuming `Password` or `UserType` exists
                        val MobileNo = obj.getString("MobileNo")
                        val Email = obj.getString("Email")
                        val AddressLine1 = obj.getString("AddressLine1")

                            progressDialog.dismiss()
                            binding.mobileEt.setText(MobileNo.toString())
                            binding.emailEt.setText(Email.toString())
                            binding.addressEt.setText(AddressLine1.toString())

                        binding.mobileEt.isEnabled=false
                        binding.emailEt.isEnabled=false
                        binding.addressEt.isEnabled=false
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }

                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    override fun date_picker_data(value: String?, key: String?) {
        when (date_from) {
            "agreement" -> {
                binding.agreementDateTv.text = value.toString()
            }
            "admin" -> {
                binding.adminSanctionDateTv.text = value.toString()
            }
            "tech" -> {
                binding.technicalSanctionDateTv.text = value.toString()
            }
            "approval" -> {
                binding.approvalDateTv.text = value.toString()
            }
            "grand" -> {
                binding.grandedUptoEt.text = value.toString()
            }
            "start" -> {
                binding.startDateTv.text = value.toString()
            }
            "end" -> {
                binding.endDateTv.text = value.toString()
            }
        }
    }

}