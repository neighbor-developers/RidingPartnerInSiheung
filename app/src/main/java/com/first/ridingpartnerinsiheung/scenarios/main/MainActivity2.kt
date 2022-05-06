package com.first.ridingpartnerinsiheung.scenarios.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.first.ridingpartnerinsiheung.R

class MainActivity2 : AppCompatActivity() {

    var lgt: Int = 0
    var rtt : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main2)
        setFrag(0)
    }

    fun setArgument(lg : Int, rt:Int){
        lgt = lg
        rtt = rt
    }

    @JvmName("getLgt1")
    public fun getLgt() : Int{
        return lgt
    }

    @JvmName("getRtt1")
    public fun getRtt() :Int{
        return rtt
    }

    public fun btnClick2(view: View){
        finish()

    }
    public fun setFrag(fragNum : Int) {
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