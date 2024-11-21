package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.data_response.DashboardWorkResponse
import com.EWPMS.databinding.LivePhotosAdapterBinding

class LivePhotosAdapter (
    private val context: Context,
    private val list: List<String>
) : RecyclerView.Adapter<LivePhotosAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LivePhotosAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LivePhotosAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.fileSize.text=data.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}