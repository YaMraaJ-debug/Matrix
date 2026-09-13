package com.example.ghostdownloader.ui.dialogs

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.data.model.TaskPriority

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (url: String, name: String, priority: TaskPriority, connections: Int) -> Unit
) {
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.NORMAL) }
    var connections by remember { mutableFloatStateOf(16f) }

    fun pasteFromClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
        if (!clip.isNullOrBlank()) {
            url = clip.trim()
            if (fileName.isBlank()) {
                val clean = url.substringBefore('?').substringBefore('#')
                fileName = clean.substringAfterLast('/')
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Download Task", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // URL input with Paste button
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        if (fileName.isBlank() && it.isNotBlank()) {
                            val clean = it.substringBefore('?').substringBefore('#')
                            val segment = clean.substringAfterLast('/')
                            if (segment.isNotBlank()) fileName = segment
                        }
                    },
                    label = { Text("Download URL or Magnet Link") },
                    placeholder = { Text("https://... or magnet:?xt=...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_url_input"),
                    trailingIcon = {
                        IconButton(onClick = { pasteFromClipboard() }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste from clipboard")
                        }
                    },
                    maxLines = 3
                )

                // Optional Custom File Name
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (Optional)") },
                    placeholder = { Text("e.g. package.zip") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_name_input"),
                    singleLine = true
                )

                // Priority Selection
                Text("Task Priority", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.name) },
                            modifier = Modifier.testTag("priority_${priority.name.lowercase()}")
                        )
                    }
                }

                // Connections Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Parallel Connections", style = MaterialTheme.typography.labelMedium)
                        Text("${connections.toInt()} Threads", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = connections,
                        onValueChange = { connections = it },
                        valueRange = 1f..32f,
                        steps = 30,
                        modifier = Modifier.testTag("connections_slider")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        onConfirm(url, fileName, selectedPriority, connections.toInt())
                    }
                },
                enabled = url.isNotBlank(),
                modifier = Modifier.testTag("create_task_confirm_button")
            ) {
                Text("Start Download")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
