package com.first.ridingpartnerinsiheung.scenarios.main.maps.routeSearchPage


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RouteSearchViewModel: ViewModel() {

    private val _event = MutableSharedFlow<RouteSearchEvent>()
    val event = _event.asSharedFlow()

    sealed class RouteSearchEvent{
        object SetStartPlace : RouteSearchEvent()
        object SetEndPlace : RouteSearchEvent()
        object StartNavigation : RouteSearchEvent()
    }

    fun setStartPlace() = viewModelScope.launch { _event.emit(RouteSearchEvent.SetStartPlace) }
    fun setEndPlace() = viewModelScope.launch { _event.emit(RouteSearchEvent.SetEndPlace) }
    fun startNavigation() = viewModelScope.launch { _event.emit(RouteSearchEvent.StartNavigation) }
}