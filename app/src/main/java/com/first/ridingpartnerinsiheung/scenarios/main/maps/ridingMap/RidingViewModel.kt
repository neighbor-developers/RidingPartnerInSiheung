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

class RidingViewModel: ViewModel() {

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    private var _event = MutableSharedFlow<RidingEvent>()
    var event = _event.asSharedFlow()

    var mLocation : Location? = null

    var sumDistance = MutableStateFlow(0.0)
    var averSpeed = MutableStateFlow(0.0)

    var speed = MutableStateFlow(0.0)
    var timer = MutableStateFlow(0)

    var kcal = MutableStateFlow(0)

    var befLatLng = LatLng(0.0, 0.0)
    var curLatLng = LatLng(0.0, 0.0)

    val distanceText = sumDistance.map {
        "주행 거리 : " + it
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val averSpeedText = averSpeed.map {
        "평균 속도 : "+ it
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val speedText = speed.map {
        "순간 속도 : "+ it
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val timerText = timer.map {
        "주행 시간 : "+"${it/3600} : ${it / 60} : ${it%60}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    private lateinit var calDisSpeedJob : Job

    fun calDisSpeed() {

        calDisSpeedJob = viewModelScope.launch(Dispatchers.Default) {
            var distance : Double

            while(true){
                delay(1000) // 1초마다 타이머 증가

                timer.value+=1

                if(timer.value % 2 == 0) { // 3초마다 속도, 거리 업데이트
                    curLatLng = LatLng(mLocation!!.latitude, mLocation!!.longitude)

                    val calDis = CalDistance()
                    distance = calDis.getDistance(
                        befLatLng.latitude,
                        befLatLng.longitude,
                        curLatLng.latitude,
                        curLatLng.longitude
                    )
                    distance = (distance * 100) / 100.0

                    sumDistance.value += distance // 총 주행거리 누적

                    speed.value = distance / 3
                    speed.value = ((speed.value * 100) / 100.0) // 순간 속도

                    averSpeed.value = sumDistance.value / timer.value // 평균 속도

                    befLatLng = curLatLng
                }
            }
        }
    }
    fun stopCal() {
        viewModelScope.launch {
            calDisSpeedJob.cancelAndJoin()
        }
    }
    fun getTimeNow() : String{
        return System.currentTimeMillis().let { current ->
            SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(current)
        }
    }
    fun saveData(onFailure : () -> Unit, data : RidingData){
        val time = getTimeNow()
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
}