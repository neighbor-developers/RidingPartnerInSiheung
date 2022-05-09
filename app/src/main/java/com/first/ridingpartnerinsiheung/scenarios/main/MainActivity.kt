package com.first.ridingpartnerinsiheung.scenarios.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.ActivityMainBinding
import com.first.ridingpartnerinsiheung.scenarios.main.fragment.DisplayMapFragment
import com.first.ridingpartnerinsiheung.scenarios.main.fragment.ListViewFragment

class MainActivity : AppCompatActivity() {

    var lgt: Int = 0
    var rtt : Int = 0

    val viewModel by viewModels<MainViewModel>()
    val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setFrag(0)
    }

    fun setArgument(lg : Int, rt:Int){
        lgt = lg
        rtt = rt
    }

    @JvmName("getLgt1")
    fun getLgt() : Int{
        return lgt
    }

    @JvmName("getRtt1")
   fun getRtt() :Int{
        return rtt
    }

     fun btnClick2(view: View){
        finish()

    }
    fun setFrag(fragNum : Int) {
        val ft = supportFragmentManager.beginTransaction()

        when(fragNum){
            0 -> {
                ft.replace(R.id.mainFrame, ListViewFragment()).commit()
            }

            1 -> {
                ft.replace(R.id.mainFrame, DisplayMapFragment()).commit()
            }
        }
    }
}