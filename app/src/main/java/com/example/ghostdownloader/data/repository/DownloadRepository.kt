package com.example.ghostdownloader.data.repository

import com.example.ghostdownloader.data.local.TaskDao
import com.example.ghostdownloader.data.local.TaskEntity
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadChunk
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.FeaturePack
import com.example.ghostdownloader.data.model.ImageResource
import com.example.ghostdownloader.data.model.InitialData
import com.example.ghostdownloader.data.model.MediaResource
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.RssFeedSubscription
import com.example.ghostdownloader.data.model.TaskPriority
import com.example.ghostdownloader.data.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class DownloadRepository(
    private val taskDao: TaskDao,
    private val scope: CoroutineScope
) {
    private val _tasks = MutableStateFlow<List<DownloadTask>>(emptyList())
    val tasks: StateFlow<List<DownloadTask>> = _tasks.asStateFlow()

    private val _featurePacks = MutableStateFlow<List<FeaturePack>>(InitialData.featurePacks)
    val featurePacks: StateFlow<List<FeaturePack>> = _featurePacks.asStateFlow()

    private val _mediaResources = MutableStateFlow<List<MediaResource>>(InitialData.mediaResources)
    val mediaResources: StateFlow<List<MediaResource>> = _mediaResources.asStateFlow()

    private val _imageResources = MutableStateFlow<List<ImageResource>>(InitialData.imageResources)
    val imageResources: StateFlow<List<ImageResource>> = _imageResources.asStateFlow()

    private val _rssSubscriptions = MutableStateFlow<List<RssFeedSubscription>>(InitialData.rssSubscriptions)
    val rssSubscriptions: StateFlow<List<RssFeedSubscription>> = _rssSubscriptions.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            val entities = taskDao.getAllTasks().first()
            if (entities.isEmpty()) {
                val initial = InitialData.tasks
                taskDao.insertAll(initial.map { TaskEntity.fromDomain(it) })
                _tasks.value = initial
            } else {
                val initialMap = InitialData.tasks.associateBy { it.id }
                _tasks.value = entities.map { entity ->
                    val domainTemplate = initialMap[entity.id]
                    entity.toDomainModel(
                        chunks = domainTemplate?.chunks ?: emptyList(),
                        trackers = domainTemplate?.trackers ?: emptyList(),
                        peersList = domainTemplate?.connectedPeers ?: emptyList()
                    )
                }
            }
        }
    }

    fun addTask(
        url: String,
        customName: String = "",
        protocol: ProtocolType? = null,
        category: CategoryType? = null,
        priority: TaskPriority = TaskPriority.NORMAL,
        connections: Int = 16,
        totalBytes: Long = (100_000_000L..2_000_000_000L).random(),
        savePath: String = _settings.value.downloadDirectory,
        referer: String = "",
        headers: Map<String, String> = emptyMap()
    ): DownloadTask {
        val detectedProto = protocol ?: detectProtocol(url)
        val rawName = if (customName.isNotBlank()) customName else extractFileName(url)
        val detectedCat = category ?: detectCategory(rawName)

        val id = "task-" + UUID.randomUUID().toString().substring(0, 8)
        val chunks = createChunks(totalBytes, 0L, connections)

        val newTask = DownloadTask(
            id = id,
            name = rawName,
            url = url,
            protocol = detectedProto,
            category = detectedCat,
            status = if (_settings.value.autoStartOnAdd) TaskStatus.DOWNLOADING else TaskStatus.WAITING,
            priority = priority,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            speed = if (_settings.value.autoStartOnAdd) (8_000_000L..18_000_000L).random() else 0L,
            etaSeconds = if (_settings.value.autoStartOnAdd) 120L else 0L,
            connections = connections,
            chunks = chunks,
            savePath = "$savePath/$rawName",
            referer = referer,
            headers = headers
        )

        _tasks.value = listOf(newTask) + _tasks.value

        scope.launch(Dispatchers.IO) {
            taskDao.insertTask(TaskEntity.fromDomain(newTask))
        }

        return newTask
    }

    fun toggleTask(taskId: String) {
        val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val task = currentList[index]
        val updated = when (task.status) {
            TaskStatus.DOWNLOADING -> task.copy(
                status = TaskStatus.PAUSED,
                speed = 0L,
                uploadSpeed = 0L,
                etaSeconds = 0L
            )
            TaskStatus.PAUSED, TaskStatus.WAITING, TaskStatus.ERROR -> task.copy(
                status = TaskStatus.DOWNLOADING,
                speed = (8_000_000L..16_000_000L).random(),
                etaSeconds = calculateEta(task.totalBytes, task.downloadedBytes, 12_000_000L)
            )
            TaskStatus.COMPLETED -> task.copy(
                status = TaskStatus.DOWNLOADING,
                downloadedBytes = 0L,
                speed = (8_000_000L..16_000_000L).random(),
                chunks = createChunks(task.totalBytes, 0L, task.connections)
            )
        }

        currentList[index] = updated
        _tasks.value = currentList

        scope.launch(Dispatchers.IO) {
            taskDao.updateTask(TaskEntity.fromDomain(updated))
        }
    }

    fun startAllTasks() {
        val updated = _tasks.value.map { task ->
            if (task.status == TaskStatus.PAUSED || task.status == TaskStatus.WAITING) {
                task.copy(
                    status = TaskStatus.DOWNLOADING,
                    speed = (8_000_000L..16_000_000L).random()
                )
            } else task
        }
        _tasks.value = updated
        scope.launch(Dispatchers.IO) {
            taskDao.insertAll(updated.map { TaskEntity.fromDomain(it) })
        }
    }

    fun pauseAllTasks() {
        val updated = _tasks.value.map { task ->
            if (task.status == TaskStatus.DOWNLOADING) {
                task.copy(
                    status = TaskStatus.PAUSED,
                    speed = 0L,
                    uploadSpeed = 0L
                )
            } else task
        }
        _tasks.value = updated
        scope.launch(Dispatchers.IO) {
            taskDao.insertAll(updated.map { TaskEntity.fromDomain(it) })
        }
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        scope.launch(Dispatchers.IO) {
            taskDao.deleteTaskById(taskId)
        }
    }

    fun updateTaskPriority(taskId: String, priority: TaskPriority) {
        val updated = _tasks.value.map {
            if (it.id == taskId) it.copy(priority = priority) else it
        }
        _tasks.value = updated
        val changed = updated.find { it.id == taskId }
        if (changed != null) {
            scope.launch(Dispatchers.IO) {
                taskDao.updateTask(TaskEntity.fromDomain(changed))
            }
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    fun toggleFeaturePack(packId: String) {
        _featurePacks.value = _featurePacks.value.map { pack ->
            if (pack.id == packId) pack.copy(enabled = !pack.enabled) else pack
        }
    }

    fun toggleImageSelection(imageId: String) {
        _imageResources.value = _imageResources.value.map { img ->
            if (img.id == imageId) img.copy(isSelected = !img.isSelected) else img
        }
    }

    fun selectAllImages(select: Boolean) {
        _imageResources.value = _imageResources.value.map { it.copy(isSelected = select) }
    }

    fun addRssSubscription(title: String, url: String, autoDownload: Boolean, filter: String) {
        val newSub = RssFeedSubscription(
            id = "rss-" + UUID.randomUUID().toString().substring(0, 6),
            title = title,
            feedUrl = url,
            autoDownload = autoDownload,
            filterRegex = filter,
            items = listOf(
                com.example.ghostdownloader.data.model.RssFeedItem(
                    id = "rss-item-" + UUID.randomUUID().toString().substring(0, 6),
                    title = "$title Latest Episode/Release",
                    link = url,
                    enclosureUrl = "$url/download.torrent",
                    pubDate = "Just now",
                    category = CategoryType.TORRENT
                )
            )
        )
        _rssSubscriptions.value = listOf(newSub) + _rssSubscriptions.value
    }

    // Engine simulation tick
    fun tickEngineSimulation() {
        val current = _tasks.value
        var hasChanges = false

        val updated = current.map { task ->
            if (task.status == TaskStatus.DOWNLOADING) {
                hasChanges = true
                val speedLimitBytes = when {
                    task.speedLimitKbps > 0 -> task.speedLimitKbps * 1024L
                    _settings.value.globalDownloadLimitKbps > 0 -> _settings.value.globalDownloadLimitKbps * 1024L
                    else -> null
                }

                val baseSpeed = task.speed.coerceAtLeast(1_000_000L)
                val jitter = (-500_000L..500_000L).random()
                var currentSpeed = (baseSpeed + jitter).coerceIn(1_500_000L, 28_000_000L)
                if (speedLimitBytes != null && currentSpeed > speedLimitBytes) {
                    currentSpeed = speedLimitBytes
                }

                val newDownloaded = (task.downloadedBytes + currentSpeed).coerceAtMost(task.totalBytes)
                val remainingBytes = task.totalBytes - newDownloaded
                val eta = if (currentSpeed > 0) remainingBytes / currentSpeed else 0L

                // Update chunks
                val updatedChunks = updateChunksProgress(task.chunks, task.totalBytes, newDownloaded)

                if (newDownloaded >= task.totalBytes) {
                    val completed = task.copy(
                        status = TaskStatus.COMPLETED,
                        downloadedBytes = task.totalBytes,
                        speed = 0L,
                        uploadSpeed = 0L,
                        etaSeconds = 0L,
                        completedAt = System.currentTimeMillis(),
                        chunks = task.chunks.map { it.copy(status = "completed", speed = 0L, downloadedBytes = it.endByte - it.startByte + 1) }
                    )
                    scope.launch(Dispatchers.IO) {
                        taskDao.updateTask(TaskEntity.fromDomain(completed))
                    }
                    completed
                } else {
                    task.copy(
                        downloadedBytes = newDownloaded,
                        speed = currentSpeed,
                        uploadSpeed = if (task.protocol == ProtocolType.TORRENT) (400_000L..1_500_000L).random() else 0L,
                        etaSeconds = eta,
                        chunks = updatedChunks
                    )
                }
            } else {
                task
            }
        }

        if (hasChanges) {
            _tasks.value = updated
        }
    }

    private fun updateChunksProgress(chunks: List<DownloadChunk>, totalBytes: Long, totalDownloaded: Long): List<DownloadChunk> {
        if (chunks.isEmpty()) return chunks
        var remainingDownloaded = totalDownloaded
        return chunks.map { chunk ->
            val capacity = chunk.endByte - chunk.startByte + 1
            if (remainingDownloaded >= capacity) {
                remainingDownloaded -= capacity
                chunk.copy(downloadedBytes = capacity, status = "completed", speed = 0L)
            } else if (remainingDownloaded > 0) {
                val d = remainingDownloaded
                remainingDownloaded = 0
                chunk.copy(downloadedBytes = d, status = "active", speed = (400_000L..1_200_000L).random())
            } else {
                chunk.copy(downloadedBytes = 0L, status = "active", speed = (200_000L..800_000L).random())
            }
        }
    }

    private fun createChunks(totalBytes: Long, downloadedBytes: Long, count: Int): List<DownloadChunk> {
        val safeTotal = totalBytes.coerceAtLeast(1024L)
        val numChunks = count.coerceIn(1, 32)
        val chunkSize = (safeTotal / numChunks).coerceAtLeast(1L)
        var remaining = downloadedBytes
        return (0 until numChunks).map { i ->
            val start = i * chunkSize
            val end = if (i == numChunks - 1) safeTotal - 1 else ((i + 1) * chunkSize - 1).coerceAtMost(safeTotal - 1)
            val capacity = (end - start + 1).coerceAtLeast(1L)
            val (d, status) = if (remaining >= capacity) {
                remaining -= capacity
                Pair(capacity, "completed")
            } else if (remaining > 0) {
                val rem = remaining
                remaining = 0
                Pair(rem, "active")
            } else {
                Pair(0L, "active")
            }

            DownloadChunk(
                id = i,
                startByte = start,
                endByte = end,
                downloadedBytes = d,
                speed = (300_000L..1_000_000L).random(),
                status = status
            )
        }
    }

    private fun calculateEta(total: Long, downloaded: Long, speed: Long): Long {
        if (speed <= 0) return 0L
        val rem = (total - downloaded).coerceAtLeast(0L)
        return rem / speed
    }

    private fun detectProtocol(url: String): ProtocolType {
        val lower = url.trim().lowercase()
        return when {
            lower.startsWith("magnet:") || lower.endsWith(".torrent") -> ProtocolType.TORRENT
            lower.endsWith(".m3u8") || lower.contains("m3u8") -> ProtocolType.M3U8
            lower.startsWith("ftp://") || lower.startsWith("ftps://") -> ProtocolType.FTP
            lower.startsWith("ed2k://") -> ProtocolType.ED2K
            lower.contains("bilibili.com") || lower.contains("b23.tv") -> ProtocolType.BILIBILI
            lower.contains("youtube.com") || lower.contains("youtu.be") -> ProtocolType.YOUTUBE
            lower.contains("github.com") -> ProtocolType.GITHUB
            lower.contains("huggingface.co") || lower.contains("hf-mirror.com") -> ProtocolType.HUGGINGFACE
            else -> ProtocolType.HTTP
        }
    }

    private fun detectCategory(filename: String): CategoryType {
        val lower = filename.lowercase()
        return when {
            lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".mov") || lower.endsWith(".avi") || lower.endsWith(".m3u8") || lower.endsWith(".flv") -> CategoryType.VIDEO
            lower.endsWith(".mp3") || lower.endsWith(".flac") || lower.endsWith(".aac") || lower.endsWith(".wav") || lower.endsWith(".ogg") -> CategoryType.MUSIC
            lower.endsWith(".iso") || lower.endsWith(".exe") || lower.endsWith(".msi") || lower.endsWith(".apk") || lower.endsWith(".dmg") || lower.endsWith(".deb") -> CategoryType.SOFTWARE
            lower.endsWith(".zip") || lower.endsWith(".rar") || lower.endsWith(".7z") || lower.endsWith(".tar.gz") || lower.endsWith(".gguf") -> CategoryType.ARCHIVE
            lower.endsWith(".pdf") || lower.endsWith(".epub") || lower.endsWith(".docx") || lower.endsWith(".txt") -> CategoryType.DOCUMENT
            lower.endsWith(".torrent") || lower.startsWith("magnet:") -> CategoryType.TORRENT
            else -> CategoryType.SOFTWARE
        }
    }

    private fun extractFileName(url: String): String {
        return try {
            val clean = url.substringBefore('?').substringBefore('#')
            val segment = clean.substringAfterLast('/')
            if (segment.isNotBlank()) segment else "download_${System.currentTimeMillis()}.bin"
        } catch (_: Exception) {
            "download_${System.currentTimeMillis()}.bin"
        }
    }

    fun setTaskVaulted(taskId: String, vaulted: Boolean) {
        val current = _tasks.value
        val task = current.find { it.id == taskId } ?: return
        val updatedTask = task.copy(isVaulted = vaulted)
        _tasks.value = current.map { if (it.id == taskId) updatedTask else it }
        scope.launch(Dispatchers.IO) {
            taskDao.updateTask(TaskEntity.fromDomain(updatedTask))
        }
    }

    fun setTaskSpeedLimit(taskId: String, speedLimitKbps: Int) {
        val current = _tasks.value
        val task = current.find { it.id == taskId } ?: return
        val updatedTask = task.copy(speedLimitKbps = speedLimitKbps)
        _tasks.value = current.map { if (it.id == taskId) updatedTask else it }
        scope.launch(Dispatchers.IO) {
            taskDao.updateTask(TaskEntity.fromDomain(updatedTask))
        }
    }

    fun updateSchedulerAndNetworkRules(
        nightMode: Boolean,
        startH: Int,
        endH: Int,
        wifiOnly: Boolean,
        lowBatteryStop: Boolean
    ) {
        _settings.value = _settings.value.copy(
            scheduledNightMode = nightMode,
            nightStartHour = startH,
            nightEndHour = endH,
            wifiOnly = wifiOnly,
            stopOnLowBattery = lowBatteryStop
        )
    }

    fun updateSpeedThrottle(limitKbps: Int, preset: String) {
        _settings.value = _settings.value.copy(
            globalDownloadLimitKbps = limitKbps,
            speedThrottlePreset = preset
        )
    }

    fun updateVaultPin(newPin: String) {
        _settings.value = _settings.value.copy(vaultPin = newPin)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }
}
