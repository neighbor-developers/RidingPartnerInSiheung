package com.first.ridingpartnerinsiheung.api.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Weather(
    val response : Response
) {
    data class Response(
        val header: Header,
        val body: Body
    )
    data class Header(
        val resultCode: Int,
        val resultMsg: String
    )
    data class Body(
        val dataType: String,
        val items: Items,
        val totalCount: Int
    )

    data class Items(
        val item: ArrayList<Item>
    )

    data class Item(
        val category: String,
        val baseDate: String,
        val baseTime: String,
        val fcstValue: String
    )
}
val retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject {
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
}