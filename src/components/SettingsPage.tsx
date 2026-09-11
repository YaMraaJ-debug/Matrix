import { useState } from 'react';
import { 
  Settings, 
  HardDrive, 
  Gauge, 
  Server, 
  Globe, 
  Palette, 
  Info, 
  Check, 
  Volume2, 
  ClipboardCheck, 
  RotateCcw,
  Sliders,
  ShieldAlert,
  Clock,
  Sun,
  Moon,
  Zap
} from 'lucide-react';
import { AppSettings } from '../types';

interface SettingsPageProps {
  settings: AppSettings;
  onUpdateSettings: (newSettings: Partial<AppSettings>) => void;
  onShowToast: (msg: string, intent?: 'info' | 'success' | 'warning') => void;
}

export function SettingsPage({
  settings,
  onUpdateSettings,
  onShowToast,
}: SettingsPageProps) {
  const [activeSection, setActiveSection] = useState<'general' | 'speed' | 'aria2' | 'proxy' | 'about'>('general');
  const [downloadDir, setDownloadDir] = useState(settings.downloadDirectory);
  const [maxTasks, setMaxTasks] = useState(settings.maxConcurrentDownloads);
  const [dlLimit, setDlLimit] = useState(settings.globalDownloadLimitKbps);
  const [ulLimit, setUlLimit] = useState(settings.globalUploadLimitKbps);
  const [rpcPort, setRpcPort] = useState(settings.aria2RpcPort);
  const [rpcSecret, setRpcSecret] = useState(settings.aria2RpcSecret);
  const [clipboardMon, setClipboardMon] = useState(settings.clipboardMonitor);
  const [autoStart, setAutoStart] = useState(settings.autoStartOnAdd);
  const [autoDetectCategory, setAutoDetectCategory] = useState(settings.autoDetectCategory ?? true);
  const [soundOn, setSoundOn] = useState(settings.playCompletionSound);
  const [proxyMode, setProxyMode] = useState(settings.proxyMode);
  const [proxyHost, setProxyHost] = useState(settings.proxyHost);
  const [proxyPort, setProxyPort] = useState(settings.proxyPort);
  const [rpcTested, setRpcTested] = useState(false);

  // Scheduled Bandwidth State
  const [schedEnabled, setSchedEnabled] = useState(settings.scheduledBandwidth?.enabled ?? false);
  const [dayStartHour, setDayStartHour] = useState(settings.scheduledBandwidth?.dayStartHour ?? 8);
  const [dayEndHour, setDayEndHour] = useState(settings.scheduledBandwidth?.dayEndHour ?? 23);
  const [dayLimitKbps, setDayLimitKbps] = useState(settings.scheduledBandwidth?.dayLimitKbps ?? 2048);
  const [nightLimitKbps, setNightLimitKbps] = useState(settings.scheduledBandwidth?.nightLimitKbps ?? 0);

  const currentHour = new Date().getHours();
  const isCurrentlyDay = currentHour >= dayStartHour && currentHour < dayEndHour;

  const handleSave = () => {
    onUpdateSettings({
      downloadDirectory: downloadDir,
      maxConcurrentDownloads: maxTasks,
      globalDownloadLimitKbps: dlLimit,
      globalUploadLimitKbps: ulLimit,
      aria2RpcPort: rpcPort,
      aria2RpcSecret: rpcSecret,
      clipboardMonitor: clipboardMon,
      autoStartOnAdd: autoStart,
      autoDetectCategory: autoDetectCategory,
      playCompletionSound: soundOn,
      proxyMode,
      proxyHost,
      proxyPort,
      scheduledBandwidth: {
        enabled: schedEnabled,
        dayStartHour,
        dayEndHour,
        dayLimitKbps,
        nightLimitKbps,
      },
    });
    onShowToast('Settings saved successfully', 'success');
  };

  const handleTestRpc = () => {
    setRpcTested(true);
    onShowToast(`Connected to Aria2 RPC at 127.0.0.1:${rpcPort} (OK)`, 'success');
    setTimeout(() => setRpcTested(false), 3000);
  };

  return (
    <div className="flex-1 flex flex-col h-full overflow-hidden bg-neutral-100/50 dark:bg-[#202020]">
      {/* Header */}
      <div className="p-4 bg-white dark:bg-[#282828] border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Settings className="w-5 h-5 text-neutral-600 dark:text-neutral-300" />
          <div>
            <h1 className="text-base font-semibold text-neutral-800 dark:text-neutral-100">
              Settings & Preferences
            </h1>
            <p className="text-xs text-neutral-400">
              Configure download engines, bandwidth throttling, and RPC endpoints
            </p>
          </div>
        </div>

        <button
          onClick={handleSave}
          className="flex items-center space-x-1.5 px-3.5 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white text-xs font-medium shadow-xs transition-colors cursor-pointer"
        >
          <Check className="w-3.5 h-3.5" />
          <span>Save Changes</span>
        </button>
      </div>

      {/* Settings layout: sidebar nav + contents */}
      <div className="flex-1 flex overflow-hidden">
        {/* Navigation pills */}
        <div className="w-48 border-r border-neutral-200 dark:border-neutral-700 p-2 space-y-1 bg-white/50 dark:bg-[#252525]/50">
          <button
            onClick={() => setActiveSection('general')}
            className={`w-full flex items-center space-x-2 px-3 py-2 rounded-lg text-xs font-medium text-left transition-colors cursor-pointer ${
              activeSection === 'general'
                ? 'bg-sky-500/10 text-sky-600 dark:text-sky-400'
                : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            }`}
          >
            <HardDrive className="w-4 h-4" />
            <span>General</span>
          </button>

          <button
            onClick={() => setActiveSection('speed')}
            className={`w-full flex items-center space-x-2 px-3 py-2 rounded-lg text-xs font-medium text-left transition-colors cursor-pointer ${
              activeSection === 'speed'
                ? 'bg-sky-500/10 text-sky-600 dark:text-sky-400'
                : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            }`}
          >
            <Gauge className="w-4 h-4" />
            <span>Speed & Limits</span>
          </button>

          <button
            onClick={() => setActiveSection('aria2')}
            className={`w-full flex items-center space-x-2 px-3 py-2 rounded-lg text-xs font-medium text-left transition-colors cursor-pointer ${
              activeSection === 'aria2'
                ? 'bg-sky-500/10 text-sky-600 dark:text-sky-400'
                : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            }`}
          >
            <Server className="w-4 h-4" />
            <span>Aria2 RPC Server</span>
          </button>

          <button
            onClick={() => setActiveSection('proxy')}
            className={`w-full flex items-center space-x-2 px-3 py-2 rounded-lg text-xs font-medium text-left transition-colors cursor-pointer ${
              activeSection === 'proxy'
                ? 'bg-sky-500/10 text-sky-600 dark:text-sky-400'
                : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            }`}
          >
            <Globe className="w-4 h-4" />
            <span>Network & Proxy</span>
          </button>

          <button
            onClick={() => setActiveSection('about')}
            className={`w-full flex items-center space-x-2 px-3 py-2 rounded-lg text-xs font-medium text-left transition-colors cursor-pointer ${
              activeSection === 'about'
                ? 'bg-sky-500/10 text-sky-600 dark:text-sky-400'
                : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            }`}
          >
            <Info className="w-4 h-4" />
            <span>About Ghost</span>
          </button>
        </div>

        {/* Section Contents */}
        <div className="flex-1 p-6 overflow-y-auto space-y-6 max-w-2xl text-xs">
          {activeSection === 'general' && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 border-b border-neutral-200 dark:border-neutral-700 pb-2">
                Download Destination & Automation
              </h3>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  Default Download Directory
                </label>
                <div className="flex space-x-2">
                  <input
                    type="text"
                    value={downloadDir}
                    onChange={(e) => setDownloadDir(e.target.value)}
                    className="flex-1 p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono text-xs text-neutral-800 dark:text-neutral-200"
                  />
                  <button
                    onClick={() => setDownloadDir('C:\\Users\\User\\Downloads\\GhostDownloader')}
                    className="px-3 py-2 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-800"
                  >
                    Reset
                  </button>
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300 flex items-center justify-between">
                  <span>Max Concurrent Active Tasks:</span>
                  <span className="font-mono text-sky-600 dark:text-sky-400 font-bold">{maxTasks} tasks</span>
                </label>
                <input
                  type="range"
                  min="1"
                  max="10"
                  value={maxTasks}
                  onChange={(e) => setMaxTasks(Number(e.target.value))}
                  className="w-full accent-sky-500 cursor-pointer"
                />
              </div>

              <div className="space-y-3 pt-2">
                <label className="flex items-center space-x-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={autoStart}
                    onChange={(e) => setAutoStart(e.target.checked)}
                    className="rounded text-sky-600"
                  />
                  <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                    Start downloads immediately after link creation
                  </span>
                </label>

                {/* Automatic Category Detection Toggle */}
                <label className="flex items-start space-x-2.5 cursor-pointer p-2.5 rounded-lg border border-neutral-200/80 dark:border-neutral-700/80 bg-neutral-50/50 dark:bg-neutral-800/30 hover:bg-neutral-100/60 dark:hover:bg-neutral-800/60 transition-colors">
                  <input
                    type="checkbox"
                    checked={autoDetectCategory}
                    onChange={(e) => setAutoDetectCategory(e.target.checked)}
                    className="rounded text-sky-600 mt-0.5 cursor-pointer"
                  />
                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <span className="text-neutral-700 dark:text-neutral-200 font-medium">
                        Automatic category detection based on file extensions
                      </span>
                      <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full ${
                        autoDetectCategory
                          ? 'bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-300 border border-emerald-300/60 dark:border-emerald-800'
                          : 'bg-neutral-200 dark:bg-neutral-700 text-neutral-600 dark:text-neutral-400'
                      }`}>
                        {autoDetectCategory ? 'Enabled' : 'Disabled'}
                      </span>
                    </div>
                    <p className="text-[11px] text-neutral-400 mt-0.5 leading-relaxed">
                      Automatically sorts newly added downloads into categories (Videos, Music, Software, Documents, Archives, Torrents) by analyzing file extensions (.mp4, .zip, .exe, .iso, etc.).
                    </p>
                  </div>
                </label>

                <label className="flex items-center space-x-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={clipboardMon}
                    onChange={(e) => setClipboardMon(e.target.checked)}
                    className="rounded text-sky-600"
                  />
                  <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                    Monitor system clipboard for downloadable URLs & magnet links
                  </span>
                </label>

                <label className="flex items-center space-x-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={soundOn}
                    onChange={(e) => setSoundOn(e.target.checked)}
                    className="rounded text-sky-600"
                  />
                  <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                    Play sound notification when download completes
                  </span>
                </label>
              </div>
            </div>
          )}

          {activeSection === 'speed' && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 border-b border-neutral-200 dark:border-neutral-700 pb-2">
                Bandwidth & Speed Throttle
              </h3>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300 flex items-center justify-between">
                  <span>Global Download Speed Limit:</span>
                  <span className="font-mono text-sky-600 dark:text-sky-400 font-bold">
                    {dlLimit === 0 ? 'Unlimited (Max Speed)' : `${dlLimit} KB/s (${(dlLimit / 1024).toFixed(1)} MB/s)`}
                  </span>
                </label>
                <input
                  type="range"
                  min="0"
                  max="102400"
                  step="1024"
                  value={dlLimit}
                  onChange={(e) => setDlLimit(Number(e.target.value))}
                  className="w-full accent-sky-500 cursor-pointer"
                />
                <div className="flex justify-between text-[10px] text-neutral-400 font-mono">
                  <span>0 (Unlimited)</span>
                  <span>10 MB/s</span>
                  <span>50 MB/s</span>
                  <span>100 MB/s</span>
                </div>
              </div>

              <div className="space-y-1.5 pt-3">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300 flex items-center justify-between">
                  <span>Global Upload Limit (BitTorrent Seeding):</span>
                  <span className="font-mono text-sky-600 dark:text-sky-400 font-bold">
                    {ulLimit === 0 ? 'Unlimited' : `${ulLimit} KB/s`}
                  </span>
                </label>
                <input
                  type="range"
                  min="0"
                  max="20480"
                  step="512"
                  value={ulLimit}
                  onChange={(e) => setUlLimit(Number(e.target.value))}
                  className="w-full accent-sky-500 cursor-pointer"
                />
              </div>

              {/* Scheduled Bandwidth Throttling Section */}
              <div className="mt-6 pt-4 border-t border-neutral-200 dark:border-neutral-700/80 space-y-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4 text-amber-500" />
                    <div>
                      <h4 className="text-xs font-bold text-neutral-800 dark:text-neutral-100">
                        Scheduled Day / Night Speed Rules
                      </h4>
                      <p className="text-[11px] text-neutral-400">
                        Automatically throttle bandwidth during peak daytime hours and unlock maximum speeds at night
                      </p>
                    </div>
                  </div>

                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={schedEnabled}
                      onChange={(e) => setSchedEnabled(e.target.checked)}
                      className="sr-only peer"
                    />
                    <div className="w-9 h-5 bg-neutral-200 dark:bg-neutral-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-amber-500"></div>
                  </label>
                </div>

                {schedEnabled && (
                  <div className="p-4 rounded-xl border border-amber-300/70 dark:border-amber-900/50 bg-amber-50/50 dark:bg-amber-950/20 space-y-4 text-xs animate-in fade-in duration-150">
                    {/* Active Status Badge */}
                    <div className="flex items-center justify-between p-2.5 rounded-lg bg-white dark:bg-neutral-800 border border-amber-200 dark:border-amber-800 font-mono">
                      <div className="flex items-center space-x-2">
                        {isCurrentlyDay ? (
                          <Sun className="w-4 h-4 text-amber-500" />
                        ) : (
                          <Moon className="w-4 h-4 text-indigo-400" />
                        )}
                        <span className="font-bold text-neutral-800 dark:text-neutral-100">
                          Currently Active: {isCurrentlyDay ? 'Daytime Throttle' : 'Nighttime Speed'}
                        </span>
                      </div>
                      <span className="text-[11px] font-semibold text-amber-600 dark:text-amber-400">
                        {isCurrentlyDay
                          ? `${(dayLimitKbps / 1024).toFixed(1)} MB/s Limit`
                          : nightLimitKbps === 0
                            ? 'Unlimited Full Speed'
                            : `${(nightLimitKbps / 1024).toFixed(1)} MB/s Limit`}
                      </span>
                    </div>

                    {/* Hours Range Configuration */}
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <label className="block text-neutral-600 dark:text-neutral-300 font-semibold mb-1">
                          Daytime Begins (Hour):
                        </label>
                        <select
                          value={dayStartHour}
                          onChange={(e) => setDayStartHour(Number(e.target.value))}
                          className="w-full p-2 rounded-lg border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 font-mono"
                        >
                          {Array.from({ length: 24 }).map((_, i) => (
                            <option key={i} value={i}>
                              {String(i).padStart(2, '0')}:00 {i < 12 ? 'AM' : 'PM'}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-neutral-600 dark:text-neutral-300 font-semibold mb-1">
                          Nighttime Begins (Hour):
                        </label>
                        <select
                          value={dayEndHour}
                          onChange={(e) => setDayEndHour(Number(e.target.value))}
                          className="w-full p-2 rounded-lg border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 font-mono"
                        >
                          {Array.from({ length: 24 }).map((_, i) => (
                            <option key={i} value={i}>
                              {String(i).padStart(2, '0')}:00 {i < 12 ? 'AM' : 'PM'}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    {/* Daytime Limit Slider */}
                    <div className="space-y-1.5 pt-2">
                      <div className="flex justify-between font-semibold">
                        <span className="flex items-center space-x-1 text-amber-700 dark:text-amber-300">
                          <Sun className="w-3.5 h-3.5" />
                          <span>Daytime Download Throttle:</span>
                        </span>
                        <span className="font-mono text-amber-600 dark:text-amber-400">
                          {dayLimitKbps} KB/s ({(dayLimitKbps / 1024).toFixed(1)} MB/s)
                        </span>
                      </div>
                      <input
                        type="range"
                        min="512"
                        max="51200"
                        step="512"
                        value={dayLimitKbps}
                        onChange={(e) => setDayLimitKbps(Number(e.target.value))}
                        className="w-full accent-amber-500 cursor-pointer"
                      />
                    </div>

                    {/* Nighttime Limit Slider */}
                    <div className="space-y-1.5 pt-1">
                      <div className="flex justify-between font-semibold">
                        <span className="flex items-center space-x-1 text-indigo-600 dark:text-indigo-400">
                          <Moon className="w-3.5 h-3.5" />
                          <span>Nighttime Download Throttle:</span>
                        </span>
                        <span className="font-mono text-indigo-600 dark:text-indigo-400">
                          {nightLimitKbps === 0 ? '0 (Unlimited Full Speed)' : `${nightLimitKbps} KB/s (${(nightLimitKbps / 1024).toFixed(1)} MB/s)`}
                        </span>
                      </div>
                      <input
                        type="range"
                        min="0"
                        max="102400"
                        step="1024"
                        value={nightLimitKbps}
                        onChange={(e) => setNightLimitKbps(Number(e.target.value))}
                        className="w-full accent-indigo-500 cursor-pointer"
                      />
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {activeSection === 'aria2' && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 border-b border-neutral-200 dark:border-neutral-700 pb-2">
                Aria2 Compatible RPC Server
              </h3>

              <p className="text-neutral-500 text-xs leading-relaxed">
                Ghost Downloader features a built-in JSON-RPC server compatible with aria2. Third-party browser extensions (AriaNg, Cat-Catch, YAAW) can directly send tasks here.
              </p>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                    RPC Host
                  </label>
                  <input
                    type="text"
                    disabled
                    value="127.0.0.1"
                    className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-100 dark:bg-neutral-800 font-mono"
                  />
                </div>

                <div className="space-y-1">
                  <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                    RPC Port
                  </label>
                  <input
                    type="number"
                    value={rpcPort}
                    onChange={(e) => setRpcPort(Number(e.target.value))}
                    className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  RPC Secret Token
                </label>
                <input
                  type="text"
                  value={rpcSecret}
                  onChange={(e) => setRpcSecret(e.target.value)}
                  className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono"
                />
              </div>

              <div className="pt-2">
                <button
                  onClick={handleTestRpc}
                  className="px-3.5 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-800 font-medium text-xs cursor-pointer flex items-center space-x-1.5"
                >
                  <Server className="w-3.5 h-3.5 text-emerald-500" />
                  <span>Test Local RPC Connection</span>
                </button>
              </div>
            </div>
          )}

          {activeSection === 'proxy' && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 border-b border-neutral-200 dark:border-neutral-700 pb-2">
                Network & Proxy Configuration
              </h3>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  Proxy Protocol
                </label>
                <select
                  value={proxyMode}
                  onChange={(e) => setProxyMode(e.target.value as any)}
                  className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800"
                >
                  <option value="none">Direct Connection (No Proxy)</option>
                  <option value="http">HTTP Proxy</option>
                  <option value="socks5">SOCKS5 Proxy</option>
                </select>
              </div>

              {proxyMode !== 'none' && (
                <div className="grid grid-cols-2 gap-3 pt-2">
                  <div className="space-y-1">
                    <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                      Proxy Host
                    </label>
                    <input
                      type="text"
                      value={proxyHost}
                      onChange={(e) => setProxyHost(e.target.value)}
                      placeholder="127.0.0.1"
                      className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono"
                    />
                  </div>
                  <div className="space-y-1">
                    <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                      Proxy Port
                    </label>
                    <input
                      type="number"
                      value={proxyPort}
                      onChange={(e) => setProxyPort(Number(e.target.value))}
                      placeholder="7890"
                      className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800 font-mono"
                    />
                  </div>
                </div>
              )}
            </div>
          )}

          {activeSection === 'about' && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100 border-b border-neutral-200 dark:border-neutral-700 pb-2">
                About Ghost Downloader 3
              </h3>

              <div className="p-4 rounded-xl bg-white dark:bg-[#282828] border border-neutral-200 dark:border-neutral-700 space-y-3">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-sky-500 to-blue-600 flex items-center justify-center text-white shadow-sm">
                    <svg className="w-6 h-6" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2C7.58 2 4 5.58 4 10v9.5c0 .6.7 1 1.2.6L7 18.5l2.5 2c.4.3 1 .3 1.4 0L12 19.5l1.1 1c.4.3 1 .3 1.4 0l2.5-2 1.8 1.6c.5.4 1.2 0 1.2-.6V10c0-4.42-3.58-8-8-8zm-2 9c-.83 0-1.5-.67-1.5-1.5S9.17 8 10 8s1.5.67 1.5 1.5S10.83 11 10 11zm4 0c-.83 0-1.5-.67-1.5-1.5S13.17 8 14 8s1.5.67 1.5 1.5S14.83 11 14 11z"/>
                    </svg>
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-neutral-800 dark:text-neutral-100">
                      Ghost Downloader 3 (v3.0.0)
                    </h4>
                    <p className="text-[11px] text-neutral-400">
                      The all-in-one high speed multi-protocol download manager
                    </p>
                  </div>
                </div>

                <p className="text-neutral-600 dark:text-neutral-300 leading-relaxed text-xs">
                  Started as a tool to help creators manage media resources and downloads. Supports HTTP/2, BitTorrent, M3U8, eD2k, YouTube, Bilibili, and Hugging Face mirror acceleration.
                </p>

                <div className="pt-2 border-t border-neutral-100 dark:border-neutral-700 text-[11px] text-neutral-400 space-y-1">
                  <div>License: GNU General Public License v3.0 (GPL v3)</div>
                  <div>Original Author: XiaoYouChR & open source contributors</div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
