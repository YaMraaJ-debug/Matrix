package com.example.ghostdownloader.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal

@Composable
fun SpeedLimiterDialog(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSaveLimit: (speedKbps: Int, preset: String) -> Unit
) {
    var limitKbps by remember(settings) {
        mutableFloatStateOf(settings.globalDownloadLimitKbps.toFloat())
    }
    var selectedPreset by remember(settings) {
        mutableStateOf(settings.speedThrottlePreset)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Icon(Icons.Default.Speed, contentDescription = null, tint = CyberTeal)
                        Column {
                            Text("Dynamic Speed Limiter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Real-time TCP bandwidth shaping", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Throttle Presets
                Text("Bandwidth Allocation Profiles", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedPreset == "eco",
                        onClick = {
                            selectedPreset = "eco"
                            limitKbps = 500f
                        },
                        label = { Text("Eco (500K)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Nature, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedPreset == "gaming",
                        onClick = {
                            selectedPreset = "gaming"
                            limitKbps = 2000f
                        },
                        label = { Text("Game (2M)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Gamepad, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedPreset == "turbo",
                        onClick = {
                            selectedPreset = "turbo"
                            limitKbps = 10000f
                        },
                        label = { Text("Turbo (10M)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                FilterChip(
                    selected = selectedPreset == "unlimited" || limitKbps == 0f,
                    onClick = {
                        selectedPreset = "unlimited"
                        limitKbps = 0f
                    },
                    label = { Text("⚡ Turbo Uncapped (Max Line Speed)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberGreen.copy(alpha = 0.2f),
                        selectedLabelColor = CyberGreen
                    )
                )

                // Interactive Precision Slider
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bandwidth Cap", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = if (limitKbps <= 0f) "Unlimited" else "${limitKbps.toInt()} KB/s (${String.format("%.1f", limitKbps / 1024f)} MB/s)",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                fontSize = 12.sp
                            )
                        }

                        Slider(
                            value = limitKbps,
                            onValueChange = {
                                limitKbps = it
                                selectedPreset = "custom"
                            },
                            valueRange = 0f..50000f,
                            steps = 49
                        )
                    }
                }

                // Actions
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
                            onSaveLimit(limitKbps.toInt(), selectedPreset)
                            onDismiss()
                        }
                    ) {
                        Text("Apply Limiter")
                    }
                }
            }
        }
    }
}
