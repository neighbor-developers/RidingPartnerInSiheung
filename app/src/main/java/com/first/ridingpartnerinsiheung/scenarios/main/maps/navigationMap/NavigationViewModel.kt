package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.graphics.Color
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.api.bikepath.ApiObject2
import com.first.ridingpartnerinsiheung.api.bikepath.Path
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.scenarios.main.maps.model.CalcModel
import com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap.CalDistance
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import kotlin.math.round

class NavigationViewModel : ViewModel() {
    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    var mLocation = MutableStateFlow<Location?>(null)
    private var _event = MutableSharedFlow<NavigationEvent>()
    var navigationEvent = _event.asSharedFlow()
    var calcModel = CalcModel()

    // 라이딩 뷰모델에서 가져온 변수들 추후 수정 예정
    var sumDistance = MutableStateFlow(0.0)
    var averSpeed = MutableStateFlow(0.0)

    var speed = MutableStateFlow(0.0)
    var timer = MutableStateFlow(0)

    var kcal = MutableStateFlow(0.0)

    var befLatLng = LatLng(0.0, 0.0)
    var curLatLng = LatLng(0.0, 0.0)

    val distanceText = sumDistance.map {
        "${round(it/10) /100} km"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val averSpeedText = averSpeed.map {
        "${round(it/10) /100} km/h"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val speedText = speed.map {
        "${round(it/10) /100}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    val timerText = timer.map {
        "${it/3600} : ${it / 60} : ${it%60}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    private lateinit var calDisSpeedJob : Job

    fun generatePath(naverMap: NaverMap, start: String, destination: String) {
        viewModelScope.launch {
            val routeSummary = getPathSuspend(start, destination)
            drawPath(naverMap, routeSummary!!)
        }
    }

    suspend fun getPathSuspend(start: String, destination: String) = runCatching {
        ApiObject2.retrofitService.getPathSuspend(
            start, // "126.9820673,37.4853855,name=이수역 7호선",
            destination, // "126.9803409,37.5029146,name=동작역 4호선",
        )
    }.getOrNull()?.routes?.get(0)?.summary

    private fun drawPath(naverMap: NaverMap, summary: Path.RouteSummary): PathOverlay {
        val path = PathOverlay()

        val startLatLng = LatLng(
            summary.start.location.split(",")[0].toDouble(),
            summary.start.location.split(",")[1].toDouble()
        )

        val waypoints = summary.road_summary.map {
            LatLng(
                it.location.split(",")[0].toDouble(),
                it.location.split(",")[1].toDouble()
            )
        }

        val endLatLng = summary.end.location.split(",").let {
            LatLng(it[0].toDouble(), it[1].toDouble())
        }

        val fullPath: List<LatLng> = listOf(startLatLng) + waypoints + endLatLng

        path.coords = fullPath
        path.color = Color.BLUE
        path.map = naverMap

        return path
    }

    sealed class NavigationEvent{
        object StartNavigation : NavigationEvent()
        object StopNavigation : NavigationEvent()
        object SaveNavigation : NavigationEvent()
        object GeneratePath: NavigationEvent()
        data class PostFailuer(var exception: Exception) : NavigationEvent()
    }

    fun startNavigation() = viewModelScope.launch { _event.emit(NavigationEvent.StartNavigation) }
    fun stopNavigation() = viewModelScope.launch { _event.emit(NavigationEvent.StopNavigation) }
    fun saveNavigation() = viewModelScope.launch { _event.emit(NavigationEvent.SaveNavigation) }
    fun generatePath() = viewModelScope.launch { _event.emit(NavigationEvent.GeneratePath) }
    fun postFailuer(e :Exception) = viewModelScope.launch { _event.emit(NavigationEvent.PostFailuer(e)) }


    // 라이딩 뷰모델에서 가져온 함수 추후 수정예정
    fun calDisSpeed() {

        calDisSpeedJob = viewModelScope.launch(Dispatchers.Default) {
            var distance : Double
            val calDis = CalDistance()

            while(true){
                delay(1000) // 1초마다 타이머 증가

                timer.value += 1

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
        kcal.value = calcModel.calculateKcal(averSpeed.value, timer.value, 60.0)
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
}