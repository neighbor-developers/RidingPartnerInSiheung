package com.first.ridingpartnerinsiheung.api.place

import com.first.ridingpartnerinsiheung.api.weather.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class PlaceDetail (
    val meta: Meta,
    val ac: List<Any?>,
    val place : List<Place>,
    val address: List<Any?>,
    val bus: List<Any?>,
    val menu: List<Any?>,
    val all: List<All>
    ){
    data class Meta (
        val model: String,
        val query: String,
        val requestID: String
    )

    data class Place (
        val type: String,
        val id: String,
        val title: String,
        val x: String,
        val y: String,
        val dist: Double,
        val totalScore: Double,
        val sid: String,
        val ctg: String,
        val cid: String,
        val jibunAddress: String,
        val roadAddress: String? = null
    )

    data class All (
        val place: Place,
        val address: Any? = null,
        val bus: Any? = null
    )
}
val placeRetrofit = Retrofit.Builder()
    .baseUrl("https://map.naver.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject {
    val retrofitService: PlaceInterface by lazy {
        placeRetrofit.create(PlaceInterface::class.java)
    }
}
