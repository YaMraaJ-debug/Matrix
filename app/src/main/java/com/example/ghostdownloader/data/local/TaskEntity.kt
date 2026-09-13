package com.example.ghostdownloader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.DownloadChunk
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.TaskPriority
import com.example.ghostdownloader.data.model.TaskStatus
import com.example.ghostdownloader.data.model.TorrentPeer
import com.example.ghostdownloader.data.model.TorrentTracker

@Entity(tableName = "download_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val protocol: String,
    val category: String,
    val status: String,
    val priority: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val speed: Long,
    val uploadSpeed: Long,
    val etaSeconds: Long,
    val connections: Int,
    val createdAt: Long,
    val completedAt: Long?,
    val savePath: String,
    val md5Hash: String,
    val sha256Hash: String,
    val mimeType: String,
    val referer: String,
    val mirror: String?,
    val peers: Int,
    val seeders: Int,
    val errorMessage: String?,
    val isVaulted: Boolean = false,
    val speedLimitKbps: Int = 0
) {
    fun toDomainModel(
        chunks: List<DownloadChunk> = emptyList(),
        trackers: List<TorrentTracker> = emptyList(),
        peersList: List<TorrentPeer> = emptyList()
    ): DownloadTask {
        val proto = try { ProtocolType.valueOf(protocol) } catch (_: Exception) { ProtocolType.HTTP }
        val cat = try { CategoryType.valueOf(category) } catch (_: Exception) { CategoryType.SOFTWARE }
        val st = try { TaskStatus.valueOf(status) } catch (_: Exception) { TaskStatus.DOWNLOADING }
        val prio = try { TaskPriority.valueOf(priority) } catch (_: Exception) { TaskPriority.NORMAL }

        return DownloadTask(
            id = id,
            name = name,
            url = url,
            protocol = proto,
            category = cat,
            status = st,
            priority = prio,
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            speed = speed,
            uploadSpeed = uploadSpeed,
            etaSeconds = etaSeconds,
            connections = connections,
            chunks = chunks,
            createdAt = createdAt,
            completedAt = completedAt,
            savePath = savePath,
            md5Hash = md5Hash,
            sha256Hash = sha256Hash,
            mimeType = mimeType,
            referer = referer,
            mirror = mirror,
            peers = peers,
            seeders = seeders,
            errorMessage = errorMessage,
            trackers = trackers,
            connectedPeers = peersList,
            isVaulted = isVaulted,
            speedLimitKbps = speedLimitKbps
        )
    }

    companion object {
        fun fromDomain(task: DownloadTask): TaskEntity {
            return TaskEntity(
                id = task.id,
                name = task.name,
                url = task.url,
                protocol = task.protocol.name,
                category = task.category.name,
                status = task.status.name,
                priority = task.priority.name,
                totalBytes = task.totalBytes,
                downloadedBytes = task.downloadedBytes,
                speed = task.speed,
                uploadSpeed = task.uploadSpeed,
                etaSeconds = task.etaSeconds,
                connections = task.connections,
                createdAt = task.createdAt,
                completedAt = task.completedAt,
                savePath = task.savePath,
                md5Hash = task.md5Hash,
                sha256Hash = task.sha256Hash,
                mimeType = task.mimeType,
                referer = task.referer,
                mirror = task.mirror,
                peers = task.peers,
                seeders = task.seeders,
                errorMessage = task.errorMessage,
                isVaulted = task.isVaulted,
                speedLimitKbps = task.speedLimitKbps
            )
        }
    }
}
