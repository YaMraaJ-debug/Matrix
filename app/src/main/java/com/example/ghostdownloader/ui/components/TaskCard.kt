package com.example.ghostdownloader.ui.components

import android.content.Intent
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.TaskPriority
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.ui.theme.CyberAmber
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.utils.Formatters

@Composable
fun TaskCard(
    task: DownloadTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onOpenDetails: () -> Unit,
    onPlayMedia: (() -> Unit)? = null,
    onToggleVault: (() -> Unit)? = null,
    onScanSecurity: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
            .clickable(onClick = onOpenDetails),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Protocol Badge, Category Icon, Priority & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Protocol badge
                    val protocolColor = when (task.protocol) {
                        ProtocolType.TORRENT -> CyberPurple
                        ProtocolType.M3U8 -> CyberGreen
                        ProtocolType.GITHUB -> CyberAmber
                        ProtocolType.BILIBILI -> Color(0xFFFB7299)
                        ProtocolType.YOUTUBE -> CyberRed
                        ProtocolType.HUGGINGFACE -> Color(0xFFFFD21E)
                        else -> CyberBlueLight
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(protocolColor.copy(alpha = 0.18f))
                            .border(1.dp, protocolColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.protocol.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = protocolColor
                        )
                    }

                    // Category Icon
                    val categoryIcon = when (task.category) {
                        CategoryType.VIDEO -> Icons.Default.Movie
                        CategoryType.MUSIC -> Icons.Default.MusicNote
                        CategoryType.SOFTWARE -> Icons.Default.Terminal
                        CategoryType.ARCHIVE -> Icons.Default.FolderZip
                        CategoryType.DOCUMENT -> Icons.Default.Description
                        else -> Icons.Default.Widgets
                    }
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = task.category.label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    if (task.status == TaskStatus.DOWNLOADING) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(CyberGreen.copy(alpha = 0.18f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(10.dp))
                                Text("DUAL-BOND", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
                            }
                        }
                    }

                    // Priority indicator if not normal
                    if (task.priority == TaskPriority.HIGH) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(CyberRed.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("HIGH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberRed)
                        }
                    }

                    if (task.speedLimitKbps > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(CyberAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("${task.speedLimitKbps} KB/s", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberAmber)
                        }
                    }
                }

                // Action buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if ((task.category == CategoryType.VIDEO || task.category == CategoryType.MUSIC || task.name.endsWith(".mp4") || task.name.endsWith(".mp3") || task.name.endsWith(".m3u8")) && onPlayMedia != null) {
                        IconButton(
                            onClick = onPlayMedia,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("task_play_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = "Play media preview",
                                tint = CyberGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("task_toggle_${task.id}")
                    ) {
                        val actionIcon = when (task.status) {
                            TaskStatus.DOWNLOADING -> Icons.Default.Pause
                            TaskStatus.COMPLETED -> Icons.Default.Refresh
                            else -> Icons.Default.PlayArrow
                        }
                        val tint = when (task.status) {
                            TaskStatus.DOWNLOADING -> CyberAmber
                            TaskStatus.COMPLETED -> CyberGreen
                            else -> CyberBlueLight
                        }
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = "Toggle task",
                            tint = tint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenDetails,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("task_details_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Task details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (task.status == TaskStatus.COMPLETED) {
                        if (onScanSecurity != null) {
                            IconButton(
                                onClick = onScanSecurity,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("task_security_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Security audit & malware scan",
                                    tint = CyberGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, task.name)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Downloaded via Ghost Downloader 3:\nName: ${task.name}\nSize: ${Formatters.formatBytes(task.totalBytes)}\nLocation: ${task.savePath}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Downloaded File"))
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("task_share_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share file",
                                tint = CyberBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (onToggleVault != null) {
                        IconButton(
                            onClick = onToggleVault,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("task_vault_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = if (task.isVaulted) "Unvault task" else "Move to vault",
                                tint = CyberPurple,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("task_delete_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // File Name
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (task.smartTags.isNotEmpty() || task.hasSubtitles || task.isMeshShared || task.activeMirror != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.smartTags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberBlueLight
                            )
                        }
                    }
                    if (task.hasSubtitles) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberTeal.copy(alpha = 0.16f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("SUB (HI/EN)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberTeal)
                        }
                    }
                    if (task.isMeshShared) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberPurple.copy(alpha = 0.16f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("P2P MESH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberPurple)
                        }
                    }
                    if (task.activeMirror != null) {
                        Text(
                            text = "⚡ ${task.activeMirror}",
                            fontSize = 9.sp,
                            color = CyberGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar / Chunk Visualizer
            if (task.chunks.isNotEmpty() && task.status == TaskStatus.DOWNLOADING) {
                MiniChunkBar(chunks = task.chunks, modifier = Modifier.height(6.dp))
            } else {
                LinearProgressIndicator(
                    progress = { task.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when (task.status) {
                        TaskStatus.COMPLETED -> CyberGreen
                        TaskStatus.PAUSED -> CyberAmber
                        TaskStatus.ERROR -> CyberRed
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Size & Percentage
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${Formatters.formatBytes(task.downloadedBytes)} / ${Formatters.formatBytes(task.totalBytes)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${task.progressPercent}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.status == TaskStatus.COMPLETED) CyberGreen else MaterialTheme.colorScheme.primary
                    )
                }

                // Speed / Status
                when (task.status) {
                    TaskStatus.DOWNLOADING -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Formatters.formatSpeed(task.speed),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (task.etaSeconds > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• ${Formatters.formatEta(task.etaSeconds)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    TaskStatus.COMPLETED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = CyberGreen
                            )
                        }
                    }
                    TaskStatus.PAUSED -> {
                        Text(
                            text = "Paused",
                            fontSize = 12.sp,
                            color = CyberAmber
                        )
                    }
                    TaskStatus.WAITING -> {
                        Text(
                            text = "Queued",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TaskStatus.ERROR -> {
                        Text(
                            text = task.errorMessage ?: "Download error",
                            fontSize = 12.sp,
                            color = CyberRed
                        )
                    }
                }
            }
        }
    }
}
