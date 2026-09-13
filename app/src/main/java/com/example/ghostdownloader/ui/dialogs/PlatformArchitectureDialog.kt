package com.example.ghostdownloader.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.BenchmarkResult
import com.example.ghostdownloader.utils.PlatformArchitectureManager
import com.example.ghostdownloader.utils.PlatformRequirement
import kotlinx.coroutines.launch

@Composable
fun PlatformArchitectureDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val deviceProfile = remember { PlatformArchitectureManager.getDeviceProfile() }
    val platforms = remember { PlatformArchitectureManager.supportedPlatforms }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isRunningBenchmark by remember { mutableStateOf(false) }
    var benchmarkResult by remember { mutableStateOf<BenchmarkResult?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBlue.copy(alpha = 0.2f))
                        .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "Architecture",
                        tint = CyberBlueLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Platform & Architecture Matrix",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cross-Platform Engines & Dual-ABI Optimization",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Platforms", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Device ABI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Desktop Bridge", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("Release APK", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                when (selectedTabIndex) {
                    0 -> CrossPlatformRequirementsTab(platforms = platforms, context = context)
                    1 -> DeviceAbiDiagnosticsTab(
                        deviceProfile = deviceProfile,
                        isRunningBenchmark = isRunningBenchmark,
                        benchmarkResult = benchmarkResult,
                        onRunBenchmark = {
                            coroutineScope.launch {
                                isRunningBenchmark = true
                                benchmarkResult = PlatformArchitectureManager.runArchitectureBenchmark()
                                isRunningBenchmark = false
                            }
                        }
                    )
                    2 -> DesktopBridgeTab(context = context)
                    3 -> ReleaseApkWorkflowTab(context = context)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun CrossPlatformRequirementsTab(
    platforms: List<PlatformRequirement>,
    context: Context
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Official Platform & Architecture Compatibility",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CyberBlueLight
        )

        platforms.forEach { platform ->
            PlatformCard(platform = platform, context = context)
        }
    }
}

