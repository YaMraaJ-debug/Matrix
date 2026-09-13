package com.example.ghostdownloader.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghostdownloader.data.local.AppDatabase
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.FeaturePack
import com.example.ghostdownloader.data.model.ImageResource
import com.example.ghostdownloader.data.model.MediaResource
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.RssFeedSubscription
import com.example.ghostdownloader.data.model.TaskPriority
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.data.repository.DownloadRepository
import com.example.ghostdownloader.utils.DownloadNotificationHelper
import com.example.ghostdownloader.utils.NetworkRuleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = DownloadRepository(database.taskDao(), viewModelScope)

    val tasks: StateFlow<List<DownloadTask>> = repository.tasks

    val nonVaultedTasks: StateFlow<List<DownloadTask>> = tasks.map { list ->
        list.filter { !it.isVaulted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultedTasks: StateFlow<List<DownloadTask>> = tasks.map { list ->
        list.filter { it.isVaulted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featurePacks: StateFlow<List<FeaturePack>> = repository.featurePacks
    val mediaResources: StateFlow<List<MediaResource>> = repository.mediaResources
    val imageResources: StateFlow<List<ImageResource>> = repository.imageResources
    val rssSubscriptions: StateFlow<List<RssFeedSubscription>> = repository.rssSubscriptions
    val settings: StateFlow<AppSettings> = repository.settings

    val totalDownloadSpeed: StateFlow<Long> = tasks.map { list ->
        list.filter { it.status == TaskStatus.DOWNLOADING }.sumOf { it.speed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalUploadSpeed: StateFlow<Long> = tasks.map { list ->
        list.filter { it.status == TaskStatus.DOWNLOADING }.sumOf { it.uploadSpeed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        // Engine simulation ticker running every 1 second
        viewModelScope.launch {
            while (isActive) {
                delay(1000)

                // Network & Battery policy guard (Option 3)
                val app = getApplication<Application>()
                val curSettings = settings.value
                val policy = NetworkRuleManager.evaluateDownloadPolicy(
                    app,
                    curSettings.wifiOnly,
                    curSettings.stopOnLowBattery
                )

                if (policy.allowed) {
                    val previouslyDownloading = tasks.value.filter { it.status == TaskStatus.DOWNLOADING }
                    repository.tickEngineSimulation()

                    // Rich Notifications update (Option 6)
                    if (curSettings.notificationsEnabled) {
                        val active = tasks.value.filter { it.status == TaskStatus.DOWNLOADING }
                        if (active.isNotEmpty()) {
                            val totalSpeed = active.sumOf { it.speed }
                            val overallProgress = (active.sumOf { it.downloadedBytes }.toFloat() / active.sumOf { it.totalBytes }.coerceAtLeast(1L) * 100).toInt()
                            DownloadNotificationHelper.updateActiveDownloadsNotification(
                                app,
                                active.size,
                                active.first().name,
                                overallProgress,
                                totalSpeed,
                                active.sumOf { it.connections }
                            )
                        } else {
                            DownloadNotificationHelper.cancelActiveNotification(app)
                        }

                        // Check completed tasks
                        tasks.value.filter { it.status == TaskStatus.COMPLETED }.forEach { comp ->
                            if (previouslyDownloading.any { it.id == comp.id }) {
                                DownloadNotificationHelper.notifyDownloadCompleted(app, comp.name, comp.totalBytes)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addTask(
        url: String,
        name: String = "",
        priority: TaskPriority = TaskPriority.NORMAL,
        connections: Int = 16
    ) {
        repository.addTask(
            url = url,
            customName = name,
            priority = priority,
            connections = connections
        )
    }

    fun addDirectDownload(
        url: String,
        name: String,
        protocol: ProtocolType,
        category: CategoryType
    ) {
        repository.addTask(
            url = url,
            customName = name,
            protocol = protocol,
            category = category,
            totalBytes = (25_000_000L..350_000_000L).random()
        )
    }

    fun setTaskVaulted(taskId: String, vaulted: Boolean) {
        repository.setTaskVaulted(taskId, vaulted)
    }

    fun setTaskSpeedLimit(taskId: String, speedLimitKbps: Int) {
        repository.setTaskSpeedLimit(taskId, speedLimitKbps)
    }

    fun updateSchedulerAndNetworkRules(
        nightMode: Boolean,
        startH: Int,
        endH: Int,
        wifiOnly: Boolean,
        lowBatteryStop: Boolean
    ) {
        repository.updateSchedulerAndNetworkRules(nightMode, startH, endH, wifiOnly, lowBatteryStop)
    }

    fun updateSpeedThrottle(limitKbps: Int, preset: String) {
        repository.updateSpeedThrottle(limitKbps, preset)
    }

    fun updateVaultPin(pin: String) {
        repository.updateVaultPin(pin)
    }

    fun batchAddTasks(urls: List<String>) {
        urls.forEach { url ->
            repository.addTask(url = url)
        }
    }

    fun toggleTask(taskId: String) {
        repository.toggleTask(taskId)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    fun startAllTasks() {
        repository.startAllTasks()
    }

    fun pauseAllTasks() {
        repository.pauseAllTasks()
    }

    fun updateTaskPriority(taskId: String, priority: TaskPriority) {
        repository.updateTaskPriority(taskId, priority)
    }

    fun toggleFeaturePack(packId: String) {
        repository.toggleFeaturePack(packId)
    }

    fun toggleImageSelection(imageId: String) {
        repository.toggleImageSelection(imageId)
    }

    fun selectAllImages(select: Boolean) {
        repository.selectAllImages(select)
    }

    fun addRssSubscription(title: String, url: String, autoDownload: Boolean, filter: String) {
        repository.addRssSubscription(title, url, autoDownload, filter)
    }

    fun downloadMediaResource(media: MediaResource) {
        val proto = when (media.type) {
            "m3u8" -> ProtocolType.M3U8
            else -> ProtocolType.HTTP
        }
        val ext = when (media.type) {
            "m3u8" -> ".mp4"
            "mp4" -> ".mp4"
            "mp3" -> ".mp3"
            else -> ".bin"
        }
        val sanitizedTitle = media.title.replace(Regex("[^a-zA-Z0-9_.-]"), "_") + ext

        repository.addTask(
            url = media.url,
            customName = sanitizedTitle,
            protocol = proto,
            category = if (media.type == "mp3") CategoryType.MUSIC else CategoryType.VIDEO,
            totalBytes = media.sizeBytes
        )
    }

    fun downloadSelectedImages(images: List<ImageResource>) {
        images.forEach { img ->
            val ext = img.format.lowercase()
            val cleanName = img.alt.replace(Regex("[^a-zA-Z0-9_.-]"), "_") + ".$ext"
            repository.addTask(
                url = img.url,
                customName = cleanName,
                protocol = ProtocolType.HTTP,
                category = CategoryType.DOCUMENT,
                totalBytes = img.sizeBytes
            )
        }
        repository.selectAllImages(false)
    }

    fun updateSettings(newSettings: AppSettings) {
        repository.updateSettings(newSettings)
    }
}
