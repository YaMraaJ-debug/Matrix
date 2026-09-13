package com.example.ghostdownloader.utils

import android.os.Build
import android.os.Process
import java.security.MessageDigest
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PlatformRequirement(
    val platformName: String,
    val iconType: String,
    val requiredVersion: String,
    val architectures: String,
    val engineBackend: String,
    val statusText: String,
    val isCurrentDevice: Boolean = false,
    val notes: String = ""
)

data class DeviceArchitectureProfile(
    val osName: String,
    val osVersion: String,
    val apiLevel: Int,
    val isAndroid9Plus: Boolean,
    val primaryAbi: String,
    val supportedAbis: List<String>,
    val is64Bit: Boolean,
    val cpuCores: Int,
    val maxHeapMemoryMb: Long,
    val neonAccelerationActive: Boolean,
    val recommendedBufferSizeBytes: Int,
    val recommendedThreads: Int,
    val architectureLabel: String
)

data class BenchmarkResult(
    val hashingThroughputMbPerSec: Double,
    val memoryThroughputMbPerSec: Double,
    val coroutineDispatchLatencyMs: Long,
    val totalScore: Int,
    val grade: String
)

object PlatformArchitectureManager {

    val supportedPlatforms: List<PlatformRequirement> by lazy {
        listOf(
            PlatformRequirement(
                platformName = "Windows",
                iconType = "windows",
                requiredVersion = "Windows 10+ (Build 19041+)",
                architectures = "x86_64 / arm64",
                engineBackend = "Win32 IOCP + Aria2 64-bit Asynchronous",
                statusText = "Fully Compatible",
                notes = "Supports Windows 10/11 x64 and Snapdragon ARM64 devices"
            ),
            PlatformRequirement(
                platformName = "macOS",
                iconType = "apple",
                requiredVersion = "macOS 13.0+ (Ventura+)",
                architectures = "x86_64 / arm64",
                engineBackend = "Apple Darwin kqueue + NEON / Accelerate framework",
                statusText = "Fully Compatible",
                notes = "Universal binary targeting Intel x86_64 and Apple Silicon (M1–M4)"
            ),
            PlatformRequirement(
                platformName = "Linux",
                iconType = "linux",
                requiredVersion = "glibc 2.35+ (Ubuntu 22.04+, Debian 12+, Arch)",
                architectures = "x86_64 / arm64",
                engineBackend = "Linux epoll / io_uring native engine",
                statusText = "Fully Compatible",
                notes = "CLI daemon, systemd unit, and WebUI RPC controller"
            ),
            PlatformRequirement(
                platformName = "Android",
                iconType = "android",
                requiredVersion = "Android 9.0+ (API 28+)",
                architectures = "arm64-v8a / armeabi-v7a (armv7)",
                engineBackend = "Jetpack Compose + OkHttp Non-blocking + NEON SIMD",
                statusText = "Active on this Device",
                isCurrentDevice = true,
                notes = "Native 64-bit and 32-bit dual-ABI targeting with adaptive buffer sizing"
            )
        )
    }

    fun getDeviceProfile(): DeviceArchitectureProfile {
        val primaryAbi = if (Build.SUPPORTED_ABIS.isNotEmpty()) Build.SUPPORTED_ABIS[0] else "unknown"
        val supportedAbis = Build.SUPPORTED_ABIS.toList()
        val is64 = Process.is64Bit()
        val apiLevel = Build.VERSION.SDK_INT
        val isAndroid9Plus = apiLevel >= 28 // Android 9.0 Pie is API 28

        val isArm64 = primaryAbi.contains("arm64", ignoreCase = true)
        val isArmv7 = primaryAbi.contains("armeabi-v7a", ignoreCase = true) || primaryAbi.contains("armv7", ignoreCase = true)

        val archLabel = when {
            isArm64 -> "arm64-v8a (64-bit ARM)"
            isArmv7 -> "armeabi-v7a (32-bit ARMv7)"
            primaryAbi.contains("x86_64", ignoreCase = true) -> "x86_64 (64-bit Intel/AMD)"
            primaryAbi.contains("x86", ignoreCase = true) -> "x86 (32-bit Intel)"
            else -> primaryAbi
        }

        // Buffer sizing optimized for architecture:
        // arm64-v8a: 8 MB chunk buffer for maximum throughput
        // armeabi-v7a: 1 MB chunk buffer to preserve 32-bit native virtual address space
        val recommendedBuffer = when {
            isArm64 -> 8 * 1024 * 1024
            isArmv7 -> 1024 * 1024
            is64 -> 8 * 1024 * 1024
            else -> 2 * 1024 * 1024
        }

        val recommendedThreads = when {
            isArm64 -> 16
            isArmv7 -> 8
            is64 -> 16
            else -> 8
        }

        val cores = Runtime.getRuntime().availableProcessors()
        val maxMemoryMb = Runtime.getRuntime().maxMemory() / (1024 * 1024)

        return DeviceArchitectureProfile(
            osName = "Android",
            osVersion = Build.VERSION.RELEASE,
            apiLevel = apiLevel,
            isAndroid9Plus = isAndroid9Plus,
            primaryAbi = primaryAbi,
            supportedAbis = supportedAbis,
            is64Bit = is64,
            cpuCores = cores,
            maxHeapMemoryMb = maxMemoryMb,
            neonAccelerationActive = isArm64 || isArmv7,
            recommendedBufferSizeBytes = recommendedBuffer,
            recommendedThreads = recommendedThreads,
            architectureLabel = archLabel
        )
    }

    suspend fun runArchitectureBenchmark(): BenchmarkResult = withContext(Dispatchers.Default) {
        // 1. Memory throughput test (allocate and cycle a 16MB buffer)
        val testBytes = ByteArray(16 * 1024 * 1024) { (it % 256).toByte() }
        val copyBytes = ByteArray(16 * 1024 * 1024)
        val memDuration = measureTimeMillis {
            repeat(4) {
                System.arraycopy(testBytes, 0, copyBytes, 0, testBytes.size)
            }
        }.coerceAtLeast(1)
        val totalMemTransferredMb = (16.0 * 4)
        val memThroughput = (totalMemTransferredMb / (memDuration / 1000.0))

        // 2. Hardware Hashing Throughput (SHA-256)
        val shaDuration = measureTimeMillis {
            val md = MessageDigest.getInstance("SHA-256")
            repeat(3) {
                md.update(testBytes)
            }
            md.digest()
        }.coerceAtLeast(1)
        val totalHashMb = 16.0 * 3
        val hashThroughput = (totalHashMb / (shaDuration / 1000.0))

        // 3. Coroutine / Dispatch Latency
        val latencyMs = measureTimeMillis {
            repeat(100) {
                withContext(Dispatchers.IO) {
                    val dummy = System.nanoTime()
                }
            }
        } / 100

        val score = ((hashThroughput * 2.5) + (memThroughput * 0.15) - (latencyMs * 2)).toInt().coerceIn(100, 9999)
        val grade = when {
            score > 1500 -> "A+ (Ultra Accelerated)"
            score > 800 -> "A (High Performance)"
            score > 400 -> "B (Optimized)"
            else -> "C (Standard)"
        }

        BenchmarkResult(
            hashingThroughputMbPerSec = (hashThroughput * 10).toInt() / 10.0,
            memoryThroughputMbPerSec = (memThroughput * 10).toInt() / 10.0,
            coroutineDispatchLatencyMs = latencyMs,
            totalScore = score,
            grade = grade
        )
    }
}
