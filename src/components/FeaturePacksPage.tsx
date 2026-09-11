import { useState } from 'react';
import { 
  Boxes, 
  Check, 
  Settings2, 
  Search, 
  Layers, 
  ExternalLink, 
  Cpu, 
  Sliders, 
  X, 
  Zap,
  ShieldCheck
} from 'lucide-react';
import { FeaturePack } from '../types';

interface FeaturePacksPageProps {
  packs: FeaturePack[];
  onTogglePack: (id: string) => void;
  onUpdatePackSettings: (id: string, settings: any) => void;
}

export function FeaturePacksPage({
  packs,
  onTogglePack,
  onUpdatePackSettings,
}: FeaturePacksPageProps) {
  const [search, setSearch] = useState('');
  const [selectedPack, setSelectedPack] = useState<FeaturePack | null>(null);

  const filteredPacks = packs.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.description.toLowerCase().includes(search.toLowerCase()) ||
      p.protocols.some((pr) => pr.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="flex-1 flex flex-col h-full overflow-hidden bg-neutral-100/50 dark:bg-[#202020]">
      {/* Top Header */}
      <div className="p-4 bg-white dark:bg-[#282828] border-b border-neutral-200 dark:border-neutral-700 flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/10 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
              <Boxes className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-base font-semibold text-neutral-800 dark:text-neutral-100">
                Ghost Feature Packs Ecosystem
              </h1>
              <p className="text-xs text-neutral-400">
                Modular protocol engines, mirror accelerators, and video extractors
              </p>
            </div>
          </div>

          <div className="text-xs text-neutral-500">
            <span className="font-semibold text-emerald-600 dark:text-emerald-400">
              {packs.filter((p) => p.enabled).length}
            </span>{' '}
            of {packs.length} packs active
          </div>
        </div>

        {/* Filter Input */}
        <div className="relative max-w-sm">
          <Search className="w-3.5 h-3.5 absolute left-2.5 top-1/2 -translate-y-1/2 text-neutral-400" />
          <input
            type="text"
            placeholder="Search packs by name, protocol, or engine..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full h-8 pl-8 pr-3 text-xs rounded-lg bg-neutral-50 dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />
        </div>
      </div>

      {/* Grid of Packs */}
      <div className="flex-1 p-4 overflow-y-auto">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
          {filteredPacks.map((pack) => (
            <div
              key={pack.id}
              className={`rounded-xl p-4 border transition-all duration-200 flex flex-col justify-between ${
                pack.enabled
                  ? 'bg-white dark:bg-[#2c2c2c] border-neutral-200 dark:border-neutral-700 shadow-xs'
                  : 'bg-neutral-50/70 dark:bg-[#252525]/60 border-neutral-200/50 dark:border-neutral-800 opacity-60'
              }`}
            >
              <div>
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center space-x-2.5">
                    <div className="w-8 h-8 rounded-lg bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-neutral-700 dark:text-neutral-200 font-mono font-bold text-xs">
                      <Zap className={`w-4 h-4 ${pack.enabled ? 'text-emerald-500' : 'text-neutral-400'}`} />
                    </div>
                    <div>
                      <h3 className="text-xs font-semibold text-neutral-800 dark:text-neutral-100">
                        {pack.name}
                      </h3>
                      <div className="flex items-center space-x-1.5 text-[10px] font-mono text-neutral-400 mt-0.5">
                        <span>v{pack.version}</span>
                        <span>•</span>
                        <span>{pack.identifier}</span>
                      </div>
                    </div>
                  </div>

                  {/* Toggle Switch */}
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={pack.enabled}
                      onChange={() => onTogglePack(pack.id)}
                      className="sr-only peer"
                    />
                    <div className="w-9 h-5 bg-neutral-200 dark:bg-neutral-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-emerald-500"></div>
                  </label>
                </div>

                {/* Description */}
                <p className="text-xs text-neutral-500 dark:text-neutral-400 mt-3 line-clamp-2 leading-relaxed">
                  {pack.description}
                </p>

                {/* Protocol Tags */}
                <div className="flex flex-wrap gap-1.5 mt-3">
                  {pack.protocols.map((proto) => (
                    <span
                      key={proto}
                      className="text-[10px] font-mono uppercase px-2 py-0.5 rounded-md bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 border border-neutral-200/60 dark:border-neutral-700/60"
                    >
                      {proto}
                    </span>
                  ))}
                </div>

                {/* Key Features list */}
                <div className="mt-3 pt-2.5 border-t border-neutral-100 dark:border-neutral-800 text-[11px] space-y-1">
                  {pack.features.map((feat) => (
                    <div key={feat} className="flex items-center space-x-1.5 text-neutral-500 dark:text-neutral-400">
                      <ShieldCheck className="w-3 h-3 text-emerald-500 flex-shrink-0" />
                      <span className="truncate">{feat}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Bottom Config button */}
              <div className="mt-4 pt-2.5 flex items-center justify-between border-t border-neutral-100 dark:border-neutral-800">
                <span className="text-[11px] flex items-center space-x-1 text-emerald-600 dark:text-emerald-400 font-medium">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 inline-block" />
                  <span>{pack.enabled ? 'Engine Operational' : 'Disabled'}</span>
                </span>

                <button
                  onClick={() => setSelectedPack(pack)}
                  className="flex items-center space-x-1 text-xs font-medium px-2.5 py-1 rounded bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 transition-colors cursor-pointer"
                >
                  <Settings2 className="w-3.5 h-3.5" />
                  <span>Configure</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Configure Pack Drawer / Modal */}
      {selectedPack && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs">
          <div className="w-full max-w-md bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden">
            <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <Settings2 className="w-4 h-4 text-emerald-500" />
                <h3 className="font-semibold text-neutral-800 dark:text-neutral-100 text-sm">
                  {selectedPack.name} Settings
                </h3>
              </div>
              <button
                onClick={() => setSelectedPack(null)}
                className="p-1 rounded text-neutral-400 hover:text-neutral-200 cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="p-4 space-y-4 text-xs">
              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  Engine Pipeline Mode
                </label>
                <select className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300">
                  <option>High Throughput (Optimized Chunking)</option>
                  <option>Low Memory (Buffered Streaming)</option>
                  <option>Strict Verification (CRC32 Check Per Chunk)</option>
                </select>
              </div>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  Max Worker Threads
                </label>
                <input
                  type="number"
                  defaultValue="32"
                  className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 font-mono"
                />
              </div>

              <div className="space-y-1.5">
                <label className="font-semibold text-neutral-700 dark:text-neutral-300">
                  Custom Headers / TLS Profile
                </label>
                <input
                  type="text"
                  defaultValue="Chrome 134 TLS Impersonation (wreq)"
                  className="w-full p-2 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 font-mono"
                />
              </div>

              <div className="p-3 rounded-lg bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300 text-[11px] leading-relaxed">
                Pack version {selectedPack.version} compiled with native acceleration.
              </div>
            </div>

            <div className="p-3.5 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 flex justify-end space-x-2">
              <button
                onClick={() => setSelectedPack(null)}
                className="px-4 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-medium text-xs shadow-xs cursor-pointer"
              >
                Apply Changes
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
