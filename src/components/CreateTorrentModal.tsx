import React, { useState } from 'react';
import { 
  X, 
  Share2, 
  FileUp, 
  Hash, 
  Copy, 
  Check, 
  Download, 
  Sliders, 
  Radio, 
  ShieldAlert, 
  ArrowRight, 
  FolderPlus,
  Play,
  CheckCircle2,
  Lock
} from 'lucide-react';
import { DownloadTask } from '../types';
import { formatBytes } from '../utils/formatters';

interface CreateTorrentModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSeedCreatedTorrent: (torrentTask: Partial<DownloadTask>) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

const DEFAULT_TRACKERS = [
  'udp://tracker.opentrackers.org:1337/announce',
  'udp://tracker.coppersurfer.tk:6969/announce',
  'udp://tracker.openbittorrent.com:6969/announce',
  'udp://tracker.internetwarriors.net:1337/announce',
  'http://tracker.cyberia.is:6969/announce',
];

export function CreateTorrentModal({
  isOpen,
  onClose,
  onSeedCreatedTorrent,
  onShowToast,
}: CreateTorrentModalProps) {
  const [fileName, setFileName] = useState('Ghost_LiveOS_v3.2_x64.iso');
  const [fileSizeBytes, setFileSizeBytes] = useState(3758096384); // ~3.5 GB
  const [pieceSizeKb, setPieceSizeKb] = useState(2048); // 2 MB
  const [trackersText, setTrackersText] = useState(DEFAULT_TRACKERS.join('\n'));
  const [webSeed, setWebSeed] = useState('https://distro.ghostdownloader.net/iso/Ghost_LiveOS_v3.2_x64.iso');
  const [comment, setComment] = useState('Packaged with Ghost Downloader 3 Torrent Engine');
  const [isPrivate, setIsPrivate] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [currentStep, setCurrentStep] = useState<1 | 2 | 3>(1);

  if (!isOpen) return null;

  // Calculated info hash
  const totalPieces = Math.ceil(fileSizeBytes / (pieceSizeKb * 1024));
  const mockInfoHash = 'a492f801c40284d720a94821e905cb834f8a921d';
  
  const encodedName = encodeURIComponent(fileName);
  const trackersList = trackersText.split('\n').map((t) => t.trim()).filter(Boolean);
  const trackerParams = trackersList.map((t) => `&tr=${encodeURIComponent(t)}`).join('');
  const magnetUri = `magnet:?xt=urn:btih:${mockInfoHash}&dn=${encodedName}${trackerParams}`;

  const handleCopyMagnet = () => {
    navigator.clipboard.writeText(magnetUri);
    setIsCopied(true);
    onShowToast('Magnet link copied to clipboard!', 'success');
    setTimeout(() => setIsCopied(false), 2000);
  };

  const handleExportTorrentFile = () => {
    // Generate simulated .torrent file payload
    const torrentContent = `d8:announce${trackersList[0]?.length || 35}:${trackersList[0] || 'udp://tracker.opentrackers.org'}4:infod6:lengthi${fileSizeBytes}e4:name${fileName.length}:${fileName}12:piece lengthi${pieceSizeKb * 1024}e6:pieces${totalPieces * 20}:SIMULATED_BINARY_HASHES_GHOST_DOWNLOADERee`;
    const blob = new Blob([torrentContent], { type: 'application/x-bittorrent' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${fileName}.torrent`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    onShowToast(`Exported ${fileName}.torrent successfully!`, 'success');
  };

  const handleSeedNow = () => {
    onSeedCreatedTorrent({
      name: fileName,
      url: magnetUri,
      protocol: 'torrent',
      category: 'torrent',
      totalBytes: fileSizeBytes,
      downloadedBytes: fileSizeBytes,
      status: 'downloading', // seeding mode
      speed: 0,
      uploadSpeed: 4500000, // 4.5 MB/s initial upload seed speed
      seeders: 1,
      peers: 12,
    });
    onShowToast(`Torrent created! Now seeding "${fileName}" to the peer swarm.`, 'success');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-2xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-purple-500/10 text-purple-600 dark:text-purple-400">
              <Share2 className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Create Torrent & Magnet Link Wizard</span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Generate BitTorrent metainfo, magnet URIs, and seed local files directly to the swarm
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

        {/* Step Indicator */}
        <div className="flex items-center justify-between px-6 py-2.5 bg-neutral-100/60 dark:bg-neutral-800/60 border-b border-neutral-200 dark:border-neutral-800 text-xs">
          <button
            onClick={() => setCurrentStep(1)}
            className={`flex items-center space-x-1.5 font-semibold cursor-pointer ${
              currentStep === 1 ? 'text-purple-600 dark:text-purple-400' : 'text-neutral-500'
            }`}
          >
            <span className="w-5 h-5 rounded-full bg-purple-500/20 flex items-center justify-center text-[10px]">1</span>
            <span>Payload & Source</span>
          </button>
          <ArrowRight className="w-3.5 h-3.5 text-neutral-400" />
          <button
            onClick={() => setCurrentStep(2)}
            className={`flex items-center space-x-1.5 font-semibold cursor-pointer ${
              currentStep === 2 ? 'text-purple-600 dark:text-purple-400' : 'text-neutral-500'
            }`}
          >
            <span className="w-5 h-5 rounded-full bg-purple-500/20 flex items-center justify-center text-[10px]">2</span>
            <span>Trackers & Flags</span>
          </button>
          <ArrowRight className="w-3.5 h-3.5 text-neutral-400" />
          <button
            onClick={() => setCurrentStep(3)}
            className={`flex items-center space-x-1.5 font-semibold cursor-pointer ${
              currentStep === 3 ? 'text-purple-600 dark:text-purple-400' : 'text-neutral-500'
            }`}
          >
            <span className="w-5 h-5 rounded-full bg-purple-500/20 flex items-center justify-center text-[10px]">3</span>
            <span>Magnet & Export</span>
          </button>
        </div>

        {/* Wizard Content */}
        <div className="p-6 overflow-y-auto space-y-4 text-xs">
          {/* STEP 1: Payload selection */}
          {currentStep === 1 && (
            <div className="space-y-4 animate-in fade-in duration-150">
              <div className="p-4 rounded-2xl border-2 border-dashed border-neutral-300 dark:border-neutral-700 bg-neutral-50/50 dark:bg-neutral-800/30 flex flex-col items-center justify-center text-center p-6 space-y-2">
                <FileUp className="w-8 h-8 text-purple-500" />
                <p className="font-bold text-neutral-800 dark:text-neutral-200">
                  Select Source File or Directory to Package
                </p>
                <p className="text-[11px] text-neutral-400">
                  Choose a file from your device or use the pre-configured release package
                </p>
                <label className="mt-2 px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold cursor-pointer shadow-sm">
                  <span>Browse Local File</span>
                  <input
                    type="file"
                    className="hidden"
                    onChange={(e) => {
                      const file = e.target.files?.[0];
                      if (file) {
                        setFileName(file.name);
                        setFileSizeBytes(file.size);
                        onShowToast(`Loaded file: ${file.name} (${formatBytes(file.size)})`, 'info');
                      }
                    }}
                  />
                </label>
              </div>

              <div className="space-y-3 pt-2">
                <div>
                  <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                    Torrent Payload Name:
                  </label>
                  <input
                    type="text"
                    value={fileName}
                    onChange={(e) => setFileName(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                      File Size:
                    </label>
                    <div className="p-2.5 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 font-mono text-neutral-800 dark:text-neutral-200">
                      {formatBytes(fileSizeBytes)} ({fileSizeBytes.toLocaleString()} bytes)
                    </div>
                  </div>

                  <div>
                    <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                      Piece Size:
                    </label>
                    <select
                      value={pieceSizeKb}
                      onChange={(e) => setPieceSizeKb(Number(e.target.value))}
                      className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs"
                    >
                      <option value={512}>512 KB (Small files)</option>
                      <option value={1024}>1024 KB / 1 MB</option>
                      <option value={2048}>2048 KB / 2 MB (Optimal)</option>
                      <option value={4096}>4096 KB / 4 MB (Large files)</option>
                      <option value={8192}>8192 KB / 8 MB</option>
                    </select>
                  </div>
                </div>

                <div className="p-3 rounded-xl bg-purple-50 dark:bg-purple-950/20 border border-purple-200 dark:border-purple-800 text-[11px] text-purple-700 dark:text-purple-300 font-mono flex justify-between">
                  <span>Calculated Pieces Count:</span>
                  <span className="font-bold">{totalPieces} pieces ({pieceSizeKb} KB each)</span>
                </div>
              </div>
            </div>
          )}

          {/* STEP 2: Trackers & Settings */}
          {currentStep === 2 && (
            <div className="space-y-4 animate-in fade-in duration-150">
              <div>
                <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                  Tiered Tracker Announce URLs (One per line):
                </label>
                <textarea
                  rows={5}
                  value={trackersText}
                  onChange={(e) => setTrackersText(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs"
                />
              </div>

              <div>
                <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                  Web Seed / HTTP Fallback URL (Optional):
                </label>
                <input
                  type="text"
                  value={webSeed}
                  onChange={(e) => setWebSeed(e.target.value)}
                  placeholder="https://example.com/direct-file.iso"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs"
                />
              </div>

              <div>
                <label className="block font-semibold text-neutral-700 dark:text-neutral-300 mb-1">
                  Comment:
                </label>
                <input
                  type="text"
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs"
                />
              </div>

              <div className="p-3 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 space-y-2">
                <label className="flex items-center space-x-2 text-neutral-700 dark:text-neutral-200 cursor-pointer font-medium">
                  <input
                    type="checkbox"
                    checked={isPrivate}
                    onChange={(e) => setIsPrivate(e.target.checked)}
                    className="rounded border-neutral-300 text-purple-600 focus:ring-purple-500"
                  />
                  <span>Private Torrent Flag (Disable DHT & Peer Exchange)</span>
                </label>
                <p className="text-[10px] text-neutral-400 pl-6">
                  Recommended for private tracker communities to restrict peer discovery exclusively to announced tracker tiers.
                </p>
              </div>
            </div>
          )}

          {/* STEP 3: Magnet & Export */}
          {currentStep === 3 && (
            <div className="space-y-4 animate-in fade-in duration-150">
              {/* Info Hash Card */}
              <div className="p-4 rounded-xl border border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-800/40 space-y-1.5">
                <div className="flex items-center space-x-2 text-neutral-500 dark:text-neutral-400 font-medium">
                  <Hash className="w-4 h-4 text-purple-500" />
                  <span>BitTorrent Info-Hash (SHA-1):</span>
                </div>
                <p className="font-mono text-xs font-bold text-neutral-900 dark:text-neutral-100 bg-white dark:bg-neutral-900 p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 select-all">
                  {mockInfoHash}
                </p>
              </div>

              {/* Magnet URI Card */}
              <div className="space-y-1.5">
                <div className="flex items-center justify-between">
                  <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                    Generated Magnet Link:
                  </label>
                  <button
                    onClick={handleCopyMagnet}
                    className="flex items-center space-x-1 text-purple-600 dark:text-purple-400 hover:underline font-semibold cursor-pointer"
                  >
                    {isCopied ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{isCopied ? 'Copied!' : 'Copy URI'}</span>
                  </button>
                </div>
                <textarea
                  readOnly
                  rows={4}
                  value={magnetUri}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-900 font-mono text-[11px] text-neutral-600 dark:text-neutral-300 select-all"
                />
              </div>

              {/* Action Buttons */}
              <div className="grid grid-cols-2 gap-3 pt-2">
                <button
                  type="button"
                  onClick={handleExportTorrentFile}
                  className="flex items-center justify-center space-x-2 p-3 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 hover:bg-neutral-100 dark:hover:bg-neutral-700 font-semibold text-neutral-800 dark:text-neutral-200 transition-all cursor-pointer shadow-sm"
                >
                  <Download className="w-4 h-4 text-purple-500" />
                  <span>Export .torrent File</span>
                </button>

                <button
                  type="button"
                  onClick={handleSeedNow}
                  className="flex items-center justify-center space-x-2 p-3 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold transition-all cursor-pointer shadow-sm active:scale-95"
                >
                  <Play className="w-4 h-4" />
                  <span>Seed Immediately</span>
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer Navigation */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          {currentStep > 1 ? (
            <button
              onClick={() => setCurrentStep((prev) => (prev - 1) as any)}
              className="px-4 py-2 rounded-xl text-xs font-medium text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
            >
              Back
            </button>
          ) : (
            <div />
          )}

          {currentStep < 3 ? (
            <button
              onClick={() => setCurrentStep((prev) => (prev + 1) as any)}
              className="flex items-center space-x-1.5 px-5 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 active:scale-95 text-white text-xs font-semibold shadow-sm transition-all cursor-pointer"
            >
              <span>Next Step</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          ) : (
            <button
              onClick={onClose}
              className="px-5 py-2 rounded-xl bg-neutral-800 dark:bg-neutral-200 hover:bg-neutral-700 dark:hover:bg-neutral-300 text-white dark:text-neutral-900 text-xs font-semibold shadow-sm transition-all cursor-pointer"
            >
              Done
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
