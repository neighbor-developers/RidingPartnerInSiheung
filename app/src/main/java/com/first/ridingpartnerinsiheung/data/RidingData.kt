package com.first.ridingpartnerinsiheung.data

import kotlinx.coroutines.flow.MutableStateFlow

data class RidingData(
    var sumDistance : Double,
    var averSpeed : Double,
    var timer: Int,
    var kcal: Int
)
