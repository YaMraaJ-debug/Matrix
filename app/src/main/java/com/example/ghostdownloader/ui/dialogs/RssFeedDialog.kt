package com.example.ghostdownloader.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ghostdownloader.data.model.RssFeedSubscription
import com.example.ghostdownloader.ui.theme.CyberAmber

@Composable
fun RssFeedDialog(
    subscriptions: List<RssFeedSubscription>,
    onDismiss: () -> Unit,
    onAddSubscription: (title: String, url: String, autoDownload: Boolean, filter: String) -> Unit,
    onDownloadItem: (url: String, title: String) -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }
    var feedTitle by remember { mutableStateOf("") }
    var feedUrl by remember { mutableStateOf("") }
    var feedFilter by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RssFeed, contentDescription = null, tint = CyberAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RSS Feed Feeder", fontWeight = FontWeight.Bold)
                }
                if (!isAddingNew) {
                    IconButton(onClick = { isAddingNew = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Feed")
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                if (isAddingNew) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Add New RSS Subscription", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = feedTitle,
                            onValueChange = { feedTitle = it },
                            label = { Text("Feed Title") },
                            placeholder = { Text("e.g. Linux ISO Tracker") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = feedUrl,
                            onValueChange = { feedUrl = it },
                            label = { Text("RSS / Atom Feed URL") },
                            placeholder = { Text("https://distrowatch.com/news/dwd.xml") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = feedFilter,
                            onValueChange = { feedFilter = it },
                            label = { Text("Filter Regex (Optional)") },
                            placeholder = { Text(".*(Ubuntu|Arch).*") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { isAddingNew = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (feedTitle.isNotBlank() && feedUrl.isNotBlank()) {
                                        onAddSubscription(feedTitle, feedUrl, true, feedFilter)
                                        isAddingNew = false
                                        feedTitle = ""
                                        feedUrl = ""
                                        feedFilter = ""
                                    }
                                },
                                enabled = feedTitle.isNotBlank() && feedUrl.isNotBlank()
                            ) {
                                Text("Subscribe")
                            }
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(subscriptions) { sub ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(sub.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberAmber.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Auto", fontSize = 10.sp, color = CyberAmber, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Feed items
                                    sub.items.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.title, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(item.pubDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            FilledTonalButton(
                                                onClick = { onDownloadItem(item.enclosureUrl ?: item.link, item.title) },
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Download", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
