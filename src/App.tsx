import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { TitleBar } from './components/TitleBar';
import { Sidebar } from './components/Sidebar';
import { DownloadsPage } from './components/DownloadsPage';
import { MediaSnifferPage } from './components/MediaSnifferPage';
import { FeaturePacksPage } from './components/FeaturePacksPage';
import { SettingsPage } from './components/SettingsPage';
import { TaskDetailsModal } from './components/TaskDetailsModal';
import { NewTaskModal } from './components/NewTaskModal';
import { FileHashModal } from './components/FileHashModal';
import { PlanTaskModal } from './components/PlanTaskModal';
import { BatchUrlModal } from './components/BatchUrlModal';
import { BrowserExtensionModal } from './components/BrowserExtensionModal';
import { ClipboardSnifferBanner } from './components/ClipboardSnifferBanner';
import { ToastContainer, ToastMessage } from './components/Toast';
import { MediaPlayerModal } from './components/MediaPlayerModal';
import { TorrentPeersModal } from './components/TorrentPeersModal';
import { BackupRestoreModal } from './components/BackupRestoreModal';
import { RssFeedModal } from './components/RssFeedModal';
import { 
  DownloadTask, 
  FeaturePack, 
  MediaResource, 
  ImageResource, 
  AppSettings, 
  ActivePage,
  DownloadChunk,
  PlanTaskConfig,
  TaskPriority,
  TorrentTracker,
  TorrentPeer,
  RssFeedSubscription,
  RssFeedItem,
  CategoryType
} from './types';
import { 
  initialTasks, 
  initialFeaturePacks, 
  initialMediaResources, 
  initialImages, 
  defaultSettings,
  initialRssSubscriptions
} from './data/initialData';
import { 
  detectProtocolFromUrl, 
  detectCategoryFromFileName 
} from './utils/formatters';

function playCompletionChime() {
  try {
    const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
    if (!AudioContextClass) return;
    const audioCtx = new AudioContextClass();
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(587.33, audioCtx.currentTime); // D5
    osc.frequency.exponentialRampToValueAtTime(880, audioCtx.currentTime + 0.15); // A5
    gain.gain.setValueAtTime(0.15, audioCtx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + 0.4);
    osc.connect(gain);
    gain.connect(audioCtx.destination);
    osc.start();
    osc.stop(audioCtx.currentTime + 0.4);
  } catch {
    // Safe fallback if audio context is blocked
  }
}

