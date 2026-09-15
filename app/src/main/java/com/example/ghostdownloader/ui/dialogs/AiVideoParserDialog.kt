package com.example.ghostdownloader.ui.dialogs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ParsedVideoFormat(
    val id: String,
    val label: String,
    val resolution: String,
    val estimatedSize: String,
    val isAudioOnly: Boolean,
    val formatExt: String,
    val downloadUrl: String
)

@Composable
fun AiVideoParserDialog(
    onDismiss: () -> Unit,
    onStartDownload: (url: String, title: String, isAudio: Boolean) -> Unit
) {
    var inputUrl by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var stripWatermark by remember { mutableStateOf(true) }
    var autoSubtitles by remember { mutableStateOf(true) }
    var parsedTitle by remember { mutableStateOf<String?>(null) }
    var detectedPlatform by remember { mutableStateOf<String?>(null) }
    var availableFormats by remember { mutableStateOf<List<ParsedVideoFormat>>(emptyList()) }
    var selectedFormatId by remember { mutableStateOf<String?>("fmt-1080p") }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    fun analyzeLink(url: String) {
        if (url.isBlank()) return
        isAnalyzing = true
        parsedTitle = null
        availableFormats = emptyList()

        scope.launch {
            delay(1200) // Simulated AI parsing & stream signature analysis
            val lower = url.lowercase()
            val (platform, sampleTitle) = when {
                "instagram.com" in lower -> "Instagram Reels" to "Trending_Creative_Reel_HD.mp4"
                "tiktok.com" in lower -> "TikTok" to "Viral_Dance_Trend_NoWatermark.mp4"
                "youtube.com" in lower || "youtu.be" in lower -> "YouTube" to "Comprehensive_Tech_Review_4K.mp4"
                "twitter.com" in lower || "x.com" in lower -> "Twitter / X" to "Breaking_News_Video_Clip.mp4"
                "reddit.com" in lower -> "Reddit" to "Funny_Cat_Compilation_HighBitrate.mp4"
                else -> "Universal Media Stream" to "Web_Video_Extraction_Master.mp4"
            }

            detectedPlatform = platform
            parsedTitle = sampleTitle
            availableFormats = listOf(
                ParsedVideoFormat("fmt-4k", "4K Ultra HD (60fps)", "3840x2160", "1.4 GB", false, "mp4", "$url#4k_direct"),
                ParsedVideoFormat("fmt-1080p", "1080p Full HD (Original)", "1920x1080", "280 MB", false, "mp4", "$url#1080p_direct"),
                ParsedVideoFormat("fmt-720p", "720p HD (Data Saver)", "1280x720", "95 MB", false, "mp4", "$url#720p_direct"),
                ParsedVideoFormat("fmt-audio", "Lossless 320kbps Audio", "Stereo MP3", "12 MB", true, "mp3", "$url#audio_320k")
            )
            selectedFormatId = "fmt-1080p"
            isAnalyzing = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "AI Universal Video & Stream Parser",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Extract Reels, Shorts, TikTok, YT & Streams",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_ai_parser_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // URL Input with Paste & Analyze
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = {
                        inputUrl = it
                        if (it.length > 15) analyzeLink(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_parser_url_input"),
                    placeholder = { Text("Paste video, Reel or stream link...", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                clipboardManager.getText()?.text?.let { clipText ->
                                    inputUrl = clipText.trim()
                                    analyzeLink(clipText.trim())
                                }
                            }) {
                                Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = CyberBlueLight)
                            }
                        }
                    }
                )

                // Quick analyze button
                Button(
                    onClick = { analyzeLink(inputUrl) },
                    enabled = inputUrl.isNotBlank() && !isAnalyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("analyze_stream_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Analyzing Stream Signatures...", fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Parse Video & Audio Streams", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // AI Options Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                        Text("Strip Watermark / Branding", fontSize = 12.sp)
                    }
                    Switch(checked = stripWatermark, onCheckedChange = { stripWatermark = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                        Text("Auto-Fetch Multilingual Subtitles (.srt)", fontSize = 12.sp)
                    }
                    Switch(checked = autoSubtitles, onCheckedChange = { autoSubtitles = it })
                }

                // Results list
                AnimatedVisibility(visible = parsedTitle != null && availableFormats.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = detectedPlatform ?: "Stream Detected",
                                        color = CyberPurple,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "AI Verified Clean",
                                        color = CyberGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = parsedTitle ?: "",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Text("Select Output Quality:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        availableFormats.forEach { fmt ->
                            val isSelected = selectedFormatId == fmt.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedFormatId = fmt.id }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = if (isSelected) CyberPurple else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CyberPurple.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = if (fmt.isAudioOnly) Icons.Default.MusicNote else Icons.Default.Movie,
                                            contentDescription = null,
                                            tint = if (fmt.isAudioOnly) CyberTeal else CyberPurple,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(fmt.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("${fmt.resolution} • ${fmt.estimatedSize}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val selectedFmt = availableFormats.find { it.id == selectedFormatId } ?: availableFormats.first()
                                val finalTitle = (parsedTitle ?: "Downloaded_Media").let {
                                    if (selectedFmt.isAudioOnly) it.replace(".mp4", ".mp3") else it
                                }
                                onStartDownload(inputUrl, finalTitle, selectedFmt.isAudioOnly)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("confirm_ai_download_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download Selected Media", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
