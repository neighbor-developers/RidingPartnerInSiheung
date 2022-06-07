package com.first.ridingpartnerinsiheung.scenarios.main.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.maps.rentalMap.RentalLocationFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap.RidingFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.routeSearchPage.RouteSearchFragment

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val to = intent.getStringExtra("to")

        if (to=="riding"){
            setFragment(RidingFragment())
        }else if(to=="rental"){
            setFragment(RentalLocationFragment())
        }else if(to=="routeSearch"){
            setFragment(RouteSearchFragment())
        }
    }
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}