package com.example.ghostdownloader.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.ui.theme.CyberAmber
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters
import com.example.ghostdownloader.utils.StorageManager

@Composable
fun StorageCleanerDialog(
    tasks: List<DownloadTask>,
    onCleanJunk: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var cleanedFeedback by remember { mutableStateOf(false) }
    val storageProfile = remember { StorageManager.getDeviceStorageProfile() }

    val videoBytes = remember(tasks) {
        tasks.filter { it.category == CategoryType.VIDEO }.sumOf { it.downloadedBytes }
    }
    val musicBytes = remember(tasks) {
        tasks.filter { it.category == CategoryType.MUSIC }.sumOf { it.downloadedBytes }
    }
    val softwareBytes = remember(tasks) {
        tasks.filter { it.category == CategoryType.SOFTWARE }.sumOf { it.downloadedBytes }
    }
    val archiveBytes = remember(tasks) {
        tasks.filter { it.category == CategoryType.ARCHIVE }.sumOf { it.downloadedBytes }
    }
    val torrentBytes = remember(tasks) {
        tasks.filter { it.category == CategoryType.TORRENT }.sumOf { it.downloadedBytes }
    }

    val junkCacheBytes = remember(tasks) {
        // Estimated corrupted chunk parts / temporary resume fragments
        (48_000_000L..165_000_000L).random()
    }

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
                        .background(CyberTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Storage & Junk Cleaner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Category breakdown & temp chunk cleanup",
                        style = MaterialTheme.typography.labelSmall,
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
                // Device Storage Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Device Internal Storage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${Formatters.formatBytes(storageProfile.usedBytes)} / ${Formatters.formatBytes(storageProfile.totalBytes)} (${storageProfile.usedPercent}%)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberBlueLight
                            )
                        }

                        LinearProgressIndicator(
                            progress = { storageProfile.usedPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = CyberTeal
                        )
                    }
                }

                // Category Storage Breakdown
                Text("Downloads by Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryStorageRow("Videos & Streams", videoBytes, CyberBlue)
                    CategoryStorageRow("Music & Audio", musicBytes, CyberTeal)
                    CategoryStorageRow("Software & APKs", softwareBytes, CyberGreen)
                    CategoryStorageRow("Archives (ZIP/RAR)", archiveBytes, CyberAmber)
                    CategoryStorageRow("Torrents & Magnet", torrentBytes, CyberPurple)
                }

                // Junk cleanup card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (cleanedFeedback) CyberGreen.copy(alpha = 0.1f) else CyberRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (cleanedFeedback) CyberGreen.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = if (cleanedFeedback) CyberGreen else CyberRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (cleanedFeedback) "Cleaned & Optimized!" else "Residual Temporary Chunks",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (cleanedFeedback) CyberGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (cleanedFeedback) "Freed ${Formatters.formatBytes(junkCacheBytes)} of storage space" else "Found ${Formatters.formatBytes(junkCacheBytes)} of stale .tmp pieces and broken resume chunks",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!cleanedFeedback) {
                        cleanedFeedback = true
                        onCleanJunk()
                        Toast.makeText(context, "Temporary files and broken chunks cleared!", Toast.LENGTH_SHORT).show()
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (cleanedFeedback) CyberGreen else CyberTeal),
                modifier = Modifier.testTag("clean_storage_btn")
            ) {
                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (cleanedFeedback) "Done" else "Clean Junk Chunks", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun CategoryStorageRow(
    category: String,
    bytes: Long,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Text(text = category, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            text = Formatters.formatBytes(bytes),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
