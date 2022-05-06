package com.first.ridingpartnerinsiheung.scenarios.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentDisplayMapBinding
import com.first.ridingpartnerinsiheung.scenarios.main.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.MainViewModel


class DisplayMapFragment : Fragment() {
    lateinit var binding : FragmentDisplayMapBinding
    val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        initBinding()
        initClickListener()

        binding.testText.text = "lgt : " +activity1.getLgt().toString() +"\n"+ "경도: "+ activity1.getRtt().toString()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display_map, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
    private fun initClickListener(){
        binding.btn1.setOnClickListener {
            activity1.setFrag(0)
        }
    }

}