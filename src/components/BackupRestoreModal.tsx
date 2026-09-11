import { useState, useRef } from 'react';
import { 
  X, 
  Download, 
  Upload, 
  FileJson, 
  FileSpreadsheet, 
  CheckCircle2, 
  AlertCircle, 
  HardDrive, 
  Layers, 
  ShieldCheck,
  RotateCcw
} from 'lucide-react';
import { DownloadTask, AppSettings } from '../types';
import { formatBytes } from '../utils/formatters';

interface BackupRestoreModalProps {
  isOpen: boolean;
  tasks: DownloadTask[];
  settings: AppSettings;
  onClose: () => void;
  onRestoreData: (restoredTasks: DownloadTask[], restoredSettings?: Partial<AppSettings>, overwrite?: boolean) => void;
  onShowToast: (msg: string, intent?: 'info' | 'success' | 'warning') => void;
}

export function BackupRestoreModal({
  isOpen,
  tasks,
  settings,
  onClose,
  onRestoreData,
  onShowToast,
}: BackupRestoreModalProps) {
  const [activeTab, setActiveTab] = useState<'export' | 'import'>('export');
  const [includeSettings, setIncludeSettings] = useState(true);
  const [includeCompleted, setIncludeCompleted] = useState(true);
  const [importOverwrite, setImportOverwrite] = useState(false);
  const [importSettings, setImportSettings] = useState(true);

  // File import state
  const [importedFileContent, setImportedFileContent] = useState<any | null>(null);
  const [importError, setImportError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!isOpen) return null;

  const exportTasks = includeCompleted ? tasks : tasks.filter((t) => t.status !== 'completed');
  const totalBytesExport = exportTasks.reduce((acc, t) => acc + t.totalBytes, 0);

  // Handle Export to JSON
  const handleExportJson = () => {
    const backupData = {
      app: 'Ghost Downloader 3',
      version: '3.0.0',
      exportedAt: new Date().toISOString(),
      timestamp: Date.now(),
      settings: includeSettings ? settings : undefined,
      taskCount: exportTasks.length,
      tasks: exportTasks,
    };

    const blob = new Blob([JSON.stringify(backupData, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    const dateStr = new Date().toISOString().split('T')[0];
    a.download = `ghost-downloader-backup-${dateStr}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    onShowToast(`Exported ${exportTasks.length} tasks to JSON backup`, 'success');
  };

  // Handle Export to CSV
  const handleExportCsv = () => {
    const headers = ['ID', 'File Name', 'Status', 'Protocol', 'Category', 'Total Bytes', 'Downloaded Bytes', 'Speed Bps', 'Date Created', 'Source URL'];
    const rows = exportTasks.map((t) => [
      `"${t.id}"`,
      `"${t.name.replace(/"/g, '""')}"`,
      `"${t.status}"`,
      `"${t.protocol}"`,
      `"${t.category}"`,
      t.totalBytes,
      t.downloadedBytes,
      t.speed,
      `"${new Date(t.createdAt).toISOString()}"`,
      `"${t.url.replace(/"/g, '""')}"`,
    ]);

    const csvContent = [headers.join(','), ...rows.map((r) => r.join(','))].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ghost-downloads-manifest-${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    onShowToast('Exported CSV manifest successfully', 'success');
  };

  // Handle File Upload for Import
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setImportError(null);
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (ev) => {
      try {
        const parsed = JSON.parse(ev.target?.result as string);
        if (!parsed || (!Array.isArray(parsed.tasks) && !Array.isArray(parsed))) {
          throw new Error('Invalid format: File does not contain a valid task collection.');
        }
        setImportedFileContent(parsed);
      } catch (err: any) {
        setImportError(err.message || 'Failed to parse JSON backup file.');
        setImportedFileContent(null);
      }
    };
    reader.readAsText(file);
  };

  const handleApplyRestore = () => {
    if (!importedFileContent) return;
    const tasksToRestore: DownloadTask[] = Array.isArray(importedFileContent.tasks)
      ? importedFileContent.tasks
      : Array.isArray(importedFileContent)
        ? importedFileContent
        : [];

    const restoredSettings = importSettings && importedFileContent.settings ? importedFileContent.settings : undefined;

    onRestoreData(tasksToRestore, restoredSettings, importOverwrite);
    onShowToast(`Restored ${tasksToRestore.length} tasks into download manager`, 'success');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
      <div className="w-full max-w-xl bg-white dark:bg-[#252525] border border-neutral-200 dark:border-neutral-700/80 rounded-2xl shadow-2xl overflow-hidden flex flex-col text-neutral-800 dark:text-neutral-100 animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-700/80 bg-neutral-50/70 dark:bg-[#1e1e1e]">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-400 border border-sky-200 dark:border-sky-800">
              <HardDrive className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-semibold">Backup & Manifest Migration</h2>
              <p className="text-xs text-neutral-400">Export download queues or restore from external JSON backups</p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-700 dark:hover:text-neutral-200 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Selector */}
        <div className="flex items-center border-b border-neutral-200 dark:border-neutral-700 px-6 bg-white dark:bg-[#252525]">
          <button
            onClick={() => setActiveTab('export')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors cursor-pointer ${
              activeTab === 'export'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export Backup</span>
          </button>

          <button
            onClick={() => setActiveTab('import')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors cursor-pointer ${
              activeTab === 'import'
                ? 'border-sky-500 text-sky-600 dark:text-sky-400'
                : 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300'
            }`}
          >
            <Upload className="w-3.5 h-3.5" />
            <span>Restore Backup</span>
          </button>
        </div>

        {/* Tab Body */}
        <div className="p-6 space-y-5">
          {activeTab === 'export' && (
            <div className="space-y-4">
              {/* Summary Metric Card */}
              <div className="p-4 rounded-xl bg-neutral-50 dark:bg-neutral-800/40 border border-neutral-200 dark:border-neutral-700 flex items-center justify-between">
                <div>
                  <span className="text-xs text-neutral-400 font-medium">Ready for Export</span>
                  <div className="text-xl font-bold text-neutral-800 dark:text-neutral-100 font-mono mt-0.5">
                    {exportTasks.length} <span className="text-xs font-normal text-neutral-400">tasks</span>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-xs text-neutral-400 font-medium">Combined Size</span>
                  <div className="text-sm font-semibold text-sky-600 dark:text-sky-400 font-mono mt-0.5">
                    {formatBytes(totalBytesExport)}
                  </div>
                </div>
              </div>

              {/* Options */}
              <div className="space-y-2.5">
                <label className="flex items-center space-x-2.5 cursor-pointer text-xs">
                  <input
                    type="checkbox"
                    checked={includeCompleted}
                    onChange={(e) => setIncludeCompleted(e.target.checked)}
                    className="rounded text-sky-600"
                  />
                  <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                    Include completed historical downloads
                  </span>
                </label>

                <label className="flex items-center space-x-2.5 cursor-pointer text-xs">
                  <input
                    type="checkbox"
                    checked={includeSettings}
                    onChange={(e) => setIncludeSettings(e.target.checked)}
                    className="rounded text-sky-600"
                  />
                  <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                    Include application settings & proxy configuration
                  </span>
                </label>
              </div>

              {/* Action Buttons */}
              <div className="pt-2 grid grid-cols-2 gap-3">
                <button
                  onClick={handleExportJson}
                  className="flex items-center justify-center space-x-2 p-3 rounded-xl bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold shadow-xs cursor-pointer"
                >
                  <FileJson className="w-4 h-4" />
                  <span>Download JSON Backup</span>
                </button>

                <button
                  onClick={handleExportCsv}
                  className="flex items-center justify-center space-x-2 p-3 rounded-xl border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-50 dark:hover:bg-neutral-800 text-neutral-700 dark:text-neutral-200 text-xs font-medium cursor-pointer"
                >
                  <FileSpreadsheet className="w-4 h-4 text-emerald-500" />
                  <span>Export CSV Manifest</span>
                </button>
              </div>
            </div>
          )}

          {activeTab === 'import' && (
            <div className="space-y-4">
              {/* File upload drag/click box */}
              <input
                ref={fileInputRef}
                type="file"
                accept=".json"
                onChange={handleFileChange}
                className="hidden"
              />

              <div
                onClick={() => fileInputRef.current?.click()}
                className="border-2 border-dashed border-neutral-300 dark:border-neutral-600 hover:border-sky-500 dark:hover:border-sky-400 rounded-xl p-6 text-center cursor-pointer transition-colors bg-neutral-50/50 dark:bg-neutral-800/30"
              >
                <FileJson className="w-8 h-8 mx-auto text-sky-500 mb-2" />
                <p className="text-xs font-semibold text-neutral-700 dark:text-neutral-200">
                  Click or drag and drop a Ghost Downloader backup JSON file here
                </p>
                <p className="text-[11px] text-neutral-400 mt-1">
                  Accepts .json backup files exported from any Ghost Downloader 3 installation
                </p>
              </div>

              {importError && (
                <div className="flex items-center space-x-2 p-3 rounded-lg bg-rose-50 dark:bg-rose-950/60 border border-rose-200 dark:border-rose-800 text-rose-700 dark:text-rose-300 text-xs">
                  <AlertCircle className="w-4 h-4 flex-shrink-0" />
                  <span>{importError}</span>
                </div>
              )}

              {importedFileContent && (
                <div className="p-3.5 rounded-xl border border-emerald-300 dark:border-emerald-800 bg-emerald-50/50 dark:bg-emerald-950/30 space-y-2">
                  <div className="flex items-center space-x-2 text-emerald-700 dark:text-emerald-300 font-semibold text-xs">
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Backup Verified: {importedFileContent.tasks?.length || 0} tasks found</span>
                  </div>
                  {importedFileContent.exportedAt && (
                    <p className="text-[11px] text-neutral-500 dark:text-neutral-400">
                      Created on: {new Date(importedFileContent.exportedAt).toLocaleString()}
                    </p>
                  )}

                  <div className="space-y-2 pt-2 border-t border-emerald-200 dark:border-emerald-800/60 text-xs">
                    <label className="flex items-center space-x-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={importOverwrite}
                        onChange={(e) => setImportOverwrite(e.target.checked)}
                        className="rounded text-sky-600"
                      />
                      <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                        Replace current task list (unchecked = merge)
                      </span>
                    </label>

                    {importedFileContent.settings && (
                      <label className="flex items-center space-x-2 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={importSettings}
                          onChange={(e) => setImportSettings(e.target.checked)}
                          className="rounded text-sky-600"
                        />
                        <span className="text-neutral-700 dark:text-neutral-300 font-medium">
                          Restore application settings from backup
                        </span>
                      </label>
                    )}
                  </div>

                  <button
                    onClick={handleApplyRestore}
                    className="w-full mt-2 py-2.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold shadow-xs cursor-pointer flex items-center justify-center space-x-1.5"
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Restore Selected Backup Data</span>
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
