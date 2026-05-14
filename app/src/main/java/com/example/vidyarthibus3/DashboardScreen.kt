package com.example.vidyarthibus3

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vidyarthibus3.data.BusReport
import com.example.vidyarthibus3.data.BusRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val stops = listOf(
    "Adyar Padavu", "Srinivas College", "Valachil",
    "Sahyadri College", "Padil", "Pumpwell",
    "Kankanady", "Jyothi", "PVS", "Mangalore"
)

val sampleRoutes = listOf(
    BusRoute(
        id = "1", number = "KA40F0904",
        name = "Mangalore → Adyar Padavu", busName = "KA40F0904",
        stops = stops.reversed(),
        schedule = listOf("7:20 AM", "9:20 AM", "11:20 AM", "3:20 PM", "5:20 PM", "7:20 PM")
    ),
    BusRoute(
        id = "2", number = "KA40F0904",
        name = "Adyar Padavu → Mangalore", busName = "KA40F0904",
        stops = stops,
        schedule = listOf("8:10 AM", "10:10 AM", "12:10 PM", "4:10 PM", "6:10 PM", "8:10 PM")
    )
)

fun computeMajorityStatus(reports: List<BusReport>): Pair<String, Map<String, Int>> {
    if (reports.isEmpty()) return Pair("unknown", emptyMap())
    val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
    val recentReports = reports.filter { (it.reportedAt?.toDate()?.time ?: 0L) >= fiveMinutesAgo }
    if (recentReports.isEmpty()) return Pair("unknown", emptyMap())
    val voteCounts = recentReports.groupBy { it.status }.mapValues { it.value.size }
    return Pair(voteCounts.maxByOrNull { it.value }?.key ?: "unknown", voteCounts)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusDashboard(
    viewModel: BusViewModel = viewModel(),
    navController: NavHostController
) {
    val reports by viewModel.reports.collectAsState()
    var activeTab by remember { mutableStateOf("status") }
    var fromStop by remember { mutableStateOf("") }
    var toStop by remember { mutableStateOf("") }
    var foundRoute by remember { mutableStateOf<BusRoute?>(null) }
    var ticker by remember { mutableStateOf(0) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val str = remember(AppSettings.selectedLanguage) { getStrings(AppSettings.selectedLanguage) }

    // ── Theme colors
    val bg          = if (AppSettings.isDarkMode) Color(0xFF0F172A) else Color(0xFFF3F4F6)
    val cardBg      = if (AppSettings.isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (AppSettings.isDarkMode) Color.White      else Color(0xFF1E293B)
    val textSecondary = if (AppSettings.isDarkMode) Color(0xFF94A3B8) else Color.Gray
    // ✅ Button always blue — clearly visible in both modes
    val btnBg       = Color(0xFF2563EB)
    val btnDisabled = if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
    val btnTextDisabled = if (AppSettings.isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSuggestDialog  by remember { mutableStateOf(false) }
    var showRateDialog     by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) { delay(30_000L); ticker++ }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = if (AppSettings.isDarkMode) Color(0xFF1E293B) else Color(0xFF2563EB),
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // App header
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("🚌", fontSize = 32.sp) }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(str.appName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("v1.0", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                DrawerMenuItem(icon = "🌐", label = str.language, value = AppSettings.selectedLanguage, onClick = {
                    showLanguageDialog = true; scope.launch { drawerState.close() }
                })
                DrawerMenuToggle(
                    icon = if (AppSettings.isDarkMode) "☀️" else "🌙",
                    label = if (AppSettings.isDarkMode) str.lightMode else str.darkMode,
                    checked = AppSettings.isDarkMode,
                    onToggle = { AppSettings.isDarkMode = !AppSettings.isDarkMode }
                )

                Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                DrawerMenuItem(icon = "🕐", label = str.scheduleStops.replace("\n", " & "), onClick = {
                    scope.launch { drawerState.close() }; navController.navigate(Screen.Schedule.route)
                })
                DrawerMenuItem(icon = "📞", label = str.alternativeContacts.replace("\n", " "), onClick = {
                    scope.launch { drawerState.close() }; navController.navigate(Screen.Alternatives.route)
                })

                Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                DrawerMenuItem(icon = "⭐", label = "Rate Us", onClick = {
                    scope.launch { drawerState.close() }; showRateDialog = true
                })
                DrawerMenuItem(icon = "💡", label = "Suggest a Feature", onClick = {
                    scope.launch { drawerState.close() }; showSuggestDialog = true
                })
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(bg)) {

            // ── Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2563EB))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(str.appName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(str.tagline, color = Color.White.copy(alpha = 0.75f), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                if (foundRoute != null) {
                    Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF22C55E), RoundedCornerShape(4.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(foundRoute!!.busName ?: foundRoute!!.number, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // ── Search Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(str.searchRoute, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(str.from, fontSize = 10.sp, color = textSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            StopDropdown(selected = fromStop, placeholder = str.startingStation, onSelect = { fromStop = it }, cardBg = cardBg, textPrimary = textPrimary, textSecondary = textSecondary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(str.to, fontSize = 10.sp, color = textSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            StopDropdown(selected = toStop, placeholder = str.destinationStation, onSelect = { toStop = it }, cardBg = cardBg, textPrimary = textPrimary, textSecondary = textSecondary)
                            Spacer(modifier = Modifier.height(16.dp))

                            val canSearch = fromStop.isNotEmpty() && toStop.isNotEmpty() && fromStop != toStop

                            // ✅ FIXED: Button always has clear color in both dark & light
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(
                                        if (canSearch) btnBg else btnDisabled,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = canSearch) {
                                        val fi = stops.indexOf(fromStop)
                                        val ti = stops.indexOf(toStop)
                                        foundRoute = if (fi <= ti) sampleRoutes[1] else sampleRoutes[0]
                                        viewModel.selectRoute(foundRoute!!)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    str.findMyBus,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp,
                                    color = if (canSearch) Color.White else btnTextDisabled
                                )
                            }

                            if (fromStop.isNotEmpty() && toStop.isNotEmpty() && fromStop == toStop) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(str.sameStopWarning, color = Color(0xFFEF4444), fontSize = 12.sp)
                            }
                        }
                    }
                }

                // ── Empty state
                if (foundRoute == null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(180.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🚌", fontSize = 40.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(str.pleaseSelect, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(str.pleaseSelectSub, fontSize = 12.sp, color = Color(0xFFCBD5E1), textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                // ── Route found
                if (foundRoute != null) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (activeTab == "status") Color(0xFF2563EB) else cardBg, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                    .border(1.dp, Color(0xFF2563EB), RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                    .clickable { activeTab = "status" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(str.crowdStatus, color = if (activeTab == "status") Color.White else Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (activeTab == "report") Color(0xFF2563EB) else cardBg, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                    .border(1.dp, Color(0xFF2563EB), RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                    .clickable { activeTab = "report" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(str.contribute, color = if (activeTab == "report") Color.White else Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (activeTab == "status") {
                        item { CrowdMeterCard(reports, foundRoute!!, ticker, str, cardBg, textPrimary, textSecondary) }
                    } else {
                        item { ReportingPortal(viewModel) }
                    }

                    // More Options
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(str.moreOptions, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                modifier = Modifier.weight(1f).clickable { navController.navigate(Screen.Schedule.route) },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(48.dp).background(Color(0xFF2563EB).copy(alpha = 0.15f), RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) { Text("🕐", fontSize = 24.sp) }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(str.scheduleStops, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center, color = textPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(str.viewAllTimings, fontSize = 10.sp, color = textSecondary, textAlign = TextAlign.Center)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f).clickable { navController.navigate(Screen.Alternatives.route) },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(48.dp).background(Color(0xFF22C55E).copy(alpha = 0.15f), RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) { Text("📞", fontSize = 24.sp) }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(str.alternativeContacts, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center, color = textPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(str.autosCabsMore, fontSize = 10.sp, color = textSecondary, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // ── Language dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = cardBg,
            title = { Text(str.language, fontWeight = FontWeight.Bold, color = textPrimary) },
            text = {
                Column {
                    supportedLanguages.forEach { lang ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { AppSettings.selectedLanguage = lang; showLanguageDialog = false }.padding(vertical = 14.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(lang, fontSize = 16.sp, color = textPrimary, fontWeight = if (lang == AppSettings.selectedLanguage) FontWeight.Bold else FontWeight.Normal)
                            if (lang == AppSettings.selectedLanguage) Text("✓", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        if (lang != supportedLanguages.last()) Divider(color = if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9))
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ── Rate Us dialog
    if (showRateDialog) {
        var selectedStars by remember { mutableStateOf(0) }
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            containerColor = cardBg,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("⭐ Rate Us", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("How is your experience?", fontSize = 12.sp, color = textSecondary, textAlign = TextAlign.Center)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Text(
                                if (star <= selectedStars) "★" else "☆",
                                fontSize = 36.sp,
                                color = if (star <= selectedStars) Color(0xFFFBBF24) else textSecondary,
                                modifier = Modifier.clickable { selectedStars = star }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        when (selectedStars) {
                            1 -> "😞 We're sorry to hear that!"
                            2 -> "😐 We'll try to do better"
                            3 -> "🙂 Thanks for the feedback"
                            4 -> "😊 Great! Glad you like it"
                            5 -> "🤩 Awesome! Thank you!"
                            else -> "Tap a star to rate"
                        },
                        fontSize = 13.sp, color = textSecondary, textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedStars > 0) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            try { context.startActivity(intent) } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                            }
                        }
                        showRateDialog = false
                    },
                    enabled = selectedStars > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Submit", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) { Text("Not Now", color = textSecondary) }
            }
        )
    }

    // ── Suggest a Feature dialog
    if (showSuggestDialog) {
        var suggestionText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSuggestDialog = false },
            containerColor = cardBg,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("💡 Suggest a Feature", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Help us improve Vidyarthi-Bus!", fontSize = 12.sp, color = textSecondary, textAlign = TextAlign.Center)
                }
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = suggestionText,
                        onValueChange = { if (it.length <= 300) suggestionText = it },
                        placeholder = { Text("Describe your feature idea...", color = textSecondary, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            cursorColor = Color(0xFF2563EB)
                        ),
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${suggestionText.length}/300", fontSize = 11.sp, color = textSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (suggestionText.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("your-email@gmail.com"))
                                putExtra(Intent.EXTRA_SUBJECT, "Vidyarthi-Bus Feature Suggestion")
                                putExtra(Intent.EXTRA_TEXT, suggestionText)
                            }
                            try { context.startActivity(intent) } catch (e: Exception) { }
                        }
                        showSuggestDialog = false
                    },
                    enabled = suggestionText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Send", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showSuggestDialog = false }) { Text("Cancel", color = textSecondary) }
            }
        )
    }
}

@Composable
fun DrawerMenuItem(icon: String, label: String, value: String = "", onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        if (value.isNotEmpty()) Text(value, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
    }
}

@Composable
fun DrawerMenuToggle(icon: String, label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked, onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF22C55E),
                uncheckedThumbColor = Color.White, uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun StopDropdown(
    selected: String, placeholder: String, onSelect: (String) -> Unit,
    cardBg: Color = Color.White, textPrimary: Color = Color(0xFF1E293B), textSecondary: Color = Color(0xFF94A3B8)
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier.fillMaxWidth()
                .border(1.dp, if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (selected.isEmpty()) placeholder else selected, color = if (selected.isEmpty()) textSecondary else textPrimary, fontSize = 14.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(cardBg)) {
            stops.forEach { stop ->
                DropdownMenuItem(text = { Text(stop, color = textPrimary) }, onClick = { onSelect(stop); expanded = false })
            }
        }
    }
}

@Composable
fun CrowdMeterCard(
    reports: List<BusReport>, route: BusRoute, ticker: Int = 0,
    str: AppStrings = getStrings("English"),
    cardBg: Color = Color.White, textPrimary: Color = Color(0xFF1E293B), textSecondary: Color = Color(0xFF94A3B8)
) {
    val (majorityStatus, voteCounts) = remember(reports, ticker) { computeMajorityStatus(reports) }
    val totalVotes = voteCounts.values.sum()

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(str.crowdMeter, fontWeight = FontWeight.Black, fontSize = 26.sp, letterSpacing = 2.sp, color = textPrimary)
            Text("BUS ${(route.busName ?: route.number).uppercase()} ${str.busApproaching}", color = textSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(str.empty, fontSize = 9.sp, color = textSecondary)
                Text(str.moderate, fontSize = 9.sp, color = textSecondary)
                Text(str.almostFull, fontSize = 9.sp, color = textSecondary)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9), RoundedCornerShape(20.dp))) {
                val barColor = when (majorityStatus) { "full" -> Color(0xFFEF4444); "empty" -> Color(0xFF22C55E); "seated" -> Color(0xFFF59E0B); else -> Color.Transparent }
                val fillFraction = when (majorityStatus) { "full" -> 1.0f; "seated" -> 0.6f; "empty" -> 0.15f; else -> 0.0f }
                if (fillFraction > 0f) Box(modifier = Modifier.fillMaxWidth(fillFraction).fillMaxHeight().background(barColor, RoundedCornerShape(20.dp)))
                Box(modifier = Modifier.size(40.dp).align(Alignment.CenterStart).background(cardBg, RoundedCornerShape(20.dp)).border(2.dp, if (AppSettings.isDarkMode) Color(0xFF475569) else Color(0xFFE2E8F0), RoundedCornerShape(20.dp)))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("\"${if (majorityStatus == "unknown") str.noRecentReports else majorityStatus.uppercase()}\"", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = textPrimary, letterSpacing = 1.sp)

            if (majorityStatus == "unknown" && reports.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(str.lastReportExpired, fontSize = 11.sp, color = Color(0xFFEF4444), textAlign = TextAlign.Center)
            }

            if (totalVotes > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("${str.basedOn} $totalVotes ${str.passengerReports}${if (totalVotes > 1) "s" else ""} (${str.last5Min})", fontSize = 11.sp, color = textSecondary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("empty", "seated", "full").forEach { status ->
                        val count = voteCounts[status] ?: 0
                        val color = when (status) { "empty" -> Color(0xFF22C55E); "seated" -> Color(0xFFF59E0B); else -> Color(0xFFEF4444) }
                        val label = when (status) { "empty" -> str.empty; "seated" -> str.seated; else -> str.full }
                        val isWinner = status == majorityStatus
                        Column(
                            modifier = Modifier.weight(1f)
                                .background(if (isWinner) color.copy(alpha = 0.15f) else if (AppSettings.isDarkMode) Color(0xFF334155) else Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .border(if (isWinner) 1.5.dp else 0.dp, if (isWinner) color else Color.Transparent, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$count", fontWeight = FontWeight.Black, fontSize = 18.sp, color = if (isWinner) color else textSecondary)
                            Text(label, fontSize = 10.sp, color = if (isWinner) color else textSecondary, fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal)
                            if (isWinner) Text(str.majority, fontSize = 8.sp, color = color, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}