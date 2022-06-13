package com.first.ridingpartnerinsiheung.api.bikepath

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

data class Path (
    val routes: List<Route>
) : Serializable {
    data class Route (
        val summary: RouteSummary,
        val route_fullpath: String,
        val legs: List<Leg>
    )

    data class Leg (
        val summary: LegSummary,
        val steps: List<Step>
    )

    data class Step (
        val path: String,
        val summary: StepSummary,
        val road: Road,
        val panorama: Panorama? = null,
        val guide: Guide,
        val traffic: Traffic? = null
    )

    data class Guide (
        val turn_point: String,
        val direction: String,
        val turn: Long,
        val entrance_type: Long,
        val point: String,
        val content: String,
        val instructions: String
    )

    data class Panorama (
        val id: String,
        val location: String,
        val pan: Long,
        val tilt: Long
    )

    data class Road (
        val road_type: Long,
        val road_name: String,
        val road_no: Long,
        val lane_num: Long,
        val road_structure: Long
    )

    data class StepSummary (
        val distance: Long,
        val duration: Long,
        val step_summary: String
    )

    data class Traffic (
        val congestion: Long,
        val speed: Long
    )

    data class LegSummary (
        val distance: Long,
        val duration: Long,
        val start: End,
        val end: End
    )

    data class End (
        val address: String,
        val location: String
    )

    data class RouteSummary (
        val distance: Long,
        val duration: Long,
        val bounds: Bounds,
        val route_option: Long,
        val toll: String,
        val taxi_fare: Long,
        val start: End,
        val end: End,
        val waypoints: List<End>?,
        val road_summary: List<RoadSummary>,
        val facility_count: FacilityCount,
        val destination_dir: Long,
        val engine_version: String,
        val result_version: String,
        val coord_type: String
    )

    data class Bounds (
        val left_top: String,
        val right_bottom: String
    )

    data class FacilityCount (
        val slide: Long,
        val elevator: Long
    )

    data class RoadSummary (
        val location: String,
        val road_name: String,
        val distance: Long,
        val congestion: Long,
        val speed: Long
    )
}


val bikeretrofit = Retrofit.Builder()
    .baseUrl("https://map.naver.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject2 {
    val retrofitService: PathInterface by lazy {
        bikeretrofit.create(PathInterface::class.java)
    }
}
