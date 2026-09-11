import React, { useState } from 'react';
import { 
  X, 
  Cloud, 
  Zap, 
  Key, 
  CheckCircle2, 
  HardDrive, 
  ArrowRight, 
  Lock, 
  Unlock, 
  ShieldCheck, 
  Plus, 
  Download,
  AlertCircle,
  ExternalLink
} from 'lucide-react';
import { CloudDebridAccount, CategoryType } from '../types';
import { initialCloudDebridAccounts } from '../data/initialData';
import { formatBytes } from '../utils/formatters';

interface CloudDebridModalProps {
  isOpen: boolean;
  onClose: () => void;
  onQueueTask: (task: { name: string; url: string; category: CategoryType; totalBytes: number }, startImmediately: boolean) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

export function CloudDebridModal({
  isOpen,
  onClose,
  onQueueTask,
  onShowToast,
}: CloudDebridModalProps) {
  const [accounts, setAccounts] = useState<CloudDebridAccount[]>(initialCloudDebridAccounts);
  const [activeTab, setActiveTab] = useState<'unrestrictor' | 'accounts'>('unrestrictor');
  
  // Unrestrictor States
  const [inputUrl, setInputUrl] = useState('https://rapidgator.net/file/9842a8b/Unreal_Engine_5_Cinematic_Pack.rar');
  const [selectedProvider, setSelectedProvider] = useState<string>('real_debrid');
  const [isResolving, setIsResolving] = useState(false);
  const [resolvedResult, setResolvedResult] = useState<{
    directUrl: string;
    filename: string;
    sizeBytes: number;
    hoster: string;
    speedEstimate: string;
  } | null>(null);

  if (!isOpen) return null;

  const handleUnrestrict = () => {
    if (!inputUrl.trim()) {
      onShowToast('Please enter a hoster or cloud URL to unrestrict', 'warning');
      return;
    }

    setIsResolving(true);
    setResolvedResult(null);

    setTimeout(() => {
      setIsResolving(false);
      const urlParts = inputUrl.split('/');
      const rawName = urlParts[urlParts.length - 1] || 'Premium_HighSpeed_Package.rar';
      const cleanName = rawName.replace(/[\?#].*$/, '');

      setResolvedResult({
        directUrl: `https://download.real-debrid.com/storage/${Math.random().toString(36).substring(2, 9)}/${cleanName}`,
        filename: cleanName,
        sizeBytes: 4890000000, // 4.89 GB
        hoster: inputUrl.includes('rapidgator') ? 'Rapidgator Premium' : inputUrl.includes('mega') ? 'MEGA Direct' : 'Google Drive High-Speed',
        speedEstimate: '110 MB/s (16 Multithreaded Streams)',
      });

      onShowToast('Direct high-speed link generated with 0s wait time!', 'success');
    }, 1100);
  };

  const handleStartDownload = () => {
    if (!resolvedResult) return;

    onQueueTask(
      {
        name: resolvedResult.filename,
        url: resolvedResult.directUrl,
        category: 'archive',
        totalBytes: resolvedResult.sizeBytes,
      },
      true
    );

    onShowToast(`Downloading ${resolvedResult.filename} via high-speed CDN!`, 'success');
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
            <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
              <Cloud className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Cloud Drive & Debrid Account Manager</span>
                <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-full bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 font-semibold border border-indigo-300 dark:border-indigo-800">
                  Premium Bypass
                </span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Direct high-speed links unrestrictor for Google Drive, MEGA, Rapidgator, and Real-Debrid
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

        {/* Tab Selector */}
        <div className="flex items-center space-x-4 px-6 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/40 dark:bg-neutral-900/50">
          <button
            onClick={() => setActiveTab('unrestrictor')}
            className={`py-3 text-xs font-semibold border-b-2 transition-colors cursor-pointer flex items-center space-x-2 ${
              activeTab === 'unrestrictor'
                ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Unlock className="w-3.5 h-3.5" />
            <span>Link Unrestrictor / Hoster Bypass</span>
          </button>

          <button
            onClick={() => setActiveTab('accounts')}
            className={`py-3 text-xs font-semibold border-b-2 transition-colors cursor-pointer flex items-center space-x-2 ${
              activeTab === 'accounts'
                ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Key className="w-3.5 h-3.5" />
            <span>Connected Accounts ({accounts.length})</span>
          </button>
        </div>

        {/* Modal Content */}
        <div className="p-6 overflow-y-auto space-y-5 text-xs">
          {activeTab === 'unrestrictor' && (
            <div className="space-y-4 animate-in fade-in duration-150">
              <div className="space-y-1.5">
                <label className="block font-semibold text-neutral-700 dark:text-neutral-300">
                  Target File Hoster / Restricted Cloud URL:
                </label>
                <div className="flex items-center space-x-2">
                  <input
                    type="text"
                    value={inputUrl}
                    onChange={(e) => setInputUrl(e.target.value)}
                    placeholder="Paste link from Rapidgator, Mega.nz, Google Drive, 1Fichier, Turbobit..."
                    className="flex-1 p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                  <button
                    onClick={handleUnrestrict}
                    disabled={isResolving}
                    className="flex items-center space-x-1.5 px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 active:scale-95 text-white font-bold transition-all cursor-pointer shadow-sm disabled:opacity-50"
                  >
                    <Unlock className={`w-3.5 h-3.5 ${isResolving ? 'animate-spin' : ''}`} />
                    <span>{isResolving ? 'Unrestricting...' : 'Unrestrict Link'}</span>
                  </button>
                </div>
              </div>

              {/* Provider selection */}
              <div className="flex items-center space-x-2 text-[11px]">
                <span className="text-neutral-400 font-medium">Bypass via:</span>
                {accounts.map((acc) => (
                  <button
                    key={acc.id}
                    onClick={() => setSelectedProvider(acc.provider)}
                    className={`px-3 py-1 rounded-lg border text-[11px] font-medium transition-all cursor-pointer ${
                      selectedProvider === acc.provider
                        ? 'border-indigo-500 bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 font-bold'
                        : 'border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400'
                    }`}
                  >
                    {acc.name}
                  </button>
                ))}
              </div>

              {/* Unrestricted Result Card */}
              {resolvedResult && (
                <div className="p-4 rounded-2xl border border-indigo-200 dark:border-indigo-800 bg-indigo-50/50 dark:bg-indigo-950/20 space-y-3 animate-in fade-in duration-200">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <span className="p-1 rounded bg-indigo-500 text-white">
                        <CheckCircle2 className="w-4 h-4" />
                      </span>
                      <span className="font-bold text-indigo-900 dark:text-indigo-200">
                        Link Successfully Unrestricted!
                      </span>
                    </div>
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-mono bg-indigo-100 dark:bg-indigo-900 text-indigo-700 dark:text-indigo-300 font-bold">
                      {resolvedResult.hoster}
                    </span>
                  </div>

                  <div className="space-y-1">
                    <p className="font-bold text-neutral-900 dark:text-neutral-100 text-sm">
                      {resolvedResult.filename}
                    </p>
                    <p className="text-[11px] text-neutral-500 dark:text-neutral-400 font-mono">
                      File Size: {formatBytes(resolvedResult.sizeBytes)} • Max segments: {resolvedResult.speedEstimate}
                    </p>
                  </div>

                  <div className="p-2.5 rounded-xl bg-white dark:bg-neutral-900 border border-indigo-200 dark:border-indigo-800 font-mono text-[11px] text-indigo-600 dark:text-indigo-400 select-all break-all">
                    {resolvedResult.directUrl}
                  </div>

                  <div className="flex items-center justify-end space-x-2 pt-1">
                    <button
                      onClick={handleStartDownload}
                      className="flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 active:scale-95 text-white font-bold shadow-sm cursor-pointer transition-all"
                    >
                      <Download className="w-4 h-4" />
                      <span>Queue to Ghost Downloader</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'accounts' && (
            <div className="space-y-3 animate-in fade-in duration-150">
              <div className="flex items-center justify-between">
                <p className="text-neutral-500 dark:text-neutral-400 text-xs">
                  Authorize your cloud storage accounts and debrid multi-hosters for unlimited maximum download bandwidth.
                </p>
              </div>

              {accounts.map((acc) => {
                const percentUsed = Math.min(100, Math.round((acc.quotaUsedGb / acc.quotaTotalGb) * 100));
                return (
                  <div
                    key={acc.id}
                    className="p-4 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800/70 space-y-2"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2.5">
                        <HardDrive className="w-4 h-4 text-indigo-500" />
                        <span className="font-bold text-neutral-800 dark:text-neutral-100">
                          {acc.name}
                        </span>
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300">
                          {acc.status.toUpperCase()}
                        </span>
                      </div>

                      <span className="text-[11px] text-neutral-400 font-mono">
                        Expires: {acc.expiresAt}
                      </span>
                    </div>

                    {/* Quota bar */}
                    <div className="space-y-1">
                      <div className="flex justify-between text-[11px] font-mono text-neutral-400">
                        <span>Daily Bandwidth Quota</span>
                        <span>{acc.quotaUsedGb} GB / {acc.quotaTotalGb} GB ({percentUsed}%)</span>
                      </div>
                      <div className="h-1.5 w-full bg-neutral-200 dark:bg-neutral-700 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-indigo-500 rounded-full"
                          style={{ width: `${percentUsed}%` }}
                        />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <div className="text-xs text-neutral-400 flex items-center space-x-1.5">
            <ShieldCheck className="w-4 h-4 text-indigo-500" />
            <span>End-to-end token encryption with automatic token refresh.</span>
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
