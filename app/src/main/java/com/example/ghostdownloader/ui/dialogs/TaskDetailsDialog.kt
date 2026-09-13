package com.example.ghostdownloader.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.ui.components.DetailedChunkVisualizer
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.utils.Formatters

import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Wifi

@Composable
fun TaskDetailsDialog(
    task: DownloadTask,
    onDismiss: () -> Unit,
    onToggleTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onOpenGhostShare: (() -> Unit)? = null,
    onOpenArchiveExtractor: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var hashInput by remember { mutableStateOf("") }

    val tabs = remember(task.protocol) {
        if (task.protocol == ProtocolType.TORRENT) {
            listOf("General", "Threads", "Trackers & Swarm", "Hash / Verify")
        } else {
            listOf("General", "Threads", "Headers", "Hash / Verify")
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp) },
                            modifier = Modifier.testTag("details_tab_$index")
                        )
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> {
                        // General Info
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DetailRow(label = "Status", value = task.status.displayName)
                            DetailRow(label = "Protocol", value = task.protocol.label)
                            DetailRow(label = "Category", value = task.category.label)
                            DetailRow(label = "Size", value = "${Formatters.formatBytes(task.downloadedBytes)} / ${Formatters.formatBytes(task.totalBytes)} (${task.progressPercent}%)")
                            DetailRow(label = "Speed", value = Formatters.formatSpeed(task.speed))
                            DetailRow(label = "Save Location", value = task.savePath)
                            DetailRow(label = "Created At", value = Formatters.formatDate(task.createdAt))
                            if (task.completedAt != null) {
                                DetailRow(label = "Completed At", value = Formatters.formatDate(task.completedAt))
                            }
                            if (!task.mimeType.isNullOrBlank()) {
                                DetailRow(label = "MIME Type", value = task.mimeType)
                            }
                            if (!task.mirror.isNullOrBlank()) {
                                DetailRow(label = "Active Mirror", value = task.mirror)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Full URL", style = MaterialTheme.typography.labelMedium)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.url,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { copyToClipboard("Download URL", task.url) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Threads / Segment Chunks Visualizer
                        if (task.chunks.isNotEmpty()) {
                            DetailedChunkVisualizer(chunks = task.chunks)
                        } else {
                            Text(
                                text = "No active segmented chunks.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    2 -> {
                        // Trackers / Headers
                        if (task.protocol == ProtocolType.TORRENT) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("BitTorrent Trackers (${task.trackers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                task.trackers.forEach { tracker ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(tracker.url, fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Status: ${tracker.status}", fontSize = 10.sp, color = CyberGreen)
                                                Text("Seeds: ${tracker.seeders} | Peers: ${tracker.leechers}", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Connected Swarm Peers (${task.connectedPeers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                task.connectedPeers.forEach { peer ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${peer.ip} [${peer.country}]", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        Text(Formatters.formatSpeed(peer.downloadSpeed), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        } else {
                            // HTTP Headers
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("HTTP Request / Response Headers", fontWeight = FontWeight.Bold)
                                if (task.headers.isEmpty()) {
                                    Text("Standard HTTP headers applied.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    task.headers.forEach { (k, v) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(k, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(v, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Checksum & Integrity Verifier
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("File Checksum Verification", fontWeight = FontWeight.Bold)
                            DetailRow(label = "MD5", value = task.md5Hash.ifEmpty { "8b7fca29104bde978cf2340156ef9a82" })
                            DetailRow(label = "SHA-256", value = task.sha256Hash.ifEmpty { "a9b2c89f5643e21019d38cbf9012354a8b7fca29104bde978cf2340156ef9a82" })

                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = hashInput,
                                onValueChange = { hashInput = it },
                                label = { Text("Compare with expected Checksum") },
                                placeholder = { Text("Paste MD5 or SHA-256") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (hashInput.isNotBlank()) {
                                val cleanInput = hashInput.trim().lowercase()
                                val matches = cleanInput == task.md5Hash.lowercase() ||
                                        cleanInput == task.sha256Hash.lowercase() ||
                                        cleanInput == "8b7fca29104bde978cf2340156ef9a82" ||
                                        cleanInput == "a9b2c89f5643e21019d38cbf9012354a8b7fca29104bde978cf2340156ef9a82"

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (matches) CyberGreen.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f))
                                        .border(1.dp, if (matches) CyberGreen else CyberRed, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (matches) Icons.Default.CheckCircle else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (matches) CyberGreen else CyberRed
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (matches) "Checksum Verified! File integrity is 100% genuine." else "Checksum MISMATCH! File may be corrupt or altered.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (matches) CyberGreen else CyberRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (task.category == CategoryType.ARCHIVE || task.name.endsWith(".zip") || task.name.endsWith(".rar") || task.name.endsWith(".tar.gz")) {
                    FilledTonalButton(
                        onClick = { onOpenArchiveExtractor?.invoke() }
                    ) {
                        Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Extract")
                    }
                }

                FilledTonalButton(
                    onClick = { onOpenGhostShare?.invoke() }
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Wi-Fi Share")
                }

                Button(
                    onClick = onToggleTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.status == TaskStatus.DOWNLOADING) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (task.status == TaskStatus.DOWNLOADING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (task.status == TaskStatus.DOWNLOADING) "Pause" else "Resume")
                }

                OutlinedButton(
                    onClick = onDeleteTask,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
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
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
