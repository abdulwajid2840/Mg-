package com.mgplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mgplus.ui.MgPlusTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MgPlusTheme { MgPlusApp() } }
    }
}

private enum class AppTab(val label: String) {
    TODAY("Today"), MEDICINES("Medicines"), HISTORY("History"), SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MgPlusApp() {
    var selected by remember { mutableStateOf(AppTab.TODAY) }
    var showAdd by remember { mutableStateOf(false) }
    var medicines by remember { mutableStateOf(listOf<Medicine>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mg+", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            when (selected) {
                                AppTab.TODAY -> "Your day, in balance"
                                AppTab.MEDICINES -> "Your medicines"
                                AppTab.HISTORY -> "Your progress"
                                AppTab.SETTINGS -> "Make it yours"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                AppTab.entries.forEach { tab ->
                    val icon = when (tab) {
                        AppTab.TODAY -> Icons.Default.Home
                        AppTab.MEDICINES -> Icons.Default.Medication
                        AppTab.HISTORY -> Icons.Default.Timeline
                        AppTab.SETTINGS -> Icons.Default.Settings
                    }
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selected == AppTab.MEDICINES || selected == AppTab.TODAY) {
                FloatingActionButton(onClick = { showAdd = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add medicine")
                }
            }
        }
    ) { inner ->
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (slideInHorizontally(animationSpec = spring()) { it / 8 } + fadeIn()) togetherWith
                    (slideOutHorizontally(animationSpec = spring()) { -it / 8 } + fadeOut())
            },
            label = "main_tab_transition",
            modifier = Modifier.padding(inner)
        ) { tab ->
            when (tab) {
                AppTab.TODAY -> TodayScreen(medicines)
                AppTab.MEDICINES -> MedicinesScreen(medicines)
                AppTab.HISTORY -> HistoryScreen()
                AppTab.SETTINGS -> SettingsScreen()
            }
        }
    }

    if (showAdd) {
        AddMedicineDialog(
            onDismiss = { showAdd = false },
            onSave = { name, dose ->
                medicines = medicines + Medicine(name = name, dose = dose)
                showAdd = false
            }
        )
    }
}

@Composable
private fun TodayScreen(medicines: List<Medicine>) {
    val completed = medicines.count { it.taken }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text("A little care goes a long way.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("TODAY'S PROGRESS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("$completed of ${medicines.size} marked taken", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { if (medicines.isEmpty()) 0f else completed.toFloat() / medicines.size },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    )
                }
            }
        }
        item { Text("Your medicines", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        if (medicines.isEmpty()) {
            item {
                Card {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Medication, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Your list starts here", style = MaterialTheme.typography.titleMedium)
                        Text("Add a medicine to begin organising your routine.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            items(medicines, key = { it.id }) { medicine ->
                MedicineCard(medicine = medicine, onTaken = {})
            }
        }
    }
}

@Composable
private fun MedicinesScreen(medicines: List<Medicine>) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Keep everything in one place.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Text("Schedules, doses and notes will live here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (medicines.isEmpty()) item { Text("No medicines yet. Tap + to add your first one.") }
        items(medicines, key = { it.id }) { MedicineCard(it, onTaken = {}) }
    }
}

@Composable
private fun MedicineCard(medicine: Medicine, onTaken: () -> Unit) {
    Card {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(medicine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(medicine.dose, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (medicine.taken) "Marked taken" else "Not marked yet", style = MaterialTheme.typography.labelMedium)
            }
            AssistChip(onClick = onTaken, label = { Text(if (medicine.taken) "Taken" else "Mark") }, leadingIcon = {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
            })
        }
    }
}

@Composable
private fun HistoryScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Default.CalendarMonth, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
        Text("Your history", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Taken, skipped and snoozed events will appear here as reminder logging is added.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Mg+ is being built with local-first medicine data and expressive themes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card { Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Text("Parchment, Forest, Ocean, Rose, Graphite and Midnight themes are planned.")
        } }
        Card { Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Privacy and backups", style = MaterialTheme.typography.titleMedium)
            Text("Encrypted API key storage and password-protected backups are planned.")
        } }
    }
}

@Composable
private fun AddMedicineDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a medicine") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine name") }, singleLine = true)
                OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Dose (e.g. 1 tablet)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name.trim(), dose.ifBlank { "Dose not set" }) }, enabled = name.isNotBlank()) {
                Text("Add medicine")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

