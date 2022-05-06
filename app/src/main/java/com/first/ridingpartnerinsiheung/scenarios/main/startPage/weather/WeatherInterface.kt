package com.first.ridingpartnerinsiheung.scenarios.main.startPage.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("getUltraSrtFcst?serviceKey=G77F3bZzXJWT%2B%2BAQafXcdQiaLmn2RlsZb9szmlWWCRJqPLN9O1U6w7tU4ywNe%2BxNBw59Jv3wOhKQh6hjZfr6yA%3D%3D")
    fun getWeather(
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("dataType") data_type: String,
        @Query("base_date") base_date : String,
        @Query("base_time") base_time : String,
        @Query("nx") nx : String,
        @Query("ny") ny : String
    ): retrofit2.Call<Weather>
}