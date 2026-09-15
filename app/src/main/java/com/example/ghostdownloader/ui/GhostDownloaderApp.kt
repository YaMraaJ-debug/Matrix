package com.example.ghostdownloader.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ghostdownloader.R
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.ui.components.FloatingSpeedBubbleWidget
import com.example.ghostdownloader.ui.dialogs.AiVideoParserDialog
import com.example.ghostdownloader.ui.dialogs.ArchiveExtractorDialog
import com.example.ghostdownloader.ui.dialogs.BatchUrlDialog
import com.example.ghostdownloader.ui.dialogs.CloudDebridDialog
import com.example.ghostdownloader.ui.dialogs.GhostShareDialog
import com.example.ghostdownloader.ui.dialogs.GhostVaultDialog
import com.example.ghostdownloader.ui.dialogs.MediaPlayerModal
import com.example.ghostdownloader.ui.dialogs.NetworkBondingDialog
import com.example.ghostdownloader.ui.dialogs.NewTaskDialog
import com.example.ghostdownloader.ui.dialogs.PlanTaskDialog
import com.example.ghostdownloader.ui.dialogs.PlatformArchitectureDialog
import com.example.ghostdownloader.ui.dialogs.RssFeedDialog
import com.example.ghostdownloader.ui.dialogs.SecurityVirusScannerDialog
import com.example.ghostdownloader.ui.dialogs.SiteDeepExtractorDialog
import com.example.ghostdownloader.ui.dialogs.SpeedLimiterDialog
import com.example.ghostdownloader.ui.dialogs.StealthProxyDialog
import com.example.ghostdownloader.ui.dialogs.StorageCleanerDialog
import com.example.ghostdownloader.ui.dialogs.TaskDetailsDialog
import com.example.ghostdownloader.ui.dialogs.ThemeSelectorDialog
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
    var showAiParserDialog by remember { mutableStateOf(false) }
    var showBondingDialog by remember { mutableStateOf(false) }
    var showCloudDebridDialog by remember { mutableStateOf(false) }
    var activeSecurityScanTask by remember { mutableStateOf<DownloadTask?>(null) }
    var bubbleVisible by remember { mutableStateOf(true) }
    var activeGhostShareTask by remember { mutableStateOf<DownloadTask?>(null) }
    var activeArchiveTask by remember { mutableStateOf<DownloadTask?>(null) }
    var activeMediaPlayerTarget by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }
    var selectedTaskForDetails by remember { mutableStateOf<DownloadTask?>(null) }
    var showThemeSelectorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var detectedClipboardLink by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(settings.clipboardAutoDetect) {
        if (settings.clipboardAutoDetect) {
            try {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                clipboard?.primaryClip?.let { clip ->
                    if (clip.itemCount > 0) {
                        val text = clip.getItemAt(0)?.text?.toString()?.trim() ?: ""
                        if ((text.startsWith("http://") || text.startsWith("https://") || text.startsWith("magnet:")) &&
                            tasks.none { it.url == text }
                        ) {
                            detectedClipboardLink = text
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

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
                            contentDescription = "Matrix-dlp Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Matrix-dlp",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "DLP-PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = "Ultra-Fast Multi-Protocol Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Theme Studio button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showThemeSelectorDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Studio",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (settings.appTheme) {
                                    "synthwave" -> "SYNTH"
                                    "oled" -> "OLED"
                                    "cyber_blue" -> "CYBER"
                                    "clean_light" -> "LIGHT"
                                    else -> "NEON"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Architecture / Platform badge button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showPlatformDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Platform & Architecture",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (deviceProfile.is64Bit) "ARM64" else "ARMv7",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.secondary
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
                    onOpenAiParser = { showAiParserDialog = true },
                    onOpenBonding = { showBondingDialog = true },
                    onOpenCloudDebrid = { showCloudDebridDialog = true },
                    onScanSecurity = { task -> activeSecurityScanTask = task },
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
                    onTogglePack = { viewModel.toggleFeaturePack(it) },
                    onOpenAiParser = { showAiParserDialog = true },
                    onOpenBonding = { showBondingDialog = true },
                    onOpenCloudDebrid = { showCloudDebridDialog = true }
                )
                3 -> SettingsScreen(
                    settings = settings,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onOpenPlatformMatrix = { showPlatformDialog = true },
                    onOpenStealthProxy = { showStealthProxyDialog = true },
                    onOpenStorageCleaner = { showStorageCleanerDialog = true }
                )
            }

            // Smart Clipboard Auto-Catch Overlay Banner
            AnimatedVisibility(
                visible = detectedClipboardLink != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                detectedClipboardLink?.let { clipUrl ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlueLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = CyberBlueLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Link detected in Clipboard",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    clipUrl,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.addTask(url = clipUrl)
                                    detectedClipboardLink = null
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlueLight, contentColor = Color.Black)
                            ) {
                                Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(
                                onClick = { detectedClipboardLink = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Floating Speed Bubble Widget (Overlay Controller)
            if (settings.floatingBubbleEnabled) {
                FloatingSpeedBubbleWidget(
                    isVisible = bubbleVisible,
                    downloadSpeed = totalDownloadSpeed,
                    uploadSpeed = totalUploadSpeed,
                    activeTasksCount = activeCount,
                    isPaused = activeCount == 0 && tasks.any { it.status == TaskStatus.PAUSED },
                    onToggleGlobalPause = {
                        if (activeCount > 0) {
                            viewModel.pauseAllTasks()
                        } else {
                            viewModel.startAllTasks()
                        }
                    },
                    onCloseBubble = { bubbleVisible = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
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
                currentPin = settings.vaultPin,
                decoyPin = settings.decoyVaultPin,
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
                },
                onOpenPlayer = {
                    activeMediaPlayerTarget = Triple(
                        liveTask.name,
                        liveTask.url,
                        liveTask.category != com.example.ghostdownloader.data.model.CategoryType.MUSIC
                    )
                },
                onOpenSecurityScanner = {
                    activeSecurityScanTask = liveTask
                }
            )
        }

        if (showAiParserDialog) {
            AiVideoParserDialog(
                onDismiss = { showAiParserDialog = false },
                onStartDownload = { url, title, isAudio ->
                    viewModel.addDirectDownload(
                        url = url,
                        name = title,
                        protocol = com.example.ghostdownloader.data.model.ProtocolType.HTTP_HTTPS,
                        category = if (isAudio) com.example.ghostdownloader.data.model.CategoryType.MUSIC else com.example.ghostdownloader.data.model.CategoryType.VIDEO
                    )
                    showAiParserDialog = false
                }
            )
        }

        if (showBondingDialog) {
            NetworkBondingDialog(
                bondingEnabled = settings.dualChannelBondingEnabled,
                onToggleBonding = { enabled ->
                    viewModel.updateSettings(settings.copy(dualChannelBondingEnabled = enabled))
                },
                onDismiss = { showBondingDialog = false }
            )
        }

        if (showCloudDebridDialog) {
            CloudDebridDialog(
                onDismiss = { showCloudDebridDialog = false },
                onAddUnrestrictedTask = { unrestrictUrl, title ->
                    viewModel.addDirectDownload(
                        url = unrestrictUrl,
                        name = title,
                        protocol = com.example.ghostdownloader.data.model.ProtocolType.HTTP_HTTPS,
                        category = com.example.ghostdownloader.data.model.CategoryType.ARCHIVE
                    )
                    showCloudDebridDialog = false
                }
            )
        }

        activeSecurityScanTask?.let { task ->
            SecurityVirusScannerDialog(
                task = task,
                onDismiss = { activeSecurityScanTask = null }
            )
        }

        if (showThemeSelectorDialog) {
            ThemeSelectorDialog(
                currentTheme = settings.appTheme,
                onSelectTheme = { themeId ->
                    viewModel.updateSettings(settings.copy(appTheme = themeId))
                },
                onDismiss = { showThemeSelectorDialog = false }
            )
        }
    }
}
