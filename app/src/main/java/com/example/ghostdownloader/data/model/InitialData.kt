package com.example.ghostdownloader.data.model

object InitialData {

    private fun generateChunks(totalBytes: Long, downloadedBytes: Long, count: Int): List<DownloadChunk> {
        val chunkSize = totalBytes / count
        var remainingDownloaded = downloadedBytes
        return (0 until count).map { i ->
            val startByte = i * chunkSize
            val endByte = if (i == count - 1) totalBytes else (i + 1) * chunkSize - 1
            val thisChunkTotal = endByte - startByte + 1

            val (chunkDownloaded, status, speed) = when {
                remainingDownloaded >= thisChunkTotal -> {
                    remainingDownloaded -= thisChunkTotal
                    Triple(thisChunkTotal, "completed", 0L)
                }
                remainingDownloaded > 0 -> {
                    val d = remainingDownloaded
                    remainingDownloaded = 0
                    Triple(d, "active", (400000L..900000L).random())
                }
                else -> {
                    Triple(0L, "active", (200000L..600000L).random())
                }
            }

            DownloadChunk(
                id = i,
                startByte = startByte,
                endByte = endByte,
                downloadedBytes = chunkDownloaded,
                speed = speed,
                status = status
            )
        }
    }

    val tasks: List<DownloadTask> = listOf(
        DownloadTask(
            id = "task-1",
            name = "ubuntu-24.04-desktop-amd64.iso",
            url = "https://releases.ubuntu.com/24.04/ubuntu-24.04-desktop-amd64.iso",
            protocol = ProtocolType.HTTP,
            category = CategoryType.SOFTWARE,
            status = TaskStatus.DOWNLOADING,
            priority = TaskPriority.HIGH,
            totalBytes = 5046586368L,
            downloadedBytes = 2422361456L,
            speed = 19451084L,
            uploadSpeed = 0L,
            etaSeconds = 135L,
            connections = 16,
            chunks = generateChunks(5046586368L, 2422361456L, 16),
            savePath = "/storage/emulated/0/Download/Ghost/Software/ubuntu-24.04-desktop-amd64.iso",
            md5Hash = "8b7fca29104bde978cf2340156ef9a82",
            sha256Hash = "a9b2c89f5643e21019d38cbf9012354a8b7fca29104bde978cf2340156ef9a82",
            mimeType = "application/x-iso9660-image",
            referer = "https://releases.ubuntu.com/24.04/",
            smartTags = listOf("Software", "Linux OS", "x86_64"),
            mirrorUrls = listOf(
                "https://mirrors.edge.kernel.org/ubuntu-releases/24.04/ubuntu-24.04-desktop-amd64.iso",
                "https://mirror.ox.ac.uk/sites/releases.ubuntu.com/24.04/ubuntu-24.04-desktop-amd64.iso",
                "https://quantum-mirror.hu/mirrors/pub/ubuntu-releases/24.04/ubuntu-24.04-desktop-amd64.iso"
            ),
            activeMirror = "Cloudflare Global Edge CDN (24ms)",
            headers = mapOf(
                "Accept-Ranges" to "bytes",
                "Server" to "Apache/2.4.52 (Ubuntu)",
                "Connection" to "keep-alive"
            )
        ),
        DownloadTask(
            id = "task-2",
            name = "blender-4.3.2-windows-x64.msi",
            url = "https://github.com/blender/blender/releases/download/v4.3.2/blender-4.3.2-windows-x64.msi",
            protocol = ProtocolType.GITHUB,
            category = CategoryType.SOFTWARE,
            status = TaskStatus.DOWNLOADING,
            priority = TaskPriority.NORMAL,
            totalBytes = 377487360L,
            downloadedBytes = 313314508L,
            speed = 13107200L,
            uploadSpeed = 0L,
            etaSeconds = 5L,
            connections = 8,
            chunks = generateChunks(377487360L, 313314508L, 8),
            savePath = "/storage/emulated/0/Download/Ghost/Software/blender-4.3.2-windows-x64.msi",
            md5Hash = "43cb6e11899a19c35b80a13d7890bfa2",
            sha256Hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            mimeType = "application/x-msi",
            mirror = "GitCode / CNB Accelerator",
            headers = mapOf(
                "X-Mirror-Source" to "cnb.cool-mirror",
                "Accept-Ranges" to "bytes"
            )
        ),
        DownloadTask(
            id = "task-3",
            name = "Big_Buck_Bunny_4K_60fps_Surround.mkv",
            url = "magnet:?xt=urn:btih:dd8255ecdc7ca55fb0bbf81323d87062db1f6d1c&dn=Big+Buck+Bunny+4K",
            protocol = ProtocolType.TORRENT,
            category = CategoryType.VIDEO,
            status = TaskStatus.DOWNLOADING,
            priority = TaskPriority.HIGH,
            totalBytes = 943718400L,
            downloadedBytes = 396361728L,
            speed = 9646899L,
            uploadSpeed = 1258291L,
            etaSeconds = 56L,
            connections = 42,
            peers = 42,
            seeders = 19,
            chunks = generateChunks(943718400L, 396361728L, 20),
            savePath = "/storage/emulated/0/Download/Ghost/Torrents/Big_Buck_Bunny_4K_60fps_Surround.mkv",
            mimeType = "video/x-matroska",
            smartTags = listOf("Movies", "4K HDR", "Surround 5.1"),
            hasSubtitles = true,
            trackers = listOf(
                TorrentTracker("trk-1", "udp://tracker.opentrackr.org:1337/announce", "active", 19, 23, 4892L, "32s ago"),
                TorrentTracker("trk-2", "udp://open.demonii.com:1337/announce", "active", 14, 18, 3201L, "1m ago"),
                TorrentTracker("trk-3", "https://tracker.torrent.eu.org:451/announce", "announcing", 9, 12, 1940L, "Just now")
            ),
            connectedPeers = listOf(
                TorrentPeer("peer-1", "198.51.100.42:6881", "US", "qBittorrent/4.6.3", 2840000L, 420000L, 88, "uX"),
                TorrentPeer("peer-2", "203.0.113.15:51413", "JP", "Transmission/4.0.5", 3120000L, 510000L, 100, "uE"),
                TorrentPeer("peer-3", "192.0.2.77:8999", "DE", "Deluge/2.1.1", 1920000L, 210000L, 64, "d")
            )
        ),
        DownloadTask(
            id = "task-4",
            name = "Cyberpunk_Edgerunners_Ep01_1080p.m3u8",
            url = "https://cdn.example.com/hls/stream_ep01/master.m3u8",
            protocol = ProtocolType.M3U8,
            category = CategoryType.VIDEO,
            status = TaskStatus.DOWNLOADING,
            priority = TaskPriority.NORMAL,
            totalBytes = 681574400L,
            downloadedBytes = 579338240L,
            speed = 8126464L,
            uploadSpeed = 0L,
            etaSeconds = 12L,
            connections = 8,
            chunks = generateChunks(681574400L, 579338240L, 16),
            savePath = "/storage/emulated/0/Download/Ghost/Videos/Cyberpunk_Edgerunners_Ep01_1080p.mp4",
            mimeType = "video/mp4",
            headers = mapOf(
                "User-Agent" to "GhostDownloader/3.0 HLS-Muxer",
                "Referer" to "https://anime.streaming.tv/watch/ep01"
            )
        ),
        DownloadTask(
            id = "task-5",
            name = "archlinux-2025.03.01-x86_64.iso",
            url = "https://geo.mirror.pkgbuild.com/iso/2025.03.01/archlinux-2025.03.01-x86_64.iso",
            protocol = ProtocolType.HTTP,
            category = CategoryType.SOFTWARE,
            status = TaskStatus.COMPLETED,
            priority = TaskPriority.NORMAL,
            totalBytes = 1184890880L,
            downloadedBytes = 1184890880L,
            speed = 0L,
            uploadSpeed = 0L,
            etaSeconds = 0L,
            connections = 16,
            chunks = generateChunks(1184890880L, 1184890880L, 16),
            createdAt = System.currentTimeMillis() - 86400000L,
            completedAt = System.currentTimeMillis() - 85200000L,
            savePath = "/storage/emulated/0/Download/Ghost/Software/archlinux-2025.03.01-x86_64.iso",
            md5Hash = "56d83cf461cb2849e7cfbd8109bfb511",
            sha256Hash = "9d6b7e289bf59d2a2c17be1f0545fbc31a61c33f2e1a3bc765c92c9b2f6ef3aa",
            mimeType = "application/x-iso9660-image"
        ),
        DownloadTask(
            id = "task-6",
            name = "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            url = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            protocol = ProtocolType.HUGGINGFACE,
            category = CategoryType.ARCHIVE,
            status = TaskStatus.PAUSED,
            priority = TaskPriority.LOW,
            totalBytes = 2021654528L,
            downloadedBytes = 852492288L,
            speed = 0L,
            uploadSpeed = 0L,
            etaSeconds = 0L,
            connections = 8,
            chunks = generateChunks(2021654528L, 852492288L, 16),
            savePath = "/storage/emulated/0/Download/Ghost/AI/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            mimeType = "application/octet-stream",
            smartTags = listOf("Software", "AI Model", "GGUF Quant")
        ),
        DownloadTask(
            id = "task-7",
            name = "Jujutsu_Kaisen_S02E23_1080p_DualAudio.mkv",
            url = "https://cdn.anime-stream.org/downloads/s02/jjk_s02e23_1080p.mkv",
            protocol = ProtocolType.HTTP,
            category = CategoryType.VIDEO,
            status = TaskStatus.DOWNLOADING,
            priority = TaskPriority.HIGH,
            totalBytes = 891289600L,
            downloadedBytes = 642289600L,
            speed = 14680064L,
            uploadSpeed = 0L,
            etaSeconds = 16L,
            connections = 16,
            chunks = generateChunks(891289600L, 642289600L, 16),
            savePath = "/storage/emulated/0/Download/Ghost/Anime/Jujutsu_Kaisen_S02E23_1080p_DualAudio.mkv",
            md5Hash = "d41d8cd98f00b204e9800998ecf8427e",
            sha256Hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            mimeType = "video/x-matroska",
            smartTags = listOf("Anime", "1080p", "Dual Audio", "Hindi/Jap"),
            hasSubtitles = true,
            isMeshShared = true,
            mirrorUrls = listOf(
                "https://edge-tokyo.animecdn.org/jjk/ep23.mkv",
                "https://edge-singapore.animecdn.org/jjk/ep23.mkv",
                "https://mirror-eu.animecdn.org/jjk/ep23.mkv"
            ),
            activeMirror = "Fastly Tokyo Edge CDN (18ms)"
        ),
        DownloadTask(
            id = "task-8",
            name = "Deep_Learning_System_Architecture_Study_Notes.pdf",
            url = "https://arxiv.org/pdf/cs.ai.2401.pdf",
            protocol = ProtocolType.HTTP,
            category = CategoryType.DOCUMENT,
            status = TaskStatus.COMPLETED,
            priority = TaskPriority.NORMAL,
            totalBytes = 48234496L,
            downloadedBytes = 48234496L,
            speed = 0L,
            uploadSpeed = 0L,
            etaSeconds = 0L,
            connections = 4,
            chunks = generateChunks(48234496L, 48234496L, 4),
            completedAt = System.currentTimeMillis() - 3600000L,
            savePath = "/storage/emulated/0/Download/Ghost/Study/Deep_Learning_System_Architecture_Study_Notes.pdf",
            md5Hash = "a1b2c3d4e5f67890123456789abcdef0",
            sha256Hash = "f0e1d2c3b4a5968778695a4b3c2d1e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d",
            mimeType = "application/pdf",
            smartTags = listOf("Study Notes", "AI/ML", "University Notes", "PDF"),
            mirrorUrls = listOf(
                "https://arxiv.org/pdf/cs.ai.2401.pdf",
                "https://openreview.net/pdf?id=deeplearning"
            ),
            activeMirror = "ArXiv Academic Cloud (32ms)"
        )
    )

