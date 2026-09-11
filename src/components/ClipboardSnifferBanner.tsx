import { useState, useEffect } from 'react';
import { Download, X, Link2, Zap } from 'lucide-react';

interface ClipboardSnifferBannerProps {
  detectedUrl: string | null;
  onAccept: (url: string) => void;
  onDismiss: () => void;
}

export function ClipboardSnifferBanner({
  detectedUrl,
  onAccept,
  onDismiss,
}: ClipboardSnifferBannerProps) {
  if (!detectedUrl) return null;

  return (
    <div className="fixed bottom-6 right-6 z-40 max-w-md w-full bg-white dark:bg-[#2b2b2b] rounded-xl shadow-2xl border border-sky-300 dark:border-sky-800 p-3.5 flex items-start space-x-3 animate-in slide-in-from-bottom-4 duration-300">
      <div className="w-8 h-8 rounded-lg bg-sky-500 text-white flex items-center justify-center flex-shrink-0 mt-0.5 shadow-sm">
        <Zap className="w-4 h-4" />
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-xs text-neutral-800 dark:text-neutral-100">
            Clipboard Link Detected!
          </span>
          <button
            onClick={onDismiss}
            className="text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 cursor-pointer p-0.5"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>

        <p className="text-[11px] text-neutral-500 dark:text-neutral-400 truncate mt-0.5 font-mono">
          {detectedUrl}
        </p>

        <div className="mt-2.5 flex items-center space-x-2">
          <button
            onClick={() => onAccept(detectedUrl)}
            className="px-3 py-1 rounded-md bg-sky-600 hover:bg-sky-500 text-white text-[11px] font-semibold flex items-center space-x-1 shadow-xs cursor-pointer"
          >
            <Download className="w-3 h-3" />
            <span>Download Now</span>
          </button>
          <button
            onClick={onDismiss}
            className="px-2.5 py-1 rounded-md text-neutral-500 dark:text-neutral-400 hover:bg-neutral-100 dark:hover:bg-neutral-800 text-[11px] cursor-pointer"
          >
            Ignore
          </button>
        </div>
      </div>
    </div>
  );
}
