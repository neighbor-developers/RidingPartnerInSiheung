package com.first.ridingpartnerinsiheung.api.bikepath

import retrofit2.http.GET
import retrofit2.http.Query

interface PathInterface {
    @GET("v5/api/dir/findbicycle?")
    fun getPAth(
        @Query("start") start: String,
        @Query("destination") destination: String,
    ): retrofit2.Call<Path>
}