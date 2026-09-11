import { useState } from 'react';
import { X, Copy, Check, Hash, Layers, Globe, FileCheck, HardDriveDownload } from 'lucide-react';
import { DownloadTask } from '../types';
import { formatBytes, formatSpeed, getProtocolBadge } from '../utils/formatters';
import { ChunkVisualizer } from './ChunkVisualizer';

interface TaskDetailsModalProps {
  task: DownloadTask | null;
  onClose: () => void;
  onSaveToDisk: (task: DownloadTask) => void;
}

export function TaskDetailsModal({ task, onClose, onSaveToDisk }: TaskDetailsModalProps) {
  const [activeTab, setActiveTab] = useState<'overview' | 'chunks' | 'headers'>('overview');
  const [copiedMd5, setCopiedMd5] = useState(false);
  const [copiedSha256, setCopiedSha256] = useState(false);
  const [copiedUrl, setCopiedUrl] = useState(false);

  if (!task) return null;

  const protocolBadge = getProtocolBadge(task.protocol);

  const handleCopy = (text: string, type: 'md5' | 'sha' | 'url') => {
    navigator.clipboard.writeText(text);
    if (type === 'md5') {
      setCopiedMd5(true);
      setTimeout(() => setCopiedMd5(false), 2000);
    } else if (type === 'sha') {
      setCopiedSha256(true);
      setTimeout(() => setCopiedSha256(false), 2000);
    } else {
      setCopiedUrl(true);
      setTimeout(() => setCopiedUrl(false), 2000);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="w-full max-w-2xl bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden flex flex-col max-h-[85vh]">
        {/* Header */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
          <div className="flex items-center space-x-2.5 min-w-0">
            <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full border ${protocolBadge.bgLight} ${protocolBadge.bgDark}`}>
              {protocolBadge.label}
            </span>
            <h2 className="text-base font-semibold text-neutral-800 dark:text-neutral-100 truncate" title={task.name}>
              {task.name}
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-md text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b border-neutral-200 dark:border-neutral-700 px-4 bg-neutral-50/50 dark:bg-neutral-800/30">
          <button
            onClick={() => setActiveTab('overview')}
            className={`flex items-center space-x-2 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              activeTab === 'overview'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
            }`}
          >
            <FileCheck className="w-3.5 h-3.5" />
            <span>Overview & Hashes</span>
          </button>
          <button
            onClick={() => setActiveTab('chunks')}
            className={`flex items-center space-x-2 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              activeTab === 'chunks'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            <span>Chunks Visualizer</span>
          </button>
          <button
            onClick={() => setActiveTab('headers')}
            className={`flex items-center space-x-2 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              activeTab === 'headers'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200'
            }`}
          >
            <Globe className="w-3.5 h-3.5" />
            <span>HTTP & Headers</span>
          </button>
        </div>

        {/* Content Body */}
        <div className="p-5 overflow-y-auto flex-1 text-xs space-y-4">
          {activeTab === 'overview' && (
            <div className="space-y-4">
              {/* Properties Grid */}
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                  <div className="text-neutral-400 text-[10px] uppercase font-semibold">Total Size</div>
                  <div className="text-sm font-mono font-semibold text-neutral-800 dark:text-neutral-200 mt-0.5">
                    {formatBytes(task.totalBytes)} ({task.totalBytes.toLocaleString()} bytes)
                  </div>
                </div>

                <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                  <div className="text-neutral-400 text-[10px] uppercase font-semibold">Downloaded Progress</div>
                  <div className="text-sm font-mono font-semibold text-sky-600 dark:text-sky-400 mt-0.5">
                    {formatBytes(task.downloadedBytes)} ({Math.round((task.downloadedBytes / task.totalBytes) * 100)}%)
                  </div>
                </div>

                <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                  <div className="text-neutral-400 text-[10px] uppercase font-semibold">MIME Type</div>
                  <div className="text-sm font-mono text-neutral-800 dark:text-neutral-200 mt-0.5">
                    {task.mimeType || 'application/octet-stream'}
                  </div>
                </div>

                <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                  <div className="text-neutral-400 text-[10px] uppercase font-semibold">Connection Threads</div>
                  <div className="text-sm font-mono text-neutral-800 dark:text-neutral-200 mt-0.5">
                    {task.connections} active connections
                  </div>
                </div>
              </div>

              {/* Save Location */}
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-neutral-500">Destination Path</label>
                <div className="p-2 rounded bg-neutral-100 dark:bg-neutral-800 font-mono text-neutral-700 dark:text-neutral-300 text-[11px]">
                  {task.savePath}
                </div>
              </div>

              {/* Checksums */}
              <div className="space-y-2 pt-2 border-t border-neutral-200 dark:border-neutral-700">
                <div className="flex items-center space-x-1.5 font-semibold text-neutral-700 dark:text-neutral-200">
                  <Hash className="w-3.5 h-3.5 text-sky-500" />
                  <span>Integrity & File Hashes</span>
                </div>

                <div className="space-y-2 font-mono">
                  {/* MD5 */}
                  <div className="flex items-center justify-between p-2 rounded bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                    <div>
                      <span className="text-[10px] font-bold text-neutral-400 mr-2">MD5:</span>
                      <span className="text-neutral-700 dark:text-neutral-300">{task.md5Hash || '8b7fca29104bde978cf2340156ef9a82'}</span>
                    </div>
                    <button
                      onClick={() => handleCopy(task.md5Hash || '8b7fca29104bde978cf2340156ef9a82', 'md5')}
                      className="text-neutral-400 hover:text-sky-500 p-1 cursor-pointer"
                    >
                      {copiedMd5 ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
                    </button>
                  </div>

                  {/* SHA-256 */}
                  <div className="flex items-center justify-between p-2 rounded bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                    <div className="truncate mr-2">
                      <span className="text-[10px] font-bold text-neutral-400 mr-2">SHA-256:</span>
                      <span className="text-neutral-700 dark:text-neutral-300 truncate">
                        {task.sha256Hash || 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855'}
                      </span>
                    </div>
                    <button
                      onClick={() => handleCopy(task.sha256Hash || 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'sha')}
                      className="text-neutral-400 hover:text-sky-500 p-1 cursor-pointer"
                    >
                      {copiedSha256 ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'chunks' && (
            <ChunkVisualizer chunks={task.chunks} totalBytes={task.totalBytes} />
          )}

          {activeTab === 'headers' && (
            <div className="space-y-3 font-mono">
              <div className="space-y-1">
                <div className="flex items-center justify-between text-[11px] font-semibold text-neutral-500">
                  <span>Source URL</span>
                  <button
                    onClick={() => handleCopy(task.url, 'url')}
                    className="flex items-center space-x-1 text-sky-600 hover:underline cursor-pointer"
                  >
                    {copiedUrl ? <Check className="w-3 h-3" /> : <Copy className="w-3 h-3" />}
                    <span>{copiedUrl ? 'Copied' : 'Copy'}</span>
                  </button>
                </div>
                <div className="p-2 rounded bg-neutral-100 dark:bg-neutral-800 break-all text-neutral-700 dark:text-neutral-300 text-[11px]">
                  {task.url}
                </div>
              </div>

              {task.referer && (
                <div className="space-y-1">
                  <span className="text-[11px] font-semibold text-neutral-500">Referer</span>
                  <div className="p-2 rounded bg-neutral-100 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 text-[11px]">
                    {task.referer}
                  </div>
                </div>
              )}

              <div className="space-y-1">
                <span className="text-[11px] font-semibold text-neutral-500">User-Agent (Emulated Browser Fingerprint)</span>
                <div className="p-2 rounded bg-neutral-100 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 text-[11px]">
                  {task.userAgent || 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 GhostDownloader/3.0'}
                </div>
              </div>

              {task.headers && Object.keys(task.headers).length > 0 && (
                <div className="space-y-1">
                  <span className="text-[11px] font-semibold text-neutral-500">HTTP Response Headers</span>
                  <div className="p-2.5 rounded bg-neutral-100 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 text-[11px] space-y-1">
                    {Object.entries(task.headers).map(([key, val]) => (
                      <div key={key}>
                        <span className="text-sky-600 dark:text-sky-400 font-semibold">{key}: </span>
                        <span>{val}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50/70 dark:bg-neutral-800/60 flex items-center justify-between">
          <button
            onClick={() => onSaveToDisk(task)}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white font-medium text-xs shadow-xs transition-colors cursor-pointer"
          >
            <HardDriveDownload className="w-3.5 h-3.5" />
            <span>Save to Browser Downloads</span>
          </button>

          <button
            onClick={onClose}
            className="px-4 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-200/60 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 text-xs font-medium transition-colors cursor-pointer"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
