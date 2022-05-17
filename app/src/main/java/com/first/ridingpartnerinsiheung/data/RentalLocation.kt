package com.first.ridingpartnerinsiheung.data

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng

data class RentalLocation(
    var name: String,
    var location: LatLng,
)