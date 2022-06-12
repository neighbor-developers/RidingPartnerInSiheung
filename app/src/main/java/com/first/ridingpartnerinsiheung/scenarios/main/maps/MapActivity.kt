package com.first.ridingpartnerinsiheung.scenarios.main.maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap.NavigationFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.rentalMap.RentalLocationFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap.RidingFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.routeSearchPage.RouteSearchFragment

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val to = intent.getStringExtra("to")

        when (to) {
            "riding" -> setFragment(RidingFragment())
            "rental" -> setFragment(RentalLocationFragment())
            "routeSearch" -> setFragment(RouteSearchFragment())
            "navi" -> setFragment(NavigationFragment())
        }

        val bundle = intent.extras

        if (bundle != null) {
            setFragment(NavigationFragment(), bundle)
        }
    }

    fun setFragment(fragment: Fragment, bundle: Bundle? = null){
        val transaction = supportFragmentManager.beginTransaction()
        if (bundle != null) {
            fragment.arguments = bundle;
        }
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        finish()
    }
}