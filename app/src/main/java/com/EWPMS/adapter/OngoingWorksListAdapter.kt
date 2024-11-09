package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.databinding.OngoingWorksListAdapterBinding

class OngoingWorksListAdapter  (
    private val context: Context,
    private val list: List<String>
) : RecyclerView.Adapter<OngoingWorksListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: OngoingWorksListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OngoingWorksListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

    }

    override fun getItemCount(): Int {
        return list.size
    }
}