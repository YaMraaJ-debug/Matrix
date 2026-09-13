package com.example.ghostdownloader.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberTeal
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MediaPlayerModal(
    title: String,
    streamUrl: String,
    isVideo: Boolean,
    onDismiss: () -> Unit,
    totalDurationSeconds: Int = 240
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var volume by remember { mutableFloatStateOf(0.85f) }
    var isMuted by remember { mutableStateOf(false) }
    var isPiPMode by remember { mutableStateOf(false) }

    // Playback progress ticker
    LaunchedEffect(isPlaying, playbackSpeed) {
        while (isPlaying) {
            delay((1000 / playbackSpeed).toLong())
            if (currentSeconds < totalDurationSeconds) {
                currentSeconds += 1f
            } else {
                isPlaying = false
                currentSeconds = 0f
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isPiPMode) 0.85f else 0.96f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVideo) Icons.Default.Movie else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = CyberBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isVideo) "Ghost In-App Video Engine (H.264/AAC)" else "Ghost Audio Master (24-bit Flac/AAC)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isPiPMode = !isPiPMode },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureInPicture,
                                contentDescription = "PiP mode",
                                tint = if (isPiPMode) CyberGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close player",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Viewport: Video surface or Audio Waveform
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (isPiPMode) 2.2f else 1.77f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090D16))
                        .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isVideo) {
                        // Video frame representation with cyber HUD overlay
                        VideoCanvasHud(
                            isPlaying = isPlaying,
                            progress = currentSeconds / totalDurationSeconds.toFloat()
                        )
                    } else {
                        // Audio animated frequency visualizer
                        AudioVisualizerBars(isPlaying = isPlaying)
                    }

                    // Floating Live / Codec HUD tag
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isVideo) "1080p • 60 FPS" else "320 KBPS • STEREO",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberGreen.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BUFFER 100%",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Seekbar & Time Row
                Column {
                    Slider(
                        value = currentSeconds,
                        onValueChange = { currentSeconds = it },
                        valueRange = 0f..totalDurationSeconds.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberBlueLight,
                            activeTrackColor = CyberBlue,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentSeconds.toInt()),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyberBlueLight
                        )
                        Text(
                            text = formatTime(totalDurationSeconds),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Primary Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentSeconds = (currentSeconds - 10f).coerceAtLeast(0f) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Replay10, contentDescription = "Back 10s", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberBlue, CyberTeal)
                                )
                            )
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = { currentSeconds = (currentSeconds + 10f).coerceAtMost(totalDurationSeconds.toFloat()) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Speed Selector & Volume Control
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = CyberBlueLight, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Speed Multiplier", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("${playbackSpeed}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberBlueLight)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                FilterChip(
                                    selected = playbackSpeed == speed,
                                    onClick = { playbackSpeed = speed },
                                    label = { Text("${speed}x", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberBlue.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberBlueLight
                                    )
                                )
                            }
                        }

                        // Volume Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMuted || volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = "Mute",
                                    tint = CyberTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Slider(
                                value = if (isMuted) 0f else volume,
                                onValueChange = {
                                    volume = it
                                    if (isMuted) isMuted = false
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (isMuted) "0%" else "${(volume * 100).toInt()}%",
                                fontSize = 11.sp,
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

@Composable
private fun VideoCanvasHud(isPlaying: Boolean, progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Simulated cinematic grid and moving cyber pulse
        val gridColor = Color(0xFF16253B)
        val step = 40.dp.toPx()
        var x = 0f
        while (x < width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y < height) {
            drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
            y += step
        }

        // Center cinema frame
        val frameWidth = width * 0.82f
        val frameHeight = height * 0.72f
        val frameLeft = (width - frameWidth) / 2f
        val frameTop = (height - frameHeight) / 2f

        drawRoundRect(
            color = Color(0xFF0F1A2A),
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameWidth, frameHeight),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Waveform scan line
        if (isPlaying) {
            val scanX = frameLeft + (frameWidth * (progress % 1f))
            drawLine(
                color = CyberBlueLight.copy(alpha = 0.8f),
                start = Offset(scanX, frameTop),
                end = Offset(scanX, frameTop + frameHeight),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun AudioVisualizerBars(isPlaying: Boolean) {
    val barHeights = remember {
        List(24) { Animatable(Random.nextFloat() * 0.7f + 0.2f) }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(120)
            barHeights.forEach { anim ->
                anim.animateTo(
                    targetValue = Random.nextFloat() * 0.8f + 0.15f,
                    animationSpec = tween(durationMillis = 110, easing = LinearEasing)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barHeights.forEachIndexed { index, anim ->
            val color = if (index % 3 == 0) CyberGreen else if (index % 2 == 0) CyberTeal else CyberBlue
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((anim.value * 90).dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
