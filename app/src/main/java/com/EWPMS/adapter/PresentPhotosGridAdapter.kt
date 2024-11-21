package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.R
import com.EWPMS.databinding.LivePhotosAdapterBinding
import com.EWPMS.databinding.PresentPhotosGridLayoutBinding
import com.bumptech.glide.Glide

class PresentPhotosGridAdapter(
    private val context: Context,
    private val list: List<String>
) : RecyclerView.Adapter<PresentPhotosGridAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PresentPhotosGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PresentPhotosGridLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        Glide.with(context).load(data.toString())
            .error(context.resources.getDrawable(R.color.pic_bg))
            .into(holder.binding.fieldImg)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}