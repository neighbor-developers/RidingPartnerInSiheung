package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.data.RidingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.round

class RecordViewModel: ViewModel() {

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    var savedTimer = MutableStateFlow(0)
    var savedSpeed = MutableStateFlow(1.0)
    var savedDistance = MutableStateFlow(0.0)
    var savedKcal = MutableStateFlow(0.0)
    var savedTime = MutableStateFlow("0/0/0")

    var time = MutableStateFlow("")

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

    val today = MutableStateFlow(
        System.currentTimeMillis().let { current ->
            SimpleDateFormat("yyyy.MM.dd").format(current)
        })

    var memo = MutableStateFlow("")

    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    init {
        viewModelScope.launch {
            time.collect { time ->
                listenerRegistration.value?.remove()
                listenerRegistration.value = db.collection(user).document(time+"massage")
                    .addSnapshotListener{ value, error ->
                        var _memo = ""
                        value?.data?.get(time)?.let {
                            _memo = it.toString()
                        }
                       memo.value = _memo
                    }
//                listenerRegistration.value = db.collection(user).document(time+"massage")
//                    .addSnapshotListener{ value, error ->
//                        var distance:Double=0.0
//                        value?.data?.get(time)?.let{
//                            distance=Integer.parseInt(it.toString()).toDouble()
//                        }
//                        savedDistance.value=distance
//                    }
            }

        }
    }
    fun changeTime(_time : String){
        time.value = _time
    }

}
