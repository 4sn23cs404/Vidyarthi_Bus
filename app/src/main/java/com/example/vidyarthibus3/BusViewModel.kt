package com.example.vidyarthibus3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthibus3.data.BusReport
import com.example.vidyarthibus3.data.BusRoute
import com.example.vidyarthibus3.data.BusRepository
import com.example.vidyarthibus3.data.CrowdStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BusViewModel : ViewModel() {
    private val repository = BusRepository()

    private val _routes = MutableStateFlow<List<BusRoute>>(emptyList())
    val routes: StateFlow<List<BusRoute>> = _routes

    private val _reports = MutableStateFlow<List<BusReport>>(emptyList())
    val reports: StateFlow<List<BusReport>> = _reports

    private val _selectedRoute = MutableStateFlow<BusRoute?>(null)
    val selectedRoute: StateFlow<BusRoute?> = _selectedRoute

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            _routes.value = repository.getRoutes()
        }
    }

    fun selectRoute(route: BusRoute) {
        _selectedRoute.value = route
        repository.listenToReports(route.id)
            .onEach { _reports.value = it }
            .launchIn(viewModelScope)
    }

    fun submitReport(status: CrowdStatus, userId: String) {
        val routeId = _selectedRoute.value?.id ?: return
        viewModelScope.launch {
            repository.submitReport(routeId, status, userId)
        }
    }
}