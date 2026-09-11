import React, { useState } from 'react';
import { 
  X, 
  Globe, 
  Search, 
  Layers, 
  CheckSquare, 
  Square, 
  Download, 
  FileText, 
  Image as ImageIcon, 
  Film, 
  Music, 
  Archive, 
  Filter, 
  Play, 
  RotateCw, 
  ExternalLink,
  Sparkles,
  CheckCircle2
} from 'lucide-react';
import { CategoryType, CrawlResultItem, DownloadTask } from '../types';
import { formatBytes } from '../utils/formatters';

interface WebsiteRipperModalProps {
  isOpen: boolean;
  onClose: () => void;
  onQueueTasks: (tasks: Array<{ name: string; url: string; category: CategoryType; totalBytes: number }>, startImmediately: boolean) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

const PRESET_TARGETS = [
  {
    title: 'NASA Deep Space Imagery Gallery',
    url: 'https://images.nasa.gov/galleries/james-webb-telescope',
    category: 'image' as const,
  },
  {
    title: 'Internet Archive Old Time Radios & Audio',
    url: 'https://archive.org/details/oldtimeradio-classic-broadcasts',
    category: 'music' as const,
  },
  {
    title: 'Open 4K Cinematic Test Sequences',
    url: 'https://media.xiph.org/video/derf/4k-film-stock',
    category: 'video' as const,
  },
  {
    title: 'Linux Kernel & Distribution Tarballs',
    url: 'https://distro.ibiblio.org/pub/linux/distributions',
    category: 'archive' as const,
  },
];

const MOCK_FOUND_ITEMS: CrawlResultItem[] = [
  {
    id: 'crawl-1',
    url: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=2400',
    filename: 'Carina_Nebula_Cosmic_Cliffs_8K.jpg',
    category: 'image',
    sizeBytes: 14200000,
    format: 'JPG',
    dimensions: '7680x4320',
    thumbnailUrl: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=200',
    selected: true,
  },
  {
    id: 'crawl-2',
    url: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=2400',
    filename: 'Deep_Orbital_Earth_Atmosphere.png',
    category: 'image',
    sizeBytes: 28400000,
    format: 'PNG',
    dimensions: '6000x4000',
    thumbnailUrl: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=200',
    selected: true,
  },
  {
    id: 'crawl-3',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
    filename: 'Interstellar_Plasma_Probe_Footage.mp4',
    category: 'video',
    sizeBytes: 154800000,
    format: 'MP4',
    dimensions: '3840x2160',
    selected: true,
  },
  {
    id: 'crawl-4',
    url: 'https://download.blender.org/demo/audio/Cosmos_Synthesizer_Score_Lossless.flac',
    filename: 'Cosmos_Synthesizer_Score_Lossless.flac',
    category: 'music',
    sizeBytes: 48900000,
    format: 'FLAC',
    selected: true,
  },
  {
    id: 'crawl-5',
    url: 'https://cdn.kernel.org/pub/linux/kernel/v6.x/linux-firmware-20260901.tar.xz',
    filename: 'linux-firmware-20260901.tar.xz',
    category: 'archive',
    sizeBytes: 178000000,
    format: 'TAR.XZ',
    selected: true,
  },
  {
    id: 'crawl-6',
    url: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=2400',
    filename: 'Aurora_Borealis_Satellite_Overpass.webp',
    category: 'image',
    sizeBytes: 6700000,
    format: 'WEBP',
    dimensions: '4096x2304',
    thumbnailUrl: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=200',
    selected: true,
  },
  {
    id: 'crawl-7',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    filename: 'Subsurface_Robotic_Explorer_Log.mp4',
    category: 'video',
    sizeBytes: 284000000,
    format: 'MP4',
    dimensions: '1920x1080',
    selected: true,
  },
  {
    id: 'crawl-8',
    url: 'https://archive.org/download/quantum-mechanics-encyclopedia/quantum_field_vol2.pdf',
    filename: 'Quantum_Field_Theory_Volume2.pdf',
    category: 'document',
    sizeBytes: 34500000,
    format: 'PDF',
    selected: true,
  },
  {
    id: 'crawl-9',
    url: 'https://releases.ubuntu.com/noble/ubuntu-24.04.1-desktop-amd64.iso',
    filename: 'ubuntu-24.04.1-desktop-amd64.iso',
    category: 'archive',
    sizeBytes: 5900000000,
    format: 'ISO',
    selected: false,
  },
];

export function WebsiteRipperModal({
  isOpen,
  onClose,
  onQueueTasks,
  onShowToast,
}: WebsiteRipperModalProps) {
  const [targetUrl, setTargetUrl] = useState('https://images.nasa.gov/galleries/james-webb-telescope');
  const [crawlDepth, setCrawlDepth] = useState<'1' | '2'>('1');
  const [minSizeOption, setMinSizeOption] = useState<'all' | '100k' | '1m' | '10m'>('100k');
  const [isScanning, setIsScanning] = useState(false);
  const [scanProgress, setScanProgress] = useState(0);
  const [scanStatusText, setScanStatusText] = useState('');
  const [items, setItems] = useState<CrawlResultItem[]>(MOCK_FOUND_ITEMS);
  const [filterCategory, setFilterCategory] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [startImmediately, setStartImmediately] = useState(true);

  if (!isOpen) return null;

  const handleStartCrawl = () => {
    if (!targetUrl.trim()) {
      onShowToast('Please enter a valid website URL', 'warning');
      return;
    }

    setIsScanning(true);
    setScanProgress(10);
    setScanStatusText('Connecting to target web host & fetching HTML index...');

    setTimeout(() => {
      setScanProgress(35);
      setScanStatusText('Parsing DOM tree for <img>, <video>, <audio>, and <a> href tags...');
    }, 600);

    setTimeout(() => {
      setScanProgress(70);
      setScanStatusText('Resolving relative paths & probing Content-Length headers...');
    }, 1200);

    setTimeout(() => {
      setScanProgress(100);
      setIsScanning(false);
      setScanStatusText('');
      onShowToast(`Crawled website successfully! Found ${items.length} media resources.`, 'success');
    }, 1800);
  };

  const handleToggleSelect = (id: string) => {
    setItems((prev) =>
      prev.map((item) => (item.id === id ? { ...item, selected: !item.selected } : item))
    );
  };

  const handleSelectAll = (select: boolean) => {
    setItems((prev) => prev.map((item) => ({ ...item, selected: select })));
  };

  const handleInvertSelect = () => {
    setItems((prev) => prev.map((item) => ({ ...item, selected: !item.selected })));
  };

  const filteredItems = items.filter((item) => {
    const matchesCat = filterCategory === 'all' || item.category === filterCategory;
    const matchesSearch = item.filename.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          item.format.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCat && matchesSearch;
  });

  const selectedCount = items.filter((i) => i.selected).length;
  const selectedBytes = items.filter((i) => i.selected).reduce((sum, i) => sum + i.sizeBytes, 0);

  const handleQueueSelected = () => {
    const selectedItems = items.filter((i) => i.selected);
    if (selectedItems.length === 0) {
      onShowToast('No files selected for download', 'warning');
      return;
    }

    const payload = selectedItems.map((item) => ({
      name: item.filename,
      url: item.url,
      category: item.category,
      totalBytes: item.sizeBytes,
    }));

    onQueueTasks(payload, startImmediately);
    onShowToast(`Queued ${selectedItems.length} files (${formatBytes(selectedBytes)}) from Website Ripper!`, 'success');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-4xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-sky-500/10 text-sky-600 dark:text-sky-400">
              <Globe className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Website Ripper & Batch Media Crawler</span>
                <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-full bg-sky-100 dark:bg-sky-950 text-sky-700 dark:text-sky-300 font-semibold border border-sky-300 dark:border-sky-800">
                  Deep Ripper
                </span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Extract, filter, and batch download all multimedia, documents, and archives from any web page
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

        {/* URL Target & Crawl Options Bar */}
        <div className="p-5 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/40 dark:bg-neutral-900/50 space-y-3">
          <div className="flex items-center space-x-2">
            <div className="relative flex-1">
              <Globe className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <input
                type="text"
                value={targetUrl}
                onChange={(e) => setTargetUrl(e.target.value)}
                placeholder="Enter target website URL (e.g. https://example.com/gallery)..."
                className="w-full pl-9 pr-4 py-2 text-xs font-mono rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 focus:outline-none focus:ring-2 focus:ring-sky-500"
              />
            </div>
            <button
              onClick={handleStartCrawl}
              disabled={isScanning}
              className="flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-sky-600 hover:bg-sky-500 active:scale-95 text-white text-xs font-semibold shadow-sm transition-all cursor-pointer disabled:opacity-50"
            >
              <RotateCw className={`w-3.5 h-3.5 ${isScanning ? 'animate-spin' : ''}`} />
              <span>{isScanning ? 'Crawling...' : 'Crawl Page'}</span>
            </button>
          </div>

          {/* Quick Presets */}
          <div className="flex items-center space-x-2 overflow-x-auto text-[11px] pb-1">
            <span className="text-neutral-400 font-medium whitespace-nowrap">Presets:</span>
            {PRESET_TARGETS.map((preset, idx) => (
              <button
                key={idx}
                onClick={() => setTargetUrl(preset.url)}
                className="px-2.5 py-0.5 rounded-full bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 whitespace-nowrap border border-neutral-200 dark:border-neutral-700 cursor-pointer"
              >
                {preset.title}
              </button>
            ))}
          </div>

          {/* Crawl Parameters */}
          <div className="grid grid-cols-3 gap-3 pt-1 text-xs">
            <div>
              <label className="block text-neutral-500 dark:text-neutral-400 text-[11px] font-medium mb-1">
                Crawl Depth
              </label>
              <select
                value={crawlDepth}
                onChange={(e) => setCrawlDepth(e.target.value as any)}
                className="w-full p-1.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs"
              >
                <option value="1">Level 1 (Target Page Only)</option>
                <option value="2">Level 2 (Follow 1st Tier Internal Links)</option>
              </select>
            </div>

            <div>
              <label className="block text-neutral-500 dark:text-neutral-400 text-[11px] font-medium mb-1">
                Minimum File Size
              </label>
              <select
                value={minSizeOption}
                onChange={(e) => setMinSizeOption(e.target.value as any)}
                className="w-full p-1.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs"
              >
                <option value="all">All Sizes (Include icons)</option>
                <option value="100k">&gt; 100 KB (Skip small icons/avatars)</option>
                <option value="1m">&gt; 1 MB (High resolution media only)</option>
                <option value="10m">&gt; 10 MB (Large video & archives)</option>
              </select>
            </div>

            <div className="flex items-end">
              <label className="flex items-center space-x-2 text-[11px] text-neutral-600 dark:text-neutral-300 cursor-pointer mb-1.5">
                <input
                  type="checkbox"
                  checked={startImmediately}
                  onChange={(e) => setStartImmediately(e.target.checked)}
                  className="rounded border-neutral-300 text-sky-600 focus:ring-sky-500"
                />
                <span>Start downloading immediately</span>
              </label>
            </div>
          </div>

          {/* Scanning Progress Bar */}
          {isScanning && (
            <div className="space-y-1.5 pt-2 animate-in fade-in duration-200">
              <div className="flex justify-between text-[11px] text-sky-600 dark:text-sky-400 font-mono">
                <span>{scanStatusText}</span>
                <span>{scanProgress}%</span>
              </div>
              <div className="h-1.5 w-full bg-neutral-200 dark:bg-neutral-700 rounded-full overflow-hidden">
                <div 
                  className="h-full bg-sky-500 transition-all duration-300"
                  style={{ width: `${scanProgress}%` }}
                />
              </div>
            </div>
          )}
        </div>

        {/* Results Toolbar */}
        <div className="flex items-center justify-between px-6 py-2.5 bg-neutral-100/60 dark:bg-neutral-800/60 border-b border-neutral-200 dark:border-neutral-800 text-xs">
          <div className="flex items-center space-x-2">
            <span className="font-semibold text-neutral-800 dark:text-neutral-200">
              Found {filteredItems.length} files
            </span>
            <span className="text-neutral-400">|</span>
            <span className="text-sky-600 dark:text-sky-400 font-medium">
              {selectedCount} selected ({formatBytes(selectedBytes)})
            </span>
          </div>

          {/* Category Filter Chips */}
          <div className="flex items-center space-x-1">
            {['all', 'image', 'video', 'music', 'archive', 'document'].map((cat) => (
              <button
                key={cat}
                onClick={() => setFilterCategory(cat)}
                className={`px-2.5 py-1 rounded-md text-[11px] font-medium capitalize transition-colors cursor-pointer ${
                  filterCategory === cat
                    ? 'bg-sky-600 text-white'
                    : 'bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-200 dark:hover:bg-neutral-700'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>

          {/* Bulk Selection Actions */}
          <div className="flex items-center space-x-1.5">
            <button
              onClick={() => handleSelectAll(true)}
              className="px-2 py-1 rounded hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 text-[11px] cursor-pointer"
            >
              Select All
            </button>
            <button
              onClick={() => handleSelectAll(false)}
              className="px-2 py-1 rounded hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 text-[11px] cursor-pointer"
            >
              Clear
            </button>
            <button
              onClick={handleInvertSelect}
              className="px-2 py-1 rounded hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 text-[11px] cursor-pointer"
            >
              Invert
            </button>
          </div>
        </div>

        {/* Results List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-2">
          {filteredItems.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-neutral-400">
              <Search className="w-8 h-8 mb-2 opacity-50" />
              <p className="text-xs">No files matched the current filters.</p>
            </div>
          ) : (
            filteredItems.map((item) => (
              <div
                key={item.id}
                onClick={() => handleToggleSelect(item.id)}
                className={`flex items-center justify-between p-3 rounded-xl border transition-all cursor-pointer ${
                  item.selected
                    ? 'bg-sky-50/60 dark:bg-sky-950/20 border-sky-300 dark:border-sky-800'
                    : 'bg-white dark:bg-neutral-800/70 border-neutral-200 dark:border-neutral-700/60 hover:border-neutral-300 dark:hover:border-neutral-600'
                }`}
              >
                <div className="flex items-center space-x-3 overflow-hidden">
                  <input
                    type="checkbox"
                    checked={item.selected}
                    onChange={() => {}} // handled by row click
                    className="w-4 h-4 rounded border-neutral-300 text-sky-600 focus:ring-sky-500 pointer-events-none"
                  />

                  {/* Thumbnail / Icon */}
                  {item.thumbnailUrl ? (
                    <img
                      src={item.thumbnailUrl}
                      alt={item.filename}
                      className="w-10 h-10 object-cover rounded-lg border border-neutral-200 dark:border-neutral-700 shrink-0"
                    />
                  ) : (
                    <div className="w-10 h-10 rounded-lg bg-neutral-100 dark:bg-neutral-700 flex items-center justify-center text-neutral-500 shrink-0">
                      {item.category === 'video' && <Film className="w-5 h-5 text-purple-500" />}
                      {item.category === 'music' && <Music className="w-5 h-5 text-emerald-500" />}
                      {item.category === 'archive' && <Archive className="w-5 h-5 text-amber-500" />}
                      {item.category === 'document' && <FileText className="w-5 h-5 text-blue-500" />}
                      {item.category === 'image' && <ImageIcon className="w-5 h-5 text-rose-500" />}
                    </div>
                  )}

                  <div className="min-w-0">
                    <p className="text-xs font-semibold text-neutral-800 dark:text-neutral-100 truncate">
                      {item.filename}
                    </p>
                    <div className="flex items-center space-x-2 text-[11px] text-neutral-400 font-mono">
                      <span className="uppercase font-bold text-neutral-600 dark:text-neutral-300">
                        {item.format}
                      </span>
                      <span>•</span>
                      <span>{formatBytes(item.sizeBytes)}</span>
                      {item.dimensions && (
                        <>
                          <span>•</span>
                          <span>{item.dimensions}</span>
                        </>
                      )}
                    </div>
                  </div>
                </div>

                <div className="text-right shrink-0 ml-4">
                  <span className="text-[11px] font-mono font-medium text-neutral-500 dark:text-neutral-400 block">
                    {formatBytes(item.sizeBytes)}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <div className="text-xs text-neutral-500 dark:text-neutral-400">
            Selected: <span className="font-semibold text-neutral-900 dark:text-neutral-100">{selectedCount}</span> of {items.length} files ({formatBytes(selectedBytes)})
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-xs font-medium text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
            >
              Cancel
            </button>
            <button
              onClick={handleQueueSelected}
              disabled={selectedCount === 0}
              className="flex items-center space-x-2 px-5 py-2 rounded-xl bg-sky-600 hover:bg-sky-500 active:scale-95 text-white text-xs font-semibold shadow-sm transition-all cursor-pointer disabled:opacity-40"
            >
              <Download className="w-4 h-4" />
              <span>Queue Selected ({selectedCount})</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
