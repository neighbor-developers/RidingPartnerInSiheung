package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.data.RidingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.round

class RecordViewModel: ViewModel() {
    var savedTimer = MutableStateFlow(0)
    var savedSpeed = MutableStateFlow(1.0)
    var savedDistance = MutableStateFlow(0.0)
    var savedKcal = MutableStateFlow(0.0)
    var savedTime = MutableStateFlow("0/0/0")
    var memo = MutableStateFlow("")

    var distanceText = savedDistance.map {
        "${ round(it/10)/100} km"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    var mainDistanceText = savedDistance.map {
        "${ round(it/10)/100}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    var speedText = savedSpeed.map {
        "${round(it/10)/100} km/h"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    var kcalText = savedKcal.map {
        "${round(it*10)/10} kcal"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    var timerText = savedTimer.map {
        "${it/3600} : ${it / 60} : ${it%60}"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")


}
