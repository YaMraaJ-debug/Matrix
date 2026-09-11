import { useState } from 'react';
import { 
  Radio, 
  Play, 
  Download, 
  Copy, 
  ExternalLink, 
  Check, 
  Image as ImageIcon, 
  Video, 
  Music, 
  RefreshCw, 
  Layers,
  Sparkles,
  Eye,
  X
} from 'lucide-react';
import { MediaResource, ImageResource } from '../types';
import { formatBytes } from '../utils/formatters';

interface MediaSnifferPageProps {
  mediaResources: MediaResource[];
  images: ImageResource[];
  onDownloadResource: (resource: MediaResource) => void;
  onDownloadImages: (selectedImages: ImageResource[]) => void;
  onRefreshSniff: () => void;
}

export function MediaSnifferPage({
  mediaResources,
  images,
  onDownloadResource,
  onDownloadImages,
  onRefreshSniff,
}: MediaSnifferPageProps) {
  const [tab, setTab] = useState<'streams' | 'images'>('streams');
  const [pageUrl, setPageUrl] = useState('https://demo.unified-streaming.com/k8s/features/stable/video');
  const [selectedImageIds, setSelectedImageIds] = useState<Set<string>>(new Set());
  const [copiedId, setCopiedId] = useState<string | null>(null);
  const [previewVideo, setPreviewVideo] = useState<MediaResource | null>(null);
  const [previewImage, setPreviewImage] = useState<ImageResource | null>(null);
  const [isScanning, setIsScanning] = useState(false);

  const handleScan = () => {
    setIsScanning(true);
    setTimeout(() => {
      setIsScanning(false);
      onRefreshSniff();
    }, 800);
  };

  const handleCopyUrl = (id: string, url: string) => {
    navigator.clipboard.writeText(url);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const toggleSelectImage = (id: string) => {
    const next = new Set(selectedImageIds);
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    setSelectedImageIds(next);
  };

  const handleSelectAllImages = () => {
    if (selectedImageIds.size === images.length) {
      setSelectedImageIds(new Set());
    } else {
      setSelectedImageIds(new Set(images.map((img) => img.id)));
    }
  };

  const handleBatchDownloadSelectedImages = () => {
    const selected = images.filter((img) => selectedImageIds.has(img.id));
    if (selected.length > 0) {
      onDownloadImages(selected);
    }
  };

  return (
    <div className="flex-1 flex flex-col h-full overflow-hidden bg-neutral-100/50 dark:bg-[#202020]">
      {/* Top Bar: URL Sniffer Input */}
      <div className="p-4 bg-white dark:bg-[#282828] border-b border-neutral-200 dark:border-neutral-700 flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Radio className="w-5 h-5 text-purple-500 animate-pulse" />
            <div>
              <h1 className="text-base font-semibold text-neutral-800 dark:text-neutral-100">
                Resource & Media Sniffer
              </h1>
              <p className="text-xs text-neutral-400">
                Cat-Catch compatible stream sniffer for HLS, DASH, dynamic video playlists, and high-res media
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <span className="text-xs px-2 py-0.5 rounded-full bg-purple-100 dark:bg-purple-950/80 text-purple-700 dark:text-purple-300 font-medium">
              {mediaResources.length} Streams • {images.length} Images
            </span>
          </div>
        </div>

        {/* URL Input Bar */}
        <div className="flex items-center space-x-2">
          <div className="relative flex-1">
            <input
              type="text"
              value={pageUrl}
              onChange={(e) => setPageUrl(e.target.value)}
              placeholder="Enter web page URL to sniff media resources..."
              className="w-full h-8 pl-3 pr-3 text-xs rounded-lg bg-neutral-50 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-purple-500 font-mono"
            />
          </div>
          <button
            onClick={handleScan}
            disabled={isScanning}
            className="flex items-center space-x-1.5 h-8 px-3 rounded-lg bg-purple-600 hover:bg-purple-500 text-white text-xs font-medium transition-colors shadow-xs cursor-pointer disabled:opacity-50"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isScanning ? 'animate-spin' : ''}`} />
            <span>{isScanning ? 'Sniffing...' : 'Rescan Page'}</span>
          </button>
        </div>

        {/* Tab switcher */}
        <div className="flex border-b border-neutral-200 dark:border-neutral-700 -mb-4 pt-1 space-x-4 text-xs font-medium">
          <button
            onClick={() => setTab('streams')}
            className={`flex items-center space-x-1.5 pb-2.5 border-b-2 transition-colors cursor-pointer ${
              tab === 'streams'
                ? 'border-purple-600 text-purple-600 dark:text-purple-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Video className="w-3.5 h-3.5" />
            <span>Video & Audio Streams ({mediaResources.length})</span>
          </button>
          <button
            onClick={() => setTab('images')}
            className={`flex items-center space-x-1.5 pb-2.5 border-b-2 transition-colors cursor-pointer ${
              tab === 'images'
                ? 'border-purple-600 text-purple-600 dark:text-purple-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <ImageIcon className="w-3.5 h-3.5" />
            <span>Image Sniffer Gallery ({images.length})</span>
          </button>
        </div>
      </div>

      {/* Content Area */}
      <div className="flex-1 p-4 overflow-y-auto">
        {tab === 'streams' && (
          <div className="space-y-3">
            {mediaResources.map((res) => (
              <div
                key={res.id}
                className="p-3.5 rounded-xl bg-white dark:bg-[#2a2a2a] border border-neutral-200 dark:border-neutral-700/80 shadow-xs flex items-center justify-between gap-4"
              >
                <div className="flex items-center space-x-3 min-w-0 flex-1">
                  <div className="w-10 h-10 rounded-lg bg-purple-50 dark:bg-purple-950/50 border border-purple-200 dark:border-purple-800 flex items-center justify-center flex-shrink-0">
                    {res.type === 'aac' || res.type === 'mp3' ? (
                      <Music className="w-5 h-5 text-purple-500" />
                    ) : (
                      <Video className="w-5 h-5 text-purple-500" />
                    )}
                  </div>

                  <div className="min-w-0 flex-1">
                    <div className="flex items-center space-x-2">
                      <h4 className="text-xs font-semibold text-neutral-800 dark:text-neutral-100 truncate">
                        {res.title}
                      </h4>
                      <span className="text-[10px] font-mono uppercase px-1.5 py-0.5 rounded bg-purple-100 dark:bg-purple-900/60 text-purple-700 dark:text-purple-300">
                        {res.type.toUpperCase()}
                      </span>
                    </div>

                    <div className="flex items-center space-x-3 text-[11px] text-neutral-400 mt-1 font-mono">
                      <span>Size: {formatBytes(res.sizeBytes)}</span>
                      {res.duration && <span>• Duration: {res.duration}</span>}
                      {res.resolution && <span>• {res.resolution}</span>}
                    </div>

                    <div className="text-[10px] text-neutral-400 font-mono truncate mt-0.5 max-w-xl">
                      {res.url}
                    </div>
                  </div>
                </div>

                {/* Stream Actions */}
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => setPreviewVideo(res)}
                    className="flex items-center space-x-1 px-2.5 py-1 rounded-md bg-neutral-100 dark:bg-neutral-700/70 hover:bg-neutral-200 dark:hover:bg-neutral-600 text-neutral-700 dark:text-neutral-200 text-xs transition-colors cursor-pointer"
                  >
                    <Play className="w-3.5 h-3.5 text-purple-500" />
                    <span>Preview</span>
                  </button>

                  <button
                    onClick={() => handleCopyUrl(res.id, res.url)}
                    className="p-1.5 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-500 transition-colors cursor-pointer"
                    title="Copy stream link"
                  >
                    {copiedId === res.id ? <Check className="w-4 h-4 text-emerald-500" /> : <Copy className="w-4 h-4" />}
                  </button>

                  <button
                    onClick={() => onDownloadResource(res)}
                    className="flex items-center space-x-1.5 px-3 py-1 rounded-md bg-sky-600 hover:bg-sky-500 text-white text-xs font-medium shadow-xs transition-colors cursor-pointer"
                  >
                    <Download className="w-3.5 h-3.5" />
                    <span>Download</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {tab === 'images' && (
          <div className="space-y-4">
            {/* Action Bar for Images */}
            <div className="flex items-center justify-between bg-white dark:bg-[#282828] p-3 rounded-xl border border-neutral-200 dark:border-neutral-700">
              <div className="flex items-center space-x-3">
                <button
                  onClick={handleSelectAllImages}
                  className="px-2.5 py-1 rounded border border-neutral-300 dark:border-neutral-600 text-xs font-medium hover:bg-neutral-100 dark:hover:bg-neutral-700 transition-colors cursor-pointer"
                >
                  {selectedImageIds.size === images.length ? 'Deselect All' : 'Select All'}
                </button>
                <span className="text-xs text-neutral-500">
                  {selectedImageIds.size} of {images.length} images selected
                </span>
              </div>

              <button
                onClick={handleBatchDownloadSelectedImages}
                disabled={selectedImageIds.size === 0}
                className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white text-xs font-medium shadow-xs transition-colors cursor-pointer disabled:opacity-40"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Download Selected ({selectedImageIds.size})</span>
              </button>
            </div>

            {/* Gallery Grid */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {images.map((img) => {
                const isChecked = selectedImageIds.has(img.id);
                return (
                  <div
                    key={img.id}
                    className={`relative group rounded-xl overflow-hidden border transition-all ${
                      isChecked
                        ? 'border-purple-500 ring-2 ring-purple-500/30 shadow-md'
                        : 'border-neutral-200 dark:border-neutral-700/80 bg-white dark:bg-[#282828]'
                    }`}
                  >
                    <div className="aspect-video bg-neutral-200 dark:bg-neutral-800 relative overflow-hidden">
                      <img
                        src={img.thumbnailUrl}
                        alt={img.alt}
                        className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                        loading="lazy"
                      />

                      {/* Checkbox Overlay */}
                      <div className="absolute top-2 left-2 z-10">
                        <input
                          type="checkbox"
                          checked={isChecked}
                          onChange={() => toggleSelectImage(img.id)}
                          className="w-4 h-4 rounded text-purple-600 focus:ring-purple-500 cursor-pointer"
                        />
                      </div>

                      {/* Preview Click */}
                      <button
                        onClick={() => setPreviewImage(img)}
                        className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 flex items-center justify-center text-white transition-opacity cursor-pointer"
                      >
                        <Eye className="w-5 h-5" />
                      </button>
                    </div>

                    <div className="p-2 text-[11px] font-mono">
                      <div className="font-semibold text-neutral-800 dark:text-neutral-200 truncate">
                        {img.alt}
                      </div>
                      <div className="flex items-center justify-between text-neutral-400 text-[10px] mt-0.5">
                        <span>{img.width}x{img.height}</span>
                        <span>{formatBytes(img.sizeBytes)}</span>
                        <span className="font-bold uppercase text-purple-600 dark:text-purple-400">{img.format}</span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Video Preview Modal */}
      {previewVideo && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-xs">
          <div className="w-full max-w-2xl bg-black rounded-2xl overflow-hidden shadow-2xl border border-neutral-800">
            <div className="p-3 bg-neutral-900 flex items-center justify-between text-white text-xs">
              <span className="font-semibold truncate">{previewVideo.title}</span>
              <button
                onClick={() => setPreviewVideo(null)}
                className="p-1 rounded text-neutral-400 hover:text-white cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="aspect-video bg-black flex items-center justify-center">
              <video
                src={previewVideo.url}
                controls
                autoPlay
                className="w-full h-full object-contain"
              />
            </div>
            <div className="p-3 bg-neutral-900 flex justify-end space-x-2">
              <button
                onClick={() => {
                  onDownloadResource(previewVideo);
                  setPreviewVideo(null);
                }}
                className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white text-xs font-medium cursor-pointer"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Send to Ghost Downloader</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Image Preview Modal */}
      {previewImage && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-xs">
          <div className="max-w-3xl max-h-[85vh] bg-neutral-900 rounded-2xl overflow-hidden shadow-2xl border border-neutral-800 flex flex-col">
            <div className="p-3 bg-neutral-900 flex items-center justify-between text-white text-xs">
              <span className="font-semibold truncate">{previewImage.alt} ({previewImage.width}x{previewImage.height})</span>
              <button
                onClick={() => setPreviewImage(null)}
                className="p-1 rounded text-neutral-400 hover:text-white cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="flex-1 overflow-auto p-2 flex items-center justify-center bg-black/50">
              <img
                src={previewImage.url}
                alt={previewImage.alt}
                className="max-h-[70vh] object-contain rounded"
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
