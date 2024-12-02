package com.EWPMS.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.EWPMS.CategoryProjects
import com.EWPMS.MainActivity
import com.EWPMS.R
import com.EWPMS.WorkDetailActivity
import com.EWPMS.adapter.MyWorksAdapter
import com.EWPMS.data_response.DashboardWorkResponse
import com.EWPMS.data_response.MyWorksResponse
import com.EWPMS.databinding.FragmentDashBoardBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.finowizx.CallBackInterface.CallBackData
import com.google.android.gms.maps.model.Dash
import org.json.JSONArray
import org.json.JSONException


class DashBoardFragment : Fragment(),CallBackData {

    private lateinit var binding: FragmentDashBoardBinding
    private lateinit var my_works_list: ArrayList<DashboardWorkResponse>
    private lateinit var my_works_searchlist: ArrayList<DashboardWorkResponse>

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

        binding.seeMoreTv.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java).putExtra("screen","dashboard"))
            requireActivity().finish()
        }

        binding.totalWorksCard.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java).putExtra("screen","dashboard"))
            requireActivity().finish()
        }

        binding.ongoingCard.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java).putExtra("screen","ongoing"))
            requireActivity().finish()
            requireActivity().finish()
        }

        binding.completedCard.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java).putExtra("screen","completed"))
            requireActivity().finish()
        }

        binding.filterImg.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryProjects::class.java))
            requireActivity().finish()
        }

        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                my_works_searchlist = ArrayList<DashboardWorkResponse>()
                my_works_searchlist.clear()

                if (binding.searchEdt.text.toString().isNotEmpty()) {
                    if (binding.searchEdt.text.toString().length >= 2) {
                        for (i in my_works_list.indices) {
                            if (my_works_list[i].ProjectName.lowercase().substring(0, 2) == binding.searchEdt.text.toString().toLowerCase().substring(0, 2)) {
                                my_works_searchlist.add(my_works_list[i])
                            }
                        }

                        if(my_works_searchlist.size>0) {
                            binding.countOneLayout.visibility=View.GONE
                            binding.countTwoLayout.visibility=View.GONE

                            binding.noDataLayout.visibility=View.GONE
                            binding.myWorksRv.visibility=View.VISIBLE

                            binding.myWorksRv.adapter = MyWorksAdapter(requireContext(), my_works_searchlist,this@DashBoardFragment)
                        }else{
                            binding.countOneLayout.visibility=View.VISIBLE
                            binding.countTwoLayout.visibility=View.VISIBLE

                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.myWorksRv.visibility=View.GONE
                        }
                    }else{
                        binding.countOneLayout.visibility=View.VISIBLE
                        binding.countTwoLayout.visibility=View.VISIBLE

                        binding.noDataLayout.visibility=View.GONE
                        binding.myWorksRv.visibility=View.VISIBLE

                        binding.myWorksRv.adapter = MyWorksAdapter(requireContext(), my_works_list,this@DashBoardFragment)
                    }
                }
            }
        })

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

                        call_my_works_api()
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
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url =
                    "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_DashboardMyWorks/?id=" + AppSharedPreferences.getStringSharedPreference(
                        requireContext(),
                        AppConstants.USERID
                    ).toString()
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            my_works_list = ArrayList<DashboardWorkResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val categoryName = jsonObject.optString("CategoryName")
                                val completedPercentage =
                                    jsonObject.optString("CompletedPercentage")
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
                                    projectName
                                )

                                my_works_list.add(workItem)
                            }

                            if (my_works_list.size > 0) {
                                binding.noDataLayout.visibility = View.GONE
                                binding.myWorksRv.visibility = View.VISIBLE
                                binding.myWorksRv.adapter =
                                    MyWorksAdapter(requireContext(), my_works_list, this)
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.myWorksRv.visibility = View.GONE
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

    override fun getTaskStatus(id: String,position : String) {
        startActivity(Intent(context, WorkDetailActivity::class.java).putExtra("project_id",id))
    }

}