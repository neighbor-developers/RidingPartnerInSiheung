package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.PlaceList

class PathListAdapter(val context: Context, val placeList : List<PlaceList>) : BaseAdapter() {
    override fun getView(position : Int, convertView: View?, parent : ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.main_lv_item, null)

        val photo = view.findViewById<ImageView>(R.id.placeImg)
        val placeName = view.findViewById<TextView>(R.id.placeTxt)
        val distance = view.findViewById<TextView>(R.id.placeDistance)

        val place = placeList[position]
        //val resourceId = context.resources.getIdentifier(place.photo, "drawable", context.packageName)
        photo.setImageResource(place.photo)
        placeName.text = place.placeTxt
        distance.text = place.placeDistance

        return view
    }
    override fun getCount(): Int {
        return placeList.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position : Int): Any {
        return placeList[position]
    }
}