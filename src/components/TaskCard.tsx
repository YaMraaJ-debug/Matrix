import { 
  Play, 
  Pause, 
  RotateCcw, 
  Trash2, 
  Copy, 
  ExternalLink, 
  FileText, 
  Film, 
  Music, 
  Archive, 
  Terminal, 
  HardDriveDownload,
  Info,
  Layers,
  Users,
  Hash,
  ArrowUp,
  ArrowDown,
  Eye,
  Radio,
  SlidersHorizontal
} from 'lucide-react';
import { DownloadTask, TaskPriority } from '../types';
import { 
  formatBytes, 
  formatSpeed, 
  formatETA, 
  getProtocolBadge, 
  getStatusDetails 
} from '../utils/formatters';

interface TaskCardProps {
  task: DownloadTask;
  isSelected: boolean;
  onToggleSelect: (id: string) => void;
  onTogglePause: (id: string) => void;
  onRestart: (id: string) => void;
  onDelete: (id: string) => void;
  onCopyUrl: (url: string) => void;
  onOpenDetails: (task: DownloadTask) => void;
  onSaveToDisk: (task: DownloadTask) => void;
  onVerifyHash?: (task: DownloadTask) => void;
  onOpenPreview?: (task: DownloadTask) => void;
  onOpenTorrentPeers?: (task: DownloadTask) => void;
  onMoveUp?: (id: string) => void;
  onMoveDown?: (id: string) => void;
  onSetPriority?: (id: string, priority: TaskPriority) => void;
}

