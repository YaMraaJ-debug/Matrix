import React, { useState } from 'react';
import { 
  X, 
  Smartphone, 
  QrCode, 
  Copy, 
  Check, 
  RotateCw, 
  Wifi, 
  ShieldCheck, 
  Pause, 
  Play, 
  Plus, 
  Download, 
  Send,
  ExternalLink,
  Laptop,
  CheckCircle2
} from 'lucide-react';
import { DownloadTask, CategoryType } from '../types';
import { formatBytes, formatSpeed } from '../utils/formatters';

interface RemoteWebUiModalProps {
  isOpen: boolean;
  onClose: () => void;
  tasks: DownloadTask[];
  onPauseAll: () => void;
  onResumeAll: () => void;
  onPushTaskFromMobile: (task: { name: string; url: string; category: CategoryType; totalBytes: number }) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

export function RemoteWebUiModal({
  isOpen,
  onClose,
  tasks,
  onPauseAll,
  onResumeAll,
  onPushTaskFromMobile,
  onShowToast,
}: RemoteWebUiModalProps) {
  const [activeTab, setActiveTab] = useState<'qr' | 'simulator'>('qr');
  const [pin, setPin] = useState('842 910');
  const [copiedUrl, setCopiedUrl] = useState(false);
  const [isServerEnabled, setIsServerEnabled] = useState(true);

  // Phone Simulator state
  const [mobilePushUrl, setMobilePushUrl] = useState('https://releases.ubuntu.com/noble/ubuntu-24.04.1-desktop-amd64.iso');
  const [mobilePushName, setMobilePushName] = useState('Ubuntu_24.04_Desktop_LTS.iso');

  if (!isOpen) return null;

  const localUrl = `http://192.168.1.105:3000/remote?pin=${pin.replace(/\s+/g, '')}`;

  const handleCopyUrl = () => {
    navigator.clipboard.writeText(localUrl);
    setCopiedUrl(true);
    onShowToast('Remote Web-UI URL copied to clipboard!', 'success');
    setTimeout(() => setCopiedUrl(false), 2000);
  };

  const handleRegeneratePin = () => {
    const p1 = Math.floor(100 + Math.random() * 900);
    const p2 = Math.floor(100 + Math.random() * 900);
    setPin(`${p1} ${p2}`);
    onShowToast('Generated new remote authentication PIN', 'info');
  };

  const handleSendFromPhone = () => {
    if (!mobilePushUrl.trim()) return;

    onPushTaskFromMobile({
      name: mobilePushName.trim() || 'Mobile_Queued_File.bin',
      url: mobilePushUrl.trim(),
      category: 'software',
      totalBytes: 5900000000,
    });

    onShowToast(`Pushed "${mobilePushName}" from Mobile Phone to Ghost Downloader!`, 'success');
    setMobilePushName('');
    setMobilePushUrl('');
  };

  const activeDownloading = tasks.filter((t) => t.status === 'downloading');
  const totalSpeed = activeDownloading.reduce((sum, t) => sum + t.speed, 0);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-3xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[92vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-blue-500/10 text-blue-600 dark:text-blue-400">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Remote Web-UI & Mobile Controller</span>
                <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-full bg-blue-100 dark:bg-blue-950 text-blue-700 dark:text-blue-300 font-semibold border border-blue-300 dark:border-blue-800">
                  LAN Remote
                </span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Control active downloads and push new links wirelessly from your phone or tablet
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

        {/* Tab Navigation */}
        <div className="flex items-center space-x-4 px-6 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/40 dark:bg-neutral-900/50">
          <button
            onClick={() => setActiveTab('qr')}
            className={`py-3 text-xs font-semibold border-b-2 transition-colors cursor-pointer flex items-center space-x-2 ${
              activeTab === 'qr'
                ? 'border-blue-600 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <QrCode className="w-3.5 h-3.5" />
            <span>Scan QR & Pair Device</span>
          </button>

          <button
            onClick={() => setActiveTab('simulator')}
            className={`py-3 text-xs font-semibold border-b-2 transition-colors cursor-pointer flex items-center space-x-2 ${
              activeTab === 'simulator'
                ? 'border-blue-600 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Smartphone className="w-3.5 h-3.5" />
            <span>Interactive Mobile Simulator</span>
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto text-xs">
          {activeTab === 'qr' && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center animate-in fade-in duration-150">
              {/* QR Code Container */}
              <div className="flex flex-col items-center justify-center p-6 rounded-2xl border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800/80 shadow-sm space-y-4">
                <div className="p-3 bg-white rounded-xl shadow border border-neutral-200 flex items-center justify-center">
                  {/* Stylized QR Code SVG */}
                  <svg
                    viewBox="0 0 160 160"
                    className="w-44 h-44 text-neutral-900"
                    fill="currentColor"
                  >
                    <rect width="160" height="160" fill="white" />
                    {/* Position detection patterns */}
                    <path d="M10 10 h50 v50 h-50 z M20 20 v30 h30 v-30 z M25 25 h20 v20 h-20 z" fill="#0f172a" />
                    <path d="M100 10 h50 v50 h-50 z M110 20 v30 h30 v-30 z M115 25 h20 v20 h-20 z" fill="#0f172a" />
                    <path d="M10 100 h50 v50 h-50 z M20 110 v30 h30 v-30 z M25 115 h20 v20 h-20 z" fill="#0f172a" />
                    
                    {/* Simulated data bits */}
                    <rect x="70" y="20" width="10" height="20" fill="#0f172a" />
                    <rect x="85" y="15" width="8" height="10" fill="#0f172a" />
                    <rect x="70" y="50" width="15" height="10" fill="#0f172a" />
                    <rect x="20" y="70" width="20" height="10" fill="#0f172a" />
                    <rect x="50" y="70" width="10" height="20" fill="#0f172a" />
                    <rect x="70" y="70" width="20" height="20" fill="#0284c7" />
                    <rect x="100" y="70" width="10" height="15" fill="#0f172a" />
                    <rect x="120" y="75" width="25" height="10" fill="#0f172a" />
                    <rect x="70" y="100" width="15" height="10" fill="#0f172a" />
                    <rect x="90" y="95" width="10" height="30" fill="#0f172a" />
                    <rect x="110" y="100" width="20" height="10" fill="#0f172a" />
                    <rect x="70" y="130" width="30" height="15" fill="#0f172a" />
                    <rect x="115" y="125" width="15" height="20" fill="#0f172a" />
                    <rect x="140" y="110" width="10" height="30" fill="#0f172a" />
                  </svg>
                </div>

                <div className="text-center space-y-1">
                  <p className="font-bold text-neutral-800 dark:text-neutral-100">
                    Scan with Phone Camera
                  </p>
                  <p className="text-[11px] text-neutral-400">
                    Ensure phone is connected to the same Wi-Fi network
                  </p>
                </div>
              </div>

              {/* Pairing Info & Credentials */}
              <div className="space-y-4">
                <div className="p-4 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800/40 space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-neutral-500 font-medium">Local LAN Gateway:</span>
                    <span className="flex items-center space-x-1.5 text-emerald-600 dark:text-emerald-400 font-bold font-mono">
                      <Wifi className="w-3.5 h-3.5" />
                      <span>Online (Port 3000)</span>
                    </span>
                  </div>

                  <div className="flex items-center space-x-2">
                    <input
                      type="text"
                      readOnly
                      value={localUrl}
                      className="flex-1 p-2 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-900 font-mono text-[11px] select-all"
                    />
                    <button
                      onClick={handleCopyUrl}
                      className="p-2 rounded-lg bg-blue-600 hover:bg-blue-500 text-white cursor-pointer"
                      title="Copy URL"
                    >
                      {copiedUrl ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                <div className="p-4 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800/40 flex items-center justify-between">
                  <div>
                    <span className="block text-neutral-400 text-[11px]">Authentication PIN:</span>
                    <span className="text-xl font-mono font-black text-neutral-900 dark:text-neutral-100 tracking-wider">
                      {pin}
                    </span>
                  </div>

                  <button
                    onClick={handleRegeneratePin}
                    className="flex items-center space-x-1 px-3 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-700 hover:bg-neutral-200 dark:hover:bg-neutral-700 font-semibold cursor-pointer"
                  >
                    <RotateCw className="w-3.5 h-3.5" />
                    <span>Regenerate</span>
                  </button>
                </div>

                <div className="p-3 rounded-xl bg-blue-50 dark:bg-blue-950/20 border border-blue-200 dark:border-blue-800/60 text-[11px] text-blue-700 dark:text-blue-300 space-y-1">
                  <p className="font-semibold flex items-center space-x-1.5">
                    <ShieldCheck className="w-4 h-4" />
                    <span>Zero-Configuration Local Tunnel</span>
                  </p>
                  <p>
                    No port forwarding required. Works seamlessly over local Wi-Fi, Hotspot, and LAN subnets.
                  </p>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'simulator' && (
            <div className="flex justify-center animate-in fade-in duration-150">
              {/* Mobile Phone Mockup */}
              <div className="w-full max-w-sm rounded-[32px] p-4 bg-neutral-900 border-4 border-neutral-800 shadow-2xl text-neutral-100 space-y-3">
                {/* Phone Notch & Header */}
                <div className="flex justify-between items-center px-3 py-1 border-b border-neutral-800 text-[10px] text-neutral-400">
                  <span>9:41 AM</span>
                  <div className="w-16 h-3 bg-neutral-800 rounded-full" />
                  <span className="flex items-center space-x-1">
                    <Wifi className="w-3 h-3 text-emerald-400" />
                    <span>5G</span>
                  </span>
                </div>

                {/* Mobile App Header */}
                <div className="flex items-center justify-between px-2 pt-1">
                  <div>
                    <h3 className="font-bold text-sm text-white">Ghost Remote</h3>
                    <p className="text-[10px] text-neutral-400">Connected to PC (192.168.1.105)</p>
                  </div>
                  <div className="text-right font-mono">
                    <span className="text-xs font-bold text-emerald-400">{formatSpeed(totalSpeed)}</span>
                  </div>
                </div>

                {/* Global Controls */}
                <div className="grid grid-cols-2 gap-2">
                  <button
                    onClick={onPauseAll}
                    className="flex items-center justify-center space-x-1.5 py-2 rounded-xl bg-neutral-800 hover:bg-neutral-700 text-xs font-semibold cursor-pointer"
                  >
                    <Pause className="w-3.5 h-3.5 text-amber-400" />
                    <span>Pause All</span>
                  </button>
                  <button
                    onClick={onResumeAll}
                    className="flex items-center justify-center space-x-1.5 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-xs font-semibold cursor-pointer"
                  >
                    <Play className="w-3.5 h-3.5 text-white" />
                    <span>Resume All</span>
                  </button>
                </div>

                {/* Mobile Push Download Input */}
                <div className="p-2.5 rounded-xl bg-neutral-800/80 border border-neutral-700/60 space-y-2">
                  <span className="text-[10px] font-bold text-neutral-300 block">Push Download Link to PC:</span>
                  <input
                    type="text"
                    value={mobilePushName}
                    onChange={(e) => setMobilePushName(e.target.value)}
                    placeholder="File Name (e.g. Movie.mp4)..."
                    className="w-full p-1.5 rounded-lg bg-neutral-900 border border-neutral-700 text-[11px] text-white font-mono"
                  />
                  <div className="flex space-x-1.5">
                    <input
                      type="text"
                      value={mobilePushUrl}
                      onChange={(e) => setMobilePushUrl(e.target.value)}
                      placeholder="Paste download URL..."
                      className="flex-1 p-1.5 rounded-lg bg-neutral-900 border border-neutral-700 text-[11px] text-white font-mono"
                    />
                    <button
                      onClick={handleSendFromPhone}
                      className="px-3 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white cursor-pointer"
                    >
                      <Send className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                {/* Live Download Queue Preview */}
                <div className="space-y-1.5 max-h-48 overflow-y-auto pr-1">
                  <span className="text-[10px] text-neutral-400 font-semibold uppercase">PC Queue ({tasks.length})</span>
                  {tasks.slice(0, 4).map((t) => (
                    <div
                      key={t.id}
                      className="p-2 rounded-lg bg-neutral-800/60 border border-neutral-700/50 text-[11px] space-y-1"
                    >
                      <div className="flex justify-between font-semibold truncate">
                        <span className="truncate pr-2">{t.name}</span>
                        <span className="text-[10px] text-neutral-400 font-mono shrink-0">
                          {Math.round((t.downloadedBytes / t.totalBytes) * 100)}%
                        </span>
                      </div>
                      <div className="h-1 w-full bg-neutral-700 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-blue-500"
                          style={{ width: `${(t.downloadedBytes / t.totalBytes) * 100}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <div className="text-xs text-neutral-400">
            Connected Devices: <span className="font-bold text-neutral-900 dark:text-neutral-100">1</span> (iPhone 16 Pro)
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
