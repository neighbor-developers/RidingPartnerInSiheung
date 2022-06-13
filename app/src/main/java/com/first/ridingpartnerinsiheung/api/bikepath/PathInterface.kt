package com.first.ridingpartnerinsiheung.api.bikepath

import retrofit2.http.GET
import retrofit2.http.Query

interface PathInterface {
    @GET("v5/api/dir/findbicycle?")
    fun getPath(
        @Query("start") start: String,
        @Query("destination") destination: String,
        @Query("waypoints") way_point: String?,
    ): retrofit2.Call<Path>


    @GET("v5/api/dir/findbicycle?")
    suspend fun getPathSuspend(
        @Query("start") start: String,
        @Query("destination") destination: String,
        // @Query("way_point") way_point: String,
    ): Path
}