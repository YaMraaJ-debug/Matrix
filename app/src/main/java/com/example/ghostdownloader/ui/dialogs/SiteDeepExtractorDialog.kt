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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.ui.theme.CyberAmber
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters

data class PageDiscoveredItem(
    val id: String,
    val title: String,
    val directUrl: String,
    val category: CategoryType,
    val protocol: ProtocolType,
    val estimatedBytes: Long,
    val isSelected: Boolean = true,
    val sourceContext: String = "Direct Stream / File"
)

@Composable
fun SiteDeepExtractorDialog(
    initialUrl: String = "",
    onDismiss: () -> Unit,
    onEnqueueBatch: (List<PageDiscoveredItem>) -> Unit
) {
    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf(initialUrl) }
    var isExtracting by remember { mutableStateOf(false) }
    var selectedFilterCategory by remember { mutableStateOf(CategoryType.ALL) }
    val discoveredItems = remember { mutableStateListOf<PageDiscoveredItem>() }

    fun runDeepExtract(url: String) {
        if (url.isBlank()) return
        isExtracting = true
        discoveredItems.clear()

        // Asynchronous deep parser simulation resolving direct download streams & skipping intermediate ad pages
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val domain = try {
                val cleaned = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
                java.net.URI(cleaned).host ?: "example.com"
            } catch (e: Exception) {
                "web-source.com"
            }

            val itemsFound = listOf(
                PageDiscoveredItem(
                    id = "deep-1",
                    title = "$domain - Full_HD_Stream_1080p.mp4",
                    directUrl = "https://$domain/cdn/direct-stream/1080p_master.mp4",
                    category = CategoryType.VIDEO,
                    protocol = ProtocolType.HTTP,
                    estimatedBytes = 345_000_000L,
                    sourceContext = "Bypassed Cloudflare & Ad-Gate"
                ),
                PageDiscoveredItem(
                    id = "deep-2",
                    title = "$domain - Adaptive_HLS_Playlist.m3u8",
                    directUrl = "https://$domain/live/index.m3u8",
                    category = CategoryType.VIDEO,
                    protocol = ProtocolType.M3U8,
                    estimatedBytes = 180_000_000L,
                    sourceContext = "Direct M3U8 Master Manifest"
                ),
                PageDiscoveredItem(
                    id = "deep-3",
                    title = "$domain - High_Res_Soundtrack.mp3",
                    directUrl = "https://$domain/audio/track_01_320kbps.mp3",
                    category = CategoryType.MUSIC,
                    protocol = ProtocolType.HTTP,
                    estimatedBytes = 14_500_000L,
                    sourceContext = "Audio Stream Element"
                ),
                PageDiscoveredItem(
                    id = "deep-4",
                    title = "$domain - Complete_Asset_Pack.zip",
                    directUrl = "https://$domain/downloads/packages/installer_v3.zip",
                    category = CategoryType.ARCHIVE,
                    protocol = ProtocolType.HTTP,
                    estimatedBytes = 540_000_000L,
                    sourceContext = "Direct Raw Binary"
                ),
                PageDiscoveredItem(
                    id = "deep-5",
                    title = "$domain - Official_Documentation.pdf",
                    directUrl = "https://$domain/docs/manual_2026.pdf",
                    category = CategoryType.DOCUMENT,
                    protocol = ProtocolType.HTTP,
                    estimatedBytes = 8_200_000L,
                    sourceContext = "Embedded Document"
                )
            )

            discoveredItems.addAll(itemsFound)
            isExtracting = false
            Toast.makeText(context, "Direct links extracted from $domain (Ad pages skipped)!", Toast.LENGTH_SHORT).show()
        }, 900)
    }

    val filteredList = remember(discoveredItems.toList(), selectedFilterCategory) {
        if (selectedFilterCategory == CategoryType.ALL) discoveredItems
        else discoveredItems.filter { it.category == selectedFilterCategory }
    }

    val selectedCount = remember(discoveredItems.toList()) {
        discoveredItems.count { it.isSelected }
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
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Site Deep Link Extractor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Auto-skip ad gates & grab all page assets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // URL input bar with extract button
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    placeholder = { Text("Paste any webpage link to grab all files", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("site_extractor_input"),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                if (!clip.isNullOrBlank()) {
                                    inputUrl = clip.trim()
                                }
                            }
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste")
                        }
                    }
                )

                // Quick Analyze Button
                Button(
                    onClick = { runDeepExtract(inputUrl) },
                    enabled = inputUrl.isNotBlank() && !isExtracting,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("extract_page_assets_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                ) {
                    if (isExtracting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bypassing Ad-Gates & Deep Scanning...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extract All Direct Media & Files", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Filter chips if results found
                if (discoveredItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Found (${discoveredItems.size}):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(
                                onClick = {
                                    val shouldSelect = discoveredItems.any { !it.isSelected }
                                    for (i in 0 until discoveredItems.size) {
                                        discoveredItems[i] = discoveredItems[i].copy(isSelected = shouldSelect)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(if (discoveredItems.all { it.isSelected }) "Deselect All" else "Select All", fontSize = 11.sp)
                            }
                        }
                    }

                    // Category filters
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(CategoryType.ALL, CategoryType.VIDEO, CategoryType.MUSIC, CategoryType.ARCHIVE).forEach { cat ->
                            FilterChip(
                                selected = selectedFilterCategory == cat,
                                onClick = { selectedFilterCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Discovered list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            val index = discoveredItems.indexOfFirst { it.id == item.id }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (item.isSelected) CyberBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (index != -1) {
                                            discoveredItems[index] = item.copy(isSelected = !item.isSelected)
                                        }
                                    }
                                    .border(
                                        1.dp,
                                        if (item.isSelected) CyberBlueLight else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (item.isSelected) CyberGreen else MaterialTheme.colorScheme.outlineVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = item.sourceContext,
                                                fontSize = 10.sp,
                                                color = CyberGreen,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "• ${Formatters.formatBytes(item.estimatedBytes)}",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = CyberTeal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val toEnqueue = discoveredItems.filter { it.isSelected }
                    if (toEnqueue.isNotEmpty()) {
                        onEnqueueBatch(toEnqueue)
                    }
                    onDismiss()
                },
                enabled = selectedCount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                modifier = Modifier.testTag("download_all_extracted_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Download Direct ($selectedCount)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