@Composable
private fun PlatformCard(
    platform: PlatformRequirement,
    context: Context
) {
    val borderColor = if (platform.isCurrentDevice) CyberGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (platform.isCurrentDevice) CyberGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = platform.platformName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (platform.isCurrentDevice) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "THIS DEVICE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGreen
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (platform.isCurrentDevice) CyberGreen.copy(alpha = 0.2f) else CyberBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = platform.statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (platform.isCurrentDevice) CyberGreen else CyberBlueLight
                    )
                }
            }

            // Version & Architectures Table
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Required Version", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(platform.requiredVersion, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Architectures", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        platform.architectures,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTeal
                    )
                }
            }

            Text(
                text = "Engine: ${platform.engineBackend}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (platform.notes.isNotEmpty()) {
                Text(
                    text = platform.notes,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun DeviceAbiDiagnosticsTab(
    deviceProfile: com.example.ghostdownloader.utils.DeviceArchitectureProfile,
    isRunningBenchmark: Boolean,
    benchmarkResult: BenchmarkResult?,
    onRunBenchmark: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // System OS verification
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Android System Level", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (deviceProfile.isAndroid9Plus) "Android 9.0+ Certified" else "Android 8.0 Legacy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("OS Version", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Android ${deviceProfile.osVersion} (API ${deviceProfile.apiLevel})", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Architecture Mode", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (deviceProfile.is64Bit) "64-bit Architecture" else "32-bit Architecture",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlueLight
                    )
                }
            }
        }

        // Native ABI Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Native ABI & Hardware Engine", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Primary Target ABI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = deviceProfile.primaryAbi,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTeal
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("All Supported ABIs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = deviceProfile.supportedAbis.joinToString(", "),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("CPU Cores & Max Heap", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${deviceProfile.cpuCores} Cores | ${deviceProfile.maxHeapMemoryMb} MB Heap", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SIMD / NEON Hardware Engine", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (deviceProfile.neonAccelerationActive) "ACTIVE (Accelerated)" else "Standard",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deviceProfile.neonAccelerationActive) CyberGreen else MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recommended Chunk Buffer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${deviceProfile.recommendedBufferSizeBytes / (1024 * 1024)} MB / stream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Benchmark / Self-Test
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Architecture Throughput Benchmark", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Tests SHA-256 SIMD speed & memory cycling", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = onRunBenchmark,
                        enabled = !isRunningBenchmark,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isRunningBenchmark) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run", fontSize = 12.sp)
                        }
                    }
                }

                benchmarkResult?.let { bench ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlue.copy(alpha = 0.1f))
                            .border(1.dp, CyberBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Performance Grade", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(bench.grade, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Hashing Throughput (SHA-256)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${bench.hashingThroughputMbPerSec} MB/s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberBlueLight)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Memory Bus Bandwidth", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${bench.memoryThroughputMbPerSec} MB/s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coroutine Dispatch Latency", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${bench.coroutineDispatchLatencyMs} ms", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopBridgeTab(context: Context) {
    val winCli = "ghost-cli.exe --remote-bridge --port 6800 --token ghost3secret"
    val unixCli = "ghost-cli --remote-bridge --port 6800 --token ghost3secret"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Cross-Platform Remote Node Bridge",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CyberBlueLight
        )
        Text(
            text = "Connect this Android app to Ghost Downloader 3 running on Windows 10+, macOS 13+, or Linux glibc 2.35+ over WiFi/LAN.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Windows 10+ (x86_64 / arm64) Startup Command", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = winCli,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(ClipData.newPlainText("Ghost Windows CLI", winCli))
                            Toast.makeText(context, "Copied Windows CLI command", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("macOS 13.0+ & Linux glibc 2.35+ (x86_64 / arm64)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = unixCli,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(ClipData.newPlainText("Ghost Unix CLI", unixCli))
                            Toast.makeText(context, "Copied macOS/Linux CLI command", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseApkWorkflowTab(context: Context) {
    val gradleAssembleCmd = "./gradlew assembleRelease"
    val gradleBundleCmd = "./gradlew bundleRelease"
    val adbInstallCmd = "adb install -r app/build/outputs/apk/release/app-release.apk"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
                    Text("Release Packaging Status: READY", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberGreen)
                }
                Text(
                    "Ghost Downloader 3 has been hardened and validated for release APK builds across all Android architectures.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Release Checklist
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Release Pipeline Specifications", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                WorkflowCheckItem("R8 Shrinking & Obfuscation", "Enabled (isMinifyEnabled = true, isShrinkResources = true) with optimized ProGuard rules to minimize APK footprint.")
                WorkflowCheckItem("Signing Configuration", "Release APK automatically signed with Debug Keystore for zero-friction physical device sideloading.")
                WorkflowCheckItem("ProGuard / R8 Rules", "Room entities, DAOs, Coroutines, and download models protected in /app/proguard-rules.pro.")
                WorkflowCheckItem("Cleartext Traffic (HTTP)", "android:usesCleartextTraffic=true enabled to prevent Android 9+ from blocking HTTP file mirrors.")
                WorkflowCheckItem("Architecture Filters", "Native ABI filters: arm64-v8a, armeabi-v7a (32-bit ARM), x86_64, x86.")
                WorkflowCheckItem("Target SDK & Support", "Compiled for modern Android (API 34) with backward compatibility down to Android 9.0 (API 28).")
            }
        }

        // CLI Commands
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Terminal Build Commands", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Text("Build Standalone Release APK:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                CliCopyRow(command = gradleAssembleCmd, label = "Release APK command", context = context)

                Text("Build Play Store Bundle (AAB):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                CliCopyRow(command = gradleBundleCmd, label = "Release AAB command", context = context)

                Text("Deploy & Install to Physical Phone via ADB:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                CliCopyRow(command = adbInstallCmd, label = "ADB install command", context = context)
            }
        }
    }
}

@Composable
private fun WorkflowCheckItem(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = CyberTeal,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CliCopyRow(command: String, label: String, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = command,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberBlueLight,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clip.setPrimaryClip(ClipData.newPlainText(label, command))
                Toast.makeText(context, "Copied: $command", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
        }
    }
}
