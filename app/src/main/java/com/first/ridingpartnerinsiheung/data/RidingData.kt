package com.first.ridingpartnerinsiheung.data

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import java.net.URL

data class RidingData(
    var sumDistance : Double = 0.0,
    var averSpeed : Double = 0.0,
    var timer: Int = 0,
    var kcal: Double = 0.0,
    var imageUrl : Uri? = null
) : Serializable
