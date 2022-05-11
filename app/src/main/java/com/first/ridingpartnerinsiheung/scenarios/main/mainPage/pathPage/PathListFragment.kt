package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.PlaceList
import com.first.ridingpartnerinsiheung.databinding.FragmentPathListBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.menuPage.StartFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.first.ridingpartnerinsiheung.views.dialog.PathDialog


class PathListFragment : Fragment() {

    private lateinit var binding : FragmentPathListBinding

    var placeList = arrayListOf<PlaceList>(
        PlaceList("오이도 빨간 등대", "23.7km", R.drawable.oido,55,55),
        PlaceList("옥구공원", "23.7km", R.drawable.okgu,46,23),
        PlaceList("월곶포구", "23.7km", R.drawable.oido,42,43),
        PlaceList("시흥 갯골 생태공원", "23.7km", R.drawable.sangtae,324,34),
        PlaceList("그린웨이", "23.7km", R.drawable.oido,123,23),
        PlaceList("호조벌", "23.7km", R.drawable.dream,456,123),
        PlaceList("연꽃테마파크", "23.7km", R.drawable.lotus,343,32),
        PlaceList("물왕저수지", "23.7km",R.drawable.mulwang,343,11),
        PlaceList("보통천 자전거길", "23.7km", R.drawable.botong,455,22),
        PlaceList("시화방조제", "23.7km", R.drawable.sihwa,432,1)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        binding = FragmentPathListBinding.inflate(inflater, container, false)
       // initBinding()
        initClickListener()
        initListView()

        return binding.root
    }
    private fun initClickListener(){
        binding.back.setOnClickListener {
            (activity as MainActivity).setFrag(StartFragment())
        }
    }
    private fun initListView(){
        binding.listView.isNestedScrollingEnabled = false
        val placeAdapter = PathListAdapter(requireContext(), placeList)
        binding.listView.adapter = placeAdapter

        binding.listView.setOnItemClickListener { adapterView, view, i, l ->
            showPathDialog(placeList[i].placeTxt, placeList[i].photo)
        }
    }
    private fun showPathDialog(pathName : String, pathImage : Int){
        val dialog = PathDialog(requireContext())
        dialog.start(pathName, pathImage)
        dialog.setOnClickListener(object:PathDialog.DialogOKCLickListener{
            override fun onOKClicked() {
                startActivity(Intent(requireContext(), MapActivity::class.java))
            }
        })
    }
}