import { useState } from 'react';
import { 
  X, 
  CalendarClock, 
  Clock, 
  Bell, 
  FolderOpen, 
  Moon, 
  Volume2, 
  Check, 
  Sparkles,
  Sliders
} from 'lucide-react';
import { PlanTaskConfig } from '../types';

interface PlanTaskModalProps {
  isOpen: boolean;
  onClose: () => void;
  config: PlanTaskConfig;
  onSaveConfig: (newConfig: PlanTaskConfig) => void;
}

export function PlanTaskModal({ isOpen, onClose, config, onSaveConfig }: PlanTaskModalProps) {
  const [enabled, setEnabled] = useState(config.enabled);
  const [scheduledTime, setScheduledTime] = useState(config.scheduledTime || '02:00');
  const [autoStartInMinutes, setAutoStartInMinutes] = useState(config.autoStartInMinutes || 0);
  const [postAction, setPostAction] = useState<PlanTaskConfig['postAction']>(config.postAction || 'sound');

  if (!isOpen) return null;

  const handleSave = () => {
    onSaveConfig({
      enabled,
      scheduledTime: enabled ? scheduledTime : undefined,
      autoStartInMinutes: enabled && autoStartInMinutes > 0 ? autoStartInMinutes : undefined,
      postAction,
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-white dark:bg-[#2b2b2b] rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-700 overflow-hidden flex flex-col">
        {/* Header */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-7 h-7 rounded-md bg-sky-500 flex items-center justify-center text-white">
              <CalendarClock className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-neutral-800 dark:text-neutral-100">
                Task Planner & Automation
              </h2>
              <p className="text-[11px] text-neutral-400">
                Off-peak night scheduler and post-completion triggers
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

        {/* Body */}
        <div className="p-5 space-y-4 text-xs">
          {/* Section 1: Scheduled Download Start */}
          <div className="space-y-2.5">
            <div className="flex items-center justify-between">
              <label className="font-semibold text-neutral-700 dark:text-neutral-200 flex items-center space-x-1.5">
                <Clock className="w-3.5 h-3.5 text-sky-500" />
                <span>Schedule Night / Off-Peak Download</span>
              </label>
              <input
                type="checkbox"
                checked={enabled}
                onChange={(e) => setEnabled(e.target.checked)}
                className="w-4 h-4 rounded text-sky-600 focus:ring-sky-500 cursor-pointer"
              />
            </div>

            {enabled && (
              <div className="p-3 rounded-xl bg-neutral-50 dark:bg-neutral-800/80 border border-neutral-200 dark:border-neutral-700 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-neutral-600 dark:text-neutral-300">Start download queue at:</span>
                  <input
                    type="time"
                    value={scheduledTime}
                    onChange={(e) => setScheduledTime(e.target.value)}
                    className="p-1.5 rounded border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-900 font-mono font-bold text-xs"
                  />
                </div>

                <div className="flex items-center justify-between pt-1 border-t border-neutral-200/50 dark:border-neutral-700/50">
                  <span className="text-neutral-600 dark:text-neutral-300">Or delay start by (countdown):</span>
                  <div className="flex items-center space-x-1">
                    <input
                      type="number"
                      min="0"
                      max="720"
                      value={autoStartInMinutes}
                      onChange={(e) => setAutoStartInMinutes(Number(e.target.value))}
                      className="w-16 p-1.5 rounded border border-neutral-300 dark:border-neutral-600 bg-white dark:bg-neutral-900 font-mono text-center font-bold text-xs"
                    />
                    <span className="text-neutral-400">mins</span>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Section 2: Post Completion Actions */}
          <div className="space-y-2.5 pt-2 border-t border-neutral-200 dark:border-neutral-700">
            <label className="font-semibold text-neutral-700 dark:text-neutral-200 block">
              When all downloads finish successfully:
            </label>

            <div className="space-y-1.5">
              {[
                { id: 'sound', label: 'Play completion audio chime', icon: Volume2 },
                { id: 'notification', label: 'Display system push notification', icon: Bell },
                { id: 'open_folder', label: 'Open download destination folder', icon: FolderOpen },
                { id: 'sleep', label: 'Trigger standby / sleep mode', icon: Moon },
                { id: 'none', label: 'Do nothing (stay idle)', icon: Sparkles },
              ].map((action) => {
                const Icon = action.icon;
                const isSelected = postAction === action.id;
                return (
                  <button
                    key={action.id}
                    type="button"
                    onClick={() => setPostAction(action.id as any)}
                    className={`w-full flex items-center space-x-2.5 p-2.5 rounded-lg border text-left transition-colors cursor-pointer ${
                      isSelected
                        ? 'bg-sky-50/80 dark:bg-sky-950/40 border-sky-400 dark:border-sky-700 text-sky-700 dark:text-sky-300 font-medium'
                        : 'bg-white dark:bg-neutral-800/40 border-neutral-200 dark:border-neutral-700/60 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-50 dark:hover:bg-neutral-800'
                    }`}
                  >
                    <div className={`w-6 h-6 rounded flex items-center justify-center ${isSelected ? 'bg-sky-500 text-white' : 'text-neutral-400'}`}>
                      <Icon className="w-3.5 h-3.5" />
                    </div>
                    <span className="flex-1">{action.label}</span>
                    {isSelected && <Check className="w-4 h-4 text-sky-600 dark:text-sky-400" />}
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-3.5 border-t border-neutral-200 dark:border-neutral-700 bg-neutral-50 dark:bg-neutral-800/60 flex justify-end space-x-2">
          <button
            onClick={onClose}
            className="px-3 py-1.5 rounded-lg border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 text-neutral-700 dark:text-neutral-300 font-medium text-xs transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="px-4 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white font-medium text-xs shadow-xs transition-colors cursor-pointer"
          >
            Save Planner
          </button>
        </div>
      </div>
    </div>
  );
}
