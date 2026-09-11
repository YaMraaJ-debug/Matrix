import { useMemo } from 'react';
import { ArrowDown, ArrowUp, Zap, Activity } from 'lucide-react';
import { formatSpeed } from '../utils/formatters';

interface SpeedGraphProps {
  speedHistory: number[]; // bytes per second array (latest at end)
  uploadHistory: number[];
  currentSpeed: number;
  currentUpload: number;
  peakSpeed: number;
}

export function SpeedGraph({
  speedHistory,
  uploadHistory,
  currentSpeed,
  currentUpload,
  peakSpeed,
}: SpeedGraphProps) {
  const maxVal = Math.max(peakSpeed, 1024 * 1024 * 5); // minimum scale 5MB/s

  // Generate SVG path for download speed
  const { pathD, areaD } = useMemo(() => {
    if (speedHistory.length < 2) return { pathD: '', areaD: '' };
    const width = 300;
    const height = 48;
    const step = width / (speedHistory.length - 1);

    const points = speedHistory.map((val, idx) => {
      const x = idx * step;
      const y = height - (val / maxVal) * (height - 6) - 3;
      return { x, y };
    });

    const linePath = points.reduce((acc, pt, i) => `${acc} ${i === 0 ? 'M' : 'L'} ${pt.x.toFixed(1)},${pt.y.toFixed(1)}`, '');
    const areaPath = `${linePath} L ${width},${height} L 0,${height} Z`;

    return { pathD: linePath, areaD: areaPath };
  }, [speedHistory, maxVal]);

  const avgSpeed = useMemo(() => {
    if (speedHistory.length === 0) return 0;
    const sum = speedHistory.reduce((a, b) => a + b, 0);
    return Math.round(sum / speedHistory.length);
  }, [speedHistory]);

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between p-3 rounded-xl bg-neutral-50/80 dark:bg-neutral-800/60 border border-neutral-200/70 dark:border-neutral-700/60 gap-3">
      {/* Metrics */}
      <div className="flex items-center space-x-4 text-xs font-mono">
        <div>
          <div className="text-[10px] text-neutral-400 uppercase font-semibold flex items-center space-x-1">
            <ArrowDown className="w-3 h-3 text-emerald-500" />
            <span>Download</span>
          </div>
          <div className="text-sm font-bold text-emerald-600 dark:text-emerald-400">
            {formatSpeed(currentSpeed)}
          </div>
        </div>

        <div className="h-6 w-px bg-neutral-200 dark:bg-neutral-700" />

        <div>
          <div className="text-[10px] text-neutral-400 uppercase font-semibold flex items-center space-x-1">
            <Zap className="w-3 h-3 text-amber-500" />
            <span>Peak</span>
          </div>
          <div className="text-xs font-semibold text-neutral-700 dark:text-neutral-300">
            {formatSpeed(peakSpeed)}
          </div>
        </div>

        <div className="h-6 w-px bg-neutral-200 dark:bg-neutral-700" />

        <div>
          <div className="text-[10px] text-neutral-400 uppercase font-semibold flex items-center space-x-1">
            <Activity className="w-3 h-3 text-sky-500" />
            <span>Average</span>
          </div>
          <div className="text-xs font-semibold text-neutral-700 dark:text-neutral-300">
            {formatSpeed(avgSpeed)}
          </div>
        </div>

        <div className="h-6 w-px bg-neutral-200 dark:bg-neutral-700" />

        <div>
          <div className="text-[10px] text-neutral-400 uppercase font-semibold flex items-center space-x-1">
            <ArrowUp className="w-3 h-3 text-cyan-500" />
            <span>Upload</span>
          </div>
          <div className="text-xs font-semibold text-cyan-600 dark:text-cyan-400">
            {formatSpeed(currentUpload)}
          </div>
        </div>
      </div>

      {/* SVG Real-time Telemetry Graph */}
      <div className="relative w-full sm:w-64 h-12 overflow-hidden rounded-md bg-neutral-100/70 dark:bg-neutral-900/60 border border-neutral-200/50 dark:border-neutral-800">
        <svg
          viewBox="0 0 300 48"
          preserveAspectRatio="none"
          className="w-full h-full"
        >
          <defs>
            <linearGradient id="speedGrad" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#10b981" stopOpacity="0.4" />
              <stop offset="100%" stopColor="#10b981" stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {areaD && (
            <path d={areaD} fill="url(#speedGrad)" />
          )}

          {pathD && (
            <path
              d={pathD}
              fill="none"
              stroke="#10b981"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          )}
        </svg>

        {/* Live Pulse Indicator */}
        <div className="absolute top-1.5 right-2 flex items-center space-x-1 text-[9px] font-mono text-emerald-600 dark:text-emerald-400">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping inline-block" />
          <span>LIVE</span>
        </div>
      </div>
    </div>
  );
}
