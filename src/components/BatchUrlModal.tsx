import { useState, useMemo } from 'react';
import { X, ListTree, Link2, Sparkles, Plus, Copy, Check } from 'lucide-react';

interface BatchUrlModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddUrls: (urls: string[]) => void;
}

export function BatchUrlModal({ isOpen, onClose, onAddUrls }: BatchUrlModalProps) {
  const [template, setTemplate] = useState('https://archive.org/data/volume_[01-10].tar.gz');
  const [fromNum, setFromNum] = useState(1);
  const [toNum, setToNum] = useState(10);
  const [padding, setPadding] = useState(2);
  const [copied, setCopied] = useState(false);

  if (!isOpen) return null;

  // Evaluate template URLs
  const generatedUrls = useMemo(() => {
    const urls: string[] = [];
    const patternRegex = /\[(\d+)-(\d+)\]/;
    const match = template.match(patternRegex);

    if (match) {
      const start = parseInt(match[1], 10);
      const end = parseInt(match[2], 10);
      const padLen = match[1].length;

      const min = Math.min(start, end);
      const max = Math.max(start, end);
      const limit = Math.min(max, min + 99); // cap at 100 to prevent runaway memory

      for (let i = min; i <= limit; i++) {
        const numStr = String(i).padStart(padLen, '0');
        urls.push(template.replace(patternRegex, numStr));
      }
    } else {
      // Fallback to manual range
      const limit = Math.min(toNum, fromNum + 99);
      for (let i = fromNum; i <= limit; i++) {
        const numStr = String(i).padStart(padding, '0');
        urls.push(template.replace(/(\*|#)/, numStr));
      }
    }
    return urls;
  }, [template, fromNum, toNum, padding]);

  const handleCopyAll = () => {
    navigator.clipboard.writeText(generatedUrls.join('\n'));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleQueue = () => {
    if (generatedUrls.length > 0) {
      onAddUrls(generatedUrls);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="w-full max-w-lg bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden flex flex-col max-h-[85vh]">
        {/* Header */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-7 h-7 rounded-md bg-purple-500 flex items-center justify-center text-white">
              <ListTree className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100">
                Batch URL Pattern Builder
              </h2>
              <p className="text-[11px] text-neutral-400">
                Generate sequential download links automatically using syntax like [01-15]
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
        <div className="p-5 space-y-4 text-xs overflow-y-auto flex-1">
          <div className="space-y-1.5">
            <label className="font-semibold text-neutral-700 dark:text-neutral-200 flex items-center justify-between">
              <span>URL Pattern Template</span>
              <span className="text-[10px] text-purple-600 dark:text-purple-400 font-mono">
                Syntax: [01-10]
              </span>
            </label>
            <input
              type="text"
              value={template}
              onChange={(e) => setTemplate(e.target.value)}
              placeholder="https://domain.com/item_[01-20].mp4"
              className="w-full p-2.5 rounded-lg border border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 focus:outline-none focus:ring-2 focus:ring-purple-500 font-mono text-xs"
            />
          </div>

          {/* Quick Preset Patterns */}
          <div className="flex items-center space-x-1.5 text-[11px] overflow-x-auto">
            <span className="text-neutral-400 font-medium">Presets:</span>
            {[
              'https://mirror.org/iso/part_[01-08].bin',
              'https://site.com/episodes/s01e[01-12].mkv',
              'https://data.gov/archive/dataset_2025_[01-12].csv',
            ].map((preset) => (
              <button
                key={preset}
                type="button"
                onClick={() => setTemplate(preset)}
                className="px-2 py-0.5 rounded bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-neutral-600 dark:text-neutral-300 font-mono text-[10px] cursor-pointer truncate max-w-[160px]"
              >
                {preset.split('/').pop()}
              </button>
            ))}
          </div>

          {/* Live Generated List Preview */}
          <div className="space-y-1.5 pt-2 border-t border-neutral-200 dark:border-neutral-700">
            <div className="flex items-center justify-between">
              <span className="font-semibold text-neutral-700 dark:text-neutral-300">
                Generated Links ({generatedUrls.length})
              </span>
              <button
                onClick={handleCopyAll}
                className="flex items-center space-x-1 text-purple-600 hover:underline cursor-pointer text-[11px]"
              >
                {copied ? <Check className="w-3 h-3 text-emerald-500" /> : <Copy className="w-3 h-3" />}
                <span>{copied ? 'Copied' : 'Copy All Links'}</span>
              </button>
            </div>

            <div className="p-2.5 rounded-lg bg-neutral-100 dark:bg-neutral-900/90 border border-neutral-200 dark:border-neutral-700 font-mono text-[11px] max-h-48 overflow-y-auto space-y-1">
              {generatedUrls.map((u, i) => (
                <div key={i} className="text-neutral-700 dark:text-neutral-300 truncate">
                  <span className="text-neutral-400 mr-2">{i + 1}.</span>
                  {u}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-3.5 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800/60 flex justify-end space-x-2">
          <button
            onClick={onClose}
            className="px-3 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 font-medium text-xs cursor-pointer"
          >
            Cancel
          </button>
          <button
            onClick={handleQueue}
            disabled={generatedUrls.length === 0}
            className="px-4 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white font-medium text-xs shadow-xs cursor-pointer flex items-center space-x-1.5 disabled:opacity-50"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Queue {generatedUrls.length} Tasks</span>
          </button>
        </div>
      </div>
    </div>
  );
}
