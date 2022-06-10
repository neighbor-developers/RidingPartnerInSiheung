package com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.data.RidingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.round

class RidingViewModel: ViewModel() {

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    private var _event = MutableSharedFlow<RidingEvent>()
    var event = _event.asSharedFlow()
    private var _navigationEvent = MutableSharedFlow<NavigationEvent>()
    var navigationEvent = _navigationEvent.asSharedFlow()

    var mLocation = MutableStateFlow<Location?>(null)

    var sumDistance = MutableStateFlow(0.0)
    var averSpeed = MutableStateFlow(0.0)

    var speed = MutableStateFlow(0.0)
    var timer = MutableStateFlow(0)

    var kcal = MutableStateFlow(0.0)

    var befLatLng = LatLng(0.0, 0.0)
    var curLatLng = LatLng(0.0, 0.0)

    val distanceText = sumDistance.map {
        "${round(it/10)/100} km"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val averSpeedText = averSpeed.map {
        "${round(it/10)/100} km/h"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val speedText = speed.map {
        "${round(it/10)/100}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val timerText = timer.map {
        "${it/3600} : ${it / 60} : ${it%60}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    private lateinit var calDisSpeedJob : Job

    fun calDisSpeed() {

        calDisSpeedJob = viewModelScope.launch(Dispatchers.Default) {
            var distance : Double
            val calDis = CalDistance()

            while(true){
                delay(1000) // 1초마다 타이머 증가

                timer.value+=1

                if(timer.value % 3 == 0) { // 3초마다 속도, 거리 업데이트
                    curLatLng = LatLng(mLocation.value!!.latitude, mLocation.value!!.longitude)

                    distance = calDis.getDistance(
                        befLatLng.latitude,
                        befLatLng.longitude,
                        curLatLng.latitude,
                        curLatLng.longitude
                    )

                    sumDistance.value += distance // 총 주행거리 누적

                    speed.value = distance / 3 * 3.6 //k/h
//                    speed.value = ((speed.value * 100) / 100.0) // 순간 속도

                    averSpeed.value = sumDistance.value / timer.value *3.6 // 평균 속도

                    befLatLng = curLatLng
                }
            }
        }
    }
    fun stopCal() {
        kcal.value = calculateKcal(averSpeed.value, timer.value, 60.0)
        viewModelScope.launch {
            calDisSpeedJob.cancelAndJoin()
        }
    }
    fun getTimeNow() : String{
        return System.currentTimeMillis().let { current ->
            SimpleDateFormat("yyyy/MM/dd HH:mm").format(current)
        }
    }
    fun saveData(onFailure : () -> Unit, data : RidingData, time: String){
        db.collection(user)
            .document(time)
            .set(data)
            .addOnSuccessListener {
                /*no-op*/
            }
            .addOnFailureListener{
                postFailuer(it)
            }
    }
    fun calculateKcal(averageSpeed: Double, savedTimer: Int, weight: Double): Double {
        // 평속을 칼로리 소비계수로 전환
        val changeKcal: Double = when (averageSpeed) {
            in 13.0..15.0 -> 0.065
            in 16.0..18.0 -> 0.0783
            in 19.0..21.0 -> 0.0939
            in 22.0..23.0 -> 0.113
            in 24.0..25.0 -> 0.124
            26.0 -> 0.136
            in 27.0..28.0 -> 0.149
            in 29.0..30.0 -> 0.163
            31.0 -> 0.179
            in 32.0..33.0 -> 0.196
            in 34.0..36.0 -> 0.215
            in 37.0..39.0 -> 0.259
            40.0 -> 0.311
            else -> 0.01
        }
        return changeKcal * savedTimer * weight
    }

    sealed class RidingEvent{
        object StartRiding : RidingEvent()
        object StopRiding : RidingEvent()
        object SaveRiding : RidingEvent()
        data class PostFailuer(var exception: Exception) : RidingEvent()
    }
    fun startRiding() = viewModelScope.launch { _event.emit(RidingEvent.StartRiding) }
    fun stopRiding() = viewModelScope.launch { _event.emit(RidingEvent.StopRiding) }
    fun saveRiding() = viewModelScope.launch { _event.emit(RidingEvent.SaveRiding) }
    fun postFailuer(e :Exception) = viewModelScope.launch { _event.emit(RidingEvent.PostFailuer(e)) }

    sealed class NavigationEvent{
        object StartNavigation : NavigationEvent()
        object StopNavigation : NavigationEvent()
        object SaveNavigation : NavigationEvent()
        object GetPath: NavigationEvent()
    }

    fun startNavigation() = viewModelScope.launch { _navigationEvent.emit(NavigationEvent.StartNavigation) }
    fun stopNavigation() = viewModelScope.launch { _navigationEvent.emit(NavigationEvent.StopNavigation) }
    fun saveNavigation() = viewModelScope.launch { _navigationEvent.emit(NavigationEvent.SaveNavigation) }
    fun getPath() = viewModelScope.launch { _navigationEvent.emit(NavigationEvent.GetPath) }
}