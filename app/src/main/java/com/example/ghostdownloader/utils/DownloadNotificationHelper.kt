package com.example.ghostdownloader.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.ghostdownloader.MainActivity
import com.example.ghostdownloader.R

object DownloadNotificationHelper {

    private const val CHANNEL_ACTIVE_ID = "ghost_active_downloads"
    private const val CHANNEL_ACTIVE_NAME = "Active Downloads"
    private const val CHANNEL_COMPLETE_ID = "ghost_completed_downloads"
    private const val CHANNEL_COMPLETE_NAME = "Completed Transfers"

    private const val NOTIFICATION_ACTIVE_ID = 1001
    private const val NOTIFICATION_COMPLETE_BASE_ID = 2000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val activeChannel = NotificationChannel(
                CHANNEL_ACTIVE_ID,
                CHANNEL_ACTIVE_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live download progress and speed"
                setShowBadge(false)
            }

            val completeChannel = NotificationChannel(
                CHANNEL_COMPLETE_ID,
                CHANNEL_COMPLETE_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when downloads are finished"
                enableVibration(true)
            }

            nm.createNotificationChannel(activeChannel)
            nm.createNotificationChannel(completeChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun updateActiveDownloadsNotification(
        context: Context,
        activeTasksCount: Int,
        firstTaskName: String,
        overallProgress: Int,
        totalSpeedBytesPerSec: Long,
        threadsCount: Int
    ) {
        if (!hasNotificationPermission(context) || activeTasksCount <= 0) {
            cancelActiveNotification(context)
            return
        }

        createNotificationChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val speedFormatted = Formatters.formatSpeed(totalSpeedBytesPerSec)
        val contentText = if (activeTasksCount == 1) {
            "$firstTaskName • $speedFormatted ($threadsCount threads)"
        } else {
            "$firstTaskName and ${activeTasksCount - 1} more • $speedFormatted"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ACTIVE_ID)
            .setSmallIcon(R.drawable.ic_logo_nobg)
            .setContentTitle("Ghost Downloader: $overallProgress%")
            .setContentText(contentText)
            .setSubText("$activeTasksCount active")
            .setProgress(100, overallProgress.coerceIn(0, 100), false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        nm.notify(NOTIFICATION_ACTIVE_ID, builder.build())
    }

    fun notifyDownloadCompleted(context: Context, taskName: String, totalBytes: Long) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sizeFormatted = Formatters.formatBytes(totalBytes)
        val builder = NotificationCompat.Builder(context, CHANNEL_COMPLETE_ID)
            .setSmallIcon(R.drawable.ic_logo_nobg)
            .setContentTitle("Download Finished")
            .setContentText("$taskName ($sizeFormatted)")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notifId = NOTIFICATION_COMPLETE_BASE_ID + (taskName.hashCode() % 1000)
        nm.notify(notifId, builder.build())
    }

    fun cancelActiveNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        nm.cancel(NOTIFICATION_ACTIVE_ID)
    }
}
