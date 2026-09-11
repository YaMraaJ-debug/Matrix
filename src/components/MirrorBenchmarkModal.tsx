import React, { useState } from 'react';
import { 
  X, 
  Gauge, 
  Activity, 
  Zap, 
  CheckCircle2, 
  Server, 
  Globe, 
  ArrowRight, 
  RefreshCw,
  Sparkles,
  ShieldCheck,
  Radio
} from 'lucide-react';
import { CdnMirrorNode, DownloadTask } from '../types';
import { initialMirrorNodes } from '../data/initialData';

interface MirrorBenchmarkModalProps {
  isOpen: boolean;
  onClose: () => void;
  task?: DownloadTask | null;
  onApplyFastestMirror?: (taskId: string, mirrorNode: CdnMirrorNode) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

export function MirrorBenchmarkModal({
  isOpen,
  onClose,
  task,
  onApplyFastestMirror,
  onShowToast,
}: MirrorBenchmarkModalProps) {
  const [mirrors, setMirrors] = useState<CdnMirrorNode[]>(initialMirrorNodes);
  const [isBenchmarking, setIsBenchmarking] = useState(false);
  const [selectedMirrorId, setSelectedMirrorId] = useState<string>('mirror-cloudflare');

  if (!isOpen) return null;

  const handleRunBenchmark = () => {
    setIsBenchmarking(true);
    setMirrors((prev) => prev.map((m) => ({ ...m, status: 'testing' })));

    let completed = 0;
    initialMirrorNodes.forEach((node, idx) => {
      setTimeout(() => {
        setMirrors((current) =>
          current.map((m) => {
            if (m.id === node.id) {
              // Add slight realistic jitter
              const jitter = Number((Math.random() * 2 + 0.5).toFixed(1));
              const randomDelta = Math.floor((Math.random() - 0.4) * 8);
              const finalPing = Math.max(12, node.pingMs + randomDelta);
              const finalSpeed = Math.floor(node.speedMbps + (Math.random() - 0.5) * 80);
              return {
                ...m,
                pingMs: finalPing,
                jitterMs: jitter,
                speedMbps: finalSpeed,
                status: finalPing < 40 ? 'online' : finalPing < 80 ? 'online' : 'slow',
              };
            }
            return m;
          })
        );

        completed++;
        if (completed === initialMirrorNodes.length) {
          setIsBenchmarking(false);
          onShowToast('CDN Latency & Bandwidth benchmark completed!', 'success');
        }
      }, (idx + 1) * 320);
    });
  };

  // Sort by pingMs ascending
  const sortedMirrors = [...mirrors].sort((a, b) => a.pingMs - b.pingMs);
  const fastestMirror = sortedMirrors[0];

  const handleApplyMirror = (mirror: CdnMirrorNode) => {
    if (task && onApplyFastestMirror) {
      onApplyFastestMirror(task.id, mirror);
    } else {
      onShowToast(`Selected ${mirror.name} as active CDN routing mirror`, 'success');
    }
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-3xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
              <Activity className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Multi-CDN Mirror Benchmarking & Latency Ping Tester</span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                {task ? `Optimizing download routes for: ${task.name}` : 'Live Anycast CDN edge routing & TTFB latency testing'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Fastest Mirror Recommended Callout */}
        <div className="p-5 border-b border-neutral-200 dark:border-neutral-800 bg-gradient-to-r from-emerald-500/10 via-teal-500/10 to-transparent flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="p-2.5 rounded-xl bg-emerald-500 text-white shadow-sm">
              <Zap className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xs font-bold text-emerald-800 dark:text-emerald-200 uppercase tracking-wider">
                  Fastest Edge Server Detected
                </span>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-800">
                  {fastestMirror.pingMs} ms Ping
                </span>
              </div>
              <p className="text-sm font-bold text-neutral-900 dark:text-neutral-100 mt-0.5">
                {fastestMirror.name} ({fastestMirror.location})
              </p>
              <p className="text-[11px] text-neutral-500 dark:text-neutral-400">
                Bandwidth Capacity: ~{fastestMirror.speedMbps} Mbps • 0% Packet Loss
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={handleRunBenchmark}
              disabled={isBenchmarking}
              className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-white dark:bg-neutral-800 hover:bg-neutral-100 dark:hover:bg-neutral-700 border border-neutral-200 dark:border-neutral-700 text-xs font-semibold shadow-sm transition-all cursor-pointer disabled:opacity-50"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isBenchmarking ? 'animate-spin' : ''}`} />
              <span>{isBenchmarking ? 'Probing Nodes...' : 'Re-test Ping'}</span>
            </button>

            <button
              onClick={() => handleApplyMirror(fastestMirror)}
              className="flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 active:scale-95 text-white text-xs font-bold shadow-sm transition-all cursor-pointer"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Switch to Fastest</span>
            </button>
          </div>
        </div>

        {/* Mirrors Table */}
        <div className="flex-1 overflow-y-auto p-4 space-y-2">
          {sortedMirrors.map((mirror, rank) => {
            const isSelected = selectedMirrorId === mirror.id;
            const isBest = rank === 0;

            const pingColor = 
              mirror.pingMs < 30 ? 'text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/60 border-emerald-200 dark:border-emerald-800' :
              mirror.pingMs < 60 ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/60 border-sky-200 dark:border-sky-800' :
              'text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/60 border-amber-200 dark:border-amber-800';

            return (
              <div
                key={mirror.id}
                onClick={() => setSelectedMirrorId(mirror.id)}
                className={`flex items-center justify-between p-3.5 rounded-xl border transition-all cursor-pointer ${
                  isSelected
                    ? 'border-emerald-500 bg-emerald-50/40 dark:bg-emerald-950/20'
                    : 'border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-800/60 hover:border-neutral-300 dark:hover:border-neutral-700'
                }`}
              >
                <div className="flex items-center space-x-3.5 min-w-0">
                  <div className={`w-7 h-7 rounded-lg flex items-center justify-center font-mono font-bold text-xs ${
                    isBest 
                      ? 'bg-emerald-500 text-white shadow-sm' 
                      : 'bg-neutral-100 dark:bg-neutral-700 text-neutral-600 dark:text-neutral-300'
                  }`}>
                    #{rank + 1}
                  </div>

                  <div className="min-w-0">
                    <div className="flex items-center space-x-2">
                      <span className="font-bold text-xs text-neutral-800 dark:text-neutral-100 truncate">
                        {mirror.name}
                      </span>
                      <span className="text-[10px] font-mono px-1.5 py-0.5 rounded bg-neutral-100 dark:bg-neutral-700 text-neutral-500 dark:text-neutral-400">
                        {mirror.provider}
                      </span>
                      {isBest && (
                        <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 uppercase">
                          Fastest
                        </span>
                      )}
                    </div>

                    <div className="flex items-center space-x-2 text-[11px] text-neutral-400 font-mono mt-0.5">
                      <Globe className="w-3 h-3" />
                      <span>{mirror.location}</span>
                      <span>•</span>
                      <span>Host: {mirror.host}</span>
                    </div>
                  </div>
                </div>

                {/* Right: Latency & Speed stats */}
                <div className="flex items-center space-x-4 shrink-0">
                  <div className="text-right">
                    <span className={`inline-block px-2.5 py-1 rounded-lg text-xs font-mono font-bold border ${pingColor}`}>
                      {mirror.status === 'testing' ? 'Testing...' : `${mirror.pingMs} ms`}
                    </span>
                    <span className="block text-[10px] text-neutral-400 font-mono mt-0.5">
                      ±{mirror.jitterMs}ms jitter
                    </span>
                  </div>

                  <div className="text-right w-24">
                    <span className="text-xs font-mono font-bold text-neutral-800 dark:text-neutral-200">
                      {mirror.speedMbps} Mbps
                    </span>
                    <span className="block text-[10px] text-emerald-600 dark:text-emerald-400">
                      Optimal Route
                    </span>
                  </div>

                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleApplyMirror(mirror);
                    }}
                    className="p-1.5 rounded-lg hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-500 dark:text-neutral-300 transition-colors cursor-pointer"
                    title="Route download through this mirror"
                  >
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <div className="text-xs text-neutral-500 dark:text-neutral-400 flex items-center space-x-1.5">
            <ShieldCheck className="w-4 h-4 text-emerald-500" />
            <span>All mirrors are TLS 1.3 encrypted with HTTP/3 QUIC acceleration.</span>
          </div>

          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-xs font-medium text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
