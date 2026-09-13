package com.example.ghostdownloader.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ghostdownloader.R
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.ui.dialogs.ArchiveExtractorDialog
import com.example.ghostdownloader.ui.dialogs.BatchUrlDialog
import com.example.ghostdownloader.ui.dialogs.GhostShareDialog
import com.example.ghostdownloader.ui.dialogs.GhostVaultDialog
import com.example.ghostdownloader.ui.dialogs.MediaPlayerModal
import com.example.ghostdownloader.ui.dialogs.NewTaskDialog
import com.example.ghostdownloader.ui.dialogs.PlanTaskDialog
import com.example.ghostdownloader.ui.dialogs.PlatformArchitectureDialog
import com.example.ghostdownloader.ui.dialogs.RssFeedDialog
import com.example.ghostdownloader.ui.dialogs.SiteDeepExtractorDialog
import com.example.ghostdownloader.ui.dialogs.SpeedLimiterDialog
import com.example.ghostdownloader.ui.dialogs.StealthProxyDialog
import com.example.ghostdownloader.ui.dialogs.StorageCleanerDialog
import com.example.ghostdownloader.ui.dialogs.TaskDetailsDialog
import com.example.ghostdownloader.ui.screens.DownloadsScreen
import com.example.ghostdownloader.ui.screens.FeaturePacksScreen
import com.example.ghostdownloader.ui.screens.MediaSnifferScreen
import com.example.ghostdownloader.ui.screens.SettingsScreen
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.PlatformArchitectureManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostDownloaderApp(viewModel: MainViewModel) {
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    val deviceProfile = remember { PlatformArchitectureManager.getDeviceProfile() }

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val nonVaultedTasks by viewModel.nonVaultedTasks.collectAsStateWithLifecycle()
    val vaultedTasks by viewModel.vaultedTasks.collectAsStateWithLifecycle()
    val packs by viewModel.featurePacks.collectAsStateWithLifecycle()
    val mediaResources by viewModel.mediaResources.collectAsStateWithLifecycle()
    val imageResources by viewModel.imageResources.collectAsStateWithLifecycle()
    val rssSubscriptions by viewModel.rssSubscriptions.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val totalDownloadSpeed by viewModel.totalDownloadSpeed.collectAsStateWithLifecycle()
    val totalUploadSpeed by viewModel.totalUploadSpeed.collectAsStateWithLifecycle()

    var showNewTaskDialog by remember { mutableStateOf(false) }
    var showBatchDialog by remember { mutableStateOf(false) }
    var showPlanDialog by remember { mutableStateOf(false) }
    var showRssDialog by remember { mutableStateOf(false) }
    var showPlatformDialog by remember { mutableStateOf(false) }
    var showVaultDialog by remember { mutableStateOf(false) }
    var showSpeedLimiterDialog by remember { mutableStateOf(false) }
    var showSiteExtractorDialog by remember { mutableStateOf(false) }
    var showStealthProxyDialog by remember { mutableStateOf(false) }
    var showStorageCleanerDialog by remember { mutableStateOf(false) }
    var activeGhostShareTask by remember { mutableStateOf<DownloadTask?>(null) }
    var activeArchiveTask by remember { mutableStateOf<DownloadTask?>(null) }
    var activeMediaPlayerTarget by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }
    var selectedTaskForDetails by remember { mutableStateOf<DownloadTask?>(null) }

    val activeCount = remember(tasks) {
        tasks.count { it.status == TaskStatus.DOWNLOADING }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_nobg),
                            contentDescription = "Ghost Downloader Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ghost Downloader",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberBlue.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "v3.0",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberBlueLight
                                    )
                                }
                            }
                            Text(
                                text = "High-Speed Multi-Protocol Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Architecture / Platform badge button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberBlue.copy(alpha = 0.15f))
                            .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showPlatformDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Platform & Architecture",
                                tint = CyberBlueLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (deviceProfile.is64Bit) "ARM64" else "ARMv7",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = CyberBlueLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(CyberGreen)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (activeCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberGreen.copy(alpha = 0.15f))
                                .border(1.dp, CyberGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$activeCount active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyberGreen
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedScreenIndex == 0,
                    onClick = { selectedScreenIndex = 0 },
                    icon = {
                        Icon(
                            if (selectedScreenIndex == 0) Icons.Filled.Download else Icons.Outlined.Download,
                            contentDescription = "Downloads"
                        )
                    },
                    label = { Text("Downloads") },
                    modifier = Modifier.testTag("nav_downloads")
                )
                NavigationBarItem(
                    selected = selectedScreenIndex == 1,
                    onClick = { selectedScreenIndex = 1 },
                    icon = {
                        Icon(
                            if (selectedScreenIndex == 1) Icons.Filled.Radar else Icons.Outlined.Radar,
                            contentDescription = "Sniffer"
                        )
                    },
                    label = { Text("Sniffer") },
                    modifier = Modifier.testTag("nav_sniffer")
                )
                NavigationBarItem(
                    selected = selectedScreenIndex == 2,
                    onClick = { selectedScreenIndex = 2 },
                    icon = {
                        Icon(
                            if (selectedScreenIndex == 2) Icons.Filled.Extension else Icons.Outlined.Extension,
                            contentDescription = "Packs"
                        )
                    },
                    label = { Text("Packs") },
                    modifier = Modifier.testTag("nav_packs")
                )
                NavigationBarItem(
                    selected = selectedScreenIndex == 3,
                    onClick = { selectedScreenIndex = 3 },
                    icon = {
                        Icon(
                            if (selectedScreenIndex == 3) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreenIndex) {
                0 -> DownloadsScreen(
                    tasks = nonVaultedTasks,
                    totalDownloadSpeed = totalDownloadSpeed,
                    totalUploadSpeed = totalUploadSpeed,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onSelectTask = { selectedTaskForDetails = it },
                    onStartAll = { viewModel.startAllTasks() },
                    onPauseAll = { viewModel.pauseAllTasks() },
                    onOpenNewTaskDialog = { showNewTaskDialog = true },
                    onOpenBatchDialog = { showBatchDialog = true },
                    onOpenPlanDialog = { showPlanDialog = true },
                    onOpenRssDialog = { showRssDialog = true },
                    onOpenVaultDialog = { showVaultDialog = true },
                    onOpenSpeedDialog = { showSpeedLimiterDialog = true },
                    onPlayMedia = { task ->
                        activeMediaPlayerTarget = Triple(
                            task.name,
                            task.url,
                            task.category != com.example.ghostdownloader.data.model.CategoryType.MUSIC
                        )
                    },
                    onToggleVault = { taskId, vaulted ->
                        viewModel.setTaskVaulted(taskId, vaulted)
                    },
                    onOpenSiteExtractor = { showSiteExtractorDialog = true },
                    onOpenStorageCleaner = { showStorageCleanerDialog = true },
                    speedThrottlePreset = settings.speedThrottlePreset,
                    globalLimitKbps = settings.globalDownloadLimitKbps
                )
                1 -> MediaSnifferScreen(
                    mediaResources = mediaResources,
                    imageResources = imageResources,
                    onDownloadMedia = { viewModel.downloadMediaResource(it) },
                    onToggleImageSelection = { viewModel.toggleImageSelection(it) },
                    onSelectAllImages = { viewModel.selectAllImages(it) },
                    onDownloadSelectedImages = { viewModel.downloadSelectedImages(it) },
                    onDownloadDirect = { url, name, proto, cat ->
                        viewModel.addDirectDownload(url, name, proto, cat)
                    }
                )
                2 -> FeaturePacksScreen(
                    packs = packs,
                    onTogglePack = { viewModel.toggleFeaturePack(it) }
                )
                3 -> SettingsScreen(
                    settings = settings,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onOpenPlatformMatrix = { showPlatformDialog = true },
                    onOpenStealthProxy = { showStealthProxyDialog = true },
                    onOpenStorageCleaner = { showStorageCleanerDialog = true }
                )
            }
        }

        // Dialogs
        if (showPlatformDialog) {
            PlatformArchitectureDialog(
                onDismiss = { showPlatformDialog = false }
            )
        }

        if (showNewTaskDialog) {
            NewTaskDialog(
                onDismiss = { showNewTaskDialog = false },
                onConfirm = { url, name, priority, connections ->
                    viewModel.addTask(url, name, priority, connections)
                    showNewTaskDialog = false
                }
            )
        }

        if (showBatchDialog) {
            BatchUrlDialog(
                onDismiss = { showBatchDialog = false },
                onConfirm = { urls ->
                    viewModel.batchAddTasks(urls)
                    showBatchDialog = false
                }
            )
        }

        if (showPlanDialog) {
            PlanTaskDialog(
                settings = settings,
                onDismiss = { showPlanDialog = false },
                onSaveSchedule = { enabled, startTime, endTime, wifiOnly, stopOnLowBattery, _ ->
                    val startH = startTime.substringBefore(':').trim().toIntOrNull() ?: 2
                    val endH = endTime.substringBefore(':').trim().toIntOrNull() ?: 7
                    viewModel.updateSchedulerAndNetworkRules(
                        nightMode = enabled,
                        startH = startH,
                        endH = endH,
                        wifiOnly = wifiOnly,
                        lowBatteryStop = stopOnLowBattery
                    )
                    showPlanDialog = false
                }
            )
        }

        if (showSpeedLimiterDialog) {
            SpeedLimiterDialog(
                settings = settings,
                onDismiss = { showSpeedLimiterDialog = false },
                onSaveLimit = { speedKbps, preset ->
                    viewModel.updateSpeedThrottle(speedKbps, preset)
                    showSpeedLimiterDialog = false
                }
            )
        }

        if (showVaultDialog) {
            GhostVaultDialog(
                vaultedTasks = vaultedTasks,
                currentPin = settings.ghostVaultPin,
                onDismiss = { showVaultDialog = false },
                onUnlockTask = { id -> viewModel.setTaskVaulted(id, false) },
                onDeleteTask = { id -> viewModel.deleteTask(id) },
                onPreviewTask = { task ->
                    activeMediaPlayerTarget = Triple(
                        task.name,
                        task.url,
                        task.category != com.example.ghostdownloader.data.model.CategoryType.MUSIC
                    )
                },
                onChangePin = { newPin ->
                    viewModel.updateVaultPin(newPin)
                }
            )
        }

        activeMediaPlayerTarget?.let { (title, url, isVideo) ->
            MediaPlayerModal(
                title = title,
                streamUrl = url,
                isVideo = isVideo,
                onDismiss = { activeMediaPlayerTarget = null }
            )
        }

        if (showRssDialog) {
            RssFeedDialog(
                subscriptions = rssSubscriptions,
                onDismiss = { showRssDialog = false },
                onAddSubscription = { title, url, auto, filter ->
                    viewModel.addRssSubscription(title, url, auto, filter)
                },
                onDownloadItem = { url, title ->
                    viewModel.addTask(url = url, name = title)
                }
            )
        }

        if (showSiteExtractorDialog) {
            SiteDeepExtractorDialog(
                onDismiss = { showSiteExtractorDialog = false },
                onEnqueueBatch = { items ->
                    items.forEach { item ->
                        viewModel.addDirectDownload(
                            url = item.directUrl,
                            name = item.title,
                            protocol = item.protocol,
                            category = item.category
                        )
                    }
                }
            )
        }

        if (showStealthProxyDialog) {
            StealthProxyDialog(
                settings = settings,
                onDismiss = { showStealthProxyDialog = false },
                onSaveConfig = { mode, host, port, ua ->
                    viewModel.updateSettings(
                        settings.copy(
                            proxyMode = mode,
                            proxyHost = host,
                            proxyPort = port,
                            customUserAgent = ua
                        )
                    )
                    showStealthProxyDialog = false
                }
            )
        }

        if (showStorageCleanerDialog) {
            StorageCleanerDialog(
                tasks = tasks,
                onCleanJunk = {
                    // Triggers storage chunk cleanup
                },
                onDismiss = { showStorageCleanerDialog = false }
            )
        }

        activeGhostShareTask?.let { task ->
            GhostShareDialog(
                task = task,
                onDismiss = { activeGhostShareTask = null }
            )
        }

        activeArchiveTask?.let { task ->
            ArchiveExtractorDialog(
                task = task,
                onDismiss = { activeArchiveTask = null }
            )
        }

        selectedTaskForDetails?.let { task ->
            // Keep task reference live if it updates in tasks list
            val liveTask = tasks.find { it.id == task.id } ?: task
            TaskDetailsDialog(
                task = liveTask,
                onDismiss = { selectedTaskForDetails = null },
                onToggleTask = { viewModel.toggleTask(liveTask.id) },
                onDeleteTask = {
                    viewModel.deleteTask(liveTask.id)
                    selectedTaskForDetails = null
                },
                onOpenGhostShare = {
                    activeGhostShareTask = liveTask
                },
                onOpenArchiveExtractor = {
                    activeArchiveTask = liveTask
                }
            )
        }
    }
}
