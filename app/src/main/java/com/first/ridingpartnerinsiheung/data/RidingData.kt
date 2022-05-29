package com.first.ridingpartnerinsiheung.data

import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable

data class RidingData(
    var sumDistance : Double = 0.0,
    var averSpeed : Double = 0.0,
    var timer: Int = 0,
    var kcal: Double = 0.0
) : Serializable
