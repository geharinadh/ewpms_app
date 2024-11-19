package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.WorkDetailActivity
import com.EWPMS.data_response.MyWorksResponse
import com.EWPMS.databinding.MyWorksListAdapterBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyWorksListAdapter  (
    private val context: Context,
    private val list: List<MyWorksResponse>
) : RecyclerView.Adapter<MyWorksListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MyWorksListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyWorksListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.categoryNameTv.text=data.categoryName.toString()
        holder.binding.projectName.text=data.projectName.toString()
        holder.binding.percentageTv.text=data.completedPercentage.toString()+"%"
        holder.binding.mileStonesTv.text=data.noOfMileStones.toString()

        //date convertion
        val inputDate = data.deadline.toString()

        // Step 1: Define the input format
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        // Step 2: Parse the input date
        val date = LocalDate.parse(inputDate, inputFormatter)

        // Step 3: Define the output format
        val outputFormatter = DateTimeFormatter.ofPattern("MMM, dd yyyy")

        // Step 4: Format the date
        val formattedDate = date.format(outputFormatter)
        holder.binding.deadlineTv.text=formattedDate.toString()

        holder.binding.progressBar.autoAnimate=true
        holder.binding.progressBar.progress=data.completedPercentage.toFloat()

        holder.binding.workDetailCardView.setOnClickListener {
            context.startActivity(Intent(context,WorkDetailActivity::class.java).putExtra("project_id",data.currentProjectsID.toString()))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}