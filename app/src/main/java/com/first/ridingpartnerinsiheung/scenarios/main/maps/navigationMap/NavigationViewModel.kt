package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.graphics.Color
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.api.bikepath.ApiObject2
import com.first.ridingpartnerinsiheung.api.bikepath.Path
import com.first.ridingpartnerinsiheung.scenarios.main.maps.CalcModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class NavigationViewModel : ViewModel() {

    val calcModel = CalcModel()
    var mLocation = MutableStateFlow<Location?>(null)
    private var _event = MutableSharedFlow<NavigationEvent>()
    var navigationEvent = _event.asSharedFlow()

    fun generatePath(naverMap: NaverMap, start: String, destination: String) {
        viewModelScope.launch {
            val routeSummary = getPathSuspend(start, destination)
            drawPath(naverMap, routeSummary!!)
        }
    }

    fun getTimeNow() : String{
        return System.currentTimeMillis().let { current ->
            SimpleDateFormat("yyyy/MM/dd HH:mm").format(current)
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
}