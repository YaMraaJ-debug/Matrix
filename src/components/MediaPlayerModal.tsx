import { useState, useRef, useEffect } from 'react';
import { 
  X, 
  Play, 
  Pause, 
  Volume2, 
  VolumeX, 
  Maximize, 
  RotateCcw, 
  Film, 
  Music, 
  Download, 
  ExternalLink,
  Layers,
  Sparkles,
  Info
} from 'lucide-react';
import { DownloadTask } from '../types';
import { formatBytes } from '../utils/formatters';

interface MediaPlayerModalProps {
  isOpen: boolean;
  task: DownloadTask | null;
  onClose: () => void;
  onSaveToDisk?: (task: DownloadTask) => void;
}

export function MediaPlayerModal({
  isOpen,
  task,
  onClose,
  onSaveToDisk,
}: MediaPlayerModalProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [playbackRate, setPlaybackRate] = useState<number>(1);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(0.85);
  const [isLooping, setIsLooping] = useState(false);

  useEffect(() => {
    if (isOpen && videoRef.current) {
      videoRef.current.playbackRate = playbackRate;
      videoRef.current.volume = volume;
    }
  }, [isOpen, playbackRate, volume]);

  if (!isOpen || !task) return null;

  // Derive playable stream URL
  const isAudio = task.category === 'music' || /\.(mp3|wav|flac|aac|ogg|m4a)$/i.test(task.name);
  const playableUrl =
    task.mediaPreviewUrl ||
    (task.url.startsWith('http') && /\.(mp4|webm|mp3|wav|ogg|m4a)$/i.test(task.url)
      ? task.url
      : isAudio
        ? 'https://cdn.freesound.org/previews/511/511484_10842268-lq.mp3'
        : 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4');

  const togglePlay = () => {
    if (!videoRef.current) return;
    if (isPlaying) {
      videoRef.current.pause();
      setIsPlaying(false);
    } else {
      videoRef.current.play().then(() => setIsPlaying(true)).catch(() => {});
    }
  };

  const handleTimeUpdate = () => {
    if (videoRef.current) {
      setCurrentTime(videoRef.current.currentTime);
      setDuration(videoRef.current.duration || 0);
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value);
    if (videoRef.current) {
      videoRef.current.currentTime = val;
      setCurrentTime(val);
    }
  };

  const handleRateChange = (rate: number) => {
    setPlaybackRate(rate);
    if (videoRef.current) {
      videoRef.current.playbackRate = rate;
    }
  };

  const handleToggleMute = () => {
    if (!videoRef.current) return;
    videoRef.current.muted = !isMuted;
    setIsMuted(!isMuted);
  };

  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value);
    setVolume(val);
    if (videoRef.current) {
      videoRef.current.volume = val;
      if (val === 0) setIsMuted(true);
      else if (isMuted) setIsMuted(false);
    }
  };

  const handleFullscreen = () => {
    if (videoRef.current) {
      if (document.fullscreenElement) {
        document.exitFullscreen().catch(() => {});
      } else {
        videoRef.current.requestFullscreen().catch(() => {});
      }
    }
  };

  const formatSecs = (sec: number) => {
    if (!isFinite(sec) || sec < 0) return '0:00';
    const m = Math.floor(sec / 60);
    const s = Math.floor(sec % 60);
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-xs">
      <div className="w-full max-w-3xl bg-neutral-900 border border-neutral-700/80 rounded-2xl shadow-2xl overflow-hidden flex flex-col text-neutral-100 animate-in fade-in zoom-in-95 duration-200">
        {/* Modal Header */}
        <div className="flex items-center justify-between px-5 py-3.5 bg-neutral-950 border-b border-neutral-800">
          <div className="flex items-center space-x-2.5 min-w-0">
            <div className="p-2 rounded-lg bg-purple-500/20 text-purple-400 border border-purple-500/30">
              {isAudio ? <Music className="w-4 h-4" /> : <Film className="w-4 h-4" />}
            </div>
            <div className="min-w-0">
              <h2 className="text-sm font-semibold truncate text-neutral-100" title={task.name}>
                {task.name}
              </h2>
              <div className="flex items-center space-x-2 text-[11px] text-neutral-400">
                <span className="font-mono">{formatBytes(task.totalBytes)}</span>
                <span>•</span>
                <span className="capitalize">{task.category} Preview</span>
                <span>•</span>
                <span className="text-emerald-400 font-medium">
                  {task.status === 'completed' ? 'Completed' : `${Math.round((task.downloadedBytes / task.totalBytes) * 100)}% Downloaded`}
                </span>
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-white hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Video / Audio Canvas Stage */}
        <div className="relative bg-black flex items-center justify-center min-h-[300px] max-h-[460px] overflow-hidden group">
          <video
            ref={videoRef}
            src={playableUrl}
            onTimeUpdate={handleTimeUpdate}
            onPlay={() => setIsPlaying(true)}
            onPause={() => setIsPlaying(false)}
            onLoadedMetadata={handleTimeUpdate}
            loop={isLooping}
            className="w-full max-h-[460px] object-contain cursor-pointer"
            onClick={togglePlay}
          />

          {/* Center Play/Pause Overlay on Hover */}
          <button
            onClick={togglePlay}
            className="absolute inset-0 m-auto w-14 h-14 rounded-full bg-black/60 hover:bg-black/80 border border-white/20 flex items-center justify-center text-white backdrop-blur-xs transition-transform hover:scale-105 cursor-pointer opacity-80 hover:opacity-100"
          >
            {isPlaying ? <Pause className="w-6 h-6" /> : <Play className="w-6 h-6 ml-1 text-emerald-400" />}
          </button>

          {/* Buffer Tag */}
          <div className="absolute top-3 left-3 flex items-center space-x-1.5 px-2.5 py-1 rounded-md bg-black/60 backdrop-blur-md border border-white/10 text-[10px] text-neutral-300 font-mono">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
            <span>Ghost Stream Engine: In-Memory Chunk Preview</span>
          </div>
        </div>

        {/* Video Controls Toolbar */}
        <div className="p-4 bg-neutral-950 border-t border-neutral-800/80 space-y-3">
          {/* Progress / Seek bar */}
          <div className="flex items-center space-x-3 text-xs font-mono">
            <span className="text-neutral-400 w-10 text-right">{formatSecs(currentTime)}</span>
            <input
              type="range"
              min={0}
              max={duration || 100}
              step={0.1}
              value={currentTime}
              onChange={handleSeek}
              className="flex-1 accent-purple-500 h-1.5 bg-neutral-800 rounded-lg cursor-pointer"
            />
            <span className="text-neutral-400 w-10">{formatSecs(duration)}</span>
          </div>

          {/* Playback rate & Controls Row */}
          <div className="flex flex-wrap items-center justify-between gap-3 text-xs">
            {/* Left: Play/Pause, Volume, Loop */}
            <div className="flex items-center space-x-3">
              <button
                onClick={togglePlay}
                className="p-2 rounded-lg bg-neutral-800 hover:bg-neutral-700 text-white cursor-pointer"
              >
                {isPlaying ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 text-emerald-400" />}
              </button>

              <div className="flex items-center space-x-1.5">
                <button
                  onClick={handleToggleMute}
                  className="p-2 rounded-lg hover:bg-neutral-800 text-neutral-400 hover:text-white cursor-pointer"
                >
                  {isMuted || volume === 0 ? <VolumeX className="w-4 h-4 text-rose-400" /> : <Volume2 className="w-4 h-4" />}
                </button>
                <input
                  type="range"
                  min={0}
                  max={1}
                  step={0.05}
                  value={isMuted ? 0 : volume}
                  onChange={handleVolumeChange}
                  className="w-18 accent-purple-500 h-1 bg-neutral-800 rounded cursor-pointer"
                />
              </div>

              <button
                onClick={() => setIsLooping(!isLooping)}
                className={`px-2 py-1 rounded text-[11px] font-medium border cursor-pointer ${
                  isLooping
                    ? 'bg-purple-950 text-purple-300 border-purple-800'
                    : 'bg-neutral-800 text-neutral-400 border-neutral-700'
                }`}
                title="Toggle Repeat"
              >
                Loop
              </button>
            </div>

            {/* Middle: Speed Presets */}
            <div className="flex items-center space-x-1 bg-neutral-900 border border-neutral-800 p-0.5 rounded-lg text-[11px]">
              {[0.75, 1.0, 1.25, 1.5, 2.0].map((rate) => (
                <button
                  key={rate}
                  onClick={() => handleRateChange(rate)}
                  className={`px-2 py-0.5 rounded cursor-pointer ${
                    playbackRate === rate
                      ? 'bg-purple-600 text-white font-semibold'
                      : 'text-neutral-400 hover:text-white'
                  }`}
                >
                  {rate}x
                </button>
              ))}
            </div>

            {/* Right: Actions */}
            <div className="flex items-center space-x-2">
              {onSaveToDisk && (
                <button
                  onClick={() => onSaveToDisk(task)}
                  className="flex items-center space-x-1 px-2.5 py-1 rounded-md bg-neutral-800 hover:bg-neutral-700 text-neutral-200 text-xs cursor-pointer"
                  title="Export to Browser Downloads"
                >
                  <Download className="w-3.5 h-3.5 text-sky-400" />
                  <span className="hidden sm:inline">Export</span>
                </button>
              )}

              <button
                onClick={handleFullscreen}
                className="p-2 rounded-lg hover:bg-neutral-800 text-neutral-400 hover:text-white cursor-pointer"
                title="Fullscreen"
              >
                <Maximize className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
