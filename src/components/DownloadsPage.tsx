import { useState, useMemo } from 'react';
import { 
  Play, 
  Pause, 
  Trash2, 
  Plus, 
  Filter, 
  ArrowUpDown, 
  Activity, 
  Hash, 
  CalendarClock, 
  ListTree, 
  Chrome, 
  CheckSquare, 
  Square,
  Sparkles,
  ChevronDown,
  Rss,
  HardDrive
} from 'lucide-react';
import { DownloadTask, CategoryType, TaskSortField, SortDirection, PlanTaskConfig, TaskPriority } from '../types';
import { TaskCard } from './TaskCard';
import { SpeedGraph } from './SpeedGraph';

interface DownloadsPageProps {
  tasks: DownloadTask[];
  searchQuery: string;
  speedHistory: number[];
  uploadHistory: number[];
  currentSpeed: number;
  currentUpload: number;
  peakSpeed: number;
  planConfig: PlanTaskConfig;
  onOpenNewTask: () => void;
  onOpenBatchUrl: () => void;
  onOpenFileHash: (task?: DownloadTask) => void;
  onOpenPlanTask: () => void;
  onOpenBrowserExt: () => void;
  onTogglePause: (id: string) => void;
  onRestart: (id: string) => void;
  onDelete: (id: string) => void;
  onPauseAll: () => void;
  onResumeAll: () => void;
  onDeleteFinished: () => void;
  onCopyUrl: (url: string) => void;
  onOpenDetails: (task: DownloadTask) => void;
  onSaveToDisk: (task: DownloadTask) => void;
  onOpenRssModal?: () => void;
  onOpenBackupModal?: () => void;
  onOpenPreview?: (task: DownloadTask) => void;
  onOpenTorrentPeers?: (task: DownloadTask) => void;
  onMoveUp?: (id: string) => void;
  onMoveDown?: (id: string) => void;
  onSetPriority?: (id: string, priority: TaskPriority) => void;
}

