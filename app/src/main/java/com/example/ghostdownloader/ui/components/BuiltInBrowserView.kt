package com.example.ghostdownloader.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ghostdownloader.data.model.CategoryType
import com.example.ghostdownloader.data.model.MediaResource
import com.example.ghostdownloader.data.model.ProtocolType
import com.example.ghostdownloader.ui.theme.CyberBlue
import com.example.ghostdownloader.ui.theme.CyberBlueLight
import com.example.ghostdownloader.ui.theme.CyberGreen
import com.example.ghostdownloader.ui.theme.CyberPurple
import com.example.ghostdownloader.ui.theme.CyberTeal
import com.example.ghostdownloader.utils.Formatters

data class SniffedLink(
    val id: String,
    val url: String,
    val name: String,
    val extension: String,
    val category: CategoryType,
    val protocol: ProtocolType,
    val estimatedBytes: Long = (10_000_000L..450_000_000L).random()
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BuiltInBrowserView(
    onDownloadUrl: (url: String, name: String, protocol: ProtocolType, category: CategoryType) -> Unit,
    onPreviewMedia: (title: String, url: String, isVideo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf("https://archive.org") }
    var inputUrl by remember { mutableStateOf("https://archive.org") }
    var isLoading by remember { mutableStateOf(false) }
    var showSnifferSheet by remember { mutableStateOf(false) }

    val sniffedLinks = remember {
        mutableStateListOf(
            SniffedLink("sniff-1", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "BigBuckBunny.mp4", "mp4", CategoryType.VIDEO, ProtocolType.HTTP, 158_000_000L),
            SniffedLink("sniff-2", "https://cdimage.debian.org/debian-cd/current/amd64/iso-cd/debian-12.7.0-netinst.iso", "debian-12.7.0-netinst.iso", "iso", CategoryType.SOFTWARE, ProtocolType.HTTP, 734_000_000L),
            SniffedLink("sniff-3", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "HLS_BigBuckBunny_Master.m3u8", "m3u8", CategoryType.VIDEO, ProtocolType.M3U8, 220_000_000L)
        )
    }

    val mediaRegex = remember {
        Regex(".*\\.(mp4|mkv|webm|m3u8|mp3|wav|flac|aac|apk|iso|zip|tar\\.gz|rar|pdf)(\\?.*)?$", RegexOption.IGNORE_CASE)
    }

    fun inspectUrlAndAdd(url: String) {
        if (url.startsWith("data:") || url.startsWith("blob:") || url.length < 5) return
        if (sniffedLinks.any { it.url == url }) return

        if (url.startsWith("magnet:") || mediaRegex.matches(url)) {
            val fileName = url.substringBefore("?").substringAfterLast("/").ifBlank { "stream_asset.mp4" }
            val ext = fileName.substringAfterLast(".", "mp4").lowercase()
            val cat = when (ext) {
                "mp4", "mkv", "webm", "m3u8" -> CategoryType.VIDEO
                "mp3", "wav", "flac", "aac" -> CategoryType.MUSIC
                "apk", "iso" -> CategoryType.SOFTWARE
                "zip", "tar.gz", "rar" -> CategoryType.ARCHIVE
                else -> CategoryType.DOCUMENT
            }
            val proto = when {
                url.startsWith("magnet:") -> ProtocolType.TORRENT
                ext == "m3u8" -> ProtocolType.M3U8
                else -> ProtocolType.HTTP
            }
            sniffedLinks.add(0, SniffedLink("sniff-${System.currentTimeMillis()}", url, fileName, ext, cat, proto))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Browser Address Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { webViewInstance?.goBack() },
                    enabled = webViewInstance?.canGoBack() == true,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = { webViewInstance?.goForward() },
                    enabled = webViewInstance?.canGoForward() == true,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Forward", modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = { webViewInstance?.reload() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                }

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    placeholder = { Text("Search or enter web URL", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            IconButton(
                                onClick = {
                                    val target = if (inputUrl.startsWith("http://") || inputUrl.startsWith("https://")) {
                                        inputUrl
                                    } else {
                                        "https://www.google.com/search?q=" + inputUrl.trim()
                                    }
                                    currentUrl = target
                                    webViewInstance?.loadUrl(target)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Go", tint = CyberBlueLight)
                            }
                        }
                    }
                )

                IconButton(
                    onClick = {
                        currentUrl = "https://archive.org"
                        inputUrl = "https://archive.org"
                        webViewInstance?.loadUrl("https://archive.org")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(18.dp))
                }
            }

            // Quick Bookmark Launch Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("Archive.org", "https://archive.org"),
                    Pair("FreeSound", "https://freesound.org"),
                    Pair("Sample Video", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
                    Pair("Debian ISO", "https://cdimage.debian.org"),
                    Pair("GitHub Releases", "https://github.com/trending")
                ).forEach { (label, url) ->
                    FilterChip(
                        selected = currentUrl == url,
                        onClick = {
                            currentUrl = url
                            inputUrl = url
                            webViewInstance?.loadUrl(url)
                        },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            // Android WebView Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 GhostDownloader/3.0"

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    url?.let {
                                        inputUrl = it
                                        inspectUrlAndAdd(it)
                                    }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    url?.let {
                                        inspectUrlAndAdd(it)
                                    }
                                }

                                override fun onLoadResource(view: WebView?, url: String?) {
                                    super.onLoadResource(view, url)
                                    url?.let { inspectUrlAndAdd(it) }
                                }

                                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                                    request?.url?.toString()?.let { inspectUrlAndAdd(it) }
                                    return super.shouldInterceptRequest(view, request)
                                }
                            }
                            webViewInstance = this
                            loadUrl(currentUrl)
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    }
                )
            }
        }

        // Floating Animated Sniffer Catch Badge
        FloatingActionButton(
            onClick = { showSnifferSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("floating_sniffer_fab"),
            containerColor = CyberBlue,
            contentColor = Color.Black
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Radar, contentDescription = "Sniffer", tint = Color.Black, modifier = Modifier.size(20.dp))
                Text(
                    text = "Sniffer (${sniffedLinks.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }

        // Sniffer Bottom Action Sheet
        if (showSnifferSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSnifferSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = CyberBlueLight)
                            Text("Captured Downloads (${sniffedLinks.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = {
                                sniffedLinks.forEach { link ->
                                    onDownloadUrl(link.url, link.name, link.protocol, link.category)
                                }
                                showSnifferSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Grab All (${sniffedLinks.size})", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sniffedLinks, key = { it.id }) { link ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = link.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberBlue.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = link.protocol.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberBlueLight
                                            )
                                        }
                                    }

                                    Text(
                                        text = link.url,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = Formatters.formatBytes(link.estimatedBytes),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CyberTeal
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (link.category == CategoryType.VIDEO || link.category == CategoryType.MUSIC) {
                                                FilledTonalButton(
                                                    onClick = {
                                                        onPreviewMedia(link.name, link.url, link.category == CategoryType.VIDEO)
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(30.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Preview", fontSize = 11.sp)
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    onDownloadUrl(link.url, link.name, link.protocol, link.category)
                                                    showSnifferSheet = false
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Download", fontSize = 11.sp)
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
