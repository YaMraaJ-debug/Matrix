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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Unarchive
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.ui.theme.CyberAmber
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters

data class ArchiveEntry(
    val path: String,
    val sizeBytes: Long,
    val isDirectory: Boolean = false
)

@Composable
fun ArchiveExtractorDialog(
    task: DownloadTask,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isExtracting by remember { mutableStateOf(false) }
    var extractionProgress by remember { mutableStateOf(0f) }
    var isExtracted by remember { mutableStateOf(false) }
    var destinationPath by remember { mutableStateOf("/storage/emulated/0/Download/Ghost/Extracted/${task.name.substringBeforeLast(".")}") }
    var passwordInput by remember { mutableStateOf("") }
    var autoDictionaryEnabled by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Mock archive entries list
    val entries = remember(task) {
        listOf(
            ArchiveEntry("README.txt", 4_280L),
            ArchiveEntry("assets/", 0L, isDirectory = true),
            ArchiveEntry("assets/config.json", 18_400L),
            ArchiveEntry("assets/media_pack.bin", task.totalBytes.coerceAtLeast(10_000_000L) / 2),
            ArchiveEntry("license.md", 2_150L),
            ArchiveEntry("run_installer.sh", 8_900L)
        )
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
                        .background(CyberAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Archive Inspector & Extractor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Supports ZIP, TAR.GZ, RAR, 7Z formats",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Archive Header
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = task.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Archive Size: ${Formatters.formatBytes(task.totalBytes)}",
                                fontSize = 11.sp,
                                color = CyberBlueLight
                            )
                            Text(
                                text = "${entries.size} items inside",
                                fontSize = 11.sp,
                                color = CyberTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Extracted target path box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text("Extract To Destination:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(destinationPath, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                // Password & Auto-Dictionary Cracker Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Password Protected / Cracker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Auto-Dictionary", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = autoDictionaryEnabled,
                                    onCheckedChange = { autoDictionaryEnabled = it },
                                    modifier = Modifier.size(32.dp),
                                    colors = SwitchDefaults.colors(checkedThumbColor = CyberAmber, checkedTrackColor = CyberAmber.copy(alpha = 0.4f))
                                )
                            }
                        }

                        if (!autoDictionaryEnabled) {
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                placeholder = { Text("Enter archive password...", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            )
                        } else {
                            Text(
                                text = "⚡ Auto-testing common passwords (123456, password, ghost, admin, zip2024)...",
                                fontSize = 10.sp,
                                color = CyberTeal,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        statusMessage?.let { msg ->
                            Text(
                                text = msg,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.contains("Found")) CyberGreen else CyberAmber
                            )
                        }
                    }
                }

                // Progress during extraction
                if (isExtracting) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Unpacking archive chunks...", fontSize = 11.sp, color = CyberAmber)
                            Text("${(extractionProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberAmber)
                        }
                        LinearProgressIndicator(
                            progress = { extractionProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = CyberAmber
                        )
                    }
                } else if (isExtracted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberGreen.copy(alpha = 0.15f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                            Text("Successfully extracted to folder!", fontSize = 12.sp, color = CyberGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Internal Files Preview List
                Text("Archive Contents:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(entries) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (item.isDirectory) Icons.Default.Storage else Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        tint = if (item.isDirectory) CyberBlueLight else CyberTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = item.path,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (!item.isDirectory) {
                                    Text(
                                        text = Formatters.formatBytes(item.sizeBytes),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                    if (!isExtracted && !isExtracting) {
                        isExtracting = true
                        if (autoDictionaryEnabled) {
                            statusMessage = "Testing dictionary hash matches..."
                        }
                        // Simple animated step
                        extractionProgress = 0.2f
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            extractionProgress = 0.65f
                            if (autoDictionaryEnabled) {
                                statusMessage = "Password Found: 'ghost2024' (Decrypted CRC32)"
                            }
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                extractionProgress = 1.0f
                                isExtracting = false
                                isExtracted = true
                                Toast.makeText(context, "Archive unpacked successfully!", Toast.LENGTH_SHORT).show()
                            }, 500)
                        }, 500)
                    } else {
                        onDismiss()
                    }
                },
                enabled = !isExtracting,
                colors = ButtonDefaults.buttonColors(containerColor = if (isExtracted) CyberGreen else CyberAmber),
                modifier = Modifier.testTag("extract_archive_btn")
            ) {
                Icon(Icons.Default.Unarchive, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isExtracted) "Done" else "Extract All",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