export function App() {
  // Application State
  const [tasks, setTasks] = useState<DownloadTask[]>(() => {
    const saved = localStorage.getItem('ghost_tasks_v3');
    return saved ? JSON.parse(saved) : initialTasks;
  });

  const [featurePacks, setFeaturePacks] = useState<FeaturePack[]>(() => {
    const saved = localStorage.getItem('ghost_packs_v3');
    return saved ? JSON.parse(saved) : initialFeaturePacks;
  });

  const [mediaResources, setMediaResources] = useState<MediaResource[]>(initialMediaResources);
  const [images, setImages] = useState<ImageResource[]>(initialImages);
  const [settings, setSettings] = useState<AppSettings>(() => {
    const saved = localStorage.getItem('ghost_settings_v3');
    return saved ? { ...defaultSettings, ...JSON.parse(saved) } : defaultSettings;
  });

  const [planConfig, setPlanConfig] = useState<PlanTaskConfig>(() => {
    const saved = localStorage.getItem('ghost_plan_config');
    return saved ? JSON.parse(saved) : { enabled: false, scheduledTime: '02:00', postAction: 'sound' };
  });

  const [activePage, setActivePage] = useState<ActivePage>('tasks');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTaskForDetails, setSelectedTaskForDetails] = useState<DownloadTask | null>(null);
  const [isNewTaskModalOpen, setIsNewTaskModalOpen] = useState(false);
  const [isFileHashOpen, setIsFileHashOpen] = useState(false);
  const [targetHashTask, setTargetHashTask] = useState<DownloadTask | null>(null);
  const [isPlanTaskOpen, setIsPlanTaskOpen] = useState(false);
  const [isBatchUrlOpen, setIsBatchUrlOpen] = useState(false);
  const [isBrowserExtOpen, setIsBrowserExtOpen] = useState(false);
  const [detectedClipboardUrl, setDetectedClipboardUrl] = useState<string | null>(null);

  // New Features Modal States
  const [selectedTaskForPreview, setSelectedTaskForPreview] = useState<DownloadTask | null>(null);
  const [selectedTaskForTorrent, setSelectedTaskForTorrent] = useState<DownloadTask | null>(null);
  const [isBackupModalOpen, setIsBackupModalOpen] = useState(false);
  const [isRssModalOpen, setIsRssModalOpen] = useState(false);
  const [rssSubscriptions, setRssSubscriptions] = useState<RssFeedSubscription[]>(() => {
    const saved = localStorage.getItem('ghost_rss_subs_v3');
    return saved ? JSON.parse(saved) : initialRssSubscriptions;
  });

  // Speed Telemetry Histories (last 30 seconds)
  const [speedHistory, setSpeedHistory] = useState<number[]>([
    12582912, 13100000, 15200000, 14800000, 16900000, 18500000, 19200000, 18000000, 19500000, 21000000
  ]);
  const [uploadHistory, setUploadHistory] = useState<number[]>([
    512000, 620000, 850000, 780000, 920000, 1050000, 1200000, 1100000
  ]);
  const [peakSpeed, setPeakSpeed] = useState<number>(24500000);

  const [isDark, setIsDark] = useState<boolean>(() => {
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  });
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  // Sync Dark class with document
  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDark]);

  // Persist tasks & settings
  useEffect(() => {
    localStorage.setItem('ghost_tasks_v3', JSON.stringify(tasks));
  }, [tasks]);

  useEffect(() => {
    localStorage.setItem('ghost_packs_v3', JSON.stringify(featurePacks));
  }, [featurePacks]);

  useEffect(() => {
    localStorage.setItem('ghost_settings_v3', JSON.stringify(settings));
  }, [settings]);

  useEffect(() => {
    localStorage.setItem('ghost_plan_config', JSON.stringify(planConfig));
  }, [planConfig]);

  useEffect(() => {
    localStorage.setItem('ghost_rss_subs_v3', JSON.stringify(rssSubscriptions));
  }, [rssSubscriptions]);

  // Toast Helper
  const showToast = useCallback((text: string, type: 'success' | 'warning' | 'info' = 'info') => {
    const id = Date.now().toString() + Math.random().toString(36).substring(2, 5);
    setToasts((prev) => [...prev, { id, text, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3500);
  }, []);

  const dismissToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // Clipboard monitor listener
  useEffect(() => {
    if (!settings.clipboardMonitor) return;

    const handleCopy = () => {
      navigator.clipboard?.readText?.().then((clipText) => {
        if (!clipText) return;
        const text = clipText.trim();
        if (
          text.startsWith('http://') || 
          text.startsWith('https://') || 
          text.startsWith('magnet:') || 
          text.startsWith('ed2k://') ||
          text.endsWith('.torrent') ||
          text.includes('.m3u8')
        ) {
          setDetectedClipboardUrl(text);
        }
      }).catch(() => {});
    };

    window.addEventListener('copy', handleCopy);
    return () => window.removeEventListener('copy', handleCopy);
  }, [settings.clipboardMonitor]);

  // Scheduled Task Planner Check ref to avoid repeated triggers
  const lastTriggeredScheduleRef = useRef<string>('');

  // Real-time Download Simulation Loop
  useEffect(() => {
    const interval = setInterval(() => {
      // 1. Check Scheduled Start
      if (planConfig.enabled && planConfig.scheduledTime) {
        const now = new Date();
        const currentTimeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
        if (currentTimeStr === planConfig.scheduledTime && lastTriggeredScheduleRef.current !== currentTimeStr) {
          lastTriggeredScheduleRef.current = currentTimeStr;
          setTasks((prev) =>
            prev.map((t) => (t.status === 'paused' ? { ...t, status: 'downloading', speed: 12582912 } : t))
          );
          showToast(`Task Planner: Scheduled download session triggered (${currentTimeStr})!`, 'success');
        }
      }

      // 2. Download Simulation
      setTasks((prevTasks) => {
        let hasChanges = false;
        let activeDlSpeed = 0;
        let activeUlSpeed = 0;

        const updated = prevTasks.map((task) => {
          if (task.status !== 'downloading') return task;

          hasChanges = true;
          // Apply speed jitter (-8% to +10%)
          const jitterFactor = 0.92 + Math.random() * 0.18;
          let currentSpeed = Math.max(200000, Math.floor(task.speed * jitterFactor));

          // Priority-based bandwidth weighting
          if (task.priority === 'high') {
            currentSpeed = Math.floor(currentSpeed * 1.35);
          } else if (task.priority === 'low') {
            currentSpeed = Math.floor(currentSpeed * 0.65);
          }

          // Throttle if global limit set
          if (settings.globalDownloadLimitKbps > 0) {
            const maxByteSpeed = settings.globalDownloadLimitKbps * 1024;
            currentSpeed = Math.min(currentSpeed, maxByteSpeed);
          }

          // Throttle if scheduled day/night bandwidth rule is enabled
          if (settings.scheduledBandwidth?.enabled) {
            const currentHour = new Date().getHours();
            const isDay = currentHour >= settings.scheduledBandwidth.dayStartHour && currentHour < settings.scheduledBandwidth.dayEndHour;
            const scheduledLimitKbps = isDay ? settings.scheduledBandwidth.dayLimitKbps : settings.scheduledBandwidth.nightLimitKbps;
            if (scheduledLimitKbps > 0) {
              const maxScheduledSpeed = scheduledLimitKbps * 1024;
              currentSpeed = Math.min(currentSpeed, maxScheduledSpeed);
            }
          }

          activeDlSpeed += currentSpeed;
          activeUlSpeed += task.uploadSpeed || 0;

          const newDownloaded = task.downloadedBytes + currentSpeed;

          // Check for completion
          if (newDownloaded >= task.totalBytes) {
            if (settings.playCompletionSound) {
              playCompletionChime();
            }
            showToast(`Completed: ${task.name}`, 'success');

            const completedChunks = task.chunks.map((c) => ({
              ...c,
              downloadedBytes: c.endByte - c.startByte + 1,
              speed: 0,
              status: 'completed' as const,
            }));

            return {
              ...task,
              downloadedBytes: task.totalBytes,
              status: 'completed' as const,
              speed: 0,
              etaSeconds: 0,
              completedAt: Date.now(),
              chunks: completedChunks,
            };
          }

          // Distribute new progress across active chunks
          const bytesToAdd = currentSpeed;
          let remainingToAdd = bytesToAdd;
          const newChunks: DownloadChunk[] = task.chunks.map((chunk) => {
            const chunkCapacity = chunk.endByte - chunk.startByte + 1;
            if (chunk.downloadedBytes >= chunkCapacity) {
              return { ...chunk, status: 'completed' as const, speed: 0 };
            }

            const needed = chunkCapacity - chunk.downloadedBytes;
            const portion = Math.min(needed, Math.floor(remainingToAdd / (task.chunks.length || 1)));
            remainingToAdd = Math.max(0, remainingToAdd - portion);

            const updatedDownloaded = chunk.downloadedBytes + portion;
            const isDone = updatedDownloaded >= chunkCapacity;

            return {
              ...chunk,
              downloadedBytes: updatedDownloaded,
              status: isDone ? ('completed' as const) : ('active' as const),
              speed: isDone ? 0 : Math.floor(currentSpeed / task.chunks.length),
            };
          });

          // Recalculate ETA
          const remainingBytes = task.totalBytes - newDownloaded;
          const eta = currentSpeed > 0 ? Math.ceil(remainingBytes / currentSpeed) : 0;

          return {
            ...task,
            downloadedBytes: newDownloaded,
            speed: currentSpeed,
            etaSeconds: eta,
            chunks: newChunks,
          };
        });

        // Update telemetry histories
        setSpeedHistory((prev) => [...prev.slice(-29), activeDlSpeed]);
        setUploadHistory((prev) => [...prev.slice(-29), activeUlSpeed]);
        setPeakSpeed((prevPeak) => Math.max(prevPeak, activeDlSpeed));

        return hasChanges ? updated : prevTasks;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [settings.globalDownloadLimitKbps, settings.playCompletionSound, planConfig, showToast]);

  // Aggregate Speeds
  const { totalDownloadSpeed, totalUploadSpeed, activeTaskCount } = useMemo(() => {
    let dl = 0;
    let ul = 0;
    let count = 0;
    for (const t of tasks) {
      if (t.status === 'downloading') {
        dl += t.speed;
        ul += t.uploadSpeed || 0;
        count++;
      }
    }
    return {
      totalDownloadSpeed: dl,
      totalUploadSpeed: ul,
      activeTaskCount: count,
    };
  }, [tasks]);

  // Task Actions
  const handleTogglePause = (id: string) => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.id !== id) return t;
        if (t.status === 'downloading') {
          showToast(`Paused: ${t.name}`, 'info');
          return { ...t, status: 'paused', speed: 0 };
        } else if (t.status === 'paused') {
          showToast(`Resumed: ${t.name}`, 'success');
          return { ...t, status: 'downloading', speed: 12582912 }; // Default ~12 MB/s
        }
        return t;
      })
    );
  };

  const handleRestart = (id: string) => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.id !== id) return t;
        showToast(`Restarting: ${t.name}`, 'info');
        const resetChunks = t.chunks.map((c) => ({
          ...c,
          downloadedBytes: 0,
          speed: 0,
          status: 'idle' as const,
        }));
        return {
          ...t,
          downloadedBytes: 0,
          status: 'downloading',
          speed: 15000000,
          chunks: resetChunks,
        };
      })
    );
  };

  const handleDeleteTask = (id: string) => {
    setTasks((prev) => prev.filter((t) => t.id !== id));
    showToast('Task removed from queue', 'warning');
  };

  const handlePauseAll = () => {
    setTasks((prev) =>
      prev.map((t) => (t.status === 'downloading' ? { ...t, status: 'paused', speed: 0 } : t))
    );
    showToast('All downloads paused', 'info');
  };

  const handleResumeAll = () => {
    setTasks((prev) =>
      prev.map((t) =>
        t.status === 'paused' ? { ...t, status: 'downloading', speed: 10485760 } : t
      )
    );
    showToast('All downloads resumed', 'success');
  };

  const handleDeleteFinished = () => {
    setTasks((prev) => prev.filter((t) => t.status !== 'completed'));
    showToast('Cleared completed tasks', 'info');
  };

  const handleCopyUrl = (url: string) => {
    navigator.clipboard.writeText(url);
    showToast('URL copied to clipboard', 'success');
  };

  const handleSaveToDisk = (task: DownloadTask) => {
    try {
      const content = `Ghost Downloader 3 - File Manifest Export\n========================================\nFile: ${task.name}\nSource URL: ${task.url}\nProtocol: ${task.protocol}\nSize: ${task.totalBytes} bytes\nMD5: ${task.md5Hash || 'N/A'}\nSHA256: ${task.sha256Hash || 'N/A'}\nCompleted At: ${new Date().toISOString()}`;
      const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
      const blobUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = blobUrl;
      a.download = task.name.endsWith('.txt') ? task.name : `${task.name}.info.txt`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(blobUrl);
      showToast(`Exported "${task.name}" to browser downloads`, 'success');
    } catch {
      window.open(task.url, '_blank');
    }
  };

  // Adding New Tasks
  const handleAddNewTask = (partial: Partial<DownloadTask>, startImmediately: boolean) => {
    const totalBytes = partial.totalBytes || 450000000;
    const connections = partial.connections || 16;
    const chunkSize = Math.floor(totalBytes / connections);
    
    const chunks: DownloadChunk[] = [];
    for (let i = 0; i < connections; i++) {
      const startByte = i * chunkSize;
      const endByte = i === connections - 1 ? totalBytes : (i + 1) * chunkSize - 1;
      chunks.push({
        id: i,
        startByte,
        endByte,
        downloadedBytes: 0,
        speed: startImmediately ? Math.floor(Math.random() * 800000) + 400000 : 0,
        status: startImmediately ? 'active' : 'idle',
      });
    }

    const finalCategory = partial.category || (settings.autoDetectCategory ? detectCategoryFromFileName(partial.name || '') : 'software');

    const newTask: DownloadTask = {
      id: `task-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      name: partial.name || 'download.bin',
      url: partial.url || 'https://sample.ghost/file.bin',
      protocol: partial.protocol || 'http',
      category: finalCategory,
      status: startImmediately ? 'downloading' : 'paused',
      totalBytes,
      downloadedBytes: 0,
      speed: startImmediately ? 14680064 : 0,
      etaSeconds: startImmediately ? Math.ceil(totalBytes / 14680064) : 0,
      connections,
      chunks,
      createdAt: Date.now(),
      savePath: `C:\\Downloads\\${finalCategory}\\${partial.name || 'download.bin'}`,
      mirror: partial.mirror,
      quality: partial.quality,
      md5Hash: Math.random().toString(16).slice(2, 10) + Math.random().toString(16).slice(2, 10) + Math.random().toString(16).slice(2, 10) + Math.random().toString(16).slice(2, 10),
      sha256Hash: Math.random().toString(16).slice(2, 18) + Math.random().toString(16).slice(2, 18) + Math.random().toString(16).slice(2, 18) + Math.random().toString(16).slice(2, 18),
    };

    setTasks((prev) => [newTask, ...prev]);
    showToast(`Added: ${newTask.name}`, 'success');
  };

  const handleAddBatchTasks = (urls: string[]) => {
    urls.forEach((url, idx) => {
      const protocol = detectProtocolFromUrl(url);
      const urlLast = url.split('/').pop() || `item_${idx + 1}.bin`;
      const name = urlLast.includes('?') ? urlLast.split('?')[0] : urlLast;
      const category = settings.autoDetectCategory ? detectCategoryFromFileName(name) : 'software';
      handleAddNewTask({ name, url, protocol, category }, true);
    });
    showToast(`Queued ${urls.length} tasks from batch pattern generator`, 'success');
  };

  // Media Sniffer Handlers
  const handleDownloadResource = (res: MediaResource) => {
    handleAddNewTask(
      {
        name: res.title.replace(/[\/\?<>\\:\*\|":]/g, '_') + (res.type === 'm3u8' ? '.mp4' : `.${res.type}`),
        url: res.url,
        protocol: res.type === 'm3u8' ? 'm3u8' : 'http',
        category: res.type === 'aac' || res.type === 'mp3' ? 'music' : 'video',
        totalBytes: res.sizeBytes,
        quality: res.resolution,
      },
      true
    );
    setActivePage('tasks');
    showToast(`Grabbed stream "${res.title}" into download queue`, 'success');
  };

  const handleDownloadImages = (selected: ImageResource[]) => {
    selected.forEach((img) => {
      handleAddNewTask(
        {
          name: `${img.alt.replace(/\s+/g, '_')}.${img.format.toLowerCase()}`,
          url: img.url,
          protocol: 'http',
          category: 'software',
          totalBytes: img.sizeBytes,
        },
        true
      );
    });
    setActivePage('tasks');
    showToast(`Queued ${selected.length} high-res images for download`, 'success');
  };

  const handleRefreshSniff = () => {
    showToast('Page rescanned: 4 streams & 4 high-res media detected', 'info');
  };

  // Feature Packs Handlers
  const handleTogglePack = (id: string) => {
    setFeaturePacks((prev) =>
      prev.map((p) => {
        if (p.id === id) {
          const next = !p.enabled;
          showToast(`${p.name} ${next ? 'Activated' : 'Deactivated'}`, next ? 'success' : 'warning');
          return { ...p, enabled: next };
        }
        return p;
      })
    );
  };

  // Open Hash modal for specific task
  const handleOpenFileHash = (task?: DownloadTask) => {
    setTargetHashTask(task || null);
    setIsFileHashOpen(true);
  };

  // Queue Reordering Handlers
  const handleMoveUp = useCallback((id: string) => {
    setTasks((prev) => {
      const idx = prev.findIndex((t) => t.id === id);
      if (idx <= 0) return prev;
      const next = [...prev];
      const temp = next[idx - 1];
      next[idx - 1] = next[idx];
      next[idx] = temp;
      return next;
    });
    showToast('Task moved up in queue', 'info');
  }, [showToast]);

  const handleMoveDown = useCallback((id: string) => {
    setTasks((prev) => {
      const idx = prev.findIndex((t) => t.id === id);
      if (idx < 0 || idx >= prev.length - 1) return prev;
      const next = [...prev];
      const temp = next[idx + 1];
      next[idx + 1] = next[idx];
      next[idx] = temp;
      return next;
    });
    showToast('Task moved down in queue', 'info');
  }, [showToast]);

  // Priority Handler
  const handleSetPriority = useCallback((id: string, priority: TaskPriority) => {
    setTasks((prev) =>
      prev.map((t) => (t.id === id ? { ...t, priority } : t))
    );
    showToast(`Priority set to ${priority.toUpperCase()}`, 'info');
  }, [showToast]);

  // BitTorrent Tracker & Peer Management Handlers
  const handleUpdateTrackers = useCallback((taskId: string, trackers: TorrentTracker[]) => {
    setTasks((prev) =>
      prev.map((t) => (t.id === taskId ? { ...t, trackers } : t))
    );
    setSelectedTaskForTorrent((prev) => (prev && prev.id === taskId ? { ...prev, trackers } : prev));
    showToast('Torrent trackers updated', 'success');
  }, [showToast]);

  const handleUpdatePeers = useCallback((taskId: string, peers: TorrentPeer[]) => {
    setTasks((prev) =>
      prev.map((t) => (t.id === taskId ? { ...t, connectedPeers: peers, peers: peers.length } : t))
    );
    setSelectedTaskForTorrent((prev) => (prev && prev.id === taskId ? { ...prev, connectedPeers: peers, peers: peers.length } : prev));
    showToast('Torrent peer list updated', 'info');
  }, [showToast]);

  // RSS Feed Download Handler
  const handleDownloadRssItem = useCallback((item: RssFeedItem) => {
    const url = item.enclosureUrl || item.link;
    const protocol = detectProtocolFromUrl(url);
    const ext = item.category === 'music' ? '.mp3' : item.category === 'video' ? '.mp4' : '.zip';
    const name = item.title.replace(/[\/\?<>\\:\*\|":]/g, '_') + ext;
    const category: CategoryType = item.category || 'software';

    handleAddNewTask(
      {
        name,
        url,
        protocol,
        category,
        totalBytes: item.enclosureLength || 350000000,
      },
      true
    );

    setRssSubscriptions((prev) =>
      prev.map((sub) => ({
        ...sub,
        items: sub.items.map((i) => (i.id === item.id ? { ...i, downloaded: true } : i)),
      }))
    );

    showToast(`Queued RSS item: ${item.title}`, 'success');
  }, [handleAddNewTask, showToast]);

  // Backup and Restore Handlers
  const handleRestoreTasks = useCallback((restoredTasks: DownloadTask[]) => {
    setTasks(restoredTasks);
    showToast(`Restored ${restoredTasks.length} tasks from manifest`, 'success');
  }, [showToast]);

  const handleRestoreSettings = useCallback((restoredSettings: Partial<AppSettings>) => {
    setSettings((prev) => ({ ...prev, ...restoredSettings }));
    showToast('Restored settings successfully', 'success');
  }, [showToast]);

  return (
    <div className="flex flex-col h-screen w-screen overflow-hidden bg-neutral-100 dark:bg-[#191919] font-sans antialiased text-neutral-800 dark:text-neutral-100">
      {/* Fluent Title Bar */}
      <TitleBar
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        totalDownloadSpeed={totalDownloadSpeed}
        totalUploadSpeed={totalUploadSpeed}
        activeTaskCount={activeTaskCount}
        onOpenNewTask={() => setIsNewTaskModalOpen(true)}
      />

      {/* Main App Workspace */}
      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar */}
        <Sidebar
          activePage={activePage}
          onPageChange={setActivePage}
          activeDownloadCount={activeTaskCount}
          sniffedCount={mediaResources.length + images.length}
          packsCount={featurePacks.filter((p) => p.enabled).length}
          isDark={isDark}
          onToggleTheme={() => setIsDark((d) => !d)}
        />

        {/* Dynamic Page Views */}
        <main className="flex-1 flex flex-col overflow-hidden">
          {activePage === 'tasks' && (
            <DownloadsPage
              tasks={tasks}
              searchQuery={searchQuery}
              speedHistory={speedHistory}
              uploadHistory={uploadHistory}
              currentSpeed={totalDownloadSpeed}
              currentUpload={totalUploadSpeed}
              peakSpeed={peakSpeed}
              planConfig={planConfig}
              onOpenNewTask={() => setIsNewTaskModalOpen(true)}
              onOpenBatchUrl={() => setIsBatchUrlOpen(true)}
              onOpenFileHash={handleOpenFileHash}
              onOpenPlanTask={() => setIsPlanTaskOpen(true)}
              onOpenBrowserExt={() => setIsBrowserExtOpen(true)}
              onTogglePause={handleTogglePause}
              onRestart={handleRestart}
              onDelete={handleDeleteTask}
              onPauseAll={handlePauseAll}
              onResumeAll={handleResumeAll}
              onDeleteFinished={handleDeleteFinished}
              onCopyUrl={handleCopyUrl}
              onOpenDetails={setSelectedTaskForDetails}
              onSaveToDisk={handleSaveToDisk}
              onOpenRssModal={() => setIsRssModalOpen(true)}
              onOpenBackupModal={() => setIsBackupModalOpen(true)}
              onOpenPreview={setSelectedTaskForPreview}
              onOpenTorrentPeers={setSelectedTaskForTorrent}
              onMoveUp={handleMoveUp}
              onMoveDown={handleMoveDown}
              onSetPriority={handleSetPriority}
            />
          )}

          {activePage === 'sniffer' && (
            <MediaSnifferPage
              mediaResources={mediaResources}
              images={images}
              onDownloadResource={handleDownloadResource}
              onDownloadImages={handleDownloadImages}
              onRefreshSniff={handleRefreshSniff}
            />
          )}

          {activePage === 'packs' && (
            <FeaturePacksPage
              packs={featurePacks}
              onTogglePack={handleTogglePack}
              onUpdatePackSettings={() => {}}
            />
          )}

          {activePage === 'settings' && (
            <SettingsPage
              settings={settings}
              onUpdateSettings={(newS) => setSettings((prev) => ({ ...prev, ...newS }))}
              onShowToast={showToast}
            />
          )}
        </main>
      </div>

      {/* Task Details & Chunks Modal */}
      {selectedTaskForDetails && (
        <TaskDetailsModal
          task={selectedTaskForDetails}
          onClose={() => setSelectedTaskForDetails(null)}
          onSaveToDisk={handleSaveToDisk}
        />
      )}

      {/* In-App Media Player & Stream Previewer Modal */}
      <MediaPlayerModal
        isOpen={!!selectedTaskForPreview}
        task={selectedTaskForPreview}
        onClose={() => setSelectedTaskForPreview(null)}
        onSaveToDisk={handleSaveToDisk}
      />

      {/* BitTorrent Trackers & Peer Swarm Modal */}
      <TorrentPeersModal
        isOpen={!!selectedTaskForTorrent}
        task={selectedTaskForTorrent}
        onClose={() => setSelectedTaskForTorrent(null)}
        onUpdateTrackers={handleUpdateTrackers}
      />

      {/* Backup & Manifest Export/Import Modal */}
      <BackupRestoreModal
        isOpen={isBackupModalOpen}
        onClose={() => setIsBackupModalOpen(false)}
        tasks={tasks}
        settings={settings}
        onRestoreData={(restoredTasks, restoredSettings) => {
          handleRestoreTasks(restoredTasks);
          if (restoredSettings) {
            handleRestoreSettings(restoredSettings);
          }
        }}
        onShowToast={showToast}
      />

      {/* RSS Feed & Podcast Auto-Downloader Modal */}
      <RssFeedModal
        isOpen={isRssModalOpen}
        onClose={() => setIsRssModalOpen(false)}
        subscriptions={rssSubscriptions}
        onAddSubscription={(sub) => setRssSubscriptions((prev) => [sub, ...prev])}
        onRemoveSubscription={(id) => setRssSubscriptions((prev) => prev.filter((s) => s.id !== id))}
        onUpdateSubscription={(sub) => setRssSubscriptions((prev) => prev.map((s) => (s.id === sub.id ? sub : s)))}
        onQueueTask={(partialTask, startImmediately) => handleAddNewTask(partialTask, startImmediately)}
        onShowToast={showToast}
      />

      {/* New Task Creator Modal */}
      <NewTaskModal
        isOpen={isNewTaskModalOpen}
        onClose={() => setIsNewTaskModalOpen(false)}
        onAddTask={handleAddNewTask}
        onAddBatchTasks={handleAddBatchTasks}
        autoDetectCategory={settings.autoDetectCategory}
      />

      {/* File Hash & Checksum Verifier Modal */}
      <FileHashModal
        isOpen={isFileHashOpen}
        onClose={() => setIsFileHashOpen(false)}
        tasks={tasks}
        initialTask={targetHashTask}
      />

      {/* Task Planner & Scheduler Modal */}
      <PlanTaskModal
        isOpen={isPlanTaskOpen}
        onClose={() => setIsPlanTaskOpen(false)}
        config={planConfig}
        onSaveConfig={(newConfig) => {
          setPlanConfig(newConfig);
          showToast(
            newConfig.enabled 
              ? `Task Planner enabled (Start at ${newConfig.scheduledTime || 'Countdown'})` 
              : 'Task Planner disabled',
            'info'
          );
        }}
      />

      {/* Batch URL Pattern Generator Modal */}
      <BatchUrlModal
        isOpen={isBatchUrlOpen}
        onClose={() => setIsBatchUrlOpen(false)}
        onAddUrls={handleAddBatchTasks}
      />

      {/* Browser Extension Integration Modal */}
      <BrowserExtensionModal
        isOpen={isBrowserExtOpen}
        onClose={() => setIsBrowserExtOpen(false)}
        onSimulateIntercept={(url) => {
          setDetectedClipboardUrl(url);
          showToast('Intercepted browser download request!', 'info');
        }}
      />

      {/* Clipboard Sniffer Detection Alert Banner */}
      <ClipboardSnifferBanner
        detectedUrl={detectedClipboardUrl}
        onAccept={(url) => {
          const protocol = detectProtocolFromUrl(url);
          const urlLast = url.split('/').pop() || 'intercepted_download.bin';
          const name = urlLast.includes('?') ? urlLast.split('?')[0] : urlLast;
          const category = settings.autoDetectCategory ? detectCategoryFromFileName(name) : 'software';
          handleAddNewTask({ name, url, protocol, category }, true);
          setDetectedClipboardUrl(null);
        }}
        onDismiss={() => setDetectedClipboardUrl(null)}
      />

      {/* Floating Notifications Toast Container */}
      <ToastContainer toasts={toasts} onDismiss={dismissToast} />
    </div>
  );
}

export default App;
