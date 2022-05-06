package com.first.ridingpartnerinsiheung.scenarios.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.first.ridingpartnerinsiheung.databinding.FragmentDisplayMapBinding


class DisplayMapFragment : Fragment() {
    private var binding : FragmentDisplayMapBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        binding = FragmentDisplayMapBinding.inflate(inflater,container,false)
        val view1 = binding!!.root

        val activity1 = activity as MainActivity2
        binding!!.btn1.setOnClickListener {
            activity1.setFrag(0)
        }
        binding!!.testText.text = "lgt : " +activity1.getLgt().toString() +"\n"+ "경도: "+ activity1.getRtt().toString()
        return view1
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}