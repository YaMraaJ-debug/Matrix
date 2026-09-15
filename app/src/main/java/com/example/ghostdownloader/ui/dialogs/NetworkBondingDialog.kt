package com.example.ghostdownloader.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberTeal
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun NetworkBondingDialog(
    bondingEnabled: Boolean,
    onToggleBonding: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var isBondingActive by remember { mutableStateOf(bondingEnabled) }
    var cdnFailoverEnabled by remember { mutableStateOf(true) }
    var localMeshEnabled by remember { mutableStateOf(true) }
    var thermalGuardEnabled by remember { mutableStateOf(true) }

    // Live simulated telemetry
    var wifiSpeed by remember { mutableFloatStateOf(28.4f) }
    var cellularSpeed by remember { mutableFloatStateOf(19.8f) }
    var batteryTemp by remember { mutableFloatStateOf(34.2f) }

    LaunchedEffect(isBondingActive) {
        while (true) {
            delay(1500)
            if (isBondingActive) {
                wifiSpeed = 26f + Random.nextFloat() * 6f
                cellularSpeed = 16f + Random.nextFloat() * 7f
            } else {
                wifiSpeed = 25f + Random.nextFloat() * 4f
                cellularSpeed = 0f
            }
            batteryTemp = 34f + Random.nextFloat() * 1.5f
        }
    }

    val totalBondedSpeed = remember(wifiSpeed, cellularSpeed, isBondingActive) {
        if (isBondingActive) wifiSpeed + cellularSpeed else wifiSpeed
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberTeal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Dual-Channel Multi-Network Bonding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Wi-Fi + 5G/LTE Aggregation Engine",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_bonding_dialog_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Live Bonded Bandwidth Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberTeal.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL AGGREGATED PIPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberTeal)
                                Text(
                                    text = String.format("%.1f MB/s", totalBondedSpeed),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isBondingActive) CyberGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isBondingActive) "BONDED BOOST ACTIVE" else "SINGLE CHANNEL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBondingActive) CyberGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Sub channel bars
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Wi-Fi Channel
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Wifi, contentDescription = null, tint = CyberBlueLight, modifier = Modifier.size(14.dp))
                                    Text("Wi-Fi 6 Channel", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text(String.format("%.1f MB/s", wifiSpeed), fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                LinearProgressIndicator(
                                    progress = { (wifiSpeed / 40f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = CyberBlueLight
                                )
                            }

                            // Cellular 5G Channel
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.CellTower, contentDescription = null, tint = if (isBondingActive) CyberTeal else Color.Gray, modifier = Modifier.size(14.dp))
                                    Text("5G/LTE Sub-channel", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text(
                                    if (isBondingActive) String.format("%.1f MB/s", cellularSpeed) else "Disabled",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBondingActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                                )
                                LinearProgressIndicator(
                                    progress = { if (isBondingActive) (cellularSpeed / 40f).coerceIn(0f, 1f) else 0f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = CyberTeal
                                )
                            }
                        }
                    }
                }

                // Controls & Toggles
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dual-Channel Bonding Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Merge Wi-Fi + Mobile data packets for 2x faster downloads", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBondingActive,
                                onCheckedChange = {
                                    isBondingActive = it
                                    onToggleBonding(it)
                                },
                                modifier = Modifier.testTag("toggle_bonding_switch")
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dynamic CDN & Mirror Auto-Failover", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Auto-switch dead or throttled links to high-speed CDN mirrors", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = cdnFailoverEnabled, onCheckedChange = { cdnFailoverEnabled = it })
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ghost Mesh Local P2P Discovery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Exchange chunks with nearby local devices over LAN zero-data", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = localMeshEnabled, onCheckedChange = { localMeshEnabled = it })
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(15.dp))
                                    Text("Battery & Thermal Guard", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Text("Current Temp: ${String.format("%.1f°C", batteryTemp)} • Auto throttle at >42°C", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = thermalGuardEnabled, onCheckedChange = { thermalGuardEnabled = it })
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_bonding_settings_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Network Configuration", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
