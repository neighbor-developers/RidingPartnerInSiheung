package com.first.ridingpartnerinsiheung.scenarios.main.maps

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModel
import com.first.ridingpartnerinsiheung.data.Date
import com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment.RidingFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat

class RidingViewModel: ViewModel() {

    var mLocation : Location? = null

    var currentTime = MutableStateFlow(
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    )

    lateinit var timeHandler: MyHandler

    private var sumDistance = 0
    private var time = 0
    private var speed = 0

    var befLat: Double = 0.0
    var befLon: Double = 0.0
    private var curLat: Double = 0.0
    private var curLon: Double = 0.0

    fun getDisSpeed(){
        timeHandler = MyHandler()
        timeHandler.sendEmptyMessage(0)
    }

    inner class MyHandler : Handler(){
        private var time = 0
        private var distance = 0
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            this.sendEmptyMessageDelayed(0, 1000)
            time ++

            if(time %3==0){

                curLat = mLocation!!.latitude
                curLon = mLocation!!.longitude

                val calDis = CalDistance()
                distance = calDis.getDistance(befLat, befLon, curLat, curLon).toInt()
                distance = ((distance*100)/100.0).toInt()
                sumDistance += distance

                speed = distance/time
                speed = ((speed*100)/100.0).toInt()

                befLon = curLon
                befLat = curLat
            }
        }
    }

}