package com.first.ridingpartnerinsiheung.scenarios.main.maps

import java.text.SimpleDateFormat

class CalcModel {
    fun calculateKcal(averageSpeed: Double, savedTimer: Int, weight: Double): Double {
        // 평속을 칼로리 소비계수로 전환
        val changeKcal: Double = when (averageSpeed) {
            in 13.0..15.0 -> 0.065
            in 16.0..18.0 -> 0.0783
            in 19.0..21.0 -> 0.0939
            in 22.0..23.0 -> 0.113
            in 24.0..25.0 -> 0.124
            26.0 -> 0.136
            in 27.0..28.0 -> 0.149
            in 29.0..30.0 -> 0.163
            31.0 -> 0.179
            in 32.0..33.0 -> 0.196
            in 34.0..36.0 -> 0.215
            in 37.0..39.0 -> 0.259
            40.0 -> 0.311
            else -> 0.01
        }
        return changeKcal * savedTimer * weight
    }
}