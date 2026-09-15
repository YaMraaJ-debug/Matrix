package com.example.ghostdownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.ui.components.SpeedTelemetryCard
import com.example.ghostdownloader.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    tasks: List<DownloadTask>,
    totalDownloadSpeed: Long,
    totalUploadSpeed: Long,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onSelectTask: (DownloadTask) -> Unit,
    onStartAll: () -> Unit,
    onPauseAll: () -> Unit,
    onOpenNewTaskDialog: () -> Unit,
    onOpenBatchDialog: () -> Unit,
    onOpenPlanDialog: () -> Unit,
    onOpenRssDialog: () -> Unit,
    onOpenVaultDialog: () -> Unit = {},
    onOpenSpeedDialog: () -> Unit = {},
    onPlayMedia: (DownloadTask) -> Unit = {},
    onToggleVault: (String, Boolean) -> Unit = { _, _ -> },
    onOpenSiteExtractor: () -> Unit = {},
    onOpenStorageCleaner: () -> Unit = {},
    onOpenAiParser: () -> Unit = {},
    onOpenBonding: () -> Unit = {},
    onOpenCloudDebrid: () -> Unit = {},
    onScanSecurity: (DownloadTask) -> Unit = {},
    speedThrottlePreset: String = "Turbo / Uncapped",
    globalLimitKbps: Int = 0,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoryType.ALL) }
    var selectedStatus by remember { mutableStateOf<TaskStatus?>(null) }

    // Filter tasks
    val filteredTasks = remember(tasks, searchQuery, selectedCategory, selectedStatus) {
        tasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() || task.name.contains(searchQuery, ignoreCase = true) || task.url.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == CategoryType.ALL || task.category == selectedCategory
            val matchesStatus = selectedStatus == null || task.status == selectedStatus
            matchesSearch && matchesCategory && matchesStatus
        }
    }

    val activeCount = remember(tasks) {
        tasks.count { it.status == TaskStatus.DOWNLOADING }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Speed Telemetry Banner
            item {
                SpeedTelemetryCard(
                    totalDownloadSpeed = totalDownloadSpeed,
                    totalUploadSpeed = totalUploadSpeed,
                    activeTasksCount = activeCount,
                    onStartAll = onStartAll,
                    onPauseAll = onPauseAll,
                    speedThrottlePreset = speedThrottlePreset,
                    globalLimitKbps = globalLimitKbps,
                    onOpenSpeedLimiter = onOpenSpeedDialog
                )
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = onOpenNewTaskDialog,
                        modifier = Modifier.testTag("add_task_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add URL")
                    }

                    FilledTonalButton(
                        onClick = onOpenAiParser,
                        modifier = Modifier.testTag("ai_video_parser_quick_btn")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Video Parser")
                    }

                    FilledTonalButton(
                        onClick = onOpenBonding,
                        modifier = Modifier.testTag("dual_bonding_quick_btn")
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dual Bonding (Wi-Fi + 5G)")
                    }

                    FilledTonalButton(
                        onClick = onOpenCloudDebrid,
                        modifier = Modifier.testTag("cloud_debrid_quick_btn")
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cloud & Debrid")
                    }

                    FilledTonalButton(
                        onClick = onOpenSiteExtractor,
                        modifier = Modifier.testTag("site_extractor_button")
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Deep Extractor (Skip Ads)")
                    }

                    FilledTonalButton(
                        onClick = onOpenBatchDialog,
                        modifier = Modifier.testTag("batch_urls_button")
                    ) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Batch Add")
                    }

                    FilledTonalButton(
                        onClick = onOpenStorageCleaner,
                        modifier = Modifier.testTag("storage_cleaner_quick_btn")
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clean Junk")
                    }

                    FilledTonalButton(
                        onClick = onOpenPlanDialog,
                        modifier = Modifier.testTag("plan_task_button")
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Plan / Night")
                    }

                    FilledTonalButton(
                        onClick = onOpenSpeedDialog,
                        modifier = Modifier.testTag("speed_limiter_button")
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (globalLimitKbps > 0) "Cap: ${globalLimitKbps}K" else "Speed Cap")
                    }

                    FilledTonalButton(
                        onClick = onOpenVaultDialog,
                        modifier = Modifier.testTag("ghost_vault_button")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ghost Vault")
                    }

                    FilledTonalButton(
                        onClick = onOpenRssDialog,
                        modifier = Modifier.testTag("rss_feeds_button")
                    ) {
                        Icon(Icons.Default.RssFeed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RSS")
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search files or URLs...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_tasks_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Category Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = CategoryType.values().indexOf(selectedCategory),
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    CategoryType.values().forEach { cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat.label, fontSize = 13.sp) },
                            modifier = Modifier.testTag("category_tab_${cat.name.lowercase()}")
                        )
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All (${tasks.size})") },
                        modifier = Modifier.testTag("status_chip_all")
                    )
                    listOf(
                        TaskStatus.DOWNLOADING,
                        TaskStatus.PAUSED,
                        TaskStatus.COMPLETED,
                        TaskStatus.WAITING,
                        TaskStatus.ERROR
                    ).forEach { status ->
                        val count = tasks.count { it.status == status }
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = if (selectedStatus == status) null else status },
                            label = { Text("${status.displayName} ($count)") },
                            modifier = Modifier.testTag("status_chip_${status.name.lowercase()}")
                        )
                    }
                }
            }

            // Tasks List
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No tasks found",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap the + button to add a download link",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task.id) },
                        onDelete = { onDeleteTask(task.id) },
                        onOpenDetails = { onSelectTask(task) },
                        onPlayMedia = { onPlayMedia(task) },
                        onToggleVault = { onToggleVault(task.id, !task.isVaulted) },
                        onScanSecurity = { onScanSecurity(task) }
                    )
                }
            }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = onOpenNewTaskDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_task"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add task")
        }
    }
}
