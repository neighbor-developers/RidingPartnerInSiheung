package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RidingData

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recode)

        val time = intent.getStringExtra("time")
        val data = intent.getSerializableExtra("data")

        setFragment(RidingFinishFragment(),time!!, data as RidingData)
    }

    fun setFragment(fragment: Fragment, time : String, data: RidingData) {
        //  RidingFinishFragment에서 데이터 수신
        val bundle = Bundle()
        bundle.putString("time", time)
        bundle.putSerializable("data", data)
        fragment.arguments = bundle


        // 데이터가 셋팅된 프래그먼트 띄우기
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}