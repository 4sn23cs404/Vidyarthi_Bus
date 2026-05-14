package com.example.vidyarthibus3

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class AlternativeContact(
    val name: String,
    val type: String,
    val phone: String,
    val description: String,
    val color: Color
)

val alternativeContacts = listOf(
    AlternativeContact(
        name = "Public Shared Auto",
        type = "AUTO",
        phone = "+919999999999",
        description = "Available along the main route",
        color = Color(0xFFF59E0B)
    ),
    AlternativeContact(
        name = "Mangalore City Bus",
        type = "CITY BUS",
        phone = "+918242220000",
        description = "KSRTC city bus service",
        color = Color(0xFF2563EB)
    ),
    AlternativeContact(
        name = "Rapido Auto",
        type = "APP RIDE",
        phone = "+918069029999",
        description = "Book via Rapido app",
        color = Color(0xFFEF4444)
    ),
    AlternativeContact(
        name = "Ola / Uber",
        type = "CAB",
        phone = "+918069086080",
        description = "Available 24/7 in Mangalore",
        color = Color(0xFF22C55E)
    ),
    AlternativeContact(
        name = "College Transport Office",
        type = "COLLEGE",
        phone = "+918241234567",
        description = "Contact for bus complaints",
        color = Color(0xFF7C3AED)
    ),
    AlternativeContact(
        name = "Emergency Contact",
        type = "EMERGENCY",
        phone = "112",
        description = "Police / Ambulance / Fire",
        color = Color(0xFFEF4444)
    )
)

@Composable
fun AlternativesScreen(navController: NavHostController) {
    // Get context here, at the top level of the composable
    val context = LocalContext.current

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
                    "Alternative Contacts",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "TRANSPORT OPTIONS",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "If the bus is unavailable or full, use these alternatives:",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            alternativeContacts.forEach { contact ->
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            contact.color.copy(alpha = 0.15f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        contact.type,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = contact.color,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        contact.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        contact.description,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        contact.phone,
                                        fontSize = 12.sp,
                                        color = contact.color,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    // ✅ FIXED: Actually opens the dialer with the number
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${contact.phone}")
                                    )
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = contact.color
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Text(
                                    "CALL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}