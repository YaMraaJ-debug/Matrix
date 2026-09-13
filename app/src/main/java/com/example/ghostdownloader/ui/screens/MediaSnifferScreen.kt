package com.example.ghostdownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.ImageResource
import com.example.ghostdownloader.data.model.MediaResource
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.ui.components.BuiltInBrowserView
import com.example.ghostdownloader.ui.dialogs.MediaPlayerModal
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.utils.Formatters

@Composable
fun MediaSnifferScreen(
    mediaResources: List<MediaResource>,
    imageResources: List<ImageResource>,
    onDownloadMedia: (MediaResource) -> Unit,
    onToggleImageSelection: (String) -> Unit,
    onSelectAllImages: (Boolean) -> Unit,
    onDownloadSelectedImages: (List<ImageResource>) -> Unit,
    onDownloadDirect: (url: String, name: String, protocol: ProtocolType, category: CategoryType) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var snifferUrl by remember { mutableStateOf("") }
    var previewTarget by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }

    val selectedImagesCount = imageResources.count { it.isSelected }

    Column(modifier = modifier.fillMaxSize()) {
        // Sniffer URL Probe Input
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = snifferUrl,
                onValueChange = { snifferUrl = it },
                label = { Text("Probe Page for Streams & Media") },
                placeholder = { Text("https://anime.tv or https://photos.org") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sniffer_url_input"),
                trailingIcon = {
                    FilledTonalButton(
                        onClick = { /* simulated sniff */ },
                        modifier = Modifier
                            .height(36.dp)
                            .padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sniff", fontSize = 12.sp)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Web Browser", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("sniffer_tab_browser")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Streams (${mediaResources.size})", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("sniffer_tab_media")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Images (${imageResources.size})", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("sniffer_tab_images")
                )
            }
        }

        if (selectedTab == 0) {
            BuiltInBrowserView(
                onDownloadUrl = onDownloadDirect,
                onPreviewMedia = { title, url, isVideo ->
                    previewTarget = Triple(title, url, isVideo)
                }
            )
        } else if (selectedTab == 1) {
            // Media Resources Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(mediaResources, key = { it.id }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("media_card_${item.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val (typeColor, typeLabel) = when (item.type) {
                                        "m3u8" -> Pair(CyberGreen, "HLS Stream")
                                        "mp4" -> Pair(MaterialTheme.colorScheme.primary, "MP4 Video")
                                        "mp3" -> Pair(CyberPurple, "Lossless Audio")
                                        else -> Pair(Color.Gray, item.type.uppercase())
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(typeColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = typeLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = typeColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.resolution,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = item.duration,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated size: ${Formatters.formatBytes(item.sizeBytes)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilledTonalButton(
                                        onClick = {
                                            previewTarget = Triple(item.title, item.url, true)
                                        },
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Preview", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onDownloadMedia(item) },
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Download", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Image Sniffer Tab
            Column(modifier = Modifier.fillMaxSize()) {
                // Batch Image Action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onSelectAllImages(selectedImagesCount < imageResources.size) },
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            if (selectedImagesCount == imageResources.size) "Deselect All" else "Select All",
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = {
                            val selected = imageResources.filter { it.isSelected }
                            if (selected.isNotEmpty()) onDownloadSelectedImages(selected)
                        },
                        enabled = selectedImagesCount > 0,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download ($selectedImagesCount)", fontSize = 11.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(imageResources, key = { it.id }) { img ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (img.isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleImageSelection(img.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (img.isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (img.isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(img.alt, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${img.width}x${img.height} • ${img.format} • ${Formatters.formatBytes(img.sizeBytes)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(img.format, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (previewTarget != null) {
            MediaPlayerModal(
                title = previewTarget!!.first,
                streamUrl = previewTarget!!.second,
                isVideo = previewTarget!!.third,
                onDismiss = { previewTarget = null }
            )
        }
    }
}
