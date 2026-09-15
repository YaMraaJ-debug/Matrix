package com.example.ghostdownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.PlatformArchitectureManager

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onOpenPlatformMatrix: () -> Unit = {},
    onOpenStealthProxy: () -> Unit = {},
    onOpenStorageCleaner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var maxConcurrent by remember(settings) { mutableFloatStateOf(settings.maxConcurrentDownloads.toFloat()) }
    var defaultConnections by remember(settings) { mutableFloatStateOf(settings.defaultConnections.toFloat()) }
    var downloadLimitKbps by remember(settings) { mutableFloatStateOf(settings.globalDownloadLimitKbps.toFloat()) }
    var savePath by remember(settings) { mutableStateOf(settings.downloadDirectory) }
    val deviceProfile = remember { PlatformArchitectureManager.getDeviceProfile() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section: Matrix Themes & Neon Engine
        item {
            SettingSectionHeader(title = "Matrix Visual Themes", icon = Icons.Default.Palette)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Cyber Aesthetic", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "Switch between Matrix Neon, Synthwave, OLED Pure Black, and Clean Light styles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val themePresets = listOf(
                        Triple("matrix_neon", "🟢 Matrix Neon", "Green Glow"),
                        Triple("synthwave", "🟣 Synthwave", "Cyber Violet"),
                        Triple("oled", "🖤 OLED Black", "Pure Black"),
                        Triple("cyber_blue", "🔵 Cyber Blue", "Deep Tech"),
                        Triple("clean_light", "⚪ Clean Light", "Minimalist")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themePresets.take(3).forEach { (id, label, _) ->
                            val isSelected = settings.appTheme == id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onUpdateSettings(settings.copy(appTheme = id)) },
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ) {
                                Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themePresets.drop(3).forEach { (id, label, _) ->
                            val isSelected = settings.appTheme == id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onUpdateSettings(settings.copy(appTheme = id)) },
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ) {
                                Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Storage & Directory
        item {
            SettingSectionHeader(title = "Storage & Directory", icon = Icons.Default.Folder)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = savePath,
                        onValueChange = {
                            savePath = it
                            onUpdateSettings(settings.copy(downloadDirectory = it))
                        },
                        label = { Text("Download Directory") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Category Folders", fontWeight = FontWeight.Medium)
                            Text("Sorts into Videos, Music, Software, Archives", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.autoDetectCategory,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoDetectCategory = it)) }
                        )
                    }

                    Button(
                        onClick = onOpenStorageCleaner,
                        modifier = Modifier.fillMaxWidth().testTag("open_storage_cleaner_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Storage & Clean Junk Chunks", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Section: Engine & Concurrency
        item {
            SettingSectionHeader(title = "Engine & Bandwidth", icon = Icons.Default.Speed)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Max Concurrent
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Max Concurrent Downloads", fontWeight = FontWeight.Medium)
                            Text("${maxConcurrent.toInt()} tasks", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = maxConcurrent,
                            onValueChange = {
                                maxConcurrent = it
                                onUpdateSettings(settings.copy(maxConcurrentDownloads = it.toInt()))
                            },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }

                    // Default Segment Connections
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Default Threads per Download", fontWeight = FontWeight.Medium)
                            Text("${defaultConnections.toInt()} threads", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = defaultConnections,
                            onValueChange = {
                                defaultConnections = it
                                onUpdateSettings(settings.copy(defaultConnections = it.toInt()))
                            },
                            valueRange = 1f..32f,
                            steps = 30
                        )
                    }

                    // Global Download Limit
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Global Download Speed Cap", fontWeight = FontWeight.Medium)
                            Text(
                                text = if (downloadLimitKbps <= 0) "Unlimited" else "${downloadLimitKbps.toInt()} KB/s",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = downloadLimitKbps,
                            onValueChange = {
                                downloadLimitKbps = it
                                onUpdateSettings(settings.copy(globalDownloadLimitKbps = it.toInt()))
                            },
                            valueRange = 0f..50000f,
                            steps = 49
                        )
                    }
                }
            }
        }

        // Section: Automation & Sniffing
        item {
            SettingSectionHeader(title = "Automation & Alerts", icon = Icons.Default.Tune)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clipboard URL Sniffer", fontWeight = FontWeight.Medium)
                            Text("Auto-detect download links and magnet URLs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.clipboardMonitor,
                            onCheckedChange = { onUpdateSettings(settings.copy(clipboardMonitor = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Start on Add", fontWeight = FontWeight.Medium)
                            Text("Immediately start downloads upon submission", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.autoStartOnAdd,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoStartOnAdd = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Play Completion Chime", fontWeight = FontWeight.Medium)
                            Text("Audio and vibration alert when transfer finishes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.playCompletionSound,
                            onCheckedChange = { onUpdateSettings(settings.copy(playCompletionSound = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Bypass Captchas & Timers", fontWeight = FontWeight.Medium)
                            Text("Automated headless resolution for Cloudflare & countdown wait-screens", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
                        }
                        Switch(
                            checked = settings.autoCaptchaBypassEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoCaptchaBypassEnabled = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Mirror & Dead Link Failover", fontWeight = FontWeight.Medium)
                            Text("Seamlessly recover dead or expired URLs from web mirrors without restarting", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.autoMirrorFailoverEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoMirrorFailoverEnabled = it)) }
                        )
                    }
                }
            }
        }

        // Section: Turbo Acceleration & Hardware Protection
        item {
            SettingSectionHeader(title = "Turbo Acceleration & Hardware Shield", icon = Icons.Default.Bolt)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dual-Channel Turbo Bond (Wi-Fi + 4G/5G)", fontWeight = FontWeight.Medium)
                            Text("Combines Wi-Fi and Cellular data simultaneously for 2x download bandwidth", style = MaterialTheme.typography.bodySmall, color = CyberGreen)
                        }
                        Switch(
                            checked = settings.dualChannelBondingEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(dualChannelBondingEnabled = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Battery & Thermal Health Shield", fontWeight = FontWeight.Medium)
                            Text("Smart pause when device temperature exceeds 42°C or battery < 15%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.thermalBatteryShieldEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(thermalBatteryShieldEnabled = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Deep Sleep on Complete", fontWeight = FontWeight.Medium)
                            Text("Release wake-locks and enter ultra-low power sleep when all downloads finish", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
                        }
                        Switch(
                            checked = settings.autoShutdownSleepOnComplete,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoShutdownSleepOnComplete = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Floating Speed Bubble (PiP Controller)", fontWeight = FontWeight.Medium)
                            Text("Displays real-time speed overlay widget while using other apps", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
                        }
                        Switch(
                            checked = settings.floatingBubbleEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(floatingBubbleEnabled = it)) }
                        )
                    }
                }
            }
        }

        // Section: Aria2 RPC Server Bridge
        item {
            SettingSectionHeader(title = "Aria2 RPC Bridge", icon = Icons.Default.NetworkCheck)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Aria2 RPC Server", fontWeight = FontWeight.Medium)
                            Text("Allows browser extensions and scripts to queue downloads", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.aria2RpcEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(aria2RpcEnabled = it)) }
                        )
                    }

                    if (settings.aria2RpcEnabled) {
                        OutlinedTextField(
                            value = settings.aria2RpcPort.toString(),
                            onValueChange = {
                                val p = it.toIntOrNull() ?: 6800
                                onUpdateSettings(settings.copy(aria2RpcPort = p))
                            },
                            label = { Text("RPC Listen Port") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Webhook & Remote Bot Trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Remote Webhook & Bot Sync", fontWeight = FontWeight.Medium)
                            Text("Trigger remote downloads via HTTP POST / Telegram Bot webhook", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberTeal.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(":6800/webhook", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyberTeal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Smart Scheduled Night Downloader (Off-Peak)
        item {
            SettingSectionHeader(title = "Smart Scheduled Night Downloader", icon = Icons.Default.Nightlight)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Scheduled Off-Peak Night Mode", fontWeight = FontWeight.Medium)
                            Text("Automatically queues and speeds up downloads during off-peak data hours (e.g. unlimited night data)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.scheduledNightMode,
                            onCheckedChange = { onUpdateSettings(settings.copy(scheduledNightMode = it)) }
                        )
                    }

                    if (settings.scheduledNightMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = settings.nightStartHour.toString(),
                                onValueChange = {
                                    val h = it.toIntOrNull()?.coerceIn(0, 23) ?: 2
                                    onUpdateSettings(settings.copy(nightStartHour = h))
                                },
                                label = { Text("Start Hour (0-23)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = settings.nightEndHour.toString(),
                                onValueChange = {
                                    val h = it.toIntOrNull()?.coerceIn(0, 23) ?: 7
                                    onUpdateSettings(settings.copy(nightEndHour = h))
                                },
                                label = { Text("End Hour (0-23)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Text(
                            "Tasks will auto-resume at ${settings.nightStartHour}:00 and auto-pause at ${settings.nightEndHour}:00 with maximum speed.",
                            fontSize = 11.sp,
                            color = CyberGreen
                        )
                    }
                }
            }
        }

        // Section: Stealth Mode & Proxy Network
        item {
            SettingSectionHeader(title = "Stealth Mode & Network Shield", icon = Icons.Default.Shield)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Active Proxy Routing", fontWeight = FontWeight.Medium)
                            val proxyDesc = if (settings.proxyMode == "tor") "Tor Onion Network (127.0.0.1:9050)"
                                else if (settings.proxyMode == "none") "Direct Connection (No Proxy)"
                                else "${settings.proxyMode.uppercase()} (${settings.proxyHost}:${settings.proxyPort})"
                            Text(proxyDesc, style = MaterialTheme.typography.bodySmall, color = CyberTeal, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Button(
                        onClick = onOpenStealthProxy,
                        modifier = Modifier.fillMaxWidth().testTag("open_stealth_proxy_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configure Proxy (Tor/SOCKS5) & UA Spoofing", fontSize = 13.sp)
                    }

                    // Decoy Vault PIN
                    OutlinedTextField(
                        value = settings.decoyVaultPin,
                        onValueChange = { onUpdateSettings(settings.copy(decoyVaultPin = it)) },
                        label = { Text("Emergency Decoy Vault PIN") },
                        supportingText = { Text("Entering this PIN opens an innocent decoy sandbox with dummy files") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // EXIF / Metadata Stripper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Strip EXIF & Device Metadata", fontWeight = FontWeight.Medium)
                            Text("Wipes GPS, camera serials, and timestamps from downloaded images & videos", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
                        }
                        Switch(
                            checked = settings.autoStripMetadata,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoStripMetadata = it)) }
                        )
                    }

                    // Storage Deduplication
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SHA-256 Storage Deduplication", fontWeight = FontWeight.Medium)
                            Text("Detects identical file hashes before downloading chunks to save internal storage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.autoDeduplication,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoDeduplication = it)) }
                        )
                    }

                    // AI Subtitle Downloader
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Subtitle Auto-Downloader", fontWeight = FontWeight.Medium)
                            Text("Automatically scrapes & pairs multi-language .srt subtitles for videos", style = MaterialTheme.typography.bodySmall, color = CyberGreen)
                        }
                        Switch(
                            checked = settings.autoSubtitleDownload,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoSubtitleDownload = it)) }
                        )
                    }
                }
            }
        }

        // Section: Platform & Architecture Matrix
        item {
            SettingSectionHeader(title = "Platform & Architecture Engine", icon = Icons.Default.Memory)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Current Architecture (ABI)", fontWeight = FontWeight.Medium)
                            Text(
                                text = "${deviceProfile.architectureLabel} • Android ${deviceProfile.osVersion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberTeal,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Android 9.0+ OK", fontSize = 10.sp, color = CyberGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("NEON / SIMD Hardware Engine", fontWeight = FontWeight.Medium)
                            Text("Accelerates SHA-256 and chunk reassembly", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.neonAccelerationEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(neonAccelerationEnabled = it)) }
                        )
                    }

                    Button(
                        onClick = onOpenPlatformMatrix,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Supported Platforms & ABIs", fontSize = 13.sp)
                    }
                }
            }
        }

        // About Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ghost Downloader 3", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "v3.0.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberBlueLight
                        )
                    }
                    Text(
                        "Multi-protocol high-performance download manager & media sniffer with native multi-architecture engine.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Supported Platforms & Architectures:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberBlueLight
                    )
                    Text("• Windows 10+ (x86_64 / arm64)", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("• macOS 13.0+ (x86_64 / arm64)", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("• Linux glibc 2.35+ (x86_64 / arm64)", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("• Android 9.0+ (arm64-v8a / armeabi-v7a)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = CyberGreen)
                }
            }
        }
    }
}

@Composable
private fun SettingSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}
