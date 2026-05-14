package com.example.vidyarthibus3.data

import com.google.firebase.Timestamp

enum class CrowdStatus { empty, seated, full, unknown }

data class BusRoute(
    val id: String = "",
    val number: String = "",
    val name: String = "",
    val busName: String? = null,
    val stops: List<String> = emptyList(),
    val schedule: List<String> = emptyList()
)

data class BusReport(
    val id: String = "",
    val routeId: String = "",
    val status: String = "seated",
    val reportedAt: Timestamp? = null,
    val userId: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class Alternative(
    val name: String = "",
    val phone: String = ""
)