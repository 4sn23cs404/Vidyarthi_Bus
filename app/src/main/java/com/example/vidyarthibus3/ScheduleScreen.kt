package com.example.vidyarthibus3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class BusScheduleEntry(
    val busNumber: String,
    val route: String,
    val from: String,
    val to: String,
    val stops: List<String>,
    val trips: List<TripTiming>  // ✅ NEW: each trip has depart + arrive
)

data class TripTiming(
    val departs: String,
    val arrives: String
)

val busSchedules = listOf(
    BusScheduleEntry(
        busNumber = "KA40F0904",
        route = "Mangalore → Adyar Padavu",
        from = "Mangalore",
        to = "Adyar Padavu",
        stops = listOf(
            "Mangalore", "PVS", "Jyothi", "Kankanady",
            "Pumpwell", "Padil", "Sahyadri College",
            "Valachil", "Srinivas College", "Adyar Padavu"
        ),
        trips = listOf(
            TripTiming(departs = "7:20 AM",  arrives = "8:10 AM"),
            TripTiming(departs = "9:20 AM",  arrives = "10:10 AM"),
            TripTiming(departs = "11:20 AM", arrives = "12:10 PM"),
            TripTiming(departs = "3:20 PM",  arrives = "4:10 PM"),
            TripTiming(departs = "5:20 PM",  arrives = "6:10 PM"),
            TripTiming(departs = "7:20 PM",  arrives = "8:10 PM")
        )
    ),
    BusScheduleEntry(
        busNumber = "KA40F0904",
        route = "Adyar Padavu → Mangalore",
        from = "Adyar Padavu",
        to = "Mangalore",
        stops = listOf(
            "Adyar Padavu", "Srinivas College", "Valachil",
            "Sahyadri College", "Padil", "Pumpwell",
            "Kankanady", "Jyothi", "PVS", "Mangalore"
        ),
        trips = listOf(
            TripTiming(departs = "8:10 AM",  arrives = "9:00 AM"),
            TripTiming(departs = "10:10 AM", arrives = "11:00 AM"),
            TripTiming(departs = "12:10 PM", arrives = "1:00 PM"),
            TripTiming(departs = "4:10 PM",  arrives = "5:00 PM"),
            TripTiming(departs = "6:10 PM",  arrives = "7:00 PM"),
            TripTiming(departs = "8:10 PM",  arrives = "9:00 PM")
        )
    )
)

@Composable
fun ScheduleScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2563EB))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    "Schedule & Stops",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "BUS KA40F0904",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Info banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                "⚡ Express service — no intermediate stops. Journey ~50 mins.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            busSchedules.forEach { schedule ->
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // Route header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFF2563EB),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        schedule.busNumber,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    schedule.route,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            // Trip timings table
                            Text(
                                "DEPARTURE TIMES",
                                fontSize = 10.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Departs ${schedule.from}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "Arrives ${schedule.to}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Trip rows
                            schedule.trips.forEachIndexed { index, trip ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (index % 2 == 0) Color.White
                                            else Color(0xFFF8FAFC),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Depart time
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFF2563EB).copy(alpha = 0.1f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                trip.departs,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2563EB)
                                            )
                                        }
                                    }

                                    // Arrow
                                    Text(
                                        "→",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    // Arrive time
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFF22C55E).copy(alpha = 0.1f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                trip.arrives,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF22C55E)
                                            )
                                        }
                                    }
                                }
                                if (index < schedule.trips.lastIndex) {
                                    Divider(color = Color(0xFFF1F5F9))
                                }
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            // Stops — only show terminal stops since express
                            Text(
                                "ROUTE",
                                fontSize = 10.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Start
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                Color(0xFF22C55E),
                                                RoundedCornerShape(18.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🚌", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        schedule.from,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF22C55E).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "START",
                                            fontSize = 9.sp,
                                            color = Color(0xFF22C55E),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Dashed line
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .padding(horizontal = 8.dp)
                                        .background(
                                            Color(0xFFE2E8F0),
                                            RoundedCornerShape(1.dp)
                                        )
                                )

                                Text("~50 min", fontSize = 10.sp, color = Color(0xFF94A3B8))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .padding(horizontal = 8.dp)
                                        .background(
                                            Color(0xFFE2E8F0),
                                            RoundedCornerShape(1.dp)
                                        )
                                )

                                // End
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                Color(0xFFEF4444),
                                                RoundedCornerShape(18.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏁", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        schedule.to,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFFEF4444).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "END",
                                            fontSize = 9.sp,
                                            color = Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}