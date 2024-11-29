package com.EWPMS.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.R
import com.EWPMS.data_response.DashboardWorkResponse
import com.EWPMS.data_response.MileStoneResponse
import com.EWPMS.data_response.ProjectDataResponse
import com.EWPMS.databinding.MilestonesListAdapterBinding
import com.EWPMS.databinding.UpdateProjectProgressAdapterBinding
import com.finowizx.CallBackInterface.CallBackData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProjectDataAdapter (
    private val context: Context,
    private val list: List<ProjectDataResponse>,
    private val task_status: CallBackData
) : RecyclerView.Adapter<ProjectDataAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: UpdateProjectProgressAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UpdateProjectProgressAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]
        holder.binding.startDateTv.text=data.StartDate.toString()
        holder.binding.endDateTv.text=data.EndDate.toString()
        holder.binding.completedTv.setText("\u20B9"+data.CompletedAmount.toString())
        holder.binding.amountTv.text="\u20B9"+data.MileStone_Amount.toString()
        if(data.Remarks.isNotEmpty()) {
            holder.binding.remarksTv.text = data.Remarks.toString()
        }

        holder.binding.mileStonesTv.text = (position+1).toString()

        holder.binding.completedTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                task_status.getTaskStatus(
                    position.toString()+","+holder.binding.completedTv.text.toString().trim(),
                    holder.binding.remarksTv.text.toString().trim()
                )
                hideKeyboard(context,holder.binding.completedTv)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        holder.binding.remarksTv.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.remarks_layout)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            // Set the width of the dialog
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

            var remarks_tv = dialog.findViewById<EditText>(R.id.remarks_tv)
            var cancelBtn = dialog.findViewById<ImageView>(R.id.cancel_button)
            var updateBtn = dialog.findViewById<Button>(R.id.update_btn)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            updateBtn.setOnClickListener {
                if(remarks_tv.text.toString().isNotEmpty()) {
                    holder.binding.remarksTv.setText(remarks_tv.text.toString())
                    task_status.getTaskStatus(
                        position.toString()+holder.binding.completedTv.text.toString().trim(),
                        holder.binding.remarksTv.text.toString().trim()
                    )
                    dialog.dismiss()
                }else{
                    Toast.makeText(context,
                        context.getString(R.string.enter_remarks),Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }

    }

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}