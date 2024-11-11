package com.EWPMS.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.EWPMS.R
import com.EWPMS.adapter.MyWorksAdapter
import com.EWPMS.adapter.OngoingWorksListAdapter
import com.EWPMS.databinding.FragmentDashBoardBinding
import com.EWPMS.databinding.FragmentMyWorksBinding

class MyWorksFragment : Fragment() {

    private lateinit var binding: FragmentMyWorksBinding
    private lateinit var my_works_list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentMyWorksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_click_listners()
        binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
        binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.white))

        binding.pendingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
        binding.pendingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
        binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

        val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
        val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
        ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_selected))
        ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
        ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

        my_works_list=ArrayList<String>()
        my_works_list.add("0")
        my_works_list.add("0")
        my_works_list.add("0")
        my_works_list.add("0")
        binding.myWorksRv.adapter = OngoingWorksListAdapter(requireContext(), my_works_list)
    }

    private fun tab_click_listners() {
       binding.ongoingCardview.setOnClickListener {
           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.pendingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.pendingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
           val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
           ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_selected))
           ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
           ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

           my_works_list=ArrayList<String>()
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           binding.myWorksRv.adapter = OngoingWorksListAdapter(requireContext(), my_works_list)
       }

        binding.pendingCardview.setOnClickListener {
           binding.pendingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.pendingTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.black))

            val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
            val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
            ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_selected))
            ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_unselected))
            ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_unselected))

           my_works_list=ArrayList<String>()
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           binding.myWorksRv.adapter = OngoingWorksListAdapter(requireContext(), my_works_list)
       }

        binding.completedCardview.setOnClickListener {
           binding.completedCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue_dark))
           binding.completedTextview.setTextColor(requireContext().resources.getColor(R.color.white))

           binding.ongoingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.ongoingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

           binding.pendingCardview.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
           binding.pendingTextview.setTextColor(requireContext().resources.getColor(R.color.black))

            val color_unselected = ContextCompat.getColor(requireContext(), R.color.white_text)
            val color_selected = ContextCompat.getColor(requireContext(), R.color.bottom_navigation)
            ImageViewCompat.setImageTintList(binding.dotThree, ColorStateList.valueOf(color_selected))
            ImageViewCompat.setImageTintList(binding.dotTwo, ColorStateList.valueOf(color_unselected))
            ImageViewCompat.setImageTintList(binding.dotOne, ColorStateList.valueOf(color_unselected))

           my_works_list=ArrayList<String>()
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           my_works_list.add("0")
           binding.myWorksRv.adapter = OngoingWorksListAdapter(requireContext(), my_works_list)
       }

    }
}