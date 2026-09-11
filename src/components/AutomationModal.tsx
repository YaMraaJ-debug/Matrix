import React, { useState } from 'react';
import { 
  X, 
  Archive, 
  Key, 
  Power, 
  Volume2, 
  FolderCheck, 
  Trash2, 
  Plus, 
  Moon, 
  ShieldCheck, 
  Play, 
  AlertTriangle,
  Sliders,
  CheckCircle2
} from 'lucide-react';
import { ArchiveUnpackConfig, PostDownloadPowerConfig, AppSettings } from '../types';

interface AutomationModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: AppSettings;
  onUpdateSettings: (newSettings: Partial<AppSettings>) => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

export function AutomationModal({
  isOpen,
  onClose,
  settings,
  onUpdateSettings,
  onShowToast,
}: AutomationModalProps) {
  const [unpackEnabled, setUnpackEnabled] = useState(settings.autoUnpackConfig?.enabled ?? true);
  const [destType, setDestType] = useState(settings.autoUnpackConfig?.destinationType ?? 'subfolder');
  const [customPath, setCustomPath] = useState(settings.autoUnpackConfig?.customDestination ?? 'C:\\Downloads\\Extracted');
  const [deleteArchive, setDeleteArchive] = useState(settings.autoUnpackConfig?.deleteArchiveAfterExtract ?? false);
  const [notifyExtract, setNotifyExtract] = useState(settings.autoUnpackConfig?.notifyOnExtract ?? true);
  const [passwords, setPasswords] = useState<string[]>(settings.autoUnpackConfig?.passwords ?? ['1234', 'ghost', 'password']);
  const [newPasswordInput, setNewPasswordInput] = useState('');

  // Post Download Power Actions
  const [postAction, setPostAction] = useState(settings.postDownloadConfig?.action ?? 'sound');
  const [playSound, setPlaySound] = useState(settings.postDownloadConfig?.playSound ?? true);
  const [countdownSec, setCountdownSec] = useState(settings.postDownloadConfig?.autoShutdownDelaySec ?? 30);

  if (!isOpen) return null;

  const handleAddPassword = () => {
    if (!newPasswordInput.trim()) return;
    if (passwords.includes(newPasswordInput.trim())) {
      onShowToast('Password already in dictionary', 'warning');
      return;
    }
    setPasswords([...passwords, newPasswordInput.trim()]);
    setNewPasswordInput('');
    onShowToast('Added password to archive dictionary', 'success');
  };

  const handleRemovePassword = (pwd: string) => {
    setPasswords(passwords.filter((p) => p !== pwd));
  };

  // Play a synthesized pleasant Web Audio chime for testing
  const playTestChime = () => {
    try {
      const audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const now = audioCtx.currentTime;

      // Note 1: E5 (659.25 Hz)
      const osc1 = audioCtx.createOscillator();
      const gain1 = audioCtx.createGain();
      osc1.type = 'sine';
      osc1.frequency.setValueAtTime(659.25, now);
      gain1.gain.setValueAtTime(0.2, now);
      gain1.gain.exponentialRampToValueAtTime(0.001, now + 0.5);
      osc1.connect(gain1);
      gain1.connect(audioCtx.destination);
      osc1.start(now);
      osc1.stop(now + 0.5);

      // Note 2: B5 (987.77 Hz)
      const osc2 = audioCtx.createOscillator();
      const gain2 = audioCtx.createGain();
      osc2.type = 'sine';
      osc2.frequency.setValueAtTime(987.77, now + 0.15);
      gain2.gain.setValueAtTime(0.25, now + 0.15);
      gain2.gain.exponentialRampToValueAtTime(0.001, now + 0.7);
      osc2.connect(gain2);
      gain2.connect(audioCtx.destination);
      osc2.start(now + 0.15);
      osc2.stop(now + 0.7);

      onShowToast('Test completion chime played', 'info');
    } catch (e) {
      console.warn('AudioContext not supported', e);
    }
  };

  const handleSave = () => {
    const updatedUnpack: ArchiveUnpackConfig = {
      enabled: unpackEnabled,
      destinationType: destType,
      customDestination: customPath,
      passwords,
      deleteArchiveAfterExtract: deleteArchive,
      notifyOnExtract: notifyExtract,
    };

    const updatedPost: PostDownloadPowerConfig = {
      action: postAction as any,
      playSound,
      autoShutdownDelaySec: countdownSec,
    };

    onUpdateSettings({
      autoUnpackConfig: updatedUnpack,
      postDownloadConfig: updatedPost,
    });

    onShowToast('Automation & Auto-Unpack preferences saved!', 'success');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-2xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400">
              <Archive className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Auto-Unpack & Post-Download Automation</span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Configure automatic archive decompression, password dictionaries, and system power actions
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto space-y-6 text-xs">
          {/* Section 1: Archive Auto-Unpack */}
          <div className="p-5 rounded-2xl border border-neutral-200 dark:border-neutral-800 bg-neutral-50/50 dark:bg-neutral-800/30 space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2.5">
                <Archive className="w-4 h-4 text-amber-500" />
                <div>
                  <h3 className="font-bold text-neutral-800 dark:text-neutral-100 text-sm">
                    Automatic Archive Extraction (.zip, .rar, .7z, .tar.xz)
                  </h3>
                  <p className="text-[11px] text-neutral-400">
                    Immediately extracts downloaded archives into clean folders upon completion
                  </p>
                </div>
              </div>

              <label className="relative inline-flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={unpackEnabled}
                  onChange={(e) => setUnpackEnabled(e.target.checked)}
                  className="sr-only peer"
                />
                <div className="w-9 h-5 bg-neutral-200 dark:bg-neutral-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-neutral-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-amber-500"></div>
              </label>
            </div>

            {unpackEnabled && (
              <div className="space-y-4 pt-2 border-t border-neutral-200 dark:border-neutral-700/60 animate-in fade-in duration-150">
                {/* Destination Type */}
                <div className="space-y-1.5">
                  <label className="block font-semibold text-neutral-700 dark:text-neutral-200">
                    Extraction Destination Folder:
                  </label>
                  <div className="grid grid-cols-2 gap-2">
                    <button
                      type="button"
                      onClick={() => setDestType('subfolder')}
                      className={`p-2.5 rounded-xl border text-left cursor-pointer transition-all ${
                        destType === 'subfolder'
                          ? 'border-amber-500 bg-amber-50/60 dark:bg-amber-950/20 text-amber-900 dark:text-amber-200'
                          : 'border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800'
                      }`}
                    >
                      <p className="font-bold">Subfolder by Archive Name</p>
                      <p className="text-[10px] text-neutral-400">/Downloads/Extracted/ArchiveName/</p>
                    </button>

                    <button
                      type="button"
                      onClick={() => setDestType('same_folder')}
                      className={`p-2.5 rounded-xl border text-left cursor-pointer transition-all ${
                        destType === 'same_folder'
                          ? 'border-amber-500 bg-amber-50/60 dark:bg-amber-950/20 text-amber-900 dark:text-amber-200'
                          : 'border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800'
                      }`}
                    >
                      <p className="font-bold">Same Folder as Download</p>
                      <p className="text-[10px] text-neutral-400">Extracts alongside the archive file</p>
                    </button>
                  </div>
                </div>

                {/* Password Dictionary */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <label className="font-semibold text-neutral-700 dark:text-neutral-200 flex items-center space-x-1.5">
                      <Key className="w-3.5 h-3.5 text-amber-500" />
                      <span>Password Dictionary (Auto-Try for Encrypted Archives):</span>
                    </label>
                    <span className="text-[11px] text-neutral-400 font-mono">
                      {passwords.length} passwords stored
                    </span>
                  </div>

                  <div className="flex items-center space-x-2">
                    <input
                      type="text"
                      value={newPasswordInput}
                      onChange={(e) => setNewPasswordInput(e.target.value)}
                      onKeyDown={(e) => e.key === 'Enter' && handleAddPassword()}
                      placeholder="Add common password (e.g. 1234, forum-password)..."
                      className="flex-1 p-2 rounded-xl border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-xs font-mono focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                    <button
                      onClick={handleAddPassword}
                      className="flex items-center space-x-1 px-3 py-2 rounded-xl bg-neutral-200 dark:bg-neutral-700 hover:bg-neutral-300 dark:hover:bg-neutral-600 font-semibold cursor-pointer"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Add</span>
                    </button>
                  </div>

                  <div className="flex flex-wrap gap-1.5 pt-1">
                    {passwords.map((pwd) => (
                      <span
                        key={pwd}
                        className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-lg bg-white dark:bg-neutral-800 border border-neutral-200 dark:border-neutral-700 font-mono text-[11px]"
                      >
                        <span>{pwd}</span>
                        <button
                          onClick={() => handleRemovePassword(pwd)}
                          className="text-neutral-400 hover:text-rose-500 ml-1"
                        >
                          <X className="w-3 h-3" />
                        </button>
                      </span>
                    ))}
                  </div>
                </div>

                {/* Additional Flags */}
                <div className="space-y-2 pt-2">
                  <label className="flex items-center space-x-2 text-neutral-600 dark:text-neutral-300 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={deleteArchive}
                      onChange={(e) => setDeleteArchive(e.target.checked)}
                      className="rounded border-neutral-300 text-amber-500 focus:ring-amber-400"
                    />
                    <span>Delete original archive file after successful extraction</span>
                  </label>

                  <label className="flex items-center space-x-2 text-neutral-600 dark:text-neutral-300 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={notifyExtract}
                      onChange={(e) => setNotifyExtract(e.target.checked)}
                      className="rounded border-neutral-300 text-amber-500 focus:ring-amber-400"
                    />
                    <span>Display desktop notification when extraction completes</span>
                  </label>
                </div>
              </div>
            )}
          </div>

          {/* Section 2: Post-Download Power & System Automation */}
          <div className="p-5 rounded-2xl border border-neutral-200 dark:border-neutral-800 bg-neutral-50/50 dark:bg-neutral-800/30 space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2.5">
                <Power className="w-4 h-4 text-rose-500" />
                <div>
                  <h3 className="font-bold text-neutral-800 dark:text-neutral-100 text-sm">
                    Post-Download Automation & System Power Action
                  </h3>
                  <p className="text-[11px] text-neutral-400">
                    What happens when the entire download queue finishes downloading
                  </p>
                </div>
              </div>

              <button
                type="button"
                onClick={playTestChime}
                className="flex items-center space-x-1.5 px-2.5 py-1 rounded-lg bg-neutral-100 dark:bg-neutral-700 hover:bg-neutral-200 dark:hover:bg-neutral-600 text-neutral-700 dark:text-neutral-200 text-[11px] font-semibold cursor-pointer"
              >
                <Volume2 className="w-3.5 h-3.5 text-amber-500" />
                <span>Test Chime</span>
              </button>
            </div>

            <div className="grid grid-cols-2 gap-2 pt-1">
              {[
                { id: 'sound', label: 'Play Completion Chime', desc: 'Alerts you with pleasant sound effect', icon: Volume2 },
                { id: 'sleep', label: 'Sleep Computer', desc: 'Puts device in low-power sleep mode', icon: Moon },
                { id: 'shutdown', label: 'Shutdown PC (Auto-Off)', desc: 'Safely turns off computer after countdown', icon: Power },
                { id: 'verify_checksum', label: 'Verify All File Hashes', desc: 'Auto-calculates MD5 & SHA-256 for all downloads', icon: ShieldCheck },
              ].map((act) => {
                const Icon = act.icon;
                return (
                  <button
                    key={act.id}
                    type="button"
                    onClick={() => setPostAction(act.id as any)}
                    className={`p-3 rounded-xl border text-left cursor-pointer transition-all flex items-start space-x-2.5 ${
                      postAction === act.id
                        ? 'border-rose-500 bg-rose-50/50 dark:bg-rose-950/20 text-rose-900 dark:text-rose-200'
                        : 'border-neutral-200 dark:border-neutral-700 bg-white dark:bg-neutral-800'
                    }`}
                  >
                    <Icon className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                    <div>
                      <p className="font-bold">{act.label}</p>
                      <p className="text-[10px] text-neutral-400">{act.desc}</p>
                    </div>
                  </button>
                );
              })}
            </div>

            {postAction === 'shutdown' && (
              <div className="p-3 rounded-xl bg-amber-50 dark:bg-amber-950/30 border border-amber-300 dark:border-amber-800/60 flex items-center justify-between text-xs animate-in fade-in duration-150">
                <div className="flex items-center space-x-2 text-amber-700 dark:text-amber-300">
                  <AlertTriangle className="w-4 h-4 shrink-0" />
                  <span>Countdown safety window before shutdown:</span>
                </div>
                <select
                  value={countdownSec}
                  onChange={(e) => setCountdownSec(Number(e.target.value))}
                  className="p-1 rounded-lg border border-amber-300 dark:border-amber-700 bg-white dark:bg-neutral-800 font-mono text-xs font-semibold"
                >
                  <option value={15}>15 seconds</option>
                  <option value={30}>30 seconds</option>
                  <option value={60}>60 seconds</option>
                  <option value={120}>2 minutes</option>
                </select>
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end space-x-2 px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-xs font-medium text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="flex items-center space-x-1.5 px-5 py-2 rounded-xl bg-amber-600 hover:bg-amber-500 active:scale-95 text-white text-xs font-semibold shadow-sm transition-all cursor-pointer"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>Apply Automation Rules</span>
          </button>
        </div>
      </div>
    </div>
  );
}
