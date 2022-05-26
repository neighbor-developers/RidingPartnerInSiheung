package com.first.ridingpartnerinsiheung.scenarios.main.mainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.startPage.StartFragment
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage.MyPageFragment
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage.PathListFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFrag(StartFragment())

        bottomNavigationView = findViewById<View>(R.id.bottomNavi) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> setFrag(StartFragment())
                R.id.myPage-> setFrag(MyPageFragment())
                R.id.pathList -> setFrag(PathListFragment())
                R.id.riding -> toMap("riding")
                R.id.rental -> toMap("rental")
            }
            true
        })

    }
    fun setFrag(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun toMap(to : String){
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("to", to)
        startActivity(intent)
    }
}