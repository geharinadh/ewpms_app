package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.addNewFinanceActivity
import com.EWPMS.data_response.FinanceDataResponse
import com.EWPMS.databinding.FinanceDetailsListAdpaterBinding

class FinanceListAdapter (
    private val context: Context,
    private val project_id: String,
    private val milestone_id: String,
    private val list: List<FinanceDataResponse>
) : RecyclerView.Adapter<FinanceListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: FinanceDetailsListAdpaterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FinanceDetailsListAdpaterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.billNoTv.text=data.BillNo.toString()
        holder.binding.billAmtTv.text="\u20b9"+data.BillAmount.toString()
        holder.binding.paidDateTv.text=data.PaidOn1.toString()
        holder.binding.remarksTv.text=data.Remarks.toString()
        holder.binding.statusTv.text=data.Status.toString()

        holder.binding.editImg.setOnClickListener {
            context.startActivity(Intent(context, addNewFinanceActivity::class.java).putExtra("project_id",project_id)
                .putExtra("milestone_id",milestone_id).putExtra("position",position.toString()))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}