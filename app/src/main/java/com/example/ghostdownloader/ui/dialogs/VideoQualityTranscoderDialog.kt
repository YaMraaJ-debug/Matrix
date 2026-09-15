package com.example.ghostdownloader.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.MediaResource
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal

data class VideoQualityOption(
    val label: String,
    val resolution: String,
    val bitrate: String,
    val sizeEstimate: String,
    val isAudioOnly: Boolean = false,
    val format: String = "MP4"
)

@Composable
fun VideoQualityTranscoderDialog(
    media: MediaResource,
    onDismiss: () -> Unit,
    onConfirmTranscodeDownload: (quality: String, extractAudio: Boolean, mergeSubtitles: Boolean) -> Unit
) {
    val qualityList = remember {
        listOf(
            VideoQualityOption("4K Ultra HD", "3840x2160", "22 Mbps", "~ 1.45 GB", format = "MKV/HEVC"),
            VideoQualityOption("1080p Full HD", "1920x1080", "6.5 Mbps", "~ 480 MB", format = "MP4"),
            VideoQualityOption("Lossless Ultra-Compress (AV1)", "1080p 60fps", "2.1 Mbps", "~ 160 MB (-65%)", format = "AV1/MKV"),
            VideoQualityOption("720p HD Ready", "1280x720", "2.8 Mbps", "~ 210 MB", format = "MP4"),
            VideoQualityOption("480p Data Saver", "854x480", "1.1 Mbps", "~ 92 MB", format = "MP4"),
            VideoQualityOption("Extract Audio (Lossless MP3)", "Audio Only", "320 kbps", "~ 12 MB", isAudioOnly = true, format = "MP3"),
            VideoQualityOption("AI 4-Stem Splitter (Vocals + Drums + Bass)", "Stem Studio", "Lossless", "~ 48 MB", isAudioOnly = true, format = "AI-STEMS")
        )
    }

    var selectedIndex by remember { mutableStateOf(1) } // Default 1080p
    var includeSubtitles by remember { mutableStateOf(true) }
    var studioNormalizer by remember { mutableStateOf(true) }
    var vocalDenoise by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = CyberBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Quality & Transcoder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Auto-Transcode & Audio Extract", fontSize = 11.sp, color = CyberTeal)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = media.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Select output format & quality streams:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    qualityList.forEachIndexed { index, option ->
                        val isSelected = selectedIndex == index
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyberBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) CyberBlue else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedIndex = index }
                                .testTag("quality_option_$index")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (option.isAudioOnly) Icons.Default.Audiotrack else Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = if (isSelected) CyberBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(option.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${option.resolution} • ${option.bitrate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(option.sizeEstimate, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) CyberGreen else MaterialTheme.colorScheme.onSurface)
                                    Text(option.format, fontSize = 9.sp, color = CyberTeal, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                // Subtitle auto-mux option
                if (!qualityList[selectedIndex].isAudioOnly) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeSubtitles = !includeSubtitles },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeSubtitles,
                            onCheckedChange = { includeSubtitles = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberTeal)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Auto-merge Subtitles (.SRT / .VTT into stream)", fontSize = 11.sp)
                    }
                }

                // Audio Studio Normalizer (-14 LUFS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { studioNormalizer = !studioNormalizer },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = studioNormalizer,
                        onCheckedChange = { studioNormalizer = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Studio Loudness Normalizer (-14 LUFS mastering)", fontSize = 11.sp)
                }

                // AI Speech Vocal Clarity Denoise
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vocalDenoise = !vocalDenoise },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = vocalDenoise,
                        onCheckedChange = { vocalDenoise = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Vocal Clarity & Dialogue Isolation", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            val chosen = qualityList[selectedIndex]
            Button(
                onClick = {
                    onConfirmTranscodeDownload(chosen.label, chosen.isAudioOnly, includeSubtitles)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                modifier = Modifier.testTag("confirm_transcode_download_btn")
            ) {
                Icon(Icons.Default.Transform, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (chosen.isAudioOnly) "Extract & Download MP3" else "Download in ${chosen.label}", fontSize = 12.sp)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
