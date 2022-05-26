package com.first.ridingpartnerinsiheung.data

import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng


data class RentalLocation(
    var name: String,
    var location: LatLng,
)
