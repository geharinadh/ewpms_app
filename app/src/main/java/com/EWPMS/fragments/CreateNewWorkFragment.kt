package com.EWPMS.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.EWPMS.R
import com.EWPMS.adapter.MyWorksAdapter
import com.EWPMS.databinding.FragmentCreateNewWorkBinding
import com.EWPMS.databinding.FragmentDashBoardBinding

class CreateNewWorkFragment : Fragment() {

    private lateinit var binding: FragmentCreateNewWorkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCreateNewWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_click_listeners()
    }

    private fun tab_click_listeners() {

        binding.continueBtn.setOnClickListener {
           if(binding.tabOneLayout.visibility==View.VISIBLE){
               binding.tabOneLayout.visibility=View.GONE
               binding.tabThreeLayout.visibility=View.GONE
               binding.tabTwoLayout.visibility=View.VISIBLE

               binding.continueBtn.visibility=View.VISIBLE
               binding.createProjectBtn.visibility=View.GONE

               binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
               binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
           }else{
               binding.tabOneLayout.visibility=View.GONE
               binding.tabTwoLayout.visibility=View.GONE
               binding.tabThreeLayout.visibility=View.VISIBLE

               binding.continueBtn.visibility=View.GONE
               binding.createProjectBtn.visibility=View.VISIBLE

               binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
           }
        }

        binding.createProjectBtn.setOnClickListener {
            binding.tabThreeLayout.visibility=View.GONE
            binding.tabTwoLayout.visibility=View.GONE
            binding.tabOneLayout.visibility=View.VISIBLE

            binding.tabOneCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            binding.tabTwoCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
            binding.tabThreeCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_text))
        }

    }

}