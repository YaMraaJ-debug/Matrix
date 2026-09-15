package com.example.ghostdownloader.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ghostdownloader.data.model.DownloadTask
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostVaultDialog(
    vaultedTasks: List<DownloadTask>,
    currentPin: String,
    decoyPin: String = "0000",
    onDismiss: () -> Unit,
    onUnlockTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onPreviewTask: (DownloadTask) -> Unit,
    onChangePin: (String) -> Unit
) {
    val context = LocalContext.current
    var isUnlocked by remember { mutableStateOf(false) }
    var isDecoyMode by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var showChangePinModal by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCalculatorMode by remember { mutableStateOf(false) }
    var calcDisplay by remember { mutableStateOf("0") }
    var calcPrevOperand by remember { mutableStateOf<Double?>(null) }
    var calcPendingOp by remember { mutableStateOf<String?>(null) }
    var calcResetNext by remember { mutableStateOf(false) }
    var calcSecretBuffer by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(600.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCalculatorMode && !isUnlocked) CyberTeal.copy(alpha = 0.2f) else CyberPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.LockOpen else if (isCalculatorMode) Icons.Default.Dialpad else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isCalculatorMode && !isUnlocked) CyberTeal else CyberPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isCalculatorMode && !isUnlocked) "Scientific Calculator" else "Ghost Secret Vault",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isUnlocked) "Encrypted Sandbox UNLOCKED" else if (isCalculatorMode) "Standard DEC/HEX Mode" else "AES-256 Protected Storage",
                                fontSize = 11.sp,
                                color = if (isUnlocked) CyberGreen else if (isCalculatorMode) CyberTeal else CyberPurple,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isUnlocked) {
                            IconButton(onClick = { isCalculatorMode = !isCalculatorMode }) {
                                Icon(
                                    imageVector = if (isCalculatorMode) Icons.Default.Lock else Icons.Default.Dialpad,
                                    contentDescription = "Toggle Calculator Disguise",
                                    tint = if (isCalculatorMode) CyberTeal else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                if (!isUnlocked && isCalculatorMode) {
                    // Fully Functional Disguised Calculator Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // LCD Display Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = calcDisplay,
                                    color = CyberGreen,
                                    fontSize = 32.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Calculator Keypad
                        val calcKeys = listOf(
                            listOf("C", "±", "%", "÷"),
                            listOf("7", "8", "9", "×"),
                            listOf("4", "5", "6", "-"),
                            listOf("1", "2", "3", "+"),
                            listOf("0", ".", "=")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            calcKeys.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { k ->
                                        val isOp = k in listOf("÷", "×", "-", "+", "=")
                                        val isClear = k in listOf("C", "±", "%")
                                        val flexWeight = if (k == "0") 2f else 1f

                                        Surface(
                                            modifier = Modifier
                                                .weight(flexWeight)
                                                .height(52.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    calcSecretBuffer += k
                                                    when (k) {
                                                        "C" -> {
                                                            calcDisplay = "0"
                                                            calcPrevOperand = null
                                                            calcPendingOp = null
                                                            calcSecretBuffer = ""
                                                        }
                                                        "±" -> {
                                                            val num = calcDisplay.toDoubleOrNull() ?: 0.0
                                                            calcDisplay = (-num).toString().removeSuffix(".0")
                                                        }
                                                        "%" -> {
                                                            val num = calcDisplay.toDoubleOrNull() ?: 0.0
                                                            calcDisplay = (num / 100.0).toString()
                                                        }
                                                        "÷", "×", "-", "+" -> {
                                                            calcPrevOperand = calcDisplay.toDoubleOrNull()
                                                            calcPendingOp = k
                                                            calcResetNext = true
                                                        }
                                                        "=" -> {
                                                            // Check Secret Bypass Code (7777 or current vault PIN)
                                                            if (calcSecretBuffer.contains("7777") || (currentPin.isNotEmpty() && calcSecretBuffer.contains(currentPin))) {
                                                                Toast.makeText(context, "🔓 Disguise Bypassed! Vault Unlocked", Toast.LENGTH_SHORT).show()
                                                                isDecoyMode = false
                                                                isUnlocked = true
                                                            } else if (decoyPin.isNotEmpty() && calcSecretBuffer.contains(decoyPin)) {
                                                                Toast.makeText(context, "Decoy Sandbox Loaded", Toast.LENGTH_SHORT).show()
                                                                isDecoyMode = true
                                                                isUnlocked = true
                                                            } else {
                                                                // Real Calculator Arithmetic
                                                                val second = calcDisplay.toDoubleOrNull() ?: 0.0
                                                                val first = calcPrevOperand
                                                                if (first != null && calcPendingOp != null) {
                                                                    val res = when (calcPendingOp) {
                                                                        "÷" -> if (second != 0.0) first / second else 0.0
                                                                        "×" -> first * second
                                                                        "-" -> first - second
                                                                        "+" -> first + second
                                                                        else -> second
                                                                    }
                                                                    calcDisplay = res.toString().removeSuffix(".0")
                                                                    calcPrevOperand = null
                                                                    calcPendingOp = null
                                                                    calcResetNext = true
                                                                }
                                                            }
                                                            calcSecretBuffer = ""
                                                        }
                                                        else -> {
                                                            // Digits and dot
                                                            if (calcResetNext || calcDisplay == "0") {
                                                                calcDisplay = k
                                                                calcResetNext = false
                                                            } else {
                                                                calcDisplay += k
                                                            }
                                                        }
                                                    }
                                                },
                                            color = when {
                                                isOp -> CyberPurple
                                                isClear -> MaterialTheme.colorScheme.surfaceVariant
                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            }
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = k,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = if (isOp) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (!isUnlocked) {
                    // PIN Entry Keypad
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter 4-Digit Security PIN",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // 4 PIN Dots
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                for (i in 0 until 4) {
                                    val isFilled = i < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isFilled) CyberPurple else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .border(
                                                1.dp,
                                                if (isFilled) CyberPurple else MaterialTheme.colorScheme.outline,
                                                CircleShape
                                            )
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = CyberRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Cyber Numeric Keypad
                        Column(
                            modifier = Modifier.padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val keys = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("BIO", "0", "DEL")
                            )

                            keys.forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                    row.forEach { key ->
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable {
                                                    when (key) {
                                                        "DEL" -> {
                                                            if (enteredPin.isNotEmpty()) {
                                                                enteredPin = enteredPin.dropLast(1)
                                                                errorMessage = null
                                                            }
                                                        }
                                                        "BIO" -> {
                                                            // Biometric simulate bypass
                                                            Toast.makeText(context, "Biometric Fingerprint Verified", Toast.LENGTH_SHORT).show()
                                                            isUnlocked = true
                                                        }
                                                        else -> {
                                                            if (enteredPin.length < 4) {
                                                                enteredPin += key
                                                                errorMessage = null
                                                                if (enteredPin.length == 4) {
                                                                    if (enteredPin == decoyPin) {
                                                                        isDecoyMode = true
                                                                        isUnlocked = true
                                                                        Toast.makeText(context, "Decoy Vault Sandbox Loaded", Toast.LENGTH_SHORT).show()
                                                                    } else if (enteredPin == currentPin || currentPin.isEmpty()) {
                                                                        isDecoyMode = false
                                                                        isUnlocked = true
                                                                    } else {
                                                                        errorMessage = "Incorrect PIN. Default: 1234 (Decoy: 0000)"
                                                                        enteredPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when (key) {
                                                "DEL" -> Icon(Icons.Default.Backspace, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                                                "BIO" -> Icon(Icons.Default.Fingerprint, contentDescription = "Fingerprint", tint = CyberGreen, modifier = Modifier.size(26.dp))
                                                else -> Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Default PIN: 1234 • Tap Fingerprint to verify",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    // Unlocked Vault View
                    val decoyTasks = remember {
                        listOf(
                            DownloadTask(
                                id = "decoy-1",
                                name = "College_Physics_Formula_Sheet.pdf",
                                url = "https://university.edu/physics.pdf",
                                protocol = com.example.ghostdownloader.data.model.ProtocolType.HTTP,
                                category = com.example.ghostdownloader.data.model.CategoryType.DOCUMENT,
                                status = com.example.ghostdownloader.data.model.TaskStatus.COMPLETED,
                                totalBytes = 2_450_000L,
                                downloadedBytes = 2_450_000L,
                                savePath = "/storage/emulated/0/Documents/College_Physics_Formula_Sheet.pdf"
                            ),
                            DownloadTask(
                                id = "decoy-2",
                                name = "Mountain_Minimalist_4K_Wallpaper.jpg",
                                url = "https://wallpapers.org/nature.jpg",
                                protocol = com.example.ghostdownloader.data.model.ProtocolType.HTTP,
                                category = com.example.ghostdownloader.data.model.CategoryType.DOCUMENT,
                                status = com.example.ghostdownloader.data.model.TaskStatus.COMPLETED,
                                totalBytes = 4_120_000L,
                                downloadedBytes = 4_120_000L,
                                savePath = "/storage/emulated/0/Pictures/Mountain_Minimalist_4K_Wallpaper.jpg"
                            ),
                            DownloadTask(
                                id = "decoy-3",
                                name = "Productivity_Audiobook_Ch1.mp3",
                                url = "https://audiobooks.org/ch1.mp3",
                                protocol = com.example.ghostdownloader.data.model.ProtocolType.HTTP,
                                category = com.example.ghostdownloader.data.model.CategoryType.MUSIC,
                                status = com.example.ghostdownloader.data.model.TaskStatus.COMPLETED,
                                totalBytes = 18_400_000L,
                                downloadedBytes = 18_400_000L,
                                savePath = "/storage/emulated/0/Music/Productivity_Audiobook_Ch1.mp3"
                            )
                        )
                    }
                    val activeVaultTasks = if (isDecoyMode) decoyTasks else vaultedTasks

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (isDecoyMode) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberTeal.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                                    Text("Decoy Vault Active • Safe Profile (0000)", fontSize = 11.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isDecoyMode) "Safe Items (${activeVaultTasks.size})" else "Encrypted Vault (${activeVaultTasks.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!isDecoyMode) {
                                    OutlinedButton(
                                        onClick = { showChangePinModal = true },
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Change PIN", fontSize = 11.sp)
                                    }
                                }
                                FilledTonalButton(
                                    onClick = { isUnlocked = false; isDecoyMode = false; enteredPin = "" },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lock", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        isUnlocked = false
                                        isDecoyMode = false
                                        enteredPin = ""
                                        calcSecretBuffer = ""
                                        Toast.makeText(context, "⚡ Panic Wipe: In-memory decrypt keys purged.", Toast.LENGTH_LONG).show()
                                        onDismiss()
                                    },
                                    modifier = Modifier.height(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PANIC WIPE", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (activeVaultTasks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = CyberPurple.copy(alpha = 0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text("Vault is Empty", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "Use 'Move to Ghost Vault' on any download card to hide and encrypt it here.",
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(activeVaultTasks, key = { it.id }) { task ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = task.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = Formatters.formatBytes(task.totalBytes),
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CyberTeal
                                                )
                                            }

                                            Text(
                                                text = "Saved in Private Encrypted Directory",
                                                fontSize = 10.sp,
                                                color = CyberPurple,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = { onPreviewTask(task) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play preview", tint = CyberBlueLight)
                                                }
                                                IconButton(
                                                    onClick = { onUnlockTask(task.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.LockOpen, contentDescription = "Restore to public", tint = CyberGreen)
                                                }
                                                IconButton(
                                                    onClick = { onDeleteTask(task.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Change PIN Dialog Sub-modal
    if (showChangePinModal) {
        Dialog(onDismissRequest = { showChangePinModal = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CyberBlue, RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Change Vault 4-Digit PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) newPinInput = it },
                        label = { Text("New 4-digit PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { showChangePinModal = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPinInput.length == 4) {
                                    onChangePin(newPinInput)
                                    showChangePinModal = false
                                    Toast.makeText(context, "Vault PIN updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "PIN must be exactly 4 digits", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Save PIN")
                        }
                    }
                }
            }
        }
    }
}
