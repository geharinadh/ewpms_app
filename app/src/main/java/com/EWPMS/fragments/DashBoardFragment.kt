package com.EWPMS.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.EWPMS.MainActivity
import com.EWPMS.R
import com.EWPMS.adapter.MyWorksAdapter
import com.EWPMS.databinding.FragmentDashBoardBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException


class DashBoardFragment : Fragment() {

    private lateinit var binding: FragmentDashBoardBinding
    private lateinit var my_works_list: ArrayList<String>

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

        binding = FragmentDashBoardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCommonClass()
        my_works_list=ArrayList<String>()
        my_works_list.add("0")
        binding.myWorksRv.adapter = MyWorksAdapter(requireContext(), my_works_list)
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_dash_board_api()
    }

    private fun call_dash_board_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_UserDashboard_Data/?id="+AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERID).toString()
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
                        val CompletedWorks = obj.getString("CompletedWorks")
                        val OngoingWorks = obj.getString("OngoingWorks")
                        val TotalWorks = obj.getString("TotalWorks")
                        val UpcomingWorks = obj.getString("UpcomingWorks")

                        if(CompletedWorks.toString().isNotEmpty()){
                            progressDialog.dismiss()
                            binding.completedWorkCountTv.text=CompletedWorks.toString()
                            binding.ongoingWorkCountTv.text=OngoingWorks.toString()
                            binding.totalWorkCountTv.text=TotalWorks.toString()
                            binding.upcomingWorkCountTv.text=UpcomingWorks.toString()
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

    private fun call_my_works_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_UserDashboard_Data/?id="+AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERID).toString()
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
                        val CompletedWorks = obj.getString("CompletedWorks")
                        val OngoingWorks = obj.getString("OngoingWorks")
                        val TotalWorks = obj.getString("TotalWorks")
                        val UpcomingWorks = obj.getString("UpcomingWorks")

                        if(CompletedWorks.toString().isNotEmpty()){
                            progressDialog.dismiss()
                            binding.completedWorkCountTv.text=CompletedWorks.toString()
                            binding.ongoingWorkCountTv.text=OngoingWorks.toString()
                            binding.totalWorkCountTv.text=TotalWorks.toString()
                            binding.upcomingWorkCountTv.text=UpcomingWorks.toString()
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

}