export type TaskStatus = 'downloading' | 'waiting' | 'paused' | 'completed' | 'error';

export type ProtocolType = 
  | 'http' 
  | 'torrent' 
  | 'm3u8' 
  | 'ftp' 
  | 'ed2k' 
  | 'bilibili' 
  | 'youtube' 
  | 'github' 
  | 'huggingface';

export type CategoryType = 
  | 'all' 
  | 'video' 
  | 'music' 
  | 'document' 
  | 'software' 
  | 'archive' 
  | 'torrent';

export interface DownloadChunk {
  id: number;
  startByte: number;
  endByte: number;
  downloadedBytes: number;
  speed: number; // bytes per sec
  status: 'active' | 'completed' | 'idle';
}

export type TaskPriority = 'high' | 'normal' | 'low';

export interface TorrentTracker {
  id: string;
  url: string;
  status: 'active' | 'announcing' | 'updating' | 'error';
  seeders: number;
  leechers: number;
  downloaded: number;
  lastAnnounce?: string;
  message?: string;
}

export interface TorrentPeer {
  id: string;
  ip: string;
  country: string;
  client: string;
  downloadSpeed: number; // bytes/sec
  uploadSpeed: number;
  progressPercent: number;
  flags: string;
}

export interface DownloadTask {
  id: string;
  name: string;
  url: string;
  protocol: ProtocolType;
  category: CategoryType;
  status: TaskStatus;
  priority?: TaskPriority;
  totalBytes: number;
  downloadedBytes: number;
  speed: number; // bytes per second
  uploadSpeed?: number;
  etaSeconds: number;
  connections: number;
  chunks: DownloadChunk[];
  createdAt: number;
  completedAt?: number;
  savePath: string;
  md5Hash?: string;
  sha256Hash?: string;
  mimeType?: string;
  referer?: string;
  userAgent?: string;
  headers?: Record<string, string>;
  peers?: number;
  seeders?: number;
  errorMessage?: string;
  mirror?: string;
  quality?: string;
  mediaPreviewUrl?: string;
  trackers?: TorrentTracker[];
  connectedPeers?: TorrentPeer[];
}

export interface FeaturePack {
  id: string;
  name: string;
  identifier: string;
  version: string;
  enabled: boolean;
  description: string;
  protocols: string[];
  features: string[];
  settings?: Record<string, any>;
}

export interface MediaResource {
  id: string;
  title: string;
  url: string;
  type: 'm3u8' | 'mp4' | 'dash' | 'mp3' | 'aac' | 'image';
  sizeBytes: number;
  duration?: string;
  resolution?: string;
  pageUrl: string;
  detectedAt: number;
}

export interface ImageResource {
  id: string;
  url: string;
  thumbnailUrl: string;
  width: number;
  height: number;
  sizeBytes: number;
  alt: string;
  format: string;
  selected?: boolean;
}

export interface ScheduledBandwidthConfig {
  enabled: boolean;
  dayStartHour: number; // e.g. 8 (08:00)
  dayEndHour: number; // e.g. 23 (23:00)
  dayLimitKbps: number; // e.g. 2048 (2 MB/s)
  nightLimitKbps: number; // e.g. 0 (unlimited)
}

export interface AppSettings {
  downloadDirectory: string;
  maxConcurrentDownloads: number;
  globalDownloadLimitKbps: number; // 0 for unlimited
  globalUploadLimitKbps: number; // 0 for unlimited
  autoStartOnAdd: boolean;
  autoDetectCategory: boolean;
  clipboardMonitor: boolean;
  playCompletionSound: boolean;
  theme: 'light' | 'dark' | 'system';
  accentColor: string;
  aria2RpcEnabled: boolean;
  aria2RpcHost: string;
  aria2RpcPort: number;
  aria2RpcSecret: string;
  proxyMode: 'none' | 'http' | 'socks5';
  proxyHost: string;
  proxyPort: number;
  scheduledBandwidth?: ScheduledBandwidthConfig;
  autoUnpackConfig?: ArchiveUnpackConfig;
  postDownloadConfig?: PostDownloadPowerConfig;
}

export interface ArchiveUnpackConfig {
  enabled: boolean;
  destinationType: 'same_folder' | 'subfolder' | 'custom';
  customDestination?: string;
  passwords: string[];
  deleteArchiveAfterExtract: boolean;
  notifyOnExtract: boolean;
}

export interface PostDownloadPowerConfig {
  action: 'none' | 'sound' | 'sleep' | 'shutdown' | 'verify_checksum';
  playSound: boolean;
  autoShutdownDelaySec: number;
}

export interface CrawlResultItem {
  id: string;
  url: string;
  filename: string;
  category: CategoryType;
  sizeBytes: number;
  format: string;
  dimensions?: string;
  thumbnailUrl?: string;
  selected: boolean;
}

export interface CdnMirrorNode {
  id: string;
  name: string;
  location: string;
  provider: 'Cloudflare' | 'AWS' | 'Fastly' | 'Google Cloud' | 'Akamai' | 'Azure';
  host: string;
  pingMs: number;
  jitterMs: number;
  packetLoss: number;
  speedMbps: number;
  status: 'idle' | 'testing' | 'online' | 'slow' | 'offline';
}

export interface CreateTorrentPayload {
  name: string;
  fileSizeBytes: number;
  pieceSizeKb: number;
  piecesCount: number;
  trackers: string[];
  webSeeds: string[];
  comment: string;
  isPrivate: boolean;
  infoHash: string;
  magnetUri: string;
  createdAt: number;
}

export interface CloudDebridAccount {
  id: string;
  provider: 'real_debrid' | 'alldebrid' | 'premiumize' | 'gdrive' | 'mega' | 'dropbox';
  name: string;
  apiKey: string;
  status: 'active' | 'expired' | 'free';
  quotaUsedGb: number;
  quotaTotalGb: number;
  expiresAt: string;
}

export interface RemoteControllerDevice {
  id: string;
  deviceName: string;
  ip: string;
  browser: string;
  connectedAt: number;
}

export interface RssFeedItem {
  id: string;
  title: string;
  link: string;
  enclosureUrl?: string;
  enclosureLength?: number;
  pubDate: string;
  category?: CategoryType;
  downloaded?: boolean;
}

export interface RssFeedSubscription {
  id: string;
  title: string;
  feedUrl: string;
  iconUrl?: string;
  lastUpdated: number;
  autoDownload: boolean;
  filterRegex?: string;
  items: RssFeedItem[];
}

export type ActivePage = 'tasks' | 'sniffer' | 'packs' | 'settings';

export type TaskSortField = 'queue' | 'date' | 'priority' | 'size' | 'speed' | 'progress' | 'name' | 'eta';
export type SortDirection = 'asc' | 'desc';

export interface PlanTaskConfig {
  enabled: boolean;
  scheduledTime?: string; // HH:MM
  autoStartInMinutes?: number;
  postAction: 'none' | 'sound' | 'notification' | 'open_folder' | 'sleep';
  actionTarget?: string;
}
