package com.example.vidyarthibus3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidyarthibus3.data.BusRoute
import com.example.vidyarthibus3.data.CrowdStatus
import androidx.compose.runtime.LaunchedEffect

@Composable
fun RowScope.TabButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(
                if (selected) Color(0xFF2563EB) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) Color.White else Color.Gray,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EmptyStateSelection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🚌", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("No route selected", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Select a route to see crowd status",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ScheduleCard(route: BusRoute) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(route.name, fontSize = 13.sp, color = Color.Gray)
            if (route.schedule.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                route.schedule.forEach { time ->
                    Text("• $time", fontSize = 13.sp, color = Color(0xFF374151))
                }
            }
        }
    }
}

@Composable
fun ReportingPortal(viewModel: BusViewModel) {
    var isOnBus by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf<CrowdStatus?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(300) } // 300 seconds = 5 minutes

    // Countdown timer — runs only when isOnBus is true
    LaunchedEffect(isOnBus) {
        if (isOnBus) {
            secondsLeft = 300
            while (secondsLeft > 0) {
                kotlinx.coroutines.delay(1000L)
                secondsLeft--
            }
            // Auto reset after 5 minutes
            isOnBus = false
            selectedStatus = null
            submitted = false
        }
    }

    // Format seconds to MM:SS
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeDisplay = "%02d:%02d".format(minutes, seconds)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "PASSENGER PORTAL",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "CONTRIBUTE LIVE DATA TO HELP OTHER STUDENTS FIND SEATS.",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (!isOnBus) {
                // ME ON THE BUS button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { isOnBus = true }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ME ON THE BUS",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF1E293B)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "CLICK ABOVE ONCE YOU BOARD TO START LIVE REPORTING FOR OTHERS.",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            } else {
                // Timer display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "HOW FULL IS THE BUS?",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    // Timer badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (secondsLeft < 60) Color(0xFFEF4444).copy(alpha = 0.2f)
                                else Color(0xFF334155),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "⏱ $timeDisplay",
                            fontSize = 11.sp,
                            color = if (secondsLeft < 60) Color(0xFFEF4444)
                            else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        CrowdStatus.empty,
                        CrowdStatus.seated,
                        CrowdStatus.full
                    ).forEach { status ->
                        val isSelected = selectedStatus == status
                        val color = when (status) {
                            CrowdStatus.empty  -> Color(0xFF22C55E)
                            CrowdStatus.seated -> Color(0xFFF59E0B)
                            CrowdStatus.full   -> Color(0xFFEF4444)
                            else               -> Color.Gray
                        }
                        val label = when (status) {
                            CrowdStatus.empty  -> "Empty"
                            CrowdStatus.seated -> "Seated"
                            CrowdStatus.full   -> "Full"
                            else               -> status.name
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) color else color.copy(alpha = 0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedStatus = status
                                    submitted = false
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (isSelected) Color.White else color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        selectedStatus?.let {
                            viewModel.submitReport(it, "anonymous")
                            submitted = true
                        }
                    },
                    enabled = selectedStatus != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Text(
                        if (submitted) "✓ SUBMITTED!" else "SUBMIT REPORT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (submitted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "✓ Report active for $timeDisplay more",
                        color = Color(0xFF22C55E),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress bar showing time remaining
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF334155), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(secondsLeft / 300f)
                            .fillMaxHeight()
                            .background(
                                if (secondsLeft < 60) Color(0xFFEF4444)
                                else Color(0xFF2563EB),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Report expires in $timeDisplay",
                    color = Color(0xFF475569),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Not on bus button
                TextButton(onClick = {
                    isOnBus = false
                    selectedStatus = null
                    submitted = false
                }) {
                    Text(
                        "← Not on the bus",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}