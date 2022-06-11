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
import kotlin.math.round

class RecordViewModel: ViewModel() {

    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

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


    var content = MutableStateFlow("")

    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    private val _event = MutableSharedFlow<RecordEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            time.collect { time ->
                listenerRegistration.value?.remove()
                listenerRegistration.value = db.collection("user")
                    .document(user).collection("Massage")
                    .document(time)
                    .addSnapshotListener{ value, error ->
                        var _diaryContent = ""
                        value?.data?.get("contents")?.let {
                            _diaryContent = it.toString()
                        }
                        content.value = _diaryContent
                    }

            }
        }
    }
    fun addDiaryContent(onSuccess : () -> Unit, onFailure : () -> Unit){
        val data = hashMapOf("contents" to content.value)
        db.collection(user)
            .document("Massage").collection(time.value)
            .document("a")
            .set(data)
            .addOnSuccessListener {
                postSuccess()
            }
            .addOnFailureListener{
                postFailuer(it)
            }
    }
    private fun postSuccess() = viewModelScope.launch { _event.emit(RecordEvent.PostSuccess) }
    private fun postFailuer(exception: Exception) = viewModelScope.launch { _event.emit(RecordEvent.PostFailure(exception)) }

    sealed class RecordEvent{
        object PostSuccess : RecordEvent()
        data class PostFailure(val exception: Exception) : RecordEvent()
    }

}
