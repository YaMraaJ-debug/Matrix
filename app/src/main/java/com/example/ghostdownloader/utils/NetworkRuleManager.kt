package com.example.ghostdownloader.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager

object NetworkRuleManager {

    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    fun isCellularConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun getBatteryLevel(context: Context): Int {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) {
            (level * 100 / scale)
        } else {
            100
        }
    }

    fun isBatteryCharging(context: Context): Boolean {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter)
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    fun evaluateDownloadPolicy(
        context: Context,
        wifiOnly: Boolean,
        stopOnLowBattery: Boolean
    ): PolicyResult {
        if (wifiOnly && !isWifiConnected(context)) {
            val isCellular = isCellularConnected(context)
            val msg = if (isCellular) "Paused by Wi-Fi Only Rule (Cellular Guard Active)" else "Paused (No Wi-Fi Connection)"
            return PolicyResult(allowed = false, reason = msg, isWifiBlocked = true)
        }

        if (stopOnLowBattery && !isBatteryCharging(context)) {
            val battery = getBatteryLevel(context)
            if (battery in 1..15) {
                return PolicyResult(allowed = false, reason = "Paused: Low Battery ($battery%)", isBatteryBlocked = true)
            }
        }

        return PolicyResult(allowed = true, reason = null)
    }

    data class PolicyResult(
        val allowed: Boolean,
        val reason: String?,
        val isWifiBlocked: Boolean = false,
        val isBatteryBlocked: Boolean = false
    )
}