    val featurePacks: List<FeaturePack> = listOf(
        FeaturePack(
            id = "http_pack",
            name = "HTTP / HTTPS Segmented",
            identifier = "gd3.core.http",
            version = "3.2.0",
            enabled = true,
            description = "Multi-threaded high throughput HTTP/1.1 and HTTP/2 downloader with Range header resume.",
            protocols = listOf("http", "https"),
            features = listOf("Multi-threading", "Dynamic Chunk Reallocation", "Cookie Vault")
        ),
        FeaturePack(
            id = "bittorrent_pack",
            name = "BitTorrent 2.0 Engine",
            identifier = "gd3.core.torrent",
            version = "2.4.1",
            enabled = true,
            description = "Full-featured BitTorrent protocol engine with Magnet link resolver, DHT, PEX, and WebSeeds.",
            protocols = listOf("magnet", "torrent"),
            features = listOf("DHT Network", "Peer Exchange (PEX)", "Custom Trackers", "Sequential Download")
        ),
        FeaturePack(
            id = "m3u8_pack",
            name = "HLS / M3U8 Stream Muxer",
            identifier = "gd3.media.m3u8",
            version = "1.9.0",
            enabled = true,
            description = "Parses master and variant M3U8 playlists, downloads TS segments in parallel, and merges to MP4.",
            protocols = listOf("m3u8", "hls"),
            features = listOf("Key Decryption (AES-128)", "Auto Quality Detection", "Fast Remux")
        ),
        FeaturePack(
            id = "bili_pack",
            name = "Bilibili Video Stream Pack",
            identifier = "gd3.extension.bilibili",
            version = "2.1.0",
            enabled = true,
            description = "Fetches 4K/1080p60fps Dash streams and FLAC audio from Bilibili video URLs.",
            protocols = listOf("bilibili", "b23.tv"),
            features = listOf("Audio-Video Merge", "VIP Cookie Auth", "Danmaku Downloader")
        ),
        FeaturePack(
            id = "yt_dlp_pack",
            name = "yt-dlp Multi-Platform Extractor",
            identifier = "gd3.media.ytdlp",
            version = "2025.02.19",
            enabled = true,
            description = "Integrated online video engine supporting YouTube, Vimeo, TikTok, X, and 1000+ streaming sites.",
            protocols = listOf("youtube", "youtu.be", "vimeo", "tiktok"),
            features = listOf("Format Selection", "Subtitle Extraction", "Thumbnail Embed")
        ),
        FeaturePack(
            id = "github_pack",
            name = "GitHub & CNB Accelerator",
            identifier = "gd3.cloud.github",
            version = "1.4.2",
            enabled = true,
            description = "High-speed mirror router for GitHub release assets, tarballs, and raw files using CDN mirrors.",
            protocols = listOf("github.com", "gitcode.com"),
            features = listOf("Mirror Auto-Failover", "CNB CDN Node", "Asset Hash Auto-Verify")
        ),
        FeaturePack(
            id = "huggingface_pack",
            name = "Hugging Face LFS Mirror",
            identifier = "gd3.ai.huggingface",
            version = "1.1.0",
            enabled = true,
            description = "Direct model weights, GGUF quants, and datasets downloader with fast checkpoint resume.",
            protocols = listOf("huggingface.co", "hf-mirror.com"),
            features = listOf("HF Token Auth", "Multi-file Model Tree", "Fast LFS Chunking")
        ),
        FeaturePack(
            id = "ftp_pack",
            name = "FTP / FTPS Secure Retriever",
            identifier = "gd3.core.ftp",
            version = "1.0.5",
            enabled = true,
            description = "Segmented file transfer over classic FTP, Passive Mode, and TLS/SSL encrypted FTPS.",
            protocols = listOf("ftp", "ftps"),
            features = listOf("Passive Mode", "TLS Encryption", "Directory Recursive Queue")
        ),
        FeaturePack(
            id = "ed2k_pack",
            name = "eDonkey2000 & Kad Client",
            identifier = "gd3.p2p.ed2k",
            version = "0.8.2",
            enabled = false,
            description = "Connects to classic eD2k server directories and decentralized Kad DHT network.",
            protocols = listOf("ed2k"),
            features = listOf("Kad DHT", "ICH Checksum Recovery", "Server Met Auto-Update")
        ),
        FeaturePack(
            id = "ai_parser_pack",
            name = "AI Universal Video & Stream Parser",
            identifier = "gd3.ai.videoparser",
            version = "2.1.0",
            enabled = true,
            description = "Extracts 4K/1080p video streams, audio, and captions from Reels, Shorts, TikTok, and web players without watermarks.",
            protocols = listOf("instagram", "tiktok", "youtube", "twitter", "reddit"),
            features = listOf("Watermark Removal", "Lossless Audio Rip", "Auto Subtitles (.srt)", "Stream Signature Decoder")
        ),
        FeaturePack(
            id = "bonding_pack",
            name = "Multi-Network Dual-Channel Bonding",
            identifier = "gd3.net.bonding",
            version = "3.2.0",
            enabled = true,
            description = "Combines Wi-Fi 6 and 5G/LTE cellular pipelines simultaneously for maximum aggregated throughput.",
            protocols = listOf("multipath-tcp", "bonding", "mesh-p2p"),
            features = listOf("Wi-Fi + 5G Aggregation", "Dynamic Mirror Failover", "Thermal & Battery Guard", "Local LAN P2P Mesh")
        ),
        FeaturePack(
            id = "cloud_debrid_pack",
            name = "Cloud & Debrid High-Speed Engine",
            identifier = "gd3.cloud.debrid",
            version = "1.5.2",
            enabled = true,
            description = "Unrestricts premium hosters (Rapidgator, 1Fichier, Mega, Torrents) and auto-syncs completed files to Google Drive, WebDAV & Telegram.",
            protocols = listOf("real-debrid", "alldebrid", "webdav", "gdrive"),
            features = listOf("Debrid Link Unlock", "Auto Cloud Backup", "Auto-Free Local Space", "Telegram Bot Sync")
        ),
        FeaturePack(
            id = "security_pack",
            name = "VirusTotal 70+ Engines & EXIF Stripper",
            identifier = "gd3.sec.antivirus",
            version = "2.5.0",
            enabled = true,
            description = "Multi-engine sandbox heuristic scanner that verifies hashes and sanitizes GPS/camera metadata from media.",
            protocols = listOf("virustotal", "sha256", "exif-clean"),
            features = listOf("72 Antivirus Engines", "EXIF GPS Sanitizer", "APK Signature Audit", "Live Hash Matching")
        )
    )

