package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MyPageViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val user = auth.currentUser

    var userName = MutableStateFlow<String?>("")
    val userEmail : String? = user!!.email

    private val _event = MutableSharedFlow<MyPageEvent>()
    val event = _event.asSharedFlow()

    fun showImage() = viewModelScope.launch { _event.emit(MyPageEvent.ShowImage) }
    fun showChangeNameDialog() = viewModelScope.launch { _event.emit(MyPageEvent.ShowChangeNameDialog) }

    sealed class MyPageEvent{
        object ShowImage: MyPageEvent()
        object ShowChangeNameDialog: MyPageEvent()
    }
}