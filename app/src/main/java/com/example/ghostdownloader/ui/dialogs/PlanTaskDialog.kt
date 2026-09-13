package com.example.ghostdownloader.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.NetworkRuleManager

@Composable
fun PlanTaskDialog(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSaveSchedule: (
        enabled: Boolean,
        startTime: String,
        endTime: String,
        wifiOnly: Boolean,
        stopOnLowBattery: Boolean,
        action: String
    ) -> Unit
) {
    val context = LocalContext.current
    var enabled by remember(settings) { mutableStateOf(settings.scheduledNightMode) }
    var startTime by remember(settings) {
        mutableStateOf(String.format("%02d:00", settings.nightStartHour))
    }
    var endTime by remember(settings) {
        mutableStateOf(String.format("%02d:00", settings.nightEndHour))
    }
    var wifiOnly by remember(settings) { mutableStateOf(settings.wifiOnly) }
    var stopOnLowBattery by remember(settings) { mutableStateOf(settings.stopOnLowBattery) }
    var selectedAction by remember { mutableStateOf("Play Completion Chime") }

    val isWifi = remember { NetworkRuleManager.isWifiConnected(context) }
    val batteryLvl = remember { NetworkRuleManager.getBatteryLevel(context) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.NightsStay, contentDescription = null, tint = CyberBlue)
                        Column {
                            Text("Download Scheduler & Rules", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Night Owl automation & network guards", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Night Owl Scheduler Toggle
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Automated Night Scheduler", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Automatically resume queued tasks at midnight", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = enabled, onCheckedChange = { enabled = it })
                        }

                        if (enabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { startTime = it },
                                    label = { Text("Start Time") },
                                    placeholder = { Text("02:00") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
                                    label = { Text("End Time") },
                                    placeholder = { Text("07:00") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                // Smart Network & Battery Rules
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Smart Power & Connectivity Rules", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                        // Wi-Fi Only Rule
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = if (wifiOnly) CyberTeal else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Download on Wi-Fi Only", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = if (isWifi) "Currently on Wi-Fi • OK" else "Cellular / Offline • Will pause on mobile data",
                                        fontSize = 10.sp,
                                        color = if (isWifi) CyberGreen else CyberTeal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Switch(checked = wifiOnly, onCheckedChange = { wifiOnly = it })
                        }

                        // Low Battery Rule
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = if (stopOnLowBattery) CyberGreen else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Stop when Battery < 15%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = "Current Battery: $batteryLvl%",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Switch(checked = stopOnLowBattery, onCheckedChange = { stopOnLowBattery = it })
                        }
                    }
                }

                // Post-Download Action
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Completion Action", style = MaterialTheme.typography.labelMedium)
                    val actions = listOf("Play Completion Chime", "Vibrate Alert", "Auto Sleep")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        actions.forEach { action ->
                            FilterChip(
                                selected = selectedAction == action,
                                onClick = { selectedAction = action },
                                label = { Text(action, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSaveSchedule(
                                enabled,
                                startTime,
                                endTime,
                                wifiOnly,
                                stopOnLowBattery,
                                selectedAction
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save Rules")
                    }
                }
            }
        }
    }
}