    val mediaResources: List<MediaResource> = listOf(
        MediaResource(
            id = "res-1",
            title = "Nature Documentary 4K UltraHD Stream",
            url = "https://cdn.wildlife-nature.org/streams/4k_rainforest/playlist.m3u8",
            type = "m3u8",
            sizeBytes = 2840000000L,
            duration = "42:15",
            resolution = "3840x2160 (60fps)",
            pageUrl = "https://wildlife-nature.org/watch/rainforest"
        ),
        MediaResource(
            id = "res-2",
            title = "Chopin Nocturne Op. 9 No. 2 (Lossless Audio)",
            url = "https://audio-archive.org/classical/chopin_op9_no2_flac.mp3",
            type = "mp3",
            sizeBytes = 34500000L,
            duration = "04:32",
            resolution = "320 kbps (48kHz)",
            pageUrl = "https://audio-archive.org/classical/piano"
        ),
        MediaResource(
            id = "res-3",
            title = "Tech Keynote Presentation 2025 (1080p MP4)",
            url = "https://video.events.org/keynote_2025_full_master.mp4",
            type = "mp4",
            sizeBytes = 1420000000L,
            duration = "1:18:40",
            resolution = "1920x1080 (30fps)",
            pageUrl = "https://events.org/keynote2025"
        )
    )

