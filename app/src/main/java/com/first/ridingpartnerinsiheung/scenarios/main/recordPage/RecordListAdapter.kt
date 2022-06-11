package com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordListAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordList.RecordList

class RecordListAdapter(val context: Context, val recordList: ArrayList<RecordList>) :
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
        val date = view.findViewById<TextView>(R.id.date)
        val record = recordList[p0]

        date.text = record.date

        return view
    }
}