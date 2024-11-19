package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.data_response.DashboardWorkResponse
import com.EWPMS.databinding.MyWorksAdapterLayoutBinding

class MyWorksAdapter  (
    private val context: Context,
    private val list: List<DashboardWorkResponse>
) : RecyclerView.Adapter<MyWorksAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MyWorksAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyWorksAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.completedPercentage.text=data.CompletedPercentage.toString()+"%"
        holder.binding.projectNameTv.text=data.ProjectName.toString()
        holder.binding.categoryNameTv.text=data.CategoryName.toString()
        holder.binding.daysLeftTv.text=data.DaysLeft.toString()
        holder.binding.mileStonesTv.text=data.NoOfMileStones.toString()+" Milestones"
    }

    override fun getItemCount(): Int {
        return list.size
    }
}