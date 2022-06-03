package com.first.ridingpartnerinsiheung.api.bikepath

import com.first.ridingpartnerinsiheung.api.weather.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Path (
    val routes : Routes
    ){
    data class Routes(
        val summary: Summary,
        val route_full_path: String,
        val legs: Legs
        // val legs: Legs 경유지만큼 더 들어감
    )
    data class Summary(
        val distance: String,
        val duration: String,
        val bounds: Bounds,
        val route_option: String,
        val toll: String,
        val taxi_fare: String,
        val start: Start,
        val end: End,
        val road_summary: RoadSummary,
        val facility_count: Facility_count,
        val destination_dir:String,
        val engine_version: String,
        val result_version: String,
        val coord_type: String
    )
    data class RoadSummary(
        val location : String,
        val road_name: String,
        val distance: String,
        val congestion: String,
        val speed: String
    )
    data class Legs(
        val summary: LegsSummary,
        val steps: Steps
    )
    data class LegsSummary(
        val distance: Long,
        val duration: Long,
        val start: End,
        val end: End
    )
    data class Steps(
        val path: String,
        val summary: StepsSummary,
        val road: Road,
        val traffic: Traffic? = null,
        val panorama : Panorama? = null,
        val guide: Guide
    )
    data class StepsSummary(
        val distance: String,
        val duration: String,
        val step_summary: String
    )
    data class Guide(
        val turn_point: String,
        val direction: String,
        val turn: String,
        val entrance_type: String,
        val point: String,
        val content: String,
        val instructions: String
    )
    data class Panorama(
        val id: String,
        val location: String,
        val pan: String,
        val tilt: String
    )
    data class Traffic(
        val congestion: String,
        val speed: String
    )
    data class Road(
        val road_type: String,
        val road_name: String,
        val road_no: String,
        val lane_num: String,
        val road_structure: String
    )
    data class Facility_count(
        val stair: String,
        val slide: String
    )
    data class End(
        val address: String,
        val location : String
    )
    data class Start(
        val address: String,
        val location : String
    )
    data class Bounds(
        val left_top : String,
        val right_bottom: String,
    )

}
val bikeretrofit = Retrofit.Builder()
    .baseUrl("https://map.naver.com/v5/api/dir/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject2 {
    val retrofitService: PathInterface by lazy {
        bikeretrofit.create(PathInterface::class.java)
    }
}
