package com.parthdesai1208.jarassignment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.parthdesai1208.jarassignment.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btn1ForScreen1.setOnClickListener {
            val nextAction = HomeFragmentDirections.goToScreen1()
            Navigation.findNavController(it).navigate(nextAction)
        }

        binding.btn2ForScreen2.setOnClickListener {
            val nextAction = HomeFragmentDirections.goToScreen2()
            Navigation.findNavController(it).navigate(nextAction)
        }
    }
}