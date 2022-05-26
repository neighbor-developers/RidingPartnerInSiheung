package com.first.ridingpartnerinsiheung.api.bikepath

import com.naver.maps.geometry.LatLng
import retrofit2.http.GET
import retrofit2.http.Query

interface PathInterface {
    @GET("findbicycle?")
    fun getPAth(
        @Query("start") start_latLon: String,
        @Query("name") start_name: String,
        @Query("destination") destination: LatLng,
        @Query("name") destination_name: String,
        /* 경유지만큼 들어감
        @Query("waypoints") way_points: String,
        @Query("name") way_points_name: String
        */
    ): retrofit2.Call<Path>
}