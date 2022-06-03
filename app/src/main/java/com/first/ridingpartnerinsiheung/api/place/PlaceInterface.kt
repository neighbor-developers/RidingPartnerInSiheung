package com.first.ridingpartnerinsiheung.api.place

import com.naver.maps.geometry.LatLng
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PlaceInterface {
    @GET("v5/api/instantSearch?")
    fun getPlaces(
        @Query("coords") coords: String,
        @Query("query") query: String,
    ): retrofit2.Call<PlaceDetail>
}