export function DownloadsPage({
  tasks,
  searchQuery,
  speedHistory,
  uploadHistory,
  currentSpeed,
  currentUpload,
  peakSpeed,
  planConfig,
  onOpenNewTask,
  onOpenBatchUrl,
  onOpenFileHash,
  onOpenPlanTask,
  onOpenBrowserExt,
  onTogglePause,
  onRestart,
  onDelete,
  onPauseAll,
  onResumeAll,
  onDeleteFinished,
  onCopyUrl,
  onOpenDetails,
  onSaveToDisk,
  onOpenRssModal,
  onOpenBackupModal,
  onOpenPreview,
  onOpenTorrentPeers,
  onMoveUp,
  onMoveDown,
  onSetPriority,
}: DownloadsPageProps) {
  const [statusFilter, setStatusFilter] = useState<'all' | 'downloading' | 'completed' | 'paused'>('all');
  const [categoryFilter, setCategoryFilter] = useState<CategoryType | 'all'>('all');
  const [selectedTaskIds, setSelectedTaskIds] = useState<Set<string>>(new Set());
  const [showSpeedGraph, setShowSpeedGraph] = useState(true);
  const [sortField, setSortField] = useState<TaskSortField>('queue');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');

  // Filter and Sort Tasks
  const filteredAndSortedTasks = useMemo(() => {
    // 1. Filter
    const filtered = tasks.filter((t) => {
      const matchesSearch =
        t.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.url.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.protocol.toLowerCase().includes(searchQuery.toLowerCase());

      if (!matchesSearch) return false;
      if (statusFilter !== 'all' && t.status !== statusFilter) return false;
      if (categoryFilter !== 'all' && t.category !== categoryFilter) return false;

      return true;
    });

    // 2. Sort
    if (sortField === 'queue') {
      // Return manual queue order
      return sortDirection === 'asc' ? [...filtered].reverse() : [...filtered];
    }

    return [...filtered].sort((a, b) => {
      let comparison = 0;
      switch (sortField) {
        case 'priority': {
          const priorityWeight = (p?: TaskPriority) => p === 'high' ? 3 : p === 'low' ? 1 : 2;
          comparison = priorityWeight(a.priority) - priorityWeight(b.priority);
          break;
        }
        case 'date':
          comparison = (a.createdAt || 0) - (b.createdAt || 0);
          break;
        case 'size':
          comparison = a.totalBytes - b.totalBytes;
          break;
        case 'speed':
          comparison = a.speed - b.speed;
          break;
        case 'progress': {
          const pA = a.totalBytes ? a.downloadedBytes / a.totalBytes : 0;
          const pB = b.totalBytes ? b.downloadedBytes / b.totalBytes : 0;
          comparison = pA - pB;
          break;
        }
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'eta':
          comparison = a.etaSeconds - b.etaSeconds;
          break;
      }
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [tasks, searchQuery, statusFilter, categoryFilter, sortField, sortDirection]);

  const toggleSelectTask = (id: string) => {
    const next = new Set(selectedTaskIds);
    if (next.has(id)) next.delete(id);
    else next.add(id);
    setSelectedTaskIds(next);
  };

  const toggleSelectAll = () => {
    if (selectedTaskIds.size === filteredAndSortedTasks.length) {
      setSelectedTaskIds(new Set());
    } else {
      setSelectedTaskIds(new Set(filteredAndSortedTasks.map((t) => t.id)));
    }
  };

  const handleDeleteSelected = () => {
    selectedTaskIds.forEach((id) => onDelete(id));
    setSelectedTaskIds(new Set());
  };

  const toggleSortDirection = () => {
    setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
  };

  // Counts
  const downloadingCount = tasks.filter((t) => t.status === 'downloading').length;
  const completedCount = tasks.filter((t) => t.status === 'completed').length;
  const pausedCount = tasks.filter((t) => t.status === 'paused').length;

  return (
    <div className="flex-1 flex flex-col h-full overflow-hidden bg-neutral-100/60 dark:bg-[#1e1e1e]">
      {/* Top Header */}
      <div className="p-3.5 bg-white dark:bg-[#282828] border-b border-neutral-200 dark:border-neutral-700/80 flex flex-col gap-2.5">
        <div className="flex flex-wrap items-center justify-between gap-2">
          {/* Status Tabs */}
          <div className="flex items-center space-x-1 bg-neutral-100 dark:bg-neutral-800 p-1 rounded-lg text-xs font-medium">
            <button
              onClick={() => setStatusFilter('all')}
              className={`px-3 py-1 rounded-md transition-colors cursor-pointer ${
                statusFilter === 'all'
                  ? 'bg-white dark:bg-[#333] text-neutral-800 dark:text-white shadow-xs font-semibold'
                  : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
              }`}
            >
              All ({tasks.length})
            </button>
            <button
              onClick={() => setStatusFilter('downloading')}
              className={`px-3 py-1 rounded-md transition-colors cursor-pointer flex items-center space-x-1 ${
                statusFilter === 'downloading'
                  ? 'bg-white dark:bg-[#333] text-sky-600 dark:text-sky-400 shadow-xs font-semibold'
                  : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
              }`}
            >
              <span className="w-1.5 h-1.5 rounded-full bg-sky-500 animate-pulse" />
              <span>Downloading ({downloadingCount})</span>
            </button>
            <button
              onClick={() => setStatusFilter('completed')}
              className={`px-3 py-1 rounded-md transition-colors cursor-pointer ${
                statusFilter === 'completed'
                  ? 'bg-white dark:bg-[#333] text-emerald-600 dark:text-emerald-400 shadow-xs font-semibold'
                  : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
              }`}
            >
              Finished ({completedCount})
            </button>
            <button
              onClick={() => setStatusFilter('paused')}
              className={`px-3 py-1 rounded-md transition-colors cursor-pointer ${
                statusFilter === 'paused'
                  ? 'bg-white dark:bg-[#333] text-amber-600 dark:text-amber-400 shadow-xs font-semibold'
                  : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
              }`}
            >
              Paused ({pausedCount})
            </button>
          </div>

          {/* Quick Actions Bar */}
          <div className="flex items-center space-x-1.5">
            <button
              onClick={onResumeAll}
              className="flex items-center space-x-1 px-2.5 py-1 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-200 text-xs font-medium cursor-pointer"
              title="Resume All Downloads"
            >
              <Play className="w-3.5 h-3.5 text-emerald-500" />
              <span className="hidden sm:inline">Start All</span>
            </button>

            <button
              onClick={onPauseAll}
              className="flex items-center space-x-1 px-2.5 py-1 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-200 text-xs font-medium cursor-pointer"
              title="Pause All Downloads"
            >
              <Pause className="w-3.5 h-3.5 text-amber-500" />
              <span className="hidden sm:inline">Pause All</span>
            </button>

            <button
              onClick={onDeleteFinished}
              className="flex items-center space-x-1 px-2.5 py-1 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-200 text-xs font-medium cursor-pointer"
              title="Clear Finished Downloads"
            >
              <Trash2 className="w-3.5 h-3.5 text-rose-500" />
              <span className="hidden sm:inline">Clear Done</span>
            </button>

            <button
              onClick={onOpenNewTask}
              className="flex items-center space-x-1 px-3 py-1 rounded-md bg-sky-600 hover:bg-sky-500 text-white text-xs font-medium shadow-xs cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Add URL</span>
            </button>
          </div>
        </div>

        {/* Second Row: Advanced Tools + Sorting Toolbar */}
        <div className="flex flex-wrap items-center justify-between gap-2 pt-1.5 border-t border-neutral-100 dark:border-neutral-800 text-xs">
          {/* Advanced Tool Buttons */}
          <div className="flex items-center space-x-1.5 flex-wrap">
            <button
              onClick={onOpenBatchUrl}
              className="flex items-center space-x-1 px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-[11px] font-medium cursor-pointer"
              title="Generate batch download URLs with patterns like [01-20]"
            >
              <ListTree className="w-3 h-3 text-purple-500" />
              <span>Batch URLs</span>
            </button>

            <button
              onClick={() => onOpenFileHash()}
              className="flex items-center space-x-1 px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-[11px] font-medium cursor-pointer"
              title="Verify file checksums (MD5, SHA1, SHA256)"
            >
              <Hash className="w-3 h-3 text-sky-500" />
              <span>Hash Verifier</span>
            </button>

            <button
              onClick={onOpenPlanTask}
              className={`flex items-center space-x-1 px-2 py-1 rounded-md text-[11px] font-medium cursor-pointer ${
                planConfig.enabled
                  ? 'bg-amber-100 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 border border-amber-300 dark:border-amber-800'
                  : 'bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300'
              }`}
              title="Scheduled off-peak downloads and post-completion actions"
            >
              <CalendarClock className="w-3 h-3 text-amber-500" />
              <span>Planner {planConfig.enabled && `(${planConfig.scheduledTime || 'Active'})`}</span>
            </button>

            <button
              onClick={onOpenBrowserExt}
              className="flex items-center space-x-1 px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-[11px] font-medium cursor-pointer"
              title="Browser extension integration guide"
            >
              <Chrome className="w-3 h-3 text-emerald-500" />
              <span className="hidden sm:inline">Browser Ext</span>
            </button>

            {onOpenRssModal && (
              <button
                onClick={onOpenRssModal}
                className="flex items-center space-x-1 px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-[11px] font-medium cursor-pointer"
                title="RSS & Podcast Auto-Downloader with regex filtering"
              >
                <Rss className="w-3 h-3 text-amber-500" />
                <span>RSS Auto-DL</span>
              </button>
            )}

            {onOpenBackupModal && (
              <button
                onClick={onOpenBackupModal}
                className="flex items-center space-x-1 px-2 py-1 rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200/80 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-[11px] font-medium cursor-pointer"
                title="Backup and restore download queue and manifests"
              >
                <HardDrive className="w-3 h-3 text-emerald-500" />
                <span>Backup & Export</span>
              </button>
            )}

            <button
              onClick={() => setShowSpeedGraph((v) => !v)}
              className={`flex items-center space-x-1 px-2 py-1 rounded-md text-[11px] font-medium cursor-pointer ${
                showSpeedGraph
                  ? 'bg-sky-50 dark:bg-sky-950 text-sky-700 dark:text-sky-300'
                  : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400'
              }`}
              title="Toggle live bandwidth graph"
            >
              <Activity className="w-3 h-3 text-sky-500" />
              <span>Telemetry</span>
            </button>
          </div>

          {/* Sorting Controls */}
          <div className="flex items-center space-x-1.5 ml-auto">
            <span className="text-[11px] text-neutral-400">Sort by:</span>
            <select
              value={sortField}
              onChange={(e) => setSortField(e.target.value as TaskSortField)}
              className="p-1 rounded-md border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-200 text-[11px] focus:outline-none cursor-pointer"
            >
              <option value="queue">Queue Order</option>
              <option value="priority">Priority Tier</option>
              <option value="date">Date Added</option>
              <option value="size">File Size</option>
              <option value="speed">Download Speed</option>
              <option value="progress">Progress %</option>
              <option value="name">File Name</option>
              <option value="eta">Estimated Time</option>
            </select>
            <button
              onClick={toggleSortDirection}
              className="p-1 rounded-md border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-700 cursor-pointer"
              title={`Sort ${sortDirection === 'asc' ? 'Ascending' : 'Descending'}`}
            >
              <ArrowUpDown className="w-3 h-3" />
            </button>
          </div>
        </div>

        {/* Third Row: Category Filter Pills */}
        <div className="flex items-center justify-between text-xs pt-1.5 border-t border-neutral-100 dark:border-neutral-800">
          <div className="flex items-center space-x-1.5 overflow-x-auto py-0.5">
            <span className="text-[11px] text-neutral-400 font-medium mr-1">Category:</span>
            {[
              { id: 'all', label: 'All Files' },
              { id: 'software', label: 'Software' },
              { id: 'video', label: 'Videos' },
              { id: 'music', label: 'Music' },
              { id: 'archive', label: 'Archives' },
              { id: 'torrent', label: 'Torrents' },
            ].map((cat) => (
              <button
                key={cat.id}
                onClick={() => setCategoryFilter(cat.id as any)}
                className={`px-2 py-0.5 rounded-full text-[11px] transition-colors cursor-pointer ${
                  categoryFilter === cat.id
                    ? 'bg-sky-100 dark:bg-sky-950 text-sky-700 dark:text-sky-300 font-medium'
                    : 'text-neutral-500 hover:bg-neutral-200/60 dark:hover:bg-neutral-800'
                }`}
              >
                {cat.label}
              </button>
            ))}
          </div>

          <div className="flex items-center space-x-2">
            {selectedTaskIds.size > 0 && (
              <div className="flex items-center space-x-2">
                <span className="text-[11px] text-neutral-500">
                  {selectedTaskIds.size} selected
                </span>
                <button
                  onClick={handleDeleteSelected}
                  className="text-rose-600 hover:underline text-[11px] font-medium cursor-pointer"
                >
                  Delete
                </button>
              </div>
            )}
            <button
              onClick={toggleSelectAll}
              className="text-[11px] text-sky-600 hover:underline cursor-pointer"
            >
              {selectedTaskIds.size === filteredAndSortedTasks.length && filteredAndSortedTasks.length > 0
                ? 'Deselect All'
                : 'Select All'}
            </button>
          </div>
        </div>
      </div>

      {/* Speed Telemetry Graph Banner */}
      {showSpeedGraph && (
        <div className="px-4 pt-3">
          <SpeedGraph
            speedHistory={speedHistory}
            uploadHistory={uploadHistory}
            currentSpeed={currentSpeed}
            currentUpload={currentUpload}
            peakSpeed={peakSpeed}
          />
        </div>
      )}

      {/* Task Cards List */}
      <div className="flex-1 p-4 overflow-y-auto space-y-3">
        {filteredAndSortedTasks.length === 0 ? (
          <div className="h-64 flex flex-col items-center justify-center text-neutral-400 space-y-3">
            <div className="w-12 h-12 rounded-full bg-neutral-200/60 dark:bg-neutral-800 flex items-center justify-center">
              <Filter className="w-6 h-6 text-neutral-400" />
            </div>
            <div className="text-center">
              <p className="text-sm font-medium text-neutral-600 dark:text-neutral-300">
                No download tasks found
              </p>
              <p className="text-xs text-neutral-400 mt-0.5">
                {searchQuery ? 'Try changing your search query' : 'Click "+ Add URL" to start downloading'}
              </p>
            </div>
            <button
              onClick={onOpenNewTask}
              className="px-3.5 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white text-xs font-medium shadow-xs cursor-pointer flex items-center space-x-1.5"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Create New Task</span>
            </button>
          </div>
        ) : (
          filteredAndSortedTasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              isSelected={selectedTaskIds.has(task.id)}
              onToggleSelect={toggleSelectTask}
              onTogglePause={onTogglePause}
              onRestart={onRestart}
              onDelete={onDelete}
              onCopyUrl={onCopyUrl}
              onOpenDetails={onOpenDetails}
              onSaveToDisk={onSaveToDisk}
              onVerifyHash={onOpenFileHash}
              onOpenPreview={onOpenPreview}
              onOpenTorrentPeers={onOpenTorrentPeers}
              onMoveUp={onMoveUp}
              onMoveDown={onMoveDown}
              onSetPriority={onSetPriority}
            />
          ))
        )}
      </div>
    </div>
  );
}
