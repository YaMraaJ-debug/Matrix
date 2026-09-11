import { useState } from 'react';
import { X, Chrome, Download, CheckCircle2, ExternalLink, Zap, ShieldCheck } from 'lucide-react';

interface BrowserExtensionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSimulateIntercept: (testUrl: string) => void;
}

export function BrowserExtensionModal({
  isOpen,
  onClose,
  onSimulateIntercept,
}: BrowserExtensionModalProps) {
  const [activeBrowser, setActiveBrowser] = useState<'chrome' | 'edge' | 'firefox'>('chrome');

  if (!isOpen) return null;

  const handleTestIntercept = () => {
    onSimulateIntercept('https://releases.ubuntu.com/24.04.1/ubuntu-24.04.1-live-server-amd64.iso');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden flex flex-col">
        {/* Header */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-7 h-7 rounded-md bg-emerald-500 flex items-center justify-center text-white">
              <Chrome className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100">
                Browser Extension Integration
              </h2>
              <p className="text-[11px] text-neutral-400">
                Intercept browser clicks and stream downloads automatically
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-5 space-y-4 text-xs">
          {/* Status Badge */}
          <div className="p-3 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
              <span className="font-semibold text-emerald-800 dark:text-emerald-300">
                Native Host Bridge Active
              </span>
            </div>
            <span className="font-mono text-[10px] text-emerald-700 dark:text-emerald-400 bg-emerald-100 dark:bg-emerald-900/60 px-1.5 py-0.5 rounded">
              WebSocket :6800
            </span>
          </div>

          {/* Browser Selection */}
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: 'chrome', name: 'Google Chrome' },
              { id: 'edge', name: 'Microsoft Edge' },
              { id: 'firefox', name: 'Mozilla Firefox' },
            ].map((b) => (
              <button
                key={b.id}
                type="button"
                onClick={() => setActiveBrowser(b.id as any)}
                className={`p-2 rounded-lg border text-center font-medium transition-colors cursor-pointer ${
                  activeBrowser === b.id
                    ? 'bg-sky-50 dark:bg-sky-950 border-sky-500 text-sky-700 dark:text-sky-300 font-semibold'
                    : 'bg-neutral-50 dark:bg-neutral-800 border-neutral-200 dark:border-neutral-700 text-neutral-600 dark:text-neutral-400'
                }`}
              >
                {b.name}
              </button>
            ))}
          </div>

          {/* Step by step guide */}
          <div className="space-y-2 text-neutral-600 dark:text-neutral-300">
            <div className="flex items-start space-x-2">
              <span className="w-4 h-4 rounded-full bg-sky-100 dark:bg-sky-900 text-sky-700 dark:text-sky-300 font-bold flex items-center justify-center text-[10px] flex-shrink-0 mt-0.5">
                1
              </span>
              <span>
                Open your browser extensions manager (<code className="bg-neutral-100 dark:bg-neutral-800 px-1 rounded font-mono">chrome://extensions</code>) and enable Developer mode.
              </span>
            </div>

            <div className="flex items-start space-x-2">
              <span className="w-4 h-4 rounded-full bg-sky-100 dark:bg-sky-900 text-sky-700 dark:text-sky-300 font-bold flex items-center justify-center text-[10px] flex-shrink-0 mt-0.5">
                2
              </span>
              <span>
                Drag & drop the bundled <code className="bg-neutral-100 dark:bg-neutral-800 px-1 rounded font-mono">chrome_extension.crx</code> file into your browser.
              </span>
            </div>

            <div className="flex items-start space-x-2">
              <span className="w-4 h-4 rounded-full bg-sky-100 dark:bg-sky-900 text-sky-700 dark:text-sky-300 font-bold flex items-center justify-center text-[10px] flex-shrink-0 mt-0.5">
                3
              </span>
              <span>
                Browser downloads will now be intercepted directly into Ghost Downloader 3.
              </span>
            </div>
          </div>

          {/* Test Interception Simulator */}
          <div className="pt-2 border-t border-neutral-200 dark:border-neutral-700">
            <button
              onClick={handleTestIntercept}
              className="w-full flex items-center justify-center space-x-2 p-2.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white font-medium transition-colors cursor-pointer shadow-xs"
            >
              <Zap className="w-3.5 h-3.5" />
              <span>Simulate Browser Click Interception</span>
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="p-3.5 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800/60 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 font-medium text-xs cursor-pointer"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
