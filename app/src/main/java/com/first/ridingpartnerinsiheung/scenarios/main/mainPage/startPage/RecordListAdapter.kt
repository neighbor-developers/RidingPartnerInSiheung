package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.startPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R

class RecordListAdapter(val context: Context, private val recordList: ArrayList<RecordList>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return recordList.size
    }

    override fun getItem(p0: Int): Any {
        return recordList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_record_item, null)

        var date = view.findViewById<TextView>(R.id.date)
        var distance = view.findViewById<TextView>(R.id.distance)
        var avgSpeed = view.findViewById<TextView>(R.id.avgSpeed)
        var time = view.findViewById<TextView>(R.id.time)

        val record = recordList[p0]

        date.text = record.date
        distance.text = record.distance
        avgSpeed.text = record.avgSpeed
        time.text = record.time

        return view
    }
}