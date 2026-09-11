import { DownloadChunk } from '../types';
import { formatBytes, formatSpeed } from '../utils/formatters';

interface ChunkVisualizerProps {
  chunks: DownloadChunk[];
  totalBytes: number;
}

export function ChunkVisualizer({ chunks, totalBytes }: ChunkVisualizerProps) {
  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between text-xs text-neutral-600 dark:text-neutral-300 font-medium">
        <div className="flex items-center space-x-2">
          <span>Multi-Thread Chunk Map</span>
          <span className="px-1.5 py-0.5 rounded bg-sky-100 dark:bg-sky-900/60 text-sky-700 dark:text-sky-300 text-[10px] font-mono">
            {chunks.length} Threads
          </span>
        </div>
        <div className="flex items-center space-x-3 text-[11px]">
          <span className="flex items-center space-x-1">
            <span className="w-2.5 h-2.5 rounded-xs bg-emerald-500 inline-block" />
            <span>Completed</span>
          </span>
          <span className="flex items-center space-x-1">
            <span className="w-2.5 h-2.5 rounded-xs bg-sky-500 inline-block" />
            <span>Active</span>
          </span>
          <span className="flex items-center space-x-1">
            <span className="w-2.5 h-2.5 rounded-xs bg-neutral-200 dark:bg-neutral-700 inline-block" />
            <span>Pending</span>
          </span>
        </div>
      </div>

      {/* Aggregate Chunk Bar */}
      <div className="h-3 w-full bg-neutral-100 dark:bg-neutral-800 rounded-md overflow-hidden flex border border-neutral-200/60 dark:border-neutral-700/60 p-0.5 gap-0.5">
        {chunks.map((chunk) => {
          const chunkTotal = chunk.endByte - chunk.startByte + 1;
          const ratio = Math.min(1, chunk.downloadedBytes / chunkTotal);
          const isDone = chunk.status === 'completed' || ratio >= 0.999;
          
          return (
            <div
              key={chunk.id}
              className="h-full flex-1 rounded-[1px] relative overflow-hidden bg-neutral-200/80 dark:bg-neutral-700/80"
              title={`Thread #${chunk.id + 1}: ${Math.round(ratio * 100)}% (${formatBytes(chunk.downloadedBytes)} / ${formatBytes(chunkTotal)})`}
            >
              <div
                className={`h-full transition-all duration-300 ${
                  isDone 
                    ? 'bg-emerald-500' 
                    : chunk.status === 'active' 
                      ? 'bg-sky-500 animate-pulse' 
                      : 'bg-neutral-300 dark:bg-neutral-600'
                }`}
                style={{ width: `${ratio * 100}%` }}
              />
            </div>
          );
        })}
      </div>

      {/* Grid of Thread Details */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 max-h-48 overflow-y-auto pr-1">
        {chunks.map((chunk) => {
          const chunkTotal = chunk.endByte - chunk.startByte + 1;
          const pct = Math.min(100, Math.round((chunk.downloadedBytes / chunkTotal) * 100));
          const isDone = chunk.status === 'completed' || pct >= 100;

          return (
            <div
              key={chunk.id}
              className="p-2 rounded border border-neutral-200/70 dark:border-neutral-700/60 bg-neutral-50/50 dark:bg-neutral-800/40 text-[11px]"
            >
              <div className="flex items-center justify-between font-mono">
                <span className="font-semibold text-neutral-700 dark:text-neutral-300">
                  T#{chunk.id + 1}
                </span>
                <span className={`text-[10px] font-medium ${isDone ? 'text-emerald-600 dark:text-emerald-400' : 'text-sky-600 dark:text-sky-400'}`}>
                  {pct}%
                </span>
              </div>

              <div className="w-full bg-neutral-200 dark:bg-neutral-700 h-1.5 rounded-full my-1.5 overflow-hidden">
                <div
                  className={`h-full transition-all duration-200 ${
                    isDone ? 'bg-emerald-500' : 'bg-sky-500'
                  }`}
                  style={{ width: `${pct}%` }}
                />
              </div>

              <div className="flex items-center justify-between text-[10px] text-neutral-400">
                <span>{formatBytes(chunk.downloadedBytes)}</span>
                <span>{chunk.status === 'active' ? formatSpeed(chunk.speed) : isDone ? 'Done' : 'Idle'}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
