import { useState } from 'react';
import { 
  X, 
  Radio, 
  Users, 
  Plus, 
  RefreshCw, 
  ShieldCheck, 
  Globe, 
  ArrowDown, 
  ArrowUp, 
  Sparkles, 
  Trash2,
  Activity,
  Check
} from 'lucide-react';
import { DownloadTask, TorrentTracker, TorrentPeer } from '../types';
import { formatSpeed } from '../utils/formatters';

interface TorrentPeersModalProps {
  isOpen: boolean;
  task: DownloadTask | null;
  onClose: () => void;
  onUpdateTrackers?: (taskId: string, trackers: TorrentTracker[]) => void;
}

const DEFAULT_PUBLIC_TRACKERS = [
  'udp://tracker.opentrackr.org:1337/announce',
  'udp://open.demonii.com:1337/announce',
  'udp://tracker.coppersurfer.tk:6969/announce',
  'wss://tracker.openwebtorrent.com',
  'https://tracker.torrent.eu.org:451/announce',
  'udp://exodus.desync.com:6969/announce'
];

export function TorrentPeersModal({
  isOpen,
  task,
  onClose,
  onUpdateTrackers,
}: TorrentPeersModalProps) {
  const [activeTab, setActiveTab] = useState<'trackers' | 'peers'>('trackers');
  const [newTrackerUrl, setNewTrackerUrl] = useState('');
  const [peerSearch, setPeerSearch] = useState('');
  const [refreshing, setRefreshing] = useState(false);

  if (!isOpen || !task) return null;

  const currentTrackers: TorrentTracker[] = task.trackers && task.trackers.length > 0
    ? task.trackers
    : [
        {
          id: 'trk-default-1',
          url: 'udp://tracker.opentrackr.org:1337/announce',
          status: 'active',
          seeders: task.seeders || 18,
          leechers: 24,
          downloaded: 4890,
          lastAnnounce: '30s ago',
        },
        {
          id: 'trk-default-2',
          url: 'https://tracker.torrent.eu.org:451/announce',
          status: 'active',
          seeders: 12,
          leechers: 15,
          downloaded: 2410,
          lastAnnounce: '45s ago',
        },
      ];

  const currentPeers: TorrentPeer[] = task.connectedPeers && task.connectedPeers.length > 0
    ? task.connectedPeers
    : [
        {
          id: 'peer-sim-1',
          ip: '198.51.100.42:6881',
          country: 'US',
          client: 'qBittorrent/4.6.3',
          downloadSpeed: 3840000,
          uploadSpeed: 420000,
          progressPercent: 94.2,
          flags: 'u - downloading, I - incoming, E - encrypted',
        },
        {
          id: 'peer-sim-2',
          ip: '185.220.101.5:51413',
          country: 'DE',
          client: 'Transmission/4.0.5',
          downloadSpeed: 2980000,
          uploadSpeed: 512000,
          progressPercent: 100.0,
          flags: 'u - downloading, S - seeder, E - encrypted',
        },
        {
          id: 'peer-sim-3',
          ip: '103.24.120.89:6889',
          country: 'JP',
          client: 'GhostDownloader/3.0',
          downloadSpeed: 1840000,
          uploadSpeed: 180000,
          progressPercent: 68.5,
          flags: 'u - downloading, O - outgoing',
        },
        {
          id: 'peer-sim-4',
          ip: '49.36.88.19:6882',
          country: 'IN',
          client: 'qBittorrent/4.5.2',
          downloadSpeed: 986899,
          uploadSpeed: 146291,
          progressPercent: 55.0,
          flags: 'u - downloading, E - encrypted',
        },
        {
          id: 'peer-sim-5',
          ip: '82.165.197.1:6881',
          country: 'GB',
          client: 'Deluge/2.1.1',
          downloadSpeed: 540000,
          uploadSpeed: 82000,
          progressPercent: 88.0,
          flags: 'u - downloading, E - encrypted',
        },
      ];

  const handleAddTracker = () => {
    if (!newTrackerUrl.trim()) return;
    const newTrk: TorrentTracker = {
      id: `trk-${Date.now()}`,
      url: newTrackerUrl.trim(),
      status: 'announcing',
      seeders: Math.floor(Math.random() * 10) + 5,
      leechers: Math.floor(Math.random() * 15) + 3,
      downloaded: Math.floor(Math.random() * 2000),
      lastAnnounce: 'Just now',
    };
    const updated = [...currentTrackers, newTrk];
    if (onUpdateTrackers) onUpdateTrackers(task.id, updated);
    setNewTrackerUrl('');
  };

  const handleAddRecommended = () => {
    const existingUrls = new Set(currentTrackers.map((t) => t.url));
    const toAdd = DEFAULT_PUBLIC_TRACKERS.filter((u) => !existingUrls.has(u)).map((url, i) => ({
      id: `trk-rec-${Date.now()}-${i}`,
      url,
      status: 'active' as const,
      seeders: Math.floor(Math.random() * 25) + 8,
      leechers: Math.floor(Math.random() * 20) + 4,
      downloaded: Math.floor(Math.random() * 4000) + 1000,
      lastAnnounce: 'Just now',
    }));
    if (toAdd.length > 0 && onUpdateTrackers) {
      onUpdateTrackers(task.id, [...currentTrackers, ...toAdd]);
    }
  };

  const handleReannounce = () => {
    setRefreshing(true);
    setTimeout(() => {
      setRefreshing(false);
      const updated = currentTrackers.map((t) => ({
        ...t,
        status: 'active' as const,
        lastAnnounce: 'Just now',
        seeders: t.seeders + Math.floor(Math.random() * 3),
      }));
      if (onUpdateTrackers) onUpdateTrackers(task.id, updated);
    }, 800);
  };

  const filteredPeers = currentPeers.filter(
    (p) =>
      p.ip.toLowerCase().includes(peerSearch.toLowerCase()) ||
      p.client.toLowerCase().includes(peerSearch.toLowerCase()) ||
      p.country.toLowerCase().includes(peerSearch.toLowerCase())
  );

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
      <div className="w-full max-w-3xl bg-white dark:bg-[#252525] border border-neutral-200 dark:border-neutral-700/80 rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[85vh] animate-in fade-in zoom-in-95 duration-200 text-neutral-800 dark:text-neutral-100">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-700/80 bg-neutral-50/70 dark:bg-[#1e1e1e]">
          <div className="flex items-center space-x-3 min-w-0">
            <div className="p-2 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-800">
              <Radio className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <h2 className="text-base font-semibold truncate" title={task.name}>
                BitTorrent Trackers & Swarm
              </h2>
              <p className="text-xs text-neutral-400 truncate max-w-md">{task.name}</p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-700 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Swarm Health Status Bar */}
        <div className="px-6 py-2.5 bg-emerald-500/5 border-b border-neutral-200/80 dark:border-neutral-800 flex flex-wrap items-center justify-between text-xs gap-3 font-mono">
          <div className="flex items-center space-x-4">
            <span className="flex items-center space-x-1.5 text-emerald-600 dark:text-emerald-400 font-medium">
              <ShieldCheck className="w-4 h-4" />
              <span>DHT: 384 Nodes (Active)</span>
            </span>
            <span className="text-neutral-400">•</span>
            <span className="text-neutral-600 dark:text-neutral-300">PEX: Enabled</span>
            <span className="text-neutral-400">•</span>
            <span className="text-neutral-600 dark:text-neutral-300">LPD: Enabled</span>
          </div>

          <div className="flex items-center space-x-3 text-neutral-500 dark:text-neutral-400">
            <span>Seeds: <strong className="text-emerald-600 dark:text-emerald-400">{task.seeders || 19}</strong></span>
            <span>Peers: <strong className="text-sky-600 dark:text-sky-400">{task.peers || 42}</strong></span>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center border-b border-neutral-200 dark:border-neutral-700/80 px-6 bg-white dark:bg-[#252525]">
          <button
            onClick={() => setActiveTab('trackers')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors cursor-pointer ${
              activeTab === 'trackers'
                ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Radio className="w-3.5 h-3.5" />
            <span>Trackers ({currentTrackers.length})</span>
          </button>

          <button
            onClick={() => setActiveTab('peers')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors cursor-pointer ${
              activeTab === 'peers'
                ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Users className="w-3.5 h-3.5" />
            <span>Connected Peers ({currentPeers.length})</span>
          </button>

          <div className="ml-auto flex items-center space-x-2">
            <button
              onClick={handleReannounce}
              disabled={refreshing}
              className="flex items-center space-x-1 px-2.5 py-1 text-xs rounded-md bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-200 font-medium transition-colors cursor-pointer disabled:opacity-50"
            >
              <RefreshCw className={`w-3 h-3 ${refreshing ? 'animate-spin' : ''}`} />
              <span>Re-announce All</span>
            </button>
          </div>
        </div>

        {/* Tab Contents */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {activeTab === 'trackers' && (
            <div className="space-y-4">
              {/* Add Tracker Box */}
              <div className="p-3 rounded-xl border border-neutral-200 dark:border-neutral-700 bg-neutral-50/50 dark:bg-neutral-800/40 space-y-2">
                <label className="text-xs font-semibold text-neutral-700 dark:text-neutral-200 block">
                  Add Custom Announce URL:
                </label>
                <div className="flex space-x-2">
                  <input
                    type="text"
                    placeholder="udp://tracker.example.org:1337/announce"
                    value={newTrackerUrl}
                    onChange={(e) => setNewTrackerUrl(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleAddTracker()}
                    className="flex-1 p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs font-mono text-neutral-800 dark:text-neutral-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                  <button
                    onClick={handleAddTracker}
                    className="px-3 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold flex items-center space-x-1 cursor-pointer"
                  >
                    <Plus className="w-3.5 h-3.5" />
                    <span>Add</span>
                  </button>
                  <button
                    onClick={handleAddRecommended}
                    className="px-3 py-2 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-200 text-xs font-medium flex items-center space-x-1 cursor-pointer"
                    title="Add fast public trackers for higher download speeds"
                  >
                    <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                    <span>Auto-Inject Best Trackers</span>
                  </button>
                </div>
              </div>

              {/* Trackers List Table */}
              <div className="border border-neutral-200 dark:border-neutral-700 rounded-xl overflow-hidden shadow-xs">
                <table className="w-full text-left text-xs">
                  <thead className="bg-neutral-100/80 dark:bg-neutral-800/80 text-neutral-500 dark:text-neutral-400 font-mono text-[11px] border-b border-neutral-200 dark:border-neutral-700">
                    <tr>
                      <th className="p-2.5 pl-3">Tracker URL</th>
                      <th className="p-2.5">Status</th>
                      <th className="p-2.5">Seeds</th>
                      <th className="p-2.5">Peers</th>
                      <th className="p-2.5">Downloaded</th>
                      <th className="p-2.5">Last Update</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-neutral-100 dark:divide-neutral-800 font-mono">
                    {currentTrackers.map((trk) => (
                      <tr key={trk.id} className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                        <td className="p-2.5 pl-3 text-neutral-800 dark:text-neutral-200 font-medium truncate max-w-xs">
                          {trk.url}
                        </td>
                        <td className="p-2.5">
                          <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-semibold ${
                            trk.status === 'active'
                              ? 'bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300'
                              : 'bg-amber-100 dark:bg-amber-950 text-amber-700 dark:text-amber-300'
                          }`}>
                            <span className="w-1.5 h-1.5 rounded-full bg-current mr-1" />
                            {trk.status}
                          </span>
                        </td>
                        <td className="p-2.5 text-emerald-600 dark:text-emerald-400 font-semibold">{trk.seeders}</td>
                        <td className="p-2.5 text-sky-600 dark:text-sky-400 font-semibold">{trk.leechers}</td>
                        <td className="p-2.5 text-neutral-500">{trk.downloaded.toLocaleString()}</td>
                        <td className="p-2.5 text-neutral-400 text-[11px]">{trk.lastAnnounce || '1m ago'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'peers' && (
            <div className="space-y-4">
              {/* Peer Search */}
              <div className="flex items-center justify-between gap-3">
                <input
                  type="text"
                  placeholder="Filter peers by IP, country, or client name..."
                  value={peerSearch}
                  onChange={(e) => setPeerSearch(e.target.value)}
                  className="flex-1 p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs font-mono text-neutral-800 dark:text-neutral-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
                <span className="text-xs text-neutral-400 font-mono">
                  Showing {filteredPeers.length} of {currentPeers.length} peers
                </span>
              </div>

              {/* Peers Table */}
              <div className="border border-neutral-200 dark:border-neutral-700 rounded-xl overflow-hidden shadow-xs">
                <table className="w-full text-left text-xs">
                  <thead className="bg-neutral-100/80 dark:bg-neutral-800/80 text-neutral-500 dark:text-neutral-400 font-mono text-[11px] border-b border-neutral-200 dark:border-neutral-700">
                    <tr>
                      <th className="p-2.5 pl-3">Peer Address</th>
                      <th className="p-2.5">Region</th>
                      <th className="p-2.5">Client Software</th>
                      <th className="p-2.5">Down Speed</th>
                      <th className="p-2.5">Up Speed</th>
                      <th className="p-2.5">Progress</th>
                      <th className="p-2.5">Flags</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-neutral-100 dark:divide-neutral-800 font-mono">
                    {filteredPeers.map((peer) => (
                      <tr key={peer.id} className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                        <td className="p-2.5 pl-3 text-neutral-800 dark:text-neutral-200 font-medium">
                          {peer.ip}
                        </td>
                        <td className="p-2.5">
                          <span className="px-1.5 py-0.5 rounded bg-neutral-200 dark:bg-neutral-700 text-[10px] font-bold">
                            {peer.country}
                          </span>
                        </td>
                        <td className="p-2.5 text-neutral-600 dark:text-neutral-300 font-mono text-[11px]">
                          {peer.client}
                        </td>
                        <td className="p-2.5 text-emerald-600 dark:text-emerald-400 font-semibold">
                          ↓ {formatSpeed(peer.downloadSpeed)}
                        </td>
                        <td className="p-2.5 text-sky-600 dark:text-sky-400 font-semibold">
                          ↑ {formatSpeed(peer.uploadSpeed)}
                        </td>
                        <td className="p-2.5">
                          <div className="flex items-center space-x-2">
                            <div className="w-14 bg-neutral-200 dark:bg-neutral-700 h-1.5 rounded-full overflow-hidden">
                              <div
                                className="bg-emerald-500 h-full"
                                style={{ width: `${peer.progressPercent}%` }}
                              />
                            </div>
                            <span className="text-[10px] text-neutral-400">{peer.progressPercent}%</span>
                          </div>
                        </td>
                        <td className="p-2.5 text-neutral-400 text-[10px] truncate max-w-[120px]" title={peer.flags}>
                          {peer.flags}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
