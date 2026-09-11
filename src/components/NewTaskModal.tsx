import { useState } from 'react';
import { 
  X, 
  Link, 
  UploadCloud, 
  ListPlus, 
  Sparkles, 
  Sliders, 
  HardDrive, 
  Globe, 
  ShieldCheck, 
  CheckCircle2,
  FileBox
} from 'lucide-react';
import { 
  ProtocolType, 
  CategoryType, 
  DownloadTask 
} from '../types';
import { 
  detectProtocolFromUrl, 
  detectCategoryFromFileName, 
  getProtocolBadge 
} from '../utils/formatters';

interface NewTaskModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddTask: (task: Partial<DownloadTask>, startImmediately: boolean) => void;
  onAddBatchTasks: (urls: string[]) => void;
  autoDetectCategory?: boolean;
}

export function NewTaskModal({ 
  isOpen, 
  onClose, 
  onAddTask, 
  onAddBatchTasks,
  autoDetectCategory = true,
}: NewTaskModalProps) {
  const [mode, setMode] = useState<'single' | 'torrent' | 'batch'>('single');
  const [url, setUrl] = useState('');
  const [customName, setCustomName] = useState('');
  const [protocol, setProtocol] = useState<ProtocolType>('http');
  const [category, setCategory] = useState<CategoryType>('software');
  const [connections, setConnections] = useState<number>(16);
  const [mirror, setMirror] = useState<string>('auto');
  const [quality, setQuality] = useState<string>('1080p60');
  const [startImmediately, setStartImmediately] = useState<boolean>(true);
  const [batchText, setBatchText] = useState('');
  const [torrentFileName, setTorrentFileName] = useState<string>('');

  if (!isOpen) return null;

  const handleUrlChange = (newUrl: string) => {
    setUrl(newUrl);
    if (!newUrl.trim()) return;

    const detected = detectProtocolFromUrl(newUrl);
    setProtocol(detected);

    // Extract file name from URL if possible
    try {
      const urlObj = new URL(newUrl);
      const pathname = urlObj.pathname;
      const lastPart = pathname.split('/').pop();
      if (lastPart && lastPart.includes('.')) {
        setCustomName(decodeURIComponent(lastPart));
        if (autoDetectCategory) {
          setCategory(detectCategoryFromFileName(lastPart));
        }
      } else if (detected === 'bilibili') {
        setCustomName('Bilibili_Video_' + (newUrl.match(/BV[0-9a-zA-Z]+/i)?.[0] || 'Clip') + '.mp4');
        if (autoDetectCategory) {
          setCategory('video');
        }
      } else if (detected === 'youtube') {
        setCustomName('YouTube_HD_Video_' + Date.now().toString().slice(-4) + '.mp4');
        if (autoDetectCategory) {
          setCategory('video');
        }
      } else if (detected === 'torrent') {
        setCustomName('Torrent_Download_' + Date.now().toString().slice(-4) + '.mkv');
        if (autoDetectCategory) {
          setCategory('torrent');
        }
      }
    } catch {
      // url might be magnet or ed2k
      if (newUrl.startsWith('magnet:?')) {
        const dnMatch = newUrl.match(/dn=([^&]+)/);
        if (dnMatch) {
          const decoded = decodeURIComponent(dnMatch[1]);
          setCustomName(decoded);
          if (autoDetectCategory) {
            setCategory(detectCategoryFromFileName(decoded));
          }
        } else {
          setCustomName('Magnet_Transfer_' + Date.now().toString().slice(-4));
          if (autoDetectCategory) {
            setCategory('torrent');
          }
        }
      }
    }
  };

  const handleCreateTask = (immediate: boolean) => {
    if (mode === 'batch') {
      const urls = batchText
        .split('\n')
        .map((u) => u.trim())
        .filter((u) => u.length > 5);
      if (urls.length > 0) {
        onAddBatchTasks(urls);
        onClose();
      }
      return;
    }

    const finalName = customName.trim() || `Download_${Date.now().toString().slice(-6)}`;
    const finalTotalBytes = Math.floor(Math.random() * 800000000) + 150000000; // Realistic size 150MB - 950MB

    onAddTask(
      {
        name: finalName,
        url: url.trim() || 'https://sample-downloads.ghost/file.bin',
        protocol,
        category,
        totalBytes: finalTotalBytes,
        connections,
        mirror: protocol === 'github' ? 'GitCode / CNB Accelerator' : protocol === 'huggingface' ? 'HF-Mirror' : undefined,
        quality: (protocol === 'bilibili' || protocol === 'youtube') ? quality : undefined,
      },
      immediate
    );
    onClose();
  };

  const handleTorrentUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setTorrentFileName(file.name);
      const cleanName = file.name.replace(/\.torrent$/i, '');
      setCustomName(cleanName);
      setUrl(`magnet:?xt=urn:btih:${Math.random().toString(16).slice(2, 10)}${Math.random().toString(16).slice(2, 10)}&dn=${encodeURIComponent(cleanName)}`);
      setProtocol('torrent');
      setCategory('torrent');
    }
  };

  const protocolBadge = getProtocolBadge(protocol);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="w-full max-w-xl bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-7 h-7 rounded-md bg-sky-500 flex items-center justify-center text-white">
              <Link className="w-4 h-4" />
            </div>
            <h2 className="text-base font-semibold text-neutral-800 dark:text-neutral-100">
              New Download Task
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-md text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Input Mode Selector */}
        <div className="flex border-b border-neutral-200 dark:border-neutral-700 px-4 bg-neutral-50/50 dark:bg-neutral-800/30">
          <button
            onClick={() => setMode('single')}
            className={`flex items-center space-x-1.5 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              mode === 'single'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Link className="w-3.5 h-3.5" />
            <span>URL / Magnet Link</span>
          </button>
          <button
            onClick={() => setMode('torrent')}
            className={`flex items-center space-x-1.5 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              mode === 'torrent'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <UploadCloud className="w-3.5 h-3.5" />
            <span>Torrent File</span>
          </button>
          <button
            onClick={() => setMode('batch')}
            className={`flex items-center space-x-1.5 py-2.5 px-3 text-xs font-medium border-b-2 transition-colors cursor-pointer ${
              mode === 'batch'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <ListPlus className="w-3.5 h-3.5" />
            <span>Batch URLs</span>
          </button>
        </div>

        {/* Form Body */}
        <div className="p-5 overflow-y-auto flex-1 text-xs space-y-4">
          {mode === 'single' && (
            <div className="space-y-3">
              <div>
                <div className="flex items-center justify-between mb-1">
                  <label className="font-semibold text-neutral-700 dark:text-neutral-200 flex items-center space-x-1">
                    <span>Source URL / Link</span>
                    <Sparkles className="w-3 h-3 text-amber-500" />
                  </label>
                  {url && (
                    <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${protocolBadge.bgLight} ${protocolBadge.bgDark}`}>
                      Auto-detected: {protocolBadge.label}
                    </span>
                  )}
                </div>
                <textarea
                  rows={2}
                  value={url}
                  onChange={(e) => handleUrlChange(e.target.value)}
                  placeholder="Paste HTTP/HTTPS, Magnet link, M3U8, Bilibili, YouTube, GitHub, Hugging Face, or eD2k URL..."
                  className="w-full p-2.5 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-sky-500 font-mono text-xs"
                />
              </div>

              {/* File Name */}
              <div>
                <label className="block font-semibold text-neutral-700 dark:text-neutral-200 mb-1">
                  File Name
                </label>
                <input
                  type="text"
                  value={customName}
                  onChange={(e) => setCustomName(e.target.value)}
                  placeholder="e.g. video.mp4, archive.zip"
                  className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-sky-500 font-mono text-xs"
                />
              </div>

              {/* Protocol Override & Category */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-semibold text-neutral-700 dark:text-neutral-200 mb-1">
                    Protocol Engine
                  </label>
                  <select
                    value={protocol}
                    onChange={(e) => setProtocol(e.target.value as ProtocolType)}
                    className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-sky-500"
                  >
                    <option value="http">HTTP / HTTPS (Chunked)</option>
                    <option value="torrent">BitTorrent / Magnet</option>
                    <option value="m3u8">M3U8 / HLS Stream</option>
                    <option value="bilibili">Bilibili Video</option>
                    <option value="youtube">YouTube (yt-dlp)</option>
                    <option value="github">GitHub Mirror Accelerator</option>
                    <option value="huggingface">Hugging Face Model</option>
                    <option value="ed2k">eD2k Network</option>
                    <option value="ftp">FTP / FTPS</option>
                  </select>
                </div>

                <div>
                  <div className="flex items-center justify-between mb-1">
                    <label className="block font-semibold text-neutral-700 dark:text-neutral-200">
                      Category Tag
                    </label>
                    <span className="text-[10px] text-neutral-400 font-normal">
                      {autoDetectCategory ? '(Auto-detection on)' : '(Manual)'}
                    </span>
                  </div>
                  <select
                    value={category}
                    onChange={(e) => setCategory(e.target.value as CategoryType)}
                    className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-sky-500"
                  >
                    <option value="software">Software / Program</option>
                    <option value="video">Video & Movies</option>
                    <option value="music">Music & Audio</option>
                    <option value="document">Documents & PDFs</option>
                    <option value="archive">Archives & ISOs</option>
                    <option value="torrent">Torrents</option>
                  </select>
                </div>
              </div>

              {/* Quality Selector for Video Platforms */}
              {(protocol === 'youtube' || protocol === 'bilibili') && (
                <div className="p-3 rounded-lg bg-sky-50/60 dark:bg-sky-950/40 border border-sky-200 dark:border-sky-800 space-y-2">
                  <div className="flex items-center justify-between font-semibold text-sky-800 dark:text-sky-300">
                    <span>Video Quality Preset</span>
                    <span className="text-[10px] bg-sky-200 dark:bg-sky-800 px-1.5 py-0.5 rounded font-mono">Parsed</span>
                  </div>
                  <div className="grid grid-cols-3 gap-2">
                    {['4K UHD (60fps)', '1080p60 Full HD', '720p HD', 'Audio (MP3 320k)'].map((q) => (
                      <button
                        key={q}
                        type="button"
                        onClick={() => setQuality(q)}
                        className={`p-1.5 rounded text-[11px] font-medium border text-center transition-colors cursor-pointer ${
                          quality === q
                            ? 'bg-sky-600 text-white border-sky-600'
                            : 'bg-white dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 border-neutral-200 dark:border-neutral-700'
                        }`}
                      >
                        {q}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Threads / Chunk Slider */}
              <div className="pt-2 border-t border-neutral-200 dark:border-neutral-700">
                <div className="flex items-center justify-between mb-1.5">
                  <label className="font-semibold text-neutral-700 dark:text-neutral-200 flex items-center space-x-1.5">
                    <Sliders className="w-3.5 h-3.5 text-sky-500" />
                    <span>Multi-Thread Connection Chunks:</span>
                  </label>
                  <span className="font-mono font-bold text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950 px-2 py-0.5 rounded">
                    {connections} Threads
                  </span>
                </div>
                <input
                  type="range"
                  min="1"
                  max="64"
                  step="1"
                  value={connections}
                  onChange={(e) => setConnections(Number(e.target.value))}
                  className="w-full accent-sky-500 cursor-pointer"
                />
                <div className="flex justify-between text-[10px] text-neutral-400 font-mono mt-1">
                  <span>1 (Single)</span>
                  <span>8</span>
                  <span>16 (Recommended)</span>
                  <span>32</span>
                  <span>64 (Max Turbo)</span>
                </div>
              </div>
            </div>
          )}

          {mode === 'torrent' && (
            <div className="space-y-4">
              <label className="border-2 border-dashed border-neutral-300 dark:border-neutral-600 rounded-xl p-6 flex flex-col items-center justify-center cursor-pointer hover:border-sky-500 transition-colors">
                <FileBox className="w-10 h-10 text-sky-500 mb-2" />
                <span className="font-semibold text-neutral-800 dark:text-neutral-200">
                  {torrentFileName ? torrentFileName : 'Click to select .torrent file or drag & drop here'}
                </span>
                <span className="text-[11px] text-neutral-400 mt-1">
                  Supports BitTorrent v1 & v2 hybrid metadata parsing
                </span>
                <input
                  type="file"
                  accept=".torrent"
                  onChange={handleTorrentUpload}
                  className="hidden"
                />
              </label>

              {torrentFileName && (
                <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/80 border border-neutral-200 dark:border-neutral-700 space-y-2">
                  <div className="flex items-center justify-between text-neutral-700 dark:text-neutral-200 font-medium">
                    <span>Parsed Torrent Contents (3 Files)</span>
                    <span className="text-emerald-600 font-mono text-[11px]">894.2 MB total</span>
                  </div>
                  <div className="space-y-1 font-mono text-[11px] text-neutral-600 dark:text-neutral-400">
                    <div className="flex items-center space-x-2">
                      <input type="checkbox" defaultChecked className="rounded text-sky-600" />
                      <span className="truncate flex-1">Video_Track_Main_1080p.mkv (840 MB)</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <input type="checkbox" defaultChecked className="rounded text-sky-600" />
                      <span className="truncate flex-1">English_Subtitles.srt (124 KB)</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <input type="checkbox" defaultChecked className="rounded text-sky-600" />
                      <span className="truncate flex-1">Poster_Cover.jpg (54 MB)</span>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {mode === 'batch' && (
            <div className="space-y-2">
              <label className="font-semibold text-neutral-700 dark:text-neutral-200">
                Batch URLs (One link per line)
              </label>
              <textarea
                rows={6}
                value={batchText}
                onChange={(e) => setBatchText(e.target.value)}
                placeholder="https://example.com/file1.zip&#10;https://example.com/file2.zip&#10;magnet:?xt=urn:btih:...&#10;https://releases.ubuntu.com/..."
                className="w-full p-2.5 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-sky-500 font-mono text-xs"
              />
              <span className="text-[11px] text-neutral-400">
                Ghost Downloader will automatically inspect each URL, detect the protocol, and queue them.
              </span>
            </div>
          )}

          {/* Destination Folder */}
          <div className="p-3 rounded-lg bg-neutral-50 dark:bg-neutral-800/50 border border-neutral-200/60 dark:border-neutral-700/60 flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <HardDrive className="w-4 h-4 text-neutral-400" />
              <div>
                <div className="font-semibold text-neutral-700 dark:text-neutral-300">Save Destination</div>
                <div className="text-[11px] font-mono text-neutral-400 truncate max-w-sm">
                  C:\Downloads\{category}\
                </div>
              </div>
            </div>
            <button className="text-sky-600 hover:underline font-medium text-xs cursor-pointer">
              Change
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50/70 dark:bg-neutral-800/60 flex items-center justify-between">
          <label className="flex items-center space-x-2 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={startImmediately}
              onChange={(e) => setStartImmediately(e.target.checked)}
              className="rounded text-sky-600 focus:ring-sky-500"
            />
            <span className="text-neutral-700 dark:text-neutral-300 font-medium">
              Start download immediately
            </span>
          </label>

          <div className="flex items-center space-x-2">
            <button
              onClick={() => handleCreateTask(false)}
              className="px-3 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 font-medium text-xs transition-colors cursor-pointer"
            >
              Add Paused
            </button>
            <button
              onClick={() => handleCreateTask(true)}
              className="px-4 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white font-medium text-xs shadow-xs transition-colors cursor-pointer flex items-center space-x-1.5"
            >
              <span>Download Now</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
