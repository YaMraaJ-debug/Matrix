package com.example.ghostdownloader.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

object Formatters {

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceIn(0, units.size - 1)
        val value = bytes / 1024.0.pow(digitGroups.toDouble())
        return String.format(Locale.US, "%.1f %s", value, units[digitGroups])
    }

    fun formatSpeed(bytesPerSec: Long): String {
        if (bytesPerSec <= 0) return "0 B/s"
        val units = arrayOf("B/s", "KB/s", "MB/s", "GB/s")
        val digitGroups = (ln(bytesPerSec.toDouble()) / ln(1024.0)).toInt().coerceIn(0, units.size - 1)
        val value = bytesPerSec / 1024.0.pow(digitGroups.toDouble())
        return String.format(Locale.US, "%.1f %s", value, units[digitGroups])
    }

    fun formatEta(seconds: Long): String {
        if (seconds <= 0) return "Done"
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
