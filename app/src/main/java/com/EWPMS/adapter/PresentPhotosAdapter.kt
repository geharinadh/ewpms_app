package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.data_response.PresentPhotosResponse
import com.EWPMS.databinding.PresentPhotosListAdapterBinding

class PresentPhotosAdapter (
    private val context: Context,
    private val list: List<PresentPhotosResponse>
) : RecyclerView.Adapter<PresentPhotosAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PresentPhotosListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PresentPhotosListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val date_list=ArrayList<String>()
        for(i in list.indices) {
            date_list.add(list[i].PhotoDate.toString())
        }
        val uniqueDateList = date_list.distinct()

        holder.binding.dateTv.text=uniqueDateList[position].toString()

        val photo_list=ArrayList<String>()
            for( j in list.indices) {
                if (uniqueDateList[position].toString()== list[j].PhotoDate){
                    photo_list.add(list[j].fn.toString())
                }
            }

        holder.binding.presentPhotosRv.layoutManager = GridLayoutManager(context, 2)
        holder.binding.presentPhotosRv.adapter = PresentPhotosGridAdapter(context,photo_list)
    }

    override fun getItemCount(): Int {
        val date_list=ArrayList<String>()
        for(i in list.indices) {
            date_list.add(list[i].PhotoDate.toString())
        }
        val uniqueDateArrayList = ArrayList(date_list.distinct())

        return uniqueDateArrayList.size
    }
}