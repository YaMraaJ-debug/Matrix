import { Search, ArrowDown, ArrowUp, Plus, Minus, Square, X, RefreshCw } from 'lucide-react';
import { formatSpeed } from '../utils/formatters';

interface TitleBarProps {
  searchQuery: string;
  onSearchChange: (q: string) => void;
  totalDownloadSpeed: number;
  totalUploadSpeed: number;
  activeTaskCount: number;
  onOpenNewTask: () => void;
}

export function TitleBar({
  searchQuery,
  onSearchChange,
  totalDownloadSpeed,
  totalUploadSpeed,
  activeTaskCount,
  onOpenNewTask,
}: TitleBarProps) {
  return (
    <header className="h-11 select-none flex items-center justify-between px-3 bg-white/80 dark:bg-[#202020]/90 backdrop-blur-md border-b border-black/[0.06] dark:border-white/[0.08] z-30 flex-shrink-0">
      {/* Left: App Logo and Title */}
      <div className="flex items-center space-x-2.5">
        <div className="w-6 h-6 rounded-md bg-gradient-to-tr from-sky-500 to-blue-600 flex items-center justify-center shadow-sm">
          <svg className="w-4 h-4 text-white" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C7.58 2 4 5.58 4 10v9.5c0 .6.7 1 1.2.6L7 18.5l2.5 2c.4.3 1 .3 1.4 0L12 19.5l1.1 1c.4.3 1 .3 1.4 0l2.5-2 1.8 1.6c.5.4 1.2 0 1.2-.6V10c0-4.42-3.58-8-8-8zm-2 9c-.83 0-1.5-.67-1.5-1.5S9.17 8 10 8s1.5.67 1.5 1.5S10.83 11 10 11zm4 0c-.83 0-1.5-.67-1.5-1.5S13.17 8 14 8s1.5.67 1.5 1.5S14.83 11 14 11z"/>
          </svg>
        </div>
        <div className="flex items-center space-x-1.5">
          <span className="font-semibold text-sm tracking-tight text-neutral-800 dark:text-neutral-100">
            Ghost Downloader
          </span>
          <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-sky-100 text-sky-700 dark:bg-sky-950/80 dark:text-sky-300">
            v3.0
          </span>
        </div>
      </div>

      {/* Center: Search Bar & Global Speed Meter */}
      <div className="flex items-center space-x-3 max-w-md w-full mx-4">
        <div className="relative flex-1">
          <Search className="w-3.5 h-3.5 absolute left-2.5 top-1/2 -translate-y-1/2 text-neutral-400" />
          <input
            type="text"
            placeholder="Search downloads, URLs, or categories..."
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            className="w-full h-7 pl-8 pr-3 text-xs rounded-md bg-neutral-100/90 dark:bg-neutral-800/80 border border-neutral-200/70 dark:border-neutral-700/60 focus:outline-none focus:ring-1 focus:ring-sky-500 text-neutral-800 dark:text-neutral-200 placeholder-neutral-400"
          />
        </div>

        {/* Real-time Bandwidth Monitor */}
        <div className="hidden sm:flex items-center space-x-2.5 px-2.5 py-1 rounded bg-neutral-100 dark:bg-neutral-800/80 border border-neutral-200/60 dark:border-neutral-700/50 text-[11px] font-medium font-mono">
          <div className="flex items-center space-x-1 text-emerald-600 dark:text-emerald-400">
            <ArrowDown className={`w-3.5 h-3.5 ${activeTaskCount > 0 ? 'animate-bounce' : ''}`} />
            <span>{formatSpeed(totalDownloadSpeed)}</span>
          </div>
          <div className="h-3 w-px bg-neutral-300 dark:bg-neutral-700" />
          <div className="flex items-center space-x-1 text-sky-600 dark:text-sky-400">
            <ArrowUp className="w-3.5 h-3.5" />
            <span>{formatSpeed(totalUploadSpeed)}</span>
          </div>
        </div>
      </div>

      {/* Right: Quick Add + Window Controls */}
      <div className="flex items-center space-x-2">
        <button
          onClick={onOpenNewTask}
          className="flex items-center space-x-1.5 h-7 px-2.5 text-xs font-medium rounded-md bg-sky-600 hover:bg-sky-500 text-white shadow-sm transition-colors cursor-pointer"
        >
          <Plus className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">New Download</span>
        </button>

        {/* Windows style control buttons */}
        <div className="flex items-center -mr-1">
          <button className="w-7 h-7 flex items-center justify-center text-neutral-500 hover:bg-neutral-200/60 dark:hover:bg-neutral-700/60 rounded">
            <Minus className="w-3.5 h-3.5" />
          </button>
          <button className="w-7 h-7 flex items-center justify-center text-neutral-500 hover:bg-neutral-200/60 dark:hover:bg-neutral-700/60 rounded">
            <Square className="w-3 h-3" />
          </button>
          <button className="w-7 h-7 flex items-center justify-center text-neutral-500 hover:bg-rose-500 hover:text-white rounded">
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </header>
  );
}
