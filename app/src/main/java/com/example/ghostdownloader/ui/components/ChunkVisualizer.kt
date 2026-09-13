package com.example.ghostdownloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.DownloadChunk
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.utils.Formatters

@Composable
fun MiniChunkBar(
    chunks: List<DownloadChunk>,
    modifier: Modifier = Modifier
) {
    if (chunks.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        chunks.forEach { chunk ->
            val color = when (chunk.status) {
                "completed" -> CyberGreen
                "active" -> CyberBlueLight
                else -> Color.Gray.copy(alpha = 0.3f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(color)
            )
        }
    }
}

@Composable
fun DetailedChunkVisualizer(
    chunks: List<DownloadChunk>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Segmented Threads (${chunks.size} Connections)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val completedCount = chunks.count { it.status == "completed" }
            Text(
                text = "$completedCount/${chunks.size} Done",
                style = MaterialTheme.typography.labelSmall,
                color = CyberGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Segment block bar
        MiniChunkBar(chunks = chunks, modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(10.dp))

        // Chunks detailed grid
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            chunks.chunked(2).forEach { rowChunks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowChunks.forEach { chunk ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val indicatorColor = when (chunk.status) {
                                        "completed" -> CyberGreen
                                        "active" -> CyberBlueLight
                                        else -> Color.Gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(indicatorColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "T#${chunk.id + 1}",
                                        fontSize = 11.sp,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (chunk.status == "completed") "100%" else Formatters.formatSpeed(chunk.speed),
                                    fontSize = 11.sp,
                                    color = if (chunk.status == "completed") CyberGreen else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (rowChunks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
