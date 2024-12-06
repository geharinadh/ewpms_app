package com.EWPMS.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.EWPMS.Interface.DatePickerCallBack
import com.EWPMS.R
import com.EWPMS.data_response.CategoryResponse
import com.EWPMS.databinding.FragmentCreateNewWorkBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class CreateNewWorkFragment : Fragment(), DatePickerCallBack {

    private lateinit var binding: FragmentCreateNewWorkBinding

    //api call
    lateinit var progressDialog: Dialog
    lateinit var datePickerDialog: DatePickerDialog

    private lateinit var category_list: ArrayList<CategoryResponse>

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
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())
        datePickerDialog = Common.getPreviousDatePicker(requireActivity(),this)

        call_category_api()
    }

    private fun onclick_listeners() {
        binding.agreementDateTv.setOnClickListener {
            datePickerDialog.show()
        }

        binding.plusLayout.setOnClickListener {
            var value=binding.tenderPremiumEt.text.toString()
            if(value.isNotEmpty()) {
                value=(value.toInt()+1).toString()
                binding.tenderPremiumEt.setText(value)
            }else{
                binding.tenderPremiumEt.setText("1")
            }
        }

        binding.plusLayout.setOnClickListener {
            var value=binding.tenderPremiumEt.text.toString()
            if(value.isNotEmpty()) {
                if(value!="1") {
                    value = (value.toInt() - 1).toString()
                    binding.tenderPremiumEt.setText(value)
                }else{
                    binding.tenderPremiumEt.setText("0")
                }
            }
        }

    }

    private fun tab_click_listeners() {

        binding.continueBtn.setOnClickListener {
           if(binding.tabOneLayout.visibility==View.VISIBLE){
               continue_btn_one()
           }else{
               binding.tabOneLayout.visibility=View.GONE
               binding.tabTwoLayout.visibility=View.GONE
               binding.tabThreeLayout.visibility=View.VISIBLE

               binding.continueBtn.visibility=View.GONE
               binding.createProjectBtn.visibility=View.VISIBLE

               binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
           }
        }

        binding.createProjectBtn.setOnClickListener {
            binding.tabThreeLayout.visibility=View.GONE
            binding.tabTwoLayout.visibility=View.GONE
            binding.tabOneLayout.visibility=View.VISIBLE

            binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
            binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
        }

    }

    private fun continue_btn_one() {
        if(binding.projectNameEt.text.toString().isNotEmpty()){
            if(binding.spinnerCategory.selectedItemPosition!=0){
              if(binding.estimatedCostEt.text.toString().isNotEmpty()){
                  if(binding.agreementNoEt.text.toString().isNotEmpty()) {
                      if(binding.agreementDateTv.text.toString().isNotEmpty()) {
                          if(binding.timePeriodEt.text.toString().isNotEmpty()) {
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
                              Toast.makeText(requireContext(), getString(R.string.please_enter_time_period),Toast.LENGTH_SHORT).show()
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

    override fun date_picker_data(value: String?, key: String?) {
        binding.agreementDateTv.text=value.toString()
    }

}