package com.EWPMS.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.EWPMS.R
import com.EWPMS.adapter.MyWorksListAdapter
import com.EWPMS.data_response.MyWorksResponse
import com.EWPMS.databinding.FragmentMyWorksBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class MyWorksFragment : Fragment() {

    private lateinit var binding: FragmentMyWorksBinding
    private lateinit var my_works_list: ArrayList<MyWorksResponse>

    //api call
    lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentMyWorksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_click_listners()
        callCommonClass()
       }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        binding.allWorksCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
        binding.allWorksTextview.setTextColor(requireContext().resources.getColor(R.color.white))

        binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
        binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
        binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

        val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
        val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
        ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_selected))
        ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
        ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

        call_Works_api("All")
    }

    private fun call_Works_api(tab_type:String) {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_MyWorksList/?id="+ AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERID).toString()+"&RType="+tab_type
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(requireContext())
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
                            binding.noDataLayout.visibility=View.GONE
                            binding.myWorksRv.visibility=View.VISIBLE
                            binding.myWorksRv.adapter = MyWorksListAdapter(requireContext(), my_works_list)
                            progressDialog.dismiss()
                        }else{
                            progressDialog.dismiss()
                            binding.myWorksRv.visibility=View.GONE
                            binding.noDataLayout.visibility=View.VISIBLE
                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        binding.myWorksRv.visibility=View.GONE
                        binding.noDataLayout.visibility=View.VISIBLE
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    binding.myWorksRv.visibility=View.GONE
                    binding.noDataLayout.visibility=View.VISIBLE
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun tab_click_listners() {
       binding.allWorksCardview.setOnClickListener {
           binding.allWorksCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.allWorksTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
           val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
           ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_selected))
           ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
           ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

           call_Works_api("All")
           }

        binding.ongoingCardview.setOnClickListener {
           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.allWorksCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.allWorksTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

            val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
            val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
            ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_selected))
            ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_unselected))
            ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

            call_Works_api("OnGoing")
       }

        binding.completedCardview.setOnClickListener {
           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.allWorksCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.allWorksTextview.setTextColor(requireContext().resources.getColor(R.color.black))

            val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
            val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
            ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_selected))
            ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
            ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_unselected))

            call_Works_api("Completed")
       }

    }
}