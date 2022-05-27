package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.first.ridingpartnerinsiheung.data.PlaceList
import com.first.ridingpartnerinsiheung.databinding.FragmentPathListBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.startPage.StartFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.first.ridingpartnerinsiheung.views.dialog.PathDialog


class PathListFragment : Fragment() {

    private lateinit var binding : FragmentPathListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? )
    : View? {
        binding = FragmentPathListBinding.inflate(inflater, container, false)

        initListView()

        return binding.root
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

    private val placeList:List<PlaceList> by lazy{
        PathList(requireActivity()).read()
    }
}