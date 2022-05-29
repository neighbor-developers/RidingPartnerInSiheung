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

class RecordViewModel: ViewModel() {
    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    var data : RidingData? = null

     var savedTimer = MutableStateFlow(0)
     var savedSpeed = MutableStateFlow(0.0)
     var savedDistance = MutableStateFlow(0.0)
     var savedKcal = MutableStateFlow(0.0)
    val distanceText = savedDistance.map {
        "$it km"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")
    val speedText = savedDistance.map {
        "$it km/h"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")
    val kcalText = savedDistance.map {
        "$it kcal"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "-")

    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    var time = MutableStateFlow("")


    init {
        viewModelScope.launch {
                listenerRegistration.value?.remove()
                listenerRegistration.value = db.collection(user)
                    .document(time.value)
                    .addSnapshotListener{ value, error ->
                        var _data : Array<String?>? = null
                        var i = 0
                        value?.data?.entries?.sortedBy { it.key }?.forEach {
                            _data?.set(i, it.value.toString())
                        }


                    }


        }
    }
}
