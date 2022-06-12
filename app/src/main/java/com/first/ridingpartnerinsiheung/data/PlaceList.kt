package com.first.ridingpartnerinsiheung.data

import com.google.android.gms.maps.model.LatLng

class PlaceList (val placeTxt : String, val placeDistance : String, val photo : Int, val path: ArrayList<String>) {
    var placeList = arrayListOf<PlaceList>()
}