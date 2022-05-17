package com.first.ridingpartnerinsiheung.scenarios.main.maps


import android.content.Context
import com.first.ridingpartnerinsiheung.data.RentalLocation
import com.google.android.gms.maps.model.LatLng

class RentalLocationXY(private val context: Context) {
    fun read():List<RentalLocation>{
        return listOf(
            RentalLocation("정왕 자전거 대여소", LatLng(37.343991285297, 126.74729588817)),
            RentalLocation("월곶 자전거 대여소", LatLng(37.3917953, 126.742692))
        )
    }
}
