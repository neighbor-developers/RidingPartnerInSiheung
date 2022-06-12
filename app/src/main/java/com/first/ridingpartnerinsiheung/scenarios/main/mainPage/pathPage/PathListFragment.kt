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
import com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap.NavigationFragment
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
            showPathDialog(placeList[i].placeTxt, placeList[i].photo, placeList[i].routePhoto, placeList[i].path)
        }
    }

    private fun showPathDialog(pathName : String, pathImage : Int, routeImage: Int, path: ArrayList<String>){
        val dialog = PathDialog(requireContext())
        dialog.start(pathName, pathImage, routeImage)
        dialog.setOnClickListener(object:PathDialog.DialogOKCLickListener{
            override fun onOKClicked() {
                val bundle = Bundle()

                val startParam = path[0]
                val destinationParam = path[path.size - 1]

                bundle.putString("startParam", startParam)
                bundle.putString("destinationParam", destinationParam)

                if (path.size > 2) {
                    val wayPointParam = path.filterIndexed { index, s -> index != 0 && index != (path.size - 1) }
                    bundle.putString("wayPointParam", wayPointParam.joinToString(separator = "|"))
                }

                val intent = Intent(requireContext(), MapActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
    }

    private val placeList:List<PlaceList> by lazy{
        PathList(requireActivity()).read()
    }
}