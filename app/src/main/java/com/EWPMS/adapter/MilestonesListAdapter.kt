package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.data_response.MileStoneResponse
import com.EWPMS.databinding.MilestonesListAdapterBinding
import com.finowizx.CallBackInterface.CallBackData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MilestonesListAdapter(
    private val context: Context,
    private val live_photo_position: String,
    private val list: List<MileStoneResponse>,
    private val live_photo_list: ArrayList<String>,
    private val task_status: CallBackData
) : RecyclerView.Adapter<MilestonesListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MilestonesListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MilestonesListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]
        holder.binding.mileStonesTv.text="Milestones "+(position+1).toString()

        holder.binding.mileStonesLayout.setOnClickListener{
            holder.binding.livePhotosRv.visibility = View.GONE
            holder.binding.uploadLivePhotosLayout.visibility = View.GONE
            holder.binding.uploadLivePhotosLayout.visibility= View.VISIBLE
        }

        holder.binding.uploadLivePhotosBtn.setOnClickListener {
            if(live_photo_position.isNotEmpty()) {
                if (live_photo_position.toInt() != position) {
                    holder.binding.livePhotosRv.visibility = View.GONE
                    holder.binding.uploadLivePhotosLayout.visibility = View.GONE
                }
            }
            task_status.getTaskStatus(data.MileStoneID.toString(), position.toString())
        }

        if(live_photo_list.size>0){
            if(position==live_photo_position.toInt()) {
                holder.binding.uploadLivePhotosLayout.visibility=View.VISIBLE
                holder.binding.livePhotosRv.visibility=View.VISIBLE
                holder.binding.livePhotosRv.adapter = LivePhotosAdapter(context,live_photo_list)
            }
        }
        //date convertion
        val inputDate1 = data.StartDate.toString()
        val inputDate2 = data.EndDate.toString()

        // Step 1: Define the input format
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        // Step 2: Parse the input date
        val date1 = LocalDate.parse(inputDate1, inputFormatter)
        val date2 = LocalDate.parse(inputDate2, inputFormatter)

        // Step 3: Define the output format
        val outputFormatter = DateTimeFormatter.ofPattern("MMM, dd yyyy")

        // Step 4: Format the date
        val formattedDate1 = date1.format(outputFormatter)
        val formattedDate2 = date2.format(outputFormatter)

        holder.binding.startDateTv.text=formattedDate1.toString()
        holder.binding.endDateTv.text=formattedDate2.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}