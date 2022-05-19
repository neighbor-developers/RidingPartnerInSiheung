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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat

class RidingViewModel: ViewModel() {

    var mLocation : Location? = null

    var sumDistance = MutableStateFlow(0.0)
    var averSpeed = MutableStateFlow(0.0)

    var speed = MutableStateFlow(0.0)
    var timer = MutableStateFlow(0)

    var befLat: Double = 0.0
    var befLon: Double = 0.0

    var curLat: Double = 0.0
    var curLon: Double = 0.0

    var speedText = MutableStateFlow("주행 거리 :")
    var distanceText = MutableStateFlow("")
    var averSpeedText = MutableStateFlow("")

    private lateinit var calDisSpeedJob : Job

    fun calDisSpeed() {

        befLat = mLocation!!.latitude
        befLon = mLocation!!.longitude

        calDisSpeedJob = CoroutineScope(Dispatchers.Main).launch {
            var distance : Double

            while(true) {
                delay(1000) // 1초마다 타이머 증가

                timer.value++

                if(timer.value % 3 == 0) { // 3초마다 속도, 거리 업데이트
                    curLat = mLocation!!.latitude
                    curLon = mLocation!!.longitude

                    val calDis = CalDistance()
                    distance = calDis.getDistance(befLat, befLon, curLat, curLon)
                    distance = (distance * 100) / 100.0

                    sumDistance.value += distance // 총 주행거리 누적

                    speed.value = distance / 3
                    speed.value = ((speed.value * 100) / 100.0) // 순간 속도

                    averSpeed.value = sumDistance.value/timer.value // 평균 속도

                    befLon = curLon
                    befLat = curLat

                    distanceText.value = "주행 거리 : ${sumDistance.value}"
                    averSpeedText.value= "평균 속도 : ${averSpeed.value}"
                    speedText.value = "속도 : ${speed.value}"
                }
            }
        }
    }
    fun stopCal() {
        CoroutineScope(Dispatchers.Main).launch {
            calDisSpeedJob.cancelAndJoin()
        }

    }

}