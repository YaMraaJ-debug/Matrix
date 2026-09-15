package com.example.ghostdownloader.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch

@Composable
fun CloudDebridDialog(
    onDismiss: () -> Unit,
    onAddUnrestrictedTask: (unrestrictedUrl: String, title: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Debrid Unrestrictor, 1: Cloud Auto-Backup
    val scope = rememberCoroutineScope()

    // Debrid State
    var debridProvider by remember { mutableStateOf("Real-Debrid") }
    var apiKey by remember { mutableStateOf("") }
    var hosterLinkInput by remember { mutableStateOf("") }
    var isUnrestricting by remember { mutableStateOf(false) }
    var unrestrictedResultUrl by remember { mutableStateOf<String?>(null) }
    var unrestrictFileName by remember { mutableStateOf<String?>(null) }

    // Cloud Backup State
    var cloudBackupEnabled by remember { mutableStateOf(true) }
    var selectedCloudTarget by remember { mutableStateOf("Google Drive") }
    var deleteLocalAfterSync by remember { mutableStateOf(false) }
    var targetFolder by remember { mutableStateOf("/GhostDownloader/Backup") }

    fun processUnrestrict() {
        if (hosterLinkInput.isBlank()) return
        isUnrestricting = true
        unrestrictedResultUrl = null

        scope.launch {
            delay(1300) // Simulated Debrid API call & high-speed link unlock
            val cleanName = hosterLinkInput.substringAfterLast("/").takeIf { it.isNotBlank() } ?: "unrestricted_highspeed_asset.zip"
            unrestrictFileName = cleanName
            unrestrictedResultUrl = "https://download.real-debrid.com/storage/direct/${cleanName}?token=gd_fast_pass"
            isUnrestricting = false
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
                .border(1.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                                .background(CyberBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = CyberBlueLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Cloud & Debrid Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "High-speed Unrestrict & Auto-Cloud Backup",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_cloud_debrid_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Tabs: Debrid vs Cloud Backup
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = CyberBlueLight,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Debrid Unrestrictor", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Cloud Auto-Backup", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                if (selectedTab == 0) {
                    // Debrid Unrestrictor View
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Real-Debrid", "AllDebrid", "Premiumize", "TorBox").forEach { provider ->
                                FilterChip(
                                    selected = debridProvider == provider,
                                    onClick = { debridProvider = provider },
                                    label = { Text(provider, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberBlue.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberBlueLight
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            placeholder = { Text("Enter $debridProvider API Key (Optional)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = CyberBlueLight, modifier = Modifier.size(16.dp)) }
                        )

                        OutlinedTextField(
                            value = hosterLinkInput,
                            onValueChange = { hosterLinkInput = it },
                            placeholder = { Text("Paste Rapidgator, 1Fichier, Mega or Torrent link...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp)) }
                        )

                        Button(
                            onClick = { processUnrestrict() },
                            enabled = hosterLinkInput.isNotBlank() && !isUnrestricting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("unrestrict_link_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isUnrestricting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unrestricting Hosters...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unrestrict & Unlock High-Speed Link", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        AnimatedVisibility(visible = unrestrictedResultUrl != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CyberGreen.copy(alpha = 0.12f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                                        Text("Unrestricted Direct Stream Ready!", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Text(unrestrictFileName ?: "", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Button(
                                        onClick = {
                                            unrestrictedResultUrl?.let { url ->
                                                onAddUnrestrictedTask(url, unrestrictFileName ?: "unrestricted_file.zip")
                                            }
                                            onDismiss()
                                        },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Push to Download Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Cloud Auto-Backup View
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Auto Cloud Backup on Complete", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Immediately mirror finished downloads to your cloud storage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = cloudBackupEnabled, onCheckedChange = { cloudBackupEnabled = it })
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Text("Select Cloud Destination:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Google Drive", "WebDAV", "Telegram", "Dropbox").forEach { target ->
                                FilterChip(
                                    selected = selectedCloudTarget == target,
                                    onClick = { selectedCloudTarget = target },
                                    label = { Text(target, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberPurple.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberPurple
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = targetFolder,
                            onValueChange = { targetFolder = it },
                            label = { Text("Cloud Folder Path", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Storage Optimization (Auto-Free Space)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Delete local copy after successful cloud transfer to save phone space", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = deleteLocalAfterSync, onCheckedChange = { deleteLocalAfterSync = it })
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Cloud Sync Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
