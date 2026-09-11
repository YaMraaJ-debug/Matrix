import { useState } from 'react';
import { 
  X, 
  Rss, 
  Plus, 
  RefreshCw, 
  Filter, 
  Download, 
  Check, 
  ExternalLink, 
  Sparkles, 
  Trash2,
  Calendar,
  Layers,
  Radio,
  Clock
} from 'lucide-react';
import { RssFeedSubscription, RssFeedItem, DownloadTask, CategoryType } from '../types';
import { formatBytes } from '../utils/formatters';

interface RssFeedModalProps {
  isOpen: boolean;
  subscriptions: RssFeedSubscription[];
  onClose: () => void;
  onAddSubscription: (sub: RssFeedSubscription) => void;
  onRemoveSubscription: (id: string) => void;
  onUpdateSubscription: (sub: RssFeedSubscription) => void;
  onQueueTask: (task: Partial<DownloadTask>, startImmediately: boolean) => void;
  onShowToast: (msg: string, intent?: 'info' | 'success' | 'warning') => void;
}

export function RssFeedModal({
  isOpen,
  subscriptions,
  onClose,
  onAddSubscription,
  onRemoveSubscription,
  onUpdateSubscription,
  onQueueTask,
  onShowToast,
}: RssFeedModalProps) {
  const [selectedFeedId, setSelectedFeedId] = useState<string>(
    subscriptions[0]?.id || ''
  );
  const [isAddingNewFeed, setIsAddingNewFeed] = useState(false);
  const [newFeedTitle, setNewFeedTitle] = useState('');
  const [newFeedUrl, setNewFeedUrl] = useState('');
  const [newFeedCategory, setNewFeedCategory] = useState<CategoryType>('video');
  const [isRefreshing, setIsRefreshing] = useState(false);

  if (!isOpen) return null;

  const currentFeed = subscriptions.find((s) => s.id === selectedFeedId) || subscriptions[0];

  const handleCreateFeed = () => {
    if (!newFeedTitle.trim() || !newFeedUrl.trim()) return;

    const newSub: RssFeedSubscription = {
      id: `feed-${Date.now()}`,
      title: newFeedTitle.trim(),
      feedUrl: newFeedUrl.trim(),
      lastUpdated: Date.now(),
      autoDownload: false,
      filterRegex: '',
      items: [
        {
          id: `item-${Date.now()}-1`,
          title: `${newFeedTitle.trim()} - Latest Episode 01`,
          link: newFeedUrl.trim(),
          enclosureUrl: newFeedUrl.trim().endsWith('.mp4') ? newFeedUrl.trim() : 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
          enclosureLength: 524288000,
          pubDate: new Date().toISOString().slice(0, 16).replace('T', ' '),
          category: newFeedCategory,
        },
      ],
    };

    onAddSubscription(newSub);
    setSelectedFeedId(newSub.id);
    setIsAddingNewFeed(false);
    setNewFeedTitle('');
    setNewFeedUrl('');
    onShowToast(`Subscribed to RSS Feed: ${newSub.title}`, 'success');
  };

  const handleRefreshFeed = () => {
    if (!currentFeed) return;
    setIsRefreshing(true);
    setTimeout(() => {
      setIsRefreshing(false);
      const updatedFeed: RssFeedSubscription = {
        ...currentFeed,
        lastUpdated: Date.now(),
      };
      onUpdateSubscription(updatedFeed);
      onShowToast(`Refreshed ${currentFeed.title} - up to date`, 'info');
    }, 600);
  };

  const handleQueueItem = (item: RssFeedItem) => {
    onQueueTask(
      {
        name: item.title,
        url: item.enclosureUrl || item.link,
        category: item.category || 'video',
        totalBytes: item.enclosureLength || 450000000,
        protocol: (item.enclosureUrl || item.link).startsWith('magnet:') ? 'torrent' : 'http',
      },
      true
    );

    if (currentFeed) {
      const updatedItems = currentFeed.items.map((it) =>
        it.id === item.id ? { ...it, downloaded: true } : it
      );
      onUpdateSubscription({ ...currentFeed, items: updatedItems });
    }
  };

  const handleQueueAll = () => {
    if (!currentFeed) return;
    const pendingItems = currentFeed.items.filter((it) => !it.downloaded);
    if (pendingItems.length === 0) {
      onShowToast('All feed items are already queued!', 'info');
      return;
    }

    pendingItems.forEach((it) => {
      onQueueTask(
        {
          name: it.title,
          url: it.enclosureUrl || it.link,
          category: it.category || 'video',
          totalBytes: it.enclosureLength || 350000000,
          protocol: (it.enclosureUrl || it.link).startsWith('magnet:') ? 'torrent' : 'http',
        },
        true
      );
    });

    const updatedItems = currentFeed.items.map((it) => ({ ...it, downloaded: true }));
    onUpdateSubscription({ ...currentFeed, items: updatedItems });
    onShowToast(`Queued ${pendingItems.length} items from ${currentFeed.title}`, 'success');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
      <div className="w-full max-w-4xl bg-white dark:bg-[#252525] border border-neutral-200 dark:border-neutral-700/80 rounded-2xl shadow-2xl overflow-hidden flex flex-col h-[82vh] text-neutral-800 dark:text-neutral-100 animate-in fade-in zoom-in-95 duration-200">
        {/* Top Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-700/80 bg-neutral-50/70 dark:bg-[#1e1e1e]">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400 border border-amber-200 dark:border-amber-800">
              <Rss className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-semibold">RSS & Podcast Auto-Downloader</h2>
              <p className="text-xs text-neutral-400">
                Subscribe to feeds, apply regex filters, and auto-download new episodes
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-700 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* 2-Column Split: Sidebar Feeds + Content */}
        <div className="flex-1 flex overflow-hidden">
          {/* Left: Feed Subscriptions List */}
          <div className="w-64 border-r border-neutral-200 dark:border-neutral-700/80 flex flex-col bg-neutral-50/40 dark:bg-[#202020]">
            <div className="p-3 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
              <span className="text-xs font-semibold text-neutral-600 dark:text-neutral-300">
                Subscriptions ({subscriptions.length})
              </span>
              <button
                onClick={() => setIsAddingNewFeed(true)}
                className="p-1 rounded-md bg-amber-500 hover:bg-amber-400 text-white text-xs cursor-pointer flex items-center space-x-1"
                title="Add New RSS Feed"
              >
                <Plus className="w-3.5 h-3.5" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-2 space-y-1">
              {subscriptions.map((sub) => (
                <button
                  key={sub.id}
                  onClick={() => {
                    setSelectedFeedId(sub.id);
                    setIsAddingNewFeed(false);
                  }}
                  className={`w-full text-left p-2.5 rounded-xl text-xs transition-colors cursor-pointer flex items-center justify-between ${
                    selectedFeedId === sub.id && !isAddingNewFeed
                      ? 'bg-amber-100/70 dark:bg-amber-950/40 text-amber-900 dark:text-amber-200 font-semibold border border-amber-300 dark:border-amber-800/80'
                      : 'hover:bg-neutral-100 dark:hover:bg-neutral-800 text-neutral-700 dark:text-neutral-300'
                  }`}
                >
                  <div className="min-w-0 pr-2">
                    <p className="truncate font-medium">{sub.title}</p>
                    <p className="text-[10px] text-neutral-400 truncate">{sub.items.length} items</p>
                  </div>
                  <Rss className="w-3 h-3 text-amber-500 flex-shrink-0" />
                </button>
              ))}
            </div>
          </div>

          {/* Right: Selected Feed / New Feed Form */}
          <div className="flex-1 flex flex-col overflow-hidden bg-white dark:bg-[#252525]">
            {isAddingNewFeed ? (
              <div className="p-6 space-y-4 max-w-lg">
                <h3 className="text-sm font-bold text-neutral-800 dark:text-neutral-100">
                  Subscribe to a New RSS Feed
                </h3>
                <div className="space-y-3">
                  <div>
                    <label className="text-xs font-semibold text-neutral-700 dark:text-neutral-300 block mb-1">
                      Feed Title:
                    </label>
                    <input
                      type="text"
                      placeholder="e.g. My Favorite Podcast or GitHub Release Tracker"
                      value={newFeedTitle}
                      onChange={(e) => setNewFeedTitle(e.target.value)}
                      className="w-full p-2 text-xs rounded-lg border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-800"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold text-neutral-700 dark:text-neutral-300 block mb-1">
                      Feed URL (XML/RSS/Atom):
                    </label>
                    <input
                      type="text"
                      placeholder="https://example.com/podcast.rss"
                      value={newFeedUrl}
                      onChange={(e) => setNewFeedUrl(e.target.value)}
                      className="w-full p-2 text-xs rounded-lg border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 font-mono"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold text-neutral-700 dark:text-neutral-300 block mb-1">
                      Default Category:
                    </label>
                    <select
                      value={newFeedCategory}
                      onChange={(e) => setNewFeedCategory(e.target.value as CategoryType)}
                      className="w-full p-2 text-xs rounded-lg border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-800"
                    >
                      <option value="video">Video & Shows</option>
                      <option value="music">Music & Podcasts</option>
                      <option value="software">Software Releases</option>
                      <option value="archive">Archives & Torrents</option>
                    </select>
                  </div>

                  <div className="flex space-x-2 pt-2">
                    <button
                      onClick={handleCreateFeed}
                      className="px-4 py-2 rounded-lg bg-amber-600 hover:bg-amber-500 text-white text-xs font-semibold cursor-pointer"
                    >
                      Subscribe & Parse
                    </button>
                    <button
                      onClick={() => setIsAddingNewFeed(false)}
                      className="px-4 py-2 rounded-lg border border-neutral-300 dark:border-neutral-600 text-xs cursor-pointer"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              </div>
            ) : currentFeed ? (
              <div className="flex-1 flex flex-col overflow-hidden">
                {/* Feed Toolbar & Filter Regex */}
                <div className="p-4 border-b border-neutral-200 dark:border-neutral-700/80 bg-neutral-50/50 dark:bg-neutral-800/30 space-y-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="text-sm font-bold text-neutral-800 dark:text-neutral-100 flex items-center space-x-2">
                        <span>{currentFeed.title}</span>
                      </h3>
                      <p className="text-[11px] text-neutral-400 font-mono truncate max-w-lg mt-0.5">
                        {currentFeed.feedUrl}
                      </p>
                    </div>

                    <div className="flex items-center space-x-2">
                      <button
                        onClick={handleRefreshFeed}
                        disabled={isRefreshing}
                        className="flex items-center space-x-1 px-2.5 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 text-xs text-neutral-700 dark:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 cursor-pointer disabled:opacity-50"
                      >
                        <RefreshCw className={`w-3 h-3 ${isRefreshing ? 'animate-spin' : ''}`} />
                        <span>Check Updates</span>
                      </button>

                      <button
                        onClick={handleQueueAll}
                        className="flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-amber-500 hover:bg-amber-400 text-white text-xs font-semibold shadow-xs cursor-pointer"
                      >
                        <Download className="w-3.5 h-3.5" />
                        <span>Queue All ({currentFeed.items.filter((i) => !i.downloaded).length})</span>
                      </button>
                    </div>
                  </div>

                  {/* Filter Regex Bar */}
                  <div className="flex items-center space-x-2 text-xs">
                    <Filter className="w-3.5 h-3.5 text-neutral-400" />
                    <input
                      type="text"
                      placeholder="Regex Filter (e.g. '(4K|1080p)' or 'FLAC')"
                      value={currentFeed.filterRegex || ''}
                      onChange={(e) => {
                        onUpdateSubscription({ ...currentFeed, filterRegex: e.target.value });
                      }}
                      className="flex-1 p-1.5 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs font-mono"
                    />
                    <label className="flex items-center space-x-1.5 cursor-pointer ml-2">
                      <input
                        type="checkbox"
                        checked={currentFeed.autoDownload}
                        onChange={(e) => {
                          onUpdateSubscription({ ...currentFeed, autoDownload: e.target.checked });
                          onShowToast(
                            e.target.checked
                              ? `Auto-download enabled for ${currentFeed.title}`
                              : `Auto-download disabled for ${currentFeed.title}`,
                            'info'
                          );
                        }}
                        className="rounded text-amber-600"
                      />
                      <span className="text-[11px] font-medium text-neutral-600 dark:text-neutral-300">
                        Auto-download matched
                      </span>
                    </label>
                  </div>
                </div>

                {/* Feed Items List */}
                <div className="flex-1 overflow-y-auto p-4 space-y-2.5">
                  {currentFeed.items.map((item) => (
                    <div
                      key={item.id}
                      className="p-3 rounded-xl border border-neutral-200/80 dark:border-neutral-700/80 bg-neutral-50/30 dark:bg-neutral-800/30 hover:bg-neutral-50 dark:hover:bg-neutral-800/60 transition-colors flex items-center justify-between gap-3"
                    >
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center space-x-2">
                          <h4 className="text-xs font-semibold text-neutral-800 dark:text-neutral-100 truncate">
                            {item.title}
                          </h4>
                          {item.category && (
                            <span className="text-[10px] font-semibold px-1.5 py-0.2 rounded bg-neutral-200 dark:bg-neutral-700 text-neutral-600 dark:text-neutral-300 capitalize">
                              {item.category}
                            </span>
                          )}
                        </div>

                        <div className="flex items-center space-x-3 text-[11px] text-neutral-400 mt-1 font-mono">
                          <span className="flex items-center space-x-1">
                            <Clock className="w-3 h-3" />
                            <span>{item.pubDate}</span>
                          </span>
                          {item.enclosureLength && (
                            <>
                              <span>•</span>
                              <span>{formatBytes(item.enclosureLength)}</span>
                            </>
                          )}
                        </div>
                      </div>

                      <div>
                        {item.downloaded ? (
                          <span className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-md bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-300 text-xs font-medium">
                            <Check className="w-3.5 h-3.5" />
                            <span>Queued</span>
                          </span>
                        ) : (
                          <button
                            onClick={() => handleQueueItem(item)}
                            className="flex items-center space-x-1 px-3 py-1 rounded-lg bg-sky-600 hover:bg-sky-500 text-white text-xs font-medium shadow-xs cursor-pointer"
                          >
                            <Download className="w-3.5 h-3.5" />
                            <span>Download</span>
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
}
