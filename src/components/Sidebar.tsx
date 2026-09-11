import { 
  Download, 
  Radio, 
  Boxes, 
  Settings, 
  Sun, 
  Moon, 
  HardDrive, 
  Server,
  Activity
} from 'lucide-react';
import { ActivePage } from '../types';

interface SidebarProps {
  activePage: ActivePage;
  onPageChange: (page: ActivePage) => void;
  activeDownloadCount: number;
  sniffedCount: number;
  packsCount: number;
  isDark: boolean;
  onToggleTheme: () => void;
}

export function Sidebar({
  activePage,
  onPageChange,
  activeDownloadCount,
  sniffedCount,
  packsCount,
  isDark,
  onToggleTheme,
}: SidebarProps) {
  const navItems = [
    {
      id: 'tasks' as ActivePage,
      label: 'Downloads',
      subtitle: 'Active & Finished Tasks',
      icon: Download,
      badge: activeDownloadCount > 0 ? activeDownloadCount : null,
      badgeColor: 'bg-sky-500 text-white',
    },
    {
      id: 'sniffer' as ActivePage,
      label: 'Resource Sniffer',
      subtitle: 'Media & Stream Grabber',
      icon: Radio,
      badge: sniffedCount > 0 ? sniffedCount : null,
      badgeColor: 'bg-purple-500 text-white',
    },
    {
      id: 'packs' as ActivePage,
      label: 'Feature Packs',
      subtitle: 'Engines & Mirrors',
      icon: Boxes,
      badge: `${packsCount} active`,
      badgeColor: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300',
    },
    {
      id: 'settings' as ActivePage,
      label: 'Settings',
      subtitle: 'Speed & Aria2 RPC',
      icon: Settings,
      badge: null,
      badgeColor: '',
    },
  ];

  return (
    <aside className="w-56 flex-shrink-0 flex flex-col justify-between bg-neutral-50/70 dark:bg-[#252525]/80 border-r border-black/[0.06] dark:border-white/[0.08] p-2 select-none">
      {/* Top: Nav links */}
      <div className="space-y-1">
        <div className="px-3 py-2 text-[11px] font-semibold uppercase tracking-wider text-neutral-400 dark:text-neutral-500">
          Navigation
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activePage === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onPageChange(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-lg text-left transition-all duration-150 cursor-pointer ${
                isActive
                  ? 'bg-sky-500/10 text-sky-700 dark:text-sky-300 font-semibold shadow-xs'
                  : 'text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200/50 dark:hover:bg-neutral-700/40'
              }`}
            >
              <div className="flex items-center space-x-2.5 min-w-0">
                <div
                  className={`w-7 h-7 rounded-md flex items-center justify-center ${
                    isActive
                      ? 'bg-sky-500 text-white'
                      : 'bg-neutral-200/70 dark:bg-neutral-700/60 text-neutral-600 dark:text-neutral-300'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                </div>
                <div className="truncate">
                  <div className="text-xs leading-tight font-medium truncate">{item.label}</div>
                  <div className="text-[10px] text-neutral-400 dark:text-neutral-500 leading-tight truncate">
                    {item.subtitle}
                  </div>
                </div>
              </div>

              {item.badge && (
                <span
                  className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full ${item.badgeColor}`}
                >
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Bottom Area: Aria2 status & Theme switcher & Drive info */}
      <div className="space-y-2 pt-2 border-t border-black/[0.06] dark:border-white/[0.08]">
        {/* Aria2 RPC Live Status Widget */}
        <div className="p-2 rounded-lg bg-neutral-100/80 dark:bg-neutral-800/60 border border-neutral-200/50 dark:border-neutral-700/40 text-[11px]">
          <div className="flex items-center justify-between font-medium text-neutral-700 dark:text-neutral-200">
            <div className="flex items-center space-x-1.5">
              <Server className="w-3.5 h-3.5 text-sky-500" />
              <span>Aria2 RPC</span>
            </div>
            <span className="flex items-center space-x-1 text-[10px] text-emerald-600 dark:text-emerald-400 font-mono">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping inline-block" />
              <span>:6800 Ready</span>
            </span>
          </div>
        </div>

        {/* Disk Free Space */}
        <div className="px-2.5 py-1 flex items-center justify-between text-[11px] text-neutral-500 dark:text-neutral-400">
          <div className="flex items-center space-x-1.5">
            <HardDrive className="w-3.5 h-3.5 text-neutral-400" />
            <span>Disk Space</span>
          </div>
          <span className="font-mono text-[10px]">142.6 GB free</span>
        </div>

        {/* Theme Toggle Button */}
        <button
          onClick={onToggleTheme}
          className="w-full flex items-center justify-between px-3 py-2 rounded-md bg-neutral-100/70 dark:bg-neutral-800/60 hover:bg-neutral-200/70 dark:hover:bg-neutral-700/60 text-neutral-700 dark:text-neutral-200 text-xs transition-colors cursor-pointer"
        >
          <span className="flex items-center space-x-2">
            {isDark ? <Moon className="w-3.5 h-3.5 text-amber-400" /> : <Sun className="w-3.5 h-3.5 text-amber-500" />}
            <span>{isDark ? 'Dark Theme' : 'Light Theme'}</span>
          </span>
          <span className="text-[10px] font-medium text-neutral-400">Toggle</span>
        </button>
      </div>
    </aside>
  );
}
