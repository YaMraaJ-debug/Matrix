package com.example.ghostdownloader.data.model

enum class TaskStatus {
    DOWNLOADING,
    WAITING,
    PAUSED,
    COMPLETED,
    ERROR;

    val displayName: String
        get() = when (this) {
            DOWNLOADING -> "Downloading"
            WAITING -> "Waiting"
            PAUSED -> "Paused"
            COMPLETED -> "Completed"
            ERROR -> "Error"
        }
}

enum class ProtocolType(val label: String) {
    HTTP("HTTP"),
    TORRENT("Torrent"),
    M3U8("HLS/M3U8"),
    FTP("FTP"),
    ED2K("eDonkey"),
    BILIBILI("Bilibili"),
    YOUTUBE("YouTube"),
    GITHUB("GitHub"),
    HUGGINGFACE("HuggingFace")
}

enum class CategoryType(val label: String) {
    ALL("All"),
    VIDEO("Video"),
    MUSIC("Music"),
    DOCUMENT("Document"),
    SOFTWARE("Software"),
    ARCHIVE("Archive"),
    TORRENT("Torrent")
}

enum class TaskPriority {
    HIGH, NORMAL, LOW
}

data class DownloadChunk(
    val id: Int,
    val startByte: Long,
    val endByte: Long,
    val downloadedBytes: Long,
    val speed: Long, // bytes/sec
    val status: String // "active", "completed", "idle"
)

data class TorrentTracker(
    val id: String,
    val url: String,
    val status: String,
    val seeders: Int,
    val leechers: Int,
    val downloaded: Long,
    val lastAnnounce: String
)

data class TorrentPeer(
    val id: String,
    val ip: String,
    val country: String,
    val client: String,
    val downloadSpeed: Long,
    val uploadSpeed: Long,
    val progressPercent: Int,
    val flags: String
)

data class DownloadTask(
    val id: String,
    val name: String,
    val url: String,
    val protocol: ProtocolType,
    val category: CategoryType,
    val status: TaskStatus,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val speed: Long = 0,
    val uploadSpeed: Long = 0,
    val etaSeconds: Long = 0,
    val connections: Int = 8,
    val chunks: List<DownloadChunk> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val savePath: String = "/storage/emulated/0/Download",
    val md5Hash: String = "",
    val sha256Hash: String = "",
    val mimeType: String = "",
    val referer: String = "",
    val userAgent: String = "",
    val headers: Map<String, String> = emptyMap(),
    val peers: Int = 0,
    val seeders: Int = 0,
    val errorMessage: String? = null,
    val mirror: String? = null,
    val trackers: List<TorrentTracker> = emptyList(),
    val connectedPeers: List<TorrentPeer> = emptyList(),
    val isVaulted: Boolean = false,
    val speedLimitKbps: Int = 0,
    val scheduledAt: Long? = null,
    val smartTags: List<String> = emptyList(),
    val mirrorUrls: List<String> = emptyList(),
    val activeMirror: String? = null,
    val hasSubtitles: Boolean = false,
    val isMeshShared: Boolean = false
) {
    val progress: Float
        get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()
}

data class FeaturePack(
    val id: String,
    val name: String,
    val identifier: String,
    val version: String,
    val enabled: Boolean,
    val description: String,
    val protocols: List<String>,
    val features: List<String>
)

data class MediaResource(
    val id: String,
    val title: String,
    val url: String,
    val type: String, // "m3u8", "mp4", "dash", "mp3"
    val sizeBytes: Long,
    val duration: String,
    val resolution: String,
    val pageUrl: String,
    val detectedAt: Long = System.currentTimeMillis()
)

data class ImageResource(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val alt: String,
    val format: String,
    val isSelected: Boolean = false
)

data class RssFeedItem(
    val id: String,
    val title: String,
    val link: String,
    val enclosureUrl: String? = null,
    val pubDate: String,
    val category: CategoryType = CategoryType.VIDEO,
    val isDownloaded: Boolean = false
)

data class RssFeedSubscription(
    val id: String,
    val title: String,
    val feedUrl: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val autoDownload: Boolean = true,
    val filterRegex: String = "",
    val items: List<RssFeedItem> = emptyList()
)

data class AppSettings(
    val downloadDirectory: String = "/storage/emulated/0/Download/Matrix-dlp",
    val appTheme: String = "matrix_neon", // "matrix_neon", "synthwave", "oled", "cyber_blue", "clean_light"
    val maxConcurrentDownloads: Int = 3,
    val globalDownloadLimitKbps: Int = 0,
    val globalUploadLimitKbps: Int = 0,
    val autoStartOnAdd: Boolean = true,
    val autoDetectCategory: Boolean = true,
    val clipboardMonitor: Boolean = true,
    val playCompletionSound: Boolean = true,
    val defaultConnections: Int = 16,
    val aria2RpcEnabled: Boolean = false,
    val aria2RpcPort: Int = 6800,
    val proxyMode: String = "none",
    val proxyHost: String = "127.0.0.1",
    val proxyPort: Int = 10809,
    val scheduledNightMode: Boolean = false,
    val nightStartHour: Int = 2,
    val nightEndHour: Int = 7,
    val abiOptimizationMode: String = "auto",
    val neonAccelerationEnabled: Boolean = true,
    val wifiOnly: Boolean = false,
    val stopOnLowBattery: Boolean = false,
    val vaultPin: String = "1234",
    val decoyVaultPin: String = "0000",
    val vaultEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val speedThrottlePreset: String = "unlimited", // "eco", "gaming", "turbo", "unlimited"
    val dualChannelBondingEnabled: Boolean = true, // Wi-Fi + Mobile Data simultaneously
    val thermalBatteryShieldEnabled: Boolean = true, // Smart pause on overheating / low battery
    val floatingBubbleEnabled: Boolean = true, // PiP floating speed controller
    val autoCaptchaBypassEnabled: Boolean = true, // Cloudflare turnstile & timer skipper
    val autoMirrorFailoverEnabled: Boolean = true, // Dead link auto-revival from mirrors
    val autoSubtitleDownload: Boolean = true, // Auto download .srt in Hindi & English
    val autoDeduplication: Boolean = true, // Duplicate hash detection
    val autoStripMetadata: Boolean = true, // EXIF / personal data wipe
    val autoShutdownSleepOnComplete: Boolean = false, // Power sleep & notification mute when finished
    val batteryTempThreshold: Float = 42.0f,
    val batteryLevelThreshold: Int = 15,
    val dailyDataBudgetLimitGb: Float = 0f, // 0 = unlimited, > 0 = cellular daily cap (e.g. 1.5f)
    val chargingOnlyMode: Boolean = false, // Only download when plugged in & battery > 80%
    val ramCachingZeroFlashWear: Boolean = true, // Sequential RAM buffer before storage flush
    val calculatorVaultDisguise: Boolean = false, // Disguise vault as working calculator
    val webDashboardEnabled: Boolean = true, // Remote PC Web UI on port 9090
    val webDashboardPort: Int = 9090
)
