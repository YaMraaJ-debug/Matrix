import { ProtocolType, CategoryType, TaskStatus } from '../types';

export function formatBytes(bytes: number, decimals = 1): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
}

export function formatSpeed(bytesPerSec: number): string {
  if (!bytesPerSec || bytesPerSec <= 0) return '0 B/s';
  return `${formatBytes(bytesPerSec)}/s`;
}

export function formatETA(seconds: number): string {
  if (!isFinite(seconds) || seconds < 0) return '∞';
  if (seconds === 0) return '0s';
  if (seconds < 60) return `${Math.floor(seconds)}s`;
  if (seconds < 3600) {
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}m ${s}s`;
  }
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  return `${h}h ${m}m`;
}

export function getProtocolBadge(protocol: ProtocolType): {
  label: string;
  bgLight: string;
  textLight: string;
  bgDark: string;
  textDark: string;
  borderColor: string;
} {
  switch (protocol) {
    case 'http':
      return {
        label: 'HTTP/2',
        bgLight: 'bg-blue-50 text-blue-700 border-blue-200',
        textLight: 'text-blue-700',
        bgDark: 'dark:bg-blue-950/60 dark:text-blue-300 dark:border-blue-800',
        textDark: 'text-blue-300',
        borderColor: '#3b82f6',
      };
    case 'torrent':
      return {
        label: 'BitTorrent',
        bgLight: 'bg-emerald-50 text-emerald-700 border-emerald-200',
        textLight: 'text-emerald-700',
        bgDark: 'dark:bg-emerald-950/60 dark:text-emerald-300 dark:border-emerald-800',
        textDark: 'text-emerald-300',
        borderColor: '#10b981',
      };
    case 'm3u8':
      return {
        label: 'M3U8 / HLS',
        bgLight: 'bg-purple-50 text-purple-700 border-purple-200',
        textLight: 'text-purple-700',
        bgDark: 'dark:bg-purple-950/60 dark:text-purple-300 dark:border-purple-800',
        textDark: 'text-purple-300',
        borderColor: '#8b5cf6',
      };
    case 'bilibili':
      return {
        label: 'Bilibili',
        bgLight: 'bg-pink-50 text-pink-700 border-pink-200',
        textLight: 'text-pink-700',
        bgDark: 'dark:bg-pink-950/60 dark:text-pink-300 dark:border-pink-800',
        textDark: 'text-pink-300',
        borderColor: '#ec4899',
      };
    case 'youtube':
      return {
        label: 'YouTube',
        bgLight: 'bg-red-50 text-red-700 border-red-200',
        textLight: 'text-red-700',
        bgDark: 'dark:bg-red-950/60 dark:text-red-300 dark:border-red-800',
        textDark: 'text-red-300',
        borderColor: '#ef4444',
      };
    case 'github':
      return {
        label: 'GitHub Fast',
        bgLight: 'bg-slate-100 text-slate-800 border-slate-300',
        textLight: 'text-slate-800',
        bgDark: 'dark:bg-slate-800 dark:text-slate-200 dark:border-slate-700',
        textDark: 'text-slate-200',
        borderColor: '#64748b',
      };
    case 'huggingface':
      return {
        label: 'Hugging Face',
        bgLight: 'bg-amber-50 text-amber-800 border-amber-300',
        textLight: 'text-amber-800',
        bgDark: 'dark:bg-amber-950/60 dark:text-amber-300 dark:border-amber-800',
        textDark: 'text-amber-300',
        borderColor: '#f59e0b',
      };
    case 'ed2k':
      return {
        label: 'eD2k',
        bgLight: 'bg-cyan-50 text-cyan-700 border-cyan-200',
        textLight: 'text-cyan-700',
        bgDark: 'dark:bg-cyan-950/60 dark:text-cyan-300 dark:border-cyan-800',
        textDark: 'text-cyan-300',
        borderColor: '#06b6d4',
      };
    case 'ftp':
      return {
        label: 'FTP',
        bgLight: 'bg-indigo-50 text-indigo-700 border-indigo-200',
        textLight: 'text-indigo-700',
        bgDark: 'dark:bg-indigo-950/60 dark:text-indigo-300 dark:border-indigo-800',
        textDark: 'text-indigo-300',
        borderColor: '#6366f1',
      };
  }
}

export function getStatusDetails(status: TaskStatus): {
  label: string;
  dotClass: string;
  badgeClass: string;
} {
  switch (status) {
    case 'downloading':
      return {
        label: 'Downloading',
        dotClass: 'bg-emerald-500 animate-pulse',
        badgeClass: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/60 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
      };
    case 'waiting':
      return {
        label: 'In Queue',
        dotClass: 'bg-sky-500',
        badgeClass: 'bg-sky-50 text-sky-700 dark:bg-sky-950/60 dark:text-sky-300 border-sky-200 dark:border-sky-800',
      };
    case 'paused':
      return {
        label: 'Paused',
        dotClass: 'bg-amber-500',
        badgeClass: 'bg-amber-50 text-amber-700 dark:bg-amber-950/60 dark:text-amber-300 border-amber-200 dark:border-amber-800',
      };
    case 'completed':
      return {
        label: 'Completed',
        dotClass: 'bg-blue-500',
        badgeClass: 'bg-blue-50 text-blue-700 dark:bg-blue-950/60 dark:text-blue-300 border-blue-200 dark:border-blue-800',
      };
    case 'error':
      return {
        label: 'Failed',
        dotClass: 'bg-rose-500',
        badgeClass: 'bg-rose-50 text-rose-700 dark:bg-rose-950/60 dark:text-rose-300 border-rose-200 dark:border-rose-800',
      };
  }
}

export function detectProtocolFromUrl(url: string): ProtocolType {
  const trimmed = url.trim().toLowerCase();
  if (trimmed.startsWith('magnet:?')) return 'torrent';
  if (trimmed.endsWith('.torrent')) return 'torrent';
  if (trimmed.startsWith('ed2k://')) return 'ed2k';
  if (trimmed.startsWith('ftp://') || trimmed.startsWith('ftps://')) return 'ftp';
  if (trimmed.includes('bilibili.com') || trimmed.includes('b23.tv')) return 'bilibili';
  if (trimmed.includes('youtube.com') || trimmed.includes('youtu.be')) return 'youtube';
  if (trimmed.includes('github.com')) return 'github';
  if (trimmed.includes('huggingface.co')) return 'huggingface';
  if (trimmed.includes('.m3u8') || trimmed.includes('hls/')) return 'm3u8';
  return 'http';
}

export function detectCategoryFromFileName(fileName: string): CategoryType {
  const lower = fileName.toLowerCase();
  if (/\.(mp4|mkv|avi|mov|flv|webm|ts|m3u8|m4v)$/i.test(lower)) return 'video';
  if (/\.(mp3|flac|wav|aac|ogg|m4a|wma)$/i.test(lower)) return 'music';
  if (/\.(pdf|docx?|pptx?|xlsx?|txt|epub|md)$/i.test(lower)) return 'document';
  if (/\.(exe|msi|dmg|pkg|deb|rpm|appimage|apk|zip|tar|gz|7z|rar)$/i.test(lower)) {
    if (/\.(exe|msi|dmg|pkg|deb|rpm|appimage|apk)$/i.test(lower)) return 'software';
    return 'archive';
  }
  if (/\.torrent$/i.test(lower)) return 'torrent';
  return 'document';
}
