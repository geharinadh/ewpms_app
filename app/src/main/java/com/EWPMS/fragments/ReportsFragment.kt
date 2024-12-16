package com.EWPMS.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.EWPMS.R
import com.EWPMS.databinding.FragmentDashBoardBinding
import com.EWPMS.databinding.FragmentReportsBinding
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences
import com.EWPMS.utilities.Common
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class ReportsFragment : Fragment() {

    //api call
    lateinit var progressDialog: Dialog
    private lateinit var binding: FragmentReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentReportsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCommonClass()
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_reports_api()
    }

    private fun call_reports_api() {
        if (Common.isInternetAvailable(requireContext())) {
            progressDialog.show()
            val url = "http://www.vmrda.gov.in/ewpms_api/api/Usp_get_WorkReports/?id="+ AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERID).toString()
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
                        val AheadPercentage = obj.getString("CompletedPercentage")
                        val AheadofShedule = obj.getString("Completedprojects")

                        val BeyondPercentage = obj.getString("BeyondPercentage")
                        val BeyondShedule = obj.getString("BeyondShedule")

                        val AsperPercentage = obj.getString("AsperPercentage")
                        val AsperShedule = obj.getString("AsperShedule")

                        val TotalWork = obj.getString("TotalCount")

                        val CompletedMileStone = obj.getString("CompletedM")
                        val CompletedMileStonePercentage = obj.getString("CompletedMPercentage")

                        val OngoingMileStone = obj.getString("OngoingM")
                        val OnGoingMileStonePercentage = obj.getString("OnGoingMPercentage")

                        val DelayMileStone = obj.getString("DelayM")
                        val DelayMileStonePercentage = obj.getString("DelaymPercentage")

                        val TotalMileStones = obj.getString("TotalMileStones")

                        val AmountsCommitted = obj.getString("AmountsCommitted")
                        val AmountsCommittedPercentage = obj.getString("CommittedPercentage")

                        val AmountsReleased = obj.getString("AmountsReleased")
                        val AmountsReleasedPercentage = obj.getString("ReleasedPercentage")

                        val AmountsPending = obj.getString("AmountsPending")
                        val AmountsPendingPercentage= obj.getString("PendingPercentage")

                        val TotalAmount= obj.getString("TotalFinance")

                        if(AheadPercentage.toString().isNotEmpty()){
                            progressDialog.dismiss()
                            binding.aheadScheduleTv.text=AheadofShedule.toString()
                            binding.aheadScheduleProgress.progress=AheadPercentage.toFloat()

                            binding.beyondScheduleProgress.progress=BeyondPercentage.toFloat()
                            binding.beyondScheduleTv.text=BeyondShedule.toString()

                            binding.asPerScheduleProgress.progress=AsperPercentage.toFloat()
                            binding.asPerScheduleTv.text=AsperShedule.toString()

                            binding.totalWorkTv.text= "Total Works: $TotalWork"

                            binding.completedProgress.progress=CompletedMileStonePercentage.toFloat()
                            binding.completedMsTv.text=CompletedMileStone.toString()

                            binding.ongoingProgress.progress=OnGoingMileStonePercentage.toFloat()
                            binding.ongoingMsTv.text=OngoingMileStone.toString()

                            binding.delayProgress.progress=DelayMileStonePercentage.toFloat()
                            binding.delayMsTv.text=DelayMileStone.toString()

                            binding.totalMilestoneTv.text="Total Milestones: "+TotalMileStones.toString()

                            binding.committedProgress.progress=AmountsCommittedPercentage.toFloat()
                            binding.committedAmtTv.text="Rs."+AmountsCommitted.toString()

                            binding.releasedProgress.progress=AmountsReleasedPercentage.toFloat()
                            binding.releasedAmtTv.text="Rs."+AmountsReleased.toString()

                            binding.pendingProgress.progress=AmountsPendingPercentage.toFloat()
                            binding.pendingAmtTv.text="Rs."+AmountsPending.toString()

                            binding.totalAmountTv.text="Total Amounts(In Lakhs): Rs."+TotalAmount.toString()
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