export function TaskCard({
  task,
  isSelected,
  onToggleSelect,
  onTogglePause,
  onRestart,
  onDelete,
  onCopyUrl,
  onOpenDetails,
  onSaveToDisk,
  onVerifyHash,
  onOpenPreview,
  onOpenTorrentPeers,
  onMoveUp,
  onMoveDown,
  onSetPriority,
}: TaskCardProps) {
  const protocolBadge = getProtocolBadge(task.protocol);
  const statusDetails = getStatusDetails(task.status);
  const percentage = task.totalBytes > 0 
    ? Math.min(100, Math.round((task.downloadedBytes / task.totalBytes) * 1000) / 10)
    : 0;

  const isFinished = task.status === 'completed';
  const isDownloading = task.status === 'downloading';
  const isMedia = task.category === 'video' || task.category === 'music' || /\.(mp4|mkv|webm|mp3|wav|flac|aac|m4a|ts)$/i.test(task.name);
  const isTorrent = task.protocol === 'torrent' || task.url.startsWith('magnet:');

  const priority = task.priority || 'normal';

  const cyclePriority = () => {
    if (!onSetPriority) return;
    const nextPriority: TaskPriority = priority === 'low' ? 'normal' : priority === 'normal' ? 'high' : 'low';
    onSetPriority(task.id, nextPriority);
  };

  const getPriorityBadge = (p: TaskPriority) => {
    switch (p) {
      case 'high':
        return { label: 'High Priority', class: 'bg-rose-100 dark:bg-rose-950/80 text-rose-700 dark:text-rose-300 border-rose-300 dark:border-rose-800' };
      case 'low':
        return { label: 'Low Priority', class: 'bg-neutral-100 dark:bg-neutral-800 text-neutral-500 dark:text-neutral-400 border-neutral-300 dark:border-neutral-700' };
      default:
        return { label: 'Normal Priority', class: 'bg-sky-50 dark:bg-sky-950 text-sky-700 dark:text-sky-300 border-sky-200 dark:border-sky-800' };
    }
  };

  const pBadge = getPriorityBadge(priority);

  // Choose file icon based on category
  const renderCategoryIcon = () => {
    switch (task.category) {
      case 'video':
        return <Film className="w-5 h-5 text-purple-500" />;
      case 'music':
        return <Music className="w-5 h-5 text-pink-500" />;
      case 'archive':
        return <Archive className="w-5 h-5 text-amber-500" />;
      case 'software':
        return <Terminal className="w-5 h-5 text-sky-500" />;
      default:
        return <FileText className="w-5 h-5 text-blue-500" />;
    }
  };

  return (
    <div
      className={`group relative rounded-xl p-3.5 transition-all duration-200 border select-none ${
        isSelected
          ? 'bg-sky-50/70 dark:bg-sky-950/30 border-sky-300 dark:border-sky-800 shadow-xs'
          : 'bg-white/95 dark:bg-[#2c2c2c]/90 hover:bg-neutral-50 dark:hover:bg-[#323232] border-neutral-200/80 dark:border-neutral-700/60 shadow-xs'
      }`}
    >
      <div className="flex items-start justify-between gap-3">
        {/* Left: Checkbox + File Icon + Info */}
        <div className="flex items-start space-x-3 flex-1 min-w-0">
          <input
            type="checkbox"
            checked={isSelected}
            onChange={() => onToggleSelect(task.id)}
            className="mt-1 w-4 h-4 rounded border-neutral-300 dark:border-neutral-600 text-sky-600 focus:ring-sky-500 cursor-pointer"
          />

          <div className="w-9 h-9 rounded-lg bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center flex-shrink-0 border border-neutral-200/60 dark:border-neutral-700/60">
            {renderCategoryIcon()}
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-center space-x-2">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 truncate" title={task.name}>
                {task.name}
              </h3>

              {/* Protocol Badge */}
              <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${protocolBadge.bgLight} ${protocolBadge.bgDark}`}>
                {protocolBadge.label}
              </span>

              {/* Status Badge */}
              <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full flex items-center space-x-1 border ${statusDetails.badgeClass}`}>
                <span className={`w-1.5 h-1.5 rounded-full ${statusDetails.dotClass}`} />
                <span>{statusDetails.label}</span>
              </span>

              {/* Priority Badge (Click to cycle) */}
              <button
                onClick={cyclePriority}
                className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border transition-transform active:scale-95 cursor-pointer ${pBadge.class}`}
                title="Click to cycle priority (High / Normal / Low)"
              >
                {pBadge.label}
              </button>
            </div>

            {/* URL / Origin / Quality */}
            <div className="flex items-center space-x-2 text-[11px] text-neutral-400 mt-0.5 truncate">
              <span className="truncate max-w-xs">{task.url}</span>
              {task.mirror && (
                <>
                  <span>•</span>
                  <span className="text-amber-600 dark:text-amber-400 font-medium">{task.mirror}</span>
                </>
              )}
              {task.quality && (
                <>
                  <span>•</span>
                  <span className="text-sky-600 dark:text-sky-400 font-medium">{task.quality}</span>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Right: Quick Actions */}
        <div className="flex items-center space-x-1 opacity-90 group-hover:opacity-100 transition-opacity">
          {/* Queue Reordering: Move Up & Move Down */}
          {onMoveUp && onMoveDown && (
            <div className="flex items-center space-x-0.5 mr-1 border-r border-neutral-200 dark:border-neutral-700 pr-1">
              <button
                onClick={() => onMoveUp(task.id)}
                className="p-1 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-500 dark:text-neutral-400 hover:text-neutral-800 dark:hover:text-neutral-100 transition-colors cursor-pointer"
                title="Move Task Up in Queue"
              >
                <ArrowUp className="w-3.5 h-3.5" />
              </button>
              <button
                onClick={() => onMoveDown(task.id)}
                className="p-1 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-500 dark:text-neutral-400 hover:text-neutral-800 dark:hover:text-neutral-100 transition-colors cursor-pointer"
                title="Move Task Down in Queue"
              >
                <ArrowDown className="w-3.5 h-3.5" />
              </button>
            </div>
          )}

          {/* In-App Media Preview Button */}
          {isMedia && onOpenPreview && (
            <button
              onClick={() => onOpenPreview(task)}
              className="p-1.5 rounded-md hover:bg-purple-100 dark:hover:bg-purple-950/60 text-purple-600 dark:text-purple-400 transition-colors cursor-pointer"
              title="Preview Video / Audio Stream"
            >
              <Eye className="w-4 h-4" />
            </button>
          )}

          {/* Torrent Trackers & Peer Manager Button */}
          {isTorrent && onOpenTorrentPeers && (
            <button
              onClick={() => onOpenTorrentPeers(task)}
              className="p-1.5 rounded-md hover:bg-emerald-100 dark:hover:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 transition-colors cursor-pointer"
              title="View BitTorrent Trackers & Swarm Peers"
            >
              <Radio className="w-4 h-4" />
            </button>
          )}

          {/* Pause / Resume Button */}
          {!isFinished && (
            <button
              onClick={() => onTogglePause(task.id)}
              className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 transition-colors cursor-pointer"
              title={isDownloading ? 'Pause Download' : 'Resume Download'}
            >
              {isDownloading ? <Pause className="w-4 h-4 text-amber-500" /> : <Play className="w-4 h-4 text-emerald-500" />}
            </button>
          )}

          {/* Redownload / Restart */}
          <button
            onClick={() => onRestart(task.id)}
            className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 transition-colors cursor-pointer"
            title="Redownload / Restart"
          >
            <RotateCcw className="w-4 h-4" />
          </button>

          {/* Save to Disk / Open File */}
          <button
            onClick={() => onSaveToDisk(task)}
            className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-sky-600 dark:text-sky-400 transition-colors cursor-pointer"
            title="Export / Save to Browser Downloads"
          >
            <HardDriveDownload className="w-4 h-4" />
          </button>

          {/* Chunks / Details Modal */}
          <button
            onClick={() => onOpenDetails(task)}
            className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 transition-colors cursor-pointer"
            title="Task Details & Chunks Map"
          >
            <Layers className="w-4 h-4" />
          </button>

          {/* Hash Verification Tool */}
          {onVerifyHash && (
            <button
              onClick={() => onVerifyHash(task)}
              className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 transition-colors cursor-pointer"
              title="Verify File Checksum / Hash"
            >
              <Hash className="w-4 h-4" />
            </button>
          )}

          {/* Copy URL */}
          <button
            onClick={() => onCopyUrl(task.url)}
            className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 transition-colors cursor-pointer"
            title="Copy Source URL"
          >
            <Copy className="w-4 h-4" />
          </button>

          {/* Delete */}
          <button
            onClick={() => onDelete(task.id)}
            className="p-1.5 rounded-md hover:bg-rose-100 dark:hover:bg-rose-950/60 text-rose-600 dark:text-rose-400 transition-colors cursor-pointer"
            title="Delete Task"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="mt-3">
        <div className="w-full bg-neutral-100 dark:bg-neutral-700/60 h-2 rounded-full overflow-hidden">
          <div
            className={`h-full transition-all duration-300 ${
              isFinished
                ? 'bg-blue-500'
                : isDownloading
                  ? 'bg-gradient-to-r from-sky-500 to-blue-600'
                  : 'bg-amber-500'
            }`}
            style={{ width: `${percentage}%` }}
          />
        </div>
      </div>

      {/* Stats Row */}
      <div className="mt-2.5 flex flex-wrap items-center justify-between text-xs text-neutral-500 dark:text-neutral-400 font-mono">
        <div className="flex items-center space-x-3">
          <span className="font-semibold text-neutral-700 dark:text-neutral-200">
            {formatBytes(task.downloadedBytes)} / {formatBytes(task.totalBytes)}
          </span>
          <span className="font-bold text-sky-600 dark:text-sky-400">
            {percentage}%
          </span>
          {task.peers !== undefined && (
            <span className="flex items-center space-x-1 text-emerald-600 dark:text-emerald-400">
              <Users className="w-3 h-3" />
              <span>{task.peers} peers ({task.seeders} seeds)</span>
            </span>
          )}
          {task.connections && !task.peers && (
            <span className="text-[11px] text-neutral-400">
              {task.connections} threads
            </span>
          )}
        </div>

        <div className="flex items-center space-x-3">
          {isDownloading ? (
            <>
              <span className="text-emerald-600 dark:text-emerald-400 font-semibold flex items-center space-x-1">
                <span>↓ {formatSpeed(task.speed)}</span>
              </span>
              <span>ETA: {formatETA(task.etaSeconds)}</span>
            </>
          ) : isFinished ? (
            <span className="text-blue-600 dark:text-blue-400 font-medium">Finished</span>
          ) : (
            <span className="text-amber-600 dark:text-amber-400">Paused</span>
          )}
        </div>
      </div>
    </div>
  );
}
