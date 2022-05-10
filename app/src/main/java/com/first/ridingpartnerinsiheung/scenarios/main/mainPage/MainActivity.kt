package com.first.ridingpartnerinsiheung.scenarios.main.mainPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.menuPage.StartFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFrag(StartFragment())
    }
    fun setFrag(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}