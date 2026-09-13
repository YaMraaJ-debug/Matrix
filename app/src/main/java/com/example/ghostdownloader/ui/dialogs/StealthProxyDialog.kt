package com.example.ghostdownloader.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.AppSettings
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberRed
import com.example.ghostdownloader.ui.theme.CyberTeal

@Composable
fun StealthProxyDialog(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSaveConfig: (proxyMode: String, proxyHost: String, proxyPort: Int, userAgent: String) -> Unit
) {
    val context = LocalContext.current
    var selectedProxyMode by remember(settings) { mutableStateOf(settings.proxyMode) } // "none", "http", "socks5", "tor"
    var host by remember(settings) { mutableStateOf(settings.proxyHost) }
    var port by remember(settings) { mutableStateOf(settings.proxyPort.toString()) }

    var selectedUserAgentPreset by remember {
        mutableStateOf("Chrome Desktop (Windows 11)")
    }

    val userAgentOptions = listOf(
        Pair("Chrome Desktop", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"),
        Pair("Mac Safari", "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15"),
        Pair("IDM Spoofer", "Mozilla/5.0 (Windows NT 10.0; WOW64) Internet Download Manager/6.42"),
        Pair("Curl / Wget", "curl/8.4.0 (x86_64-pc-linux-gnu) libcurl/8.4.0"),
        Pair("iPhone iOS", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148 Safari/604.1")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberPurple.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Stealth & Proxy Shield",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Tor / SOCKS5 proxy & User-Agent spoofer",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mode Select Chips
                Text("Proxy Gateway:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        Pair("none", "Direct (None)"),
                        Pair("tor", "Tor (Orbot)"),
                        Pair("socks5", "SOCKS5"),
                        Pair("http", "HTTP Proxy")
                    ).forEach { (modeKey, label) ->
                        FilterChip(
                            selected = selectedProxyMode == modeKey,
                            onClick = {
                                selectedProxyMode = modeKey
                                if (modeKey == "tor") {
                                    host = "127.0.0.1"
                                    port = "9050"
                                } else if (modeKey == "http") {
                                    port = "8080"
                                } else if (modeKey == "socks5") {
                                    port = "1080"
                                }
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.testTag("proxy_mode_$modeKey")
                        )
                    }
                }

                if (selectedProxyMode != "none") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = host,
                                onValueChange = { host = it },
                                label = { Text("Proxy Host / IP") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = port,
                                onValueChange = { port = it },
                                label = { Text("Proxy Port") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (selectedProxyMode == "tor") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberPurple.copy(alpha = 0.15f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        "Routing downloads through Orbot / Tor Onion Network (SOCKS 127.0.0.1:9050). IP is concealed.",
                                        fontSize = 11.sp,
                                        color = CyberPurple,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // User-Agent Spoofer Section
                Text("User-Agent Spoofer (Bypass Site Blocks):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    userAgentOptions.forEach { (name, uaString) ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedUserAgentPreset == name) CyberBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (selectedUserAgentPreset == name) CyberBlueLight else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text(
                                        uaString,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                FilterChip(
                                    selected = selectedUserAgentPreset == name,
                                    onClick = { selectedUserAgentPreset = name },
                                    label = { Text(if (selectedUserAgentPreset == name) "Active" else "Select", fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = port.toIntOrNull() ?: 10809
                    val ua = userAgentOptions.firstOrNull { it.first == selectedUserAgentPreset }?.second ?: ""
                    onSaveConfig(selectedProxyMode, host, p, ua)
                    Toast.makeText(context, "Stealth shield & proxy saved!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                modifier = Modifier.testTag("save_proxy_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apply Shield", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
