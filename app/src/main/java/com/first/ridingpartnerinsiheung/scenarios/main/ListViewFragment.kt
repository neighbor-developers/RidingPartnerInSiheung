package com.first.ridingpartnerinsiheung.scenarios.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.PlaceList
import com.first.ridingpartnerinsiheung.databinding.FragmentListViewBinding


class ListViewFragment : Fragment() {

    val viewModel by viewModels<Main2ViewModel>()
    lateinit var binding : FragmentListViewBinding

    var placeList = arrayListOf<PlaceList>(
        PlaceList("오이도 빨간 등대", "23.7km", "oido",55,55),
        PlaceList("옥구공원", "23.7km", "okgu",46,23),
        PlaceList("월곶포구", "23.7km", "oido",42,43),
        PlaceList("시흥 갯골 생태공원", "23.7km", "sangtae",324,34),
        PlaceList("그린웨이", "23.7km", "oido",123,23),
        PlaceList("호조벌", "23.7km", "dream",456,123),
        PlaceList("연꽃테마파크", "23.7km", "lotus",343,32),
        PlaceList("물왕저수지", "23.7km", "mulwang",343,11),
        PlaceList("보통천 자전거길", "23.7km", "botong",455,22),
        PlaceList("시화방조제", "23.7km", "sihwa",432,1)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        initBinding()
        initClickListener()

        binding.listView.isNestedScrollingEnabled = false
        val placeAdapter = MainListAdapter(requireContext(), placeList)
        binding.listView.adapter = placeAdapter

        binding.listView.setOnItemClickListener { adapterView, view, i, l ->
            activity1.setArgument(placeList[i].lng,placeList[i].rtt)
            activity1.setFrag(1)
        }

        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
    private fun initClickListener(){
        binding.back.setOnClickListener {
            startActivity(Intent(requireActivity(),MainActivity::class.java ))
        }
    }
}