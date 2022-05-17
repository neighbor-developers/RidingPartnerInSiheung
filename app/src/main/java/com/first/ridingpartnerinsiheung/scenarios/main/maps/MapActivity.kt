package com.first.ridingpartnerinsiheung.scenarios.main.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment.RidingFragment

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        setFragment(RidingFragment())
    }
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun setRentalFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}