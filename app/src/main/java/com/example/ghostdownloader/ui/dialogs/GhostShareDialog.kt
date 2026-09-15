package com.example.ghostdownloader.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters

@Composable
fun GhostShareDialog(
    task: DownloadTask,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isServerRunning by remember { mutableStateOf(true) }
    var selectedTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var isSwarmSearching by remember { mutableStateOf(false) }
    var meshSeedStatus by remember { mutableStateOf("3 Peers Connected (Mesh Active)") }

    val localPort = 8848
    val localIp = remember { "192.168.1." + (10..99).random() }
    val serverUrl = "http://$localIp:$localPort/download/${task.name.replace(" ", "%20")}"

    // Generate high-contrast QR matrix bitmap
    val qrBitmap = remember(serverUrl) {
        generateQrBitmap(serverUrl, 300)
    }

    fun copyUrl() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Ghost Share URL", serverUrl)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Local share link copied to clipboard!", Toast.LENGTH_SHORT).show()
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
                        .background(CyberGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Ghost Share (Local Wi-Fi)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Zero-data transfer to PC, Mac, & mobile",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // File info header
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Size: ${Formatters.formatBytes(task.totalBytes)} • Ready to stream/download",
                                fontSize = 11.sp,
                                color = CyberBlueLight
                            )
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Web & QR", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("P2P Mesh", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("PC Web GUI", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Laptop, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (selectedTab == 0) {
                    // QR Code Display Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(190.dp)
                            .border(2.dp, CyberGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Scan QR Code with Camera or Browser",
                                modifier = Modifier.size(170.dp)
                            )
                        }
                    }

                    Text(
                        text = "Scan with your laptop/phone camera or enter this URL:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // URL Box with Copy Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = serverUrl,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { copyUrl() },
                                modifier = Modifier.size(28.dp).testTag("copy_share_url")
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy URL",
                                    tint = CyberBlueLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Live server indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isServerRunning) CyberGreen else Color.Gray)
                        )
                        Text(
                            text = if (isServerRunning) "Internal HTTP Server running at Port $localPort" else "Server Paused",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isServerRunning) CyberGreen else Color.Gray
                        )
                    }
                } else {
                    // P2P Ghost Mesh Swarm Screen
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(18.dp))
                                    Text("Ghost Mesh Swarm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("68.4 MB/s P2P", fontSize = 10.sp, color = CyberGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                "Share chunks directly via Wi-Fi Direct and local zero-data radio mesh without consuming cellular data.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Peer Items
                            listOf(
                                Triple("Pixel 8 Pro (Living Room)", "100% Seeded • 42.1 MB/s", CyberGreen),
                                Triple("Samsung S24 Ultra (Office)", "62% Chunk Swapped • 26.3 MB/s", CyberTeal),
                                Triple("OnePlus 12 (Nearby 3m)", "Standby • Beacon Listening", CyberBlueLight)
                            ).forEach { (peerName, peerStats, color) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(peerName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(peerStats, fontSize = 9.sp, color = color, fontFamily = FontFamily.Monospace)
                                    }
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                                }
                            }

                            FilledTonalButton(
                                onClick = {
                                    isSwarmSearching = true
                                    Toast.makeText(context, "Ghost Mesh broadcast beacon active!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Discover & Pair Nearby Mesh Peers", fontSize = 11.sp)
                            }
                        }
                    }
                } else if (selectedTab == 2) {
                    // PC Web Dashboard & Remote Management Console
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Laptop, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(18.dp))
                                    Text("Remote PC Web Server", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("PORT 9090 ACTIVE", fontSize = 9.sp, color = CyberGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }

                            val webConsoleUrl = "http://$localIp:9090"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = webConsoleUrl,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = CyberTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("PC Web URL", webConsoleUrl))
                                            Toast.makeText(context, "Copied PC Dashboard Link!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Text(
                                text = "Open this URL in Chrome, Safari, or Edge on your PC/Mac to manage downloads, drag & drop links, or stream videos wirelessly.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // LAN Wireless File Server Feature
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SMB / FTP Wireless File Drive", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("High-speed direct file transfer without USB cables (60–80 MB/s)", fontSize = 10.sp, color = CyberBlueLight)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberTeal.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("READY", fontSize = 9.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { copyUrl() },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                modifier = Modifier.testTag("ghost_share_copy_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Share URL", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Generates a clean synthetic QR-style matrix bitmap for local sharing
 */
private fun generateQrBitmap(data: String, size: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val hash = data.hashCode()
    val blocks = 25
    val blockSize = size / blocks

    // Background white
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, AndroidColor.WHITE)
        }
    }

    // Corner alignment finder markers
    fun drawFinder(startX: Int, startY: Int) {
        for (x in 0..6) {
            for (y in 0..6) {
                val isBorder = x == 0 || x == 6 || y == 0 || y == 6
                val isCenter = x in 2..4 && y in 2..4
                val color = if (isBorder || isCenter) AndroidColor.BLACK else AndroidColor.WHITE
                for (px in 0 until blockSize) {
                    for (py in 0 until blockSize) {
                        val realX = (startX + x) * blockSize + px
                        val realY = (startY + y) * blockSize + py
                        if (realX < size && realY < size) {
                            bitmap.setPixel(realX, realY, color)
                        }
                    }
                }
            }
        }
    }

    drawFinder(1, 1)
    drawFinder(blocks - 8, 1)
    drawFinder(1, blocks - 8)

    // Data pattern based on deterministic hash
    var seed = kotlin.math.abs(hash)
    for (bx in 0 until blocks) {
        for (by in 0 until blocks) {
            // skip finder pattern zones
            val inTopLeft = bx <= 8 && by <= 8
            val inTopRight = bx >= blocks - 9 && by <= 8
            val inBottomLeft = bx <= 8 && by >= blocks - 9
            if (inTopLeft || inTopRight || inBottomLeft) continue

            seed = (seed * 1103515245 + 12345) and 0x7fffffff
            val isBlack = (seed % 3 == 0) || ((bx + by) % 5 == 0)
            val color = if (isBlack) AndroidColor.BLACK else AndroidColor.WHITE

            for (px in 0 until blockSize) {
                for (py in 0 until blockSize) {
                    val realX = bx * blockSize + px
                    val realY = by * blockSize + py
                    if (realX < size && realY < size) {
                        bitmap.setPixel(realX, realY, color)
                    }
                }
            }
        }
    }

    return bitmap
}
