package com.first.ridingpartnerinsiheung.scenarios.main.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment.RentalLocationFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment.RidingFragment

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        var data :Int
        data = intent.getIntExtra("key",0)
        setFragment(data)
    }
    private fun setFragment(data : Int){

        val transaction = supportFragmentManager.beginTransaction()
        when(data){
            1 -> transaction.replace(R.id.mainframe, RentalLocationFragment())
            0 -> transaction.replace(R.id.mainframe, RidingFragment())
        }

        transaction.addToBackStack(null)
        transaction.commit()
    }
//    private fun setRentalFragment(fragment:Fragment){
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.mainframe, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }
}