    val imageResources: List<ImageResource> = listOf(
        ImageResource(
            id = "img-1",
            url = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200",
            width = 3840,
            height = 2160,
            sizeBytes = 4200000L,
            alt = "Futuristic Cyber Circuit Board",
            format = "PNG"
        ),
        ImageResource(
            id = "img-2",
            url = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200",
            width = 4096,
            height = 2304,
            sizeBytes = 5600000L,
            alt = "Deep Space Galaxy Nebula",
            format = "JPEG"
        ),
        ImageResource(
            id = "img-3",
            url = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200",
            width = 3000,
            height = 2000,
            sizeBytes = 3100000L,
            alt = "Tropical Turquoise Coastline",
            format = "WEBP"
        )
    )

    val rssSubscriptions: List<RssFeedSubscription> = listOf(
        RssFeedSubscription(
            id = "rss-1",
            title = "Linux Kernel & Distro Releases",
            feedUrl = "https://distrowatch.com/news/dwd.xml",
            autoDownload = true,
            filterRegex = ".*(Ubuntu|Arch|Fedora).*",
            items = listOf(
                RssFeedItem(
                    id = "rss-item-1",
                    title = "Debian GNU/Linux 12.9 'Bookworm' Released",
                    link = "https://www.debian.org/News/2025/20250111",
                    enclosureUrl = "https://cdimage.debian.org/debian-cd/current/amd64/iso-dvd/debian-12.9.0-amd64-DVD-1.iso",
                    pubDate = "2 days ago",
                    category = CategoryType.SOFTWARE
                ),
                RssFeedItem(
                    id = "rss-item-2",
                    title = "Fedora 42 Beta Live Desktop x86_64",
                    link = "https://getfedora.org/",
                    enclosureUrl = "https://download.fedoraproject.org/pub/fedora/linux/releases/test/42_Beta/Workstation/x86_64/iso/Fedora-Workstation-Live-x86_64-42_Beta-1.1.iso",
                    pubDate = "4 days ago",
                    category = CategoryType.SOFTWARE
                )
            )
        )
    )
}
