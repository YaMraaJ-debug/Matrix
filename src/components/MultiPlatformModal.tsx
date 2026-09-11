import React, { useState } from 'react';
import { 
  X, 
  Monitor, 
  Apple, 
  Smartphone, 
  Terminal, 
  Download, 
  Check, 
  Copy, 
  ShieldCheck, 
  Cpu, 
  HardDrive, 
  Layers,
  ExternalLink
} from 'lucide-react';

interface MultiPlatformModalProps {
  isOpen: boolean;
  onClose: () => void;
  onShowToast: (text: string, type?: 'success' | 'warning' | 'info') => void;
}

interface PlatformTarget {
  id: string;
  name: string;
  icon: any;
  requiredVersion: string;
  architectures: string[];
  recommendedPackage: string;
  downloads: {
    arch: string;
    label: string;
    file: string;
    size: string;
    checksum: string;
  }[];
  packageCommand?: string;
}

const PLATFORMS: PlatformTarget[] = [
  {
    id: 'windows',
    name: 'Windows',
    icon: Monitor,
    requiredVersion: 'Windows 10 / 11 (Build 19041+)',
    architectures: ['x86_64 (64-bit Intel/AMD)', 'arm64 (Snapdragon X / Surface Pro)'],
    recommendedPackage: 'GhostDownloader-v3.0.0-win-x64-setup.exe',
    downloads: [
      {
        arch: 'x86_64',
        label: 'Windows 64-bit Installer (.exe)',
        file: 'GhostDownloader-v3.0.0-win-x64-setup.exe',
        size: '64.2 MB',
        checksum: 'sha256: 7f8a192bc01284de88291039841fba09',
      },
      {
        arch: 'x86_64',
        label: 'Windows Portable (.zip)',
        file: 'GhostDownloader-v3.0.0-win-x64-portable.zip',
        size: '59.8 MB',
        checksum: 'sha256: d184a92bc51034fe991048210394af18',
      },
      {
        arch: 'arm64',
        label: 'Windows ARM64 Native Installer (.exe)',
        file: 'GhostDownloader-v3.0.0-win-arm64-setup.exe',
        size: '62.4 MB',
        checksum: 'sha256: c409218bc1920398fa90192401823901',
      },
    ],
    packageCommand: 'winget install GhostDownloader.GhostDownloader',
  },
  {
    id: 'macos',
    name: 'macOS',
    icon: Apple,
    requiredVersion: 'macOS 13.0+ (Ventura, Sonoma, Sequoia)',
    architectures: ['arm64 (Apple Silicon M1 / M2 / M3 / M4)', 'x86_64 (Intel Mac)'],
    recommendedPackage: 'GhostDownloader-v3.0.0-mac-arm64.dmg',
    downloads: [
      {
        arch: 'arm64',
        label: 'Apple Silicon DMG (M1/M2/M3/M4)',
        file: 'GhostDownloader-v3.0.0-mac-arm64.dmg',
        size: '68.5 MB',
        checksum: 'sha256: e82104928bfa019284019284019283fa',
      },
      {
        arch: 'x86_64',
        label: 'Intel Mac DMG (x86_64)',
        file: 'GhostDownloader-v3.0.0-mac-x64.dmg',
        size: '71.2 MB',
        checksum: 'sha256: b1092837401928401928301928401928',
      },
      {
        arch: 'universal',
        label: 'Universal Binary (.pkg)',
        file: 'GhostDownloader-v3.0.0-mac-universal.pkg',
        size: '138.4 MB',
        checksum: 'sha256: 92837401928301928301928301928301',
      },
    ],
    packageCommand: 'brew install --cask ghost-downloader',
  },
  {
    id: 'linux',
    name: 'Linux',
    icon: Terminal,
    requiredVersion: 'glibc 2.35+ (Ubuntu 22.04+, Debian 12+, Fedora 38+, Arch)',
    architectures: ['x86_64', 'arm64 / aarch64'],
    recommendedPackage: 'GhostDownloader-v3.0.0-linux-x86_64.AppImage',
    downloads: [
      {
        arch: 'x86_64',
        label: 'Universal AppImage (x86_64)',
        file: 'GhostDownloader-v3.0.0-linux-x86_64.AppImage',
        size: '78.1 MB',
        checksum: 'sha256: 48192039840192830192830192840192',
      },
      {
        arch: 'x86_64',
        label: 'Debian / Ubuntu Package (.deb)',
        file: 'ghost-downloader_3.0.0_amd64.deb',
        size: '54.3 MB',
        checksum: 'sha256: 59102938401928301928401928301928',
      },
      {
        arch: 'arm64',
        label: 'Linux ARM64 AppImage (aarch64)',
        file: 'GhostDownloader-v3.0.0-linux-arm64.AppImage',
        size: '74.6 MB',
        checksum: 'sha256: 10293840192830192840192830192840',
      },
      {
        arch: 'x86_64',
        label: 'Fedora / RedHat RPM (.rpm)',
        file: 'ghost-downloader-3.0.0.x86_64.rpm',
        size: '56.1 MB',
        checksum: 'sha256: 20192830192840192830192840192830',
      },
    ],
    packageCommand: 'flatpak install flathub io.github.ghostdownloader',
  },
  {
    id: 'android',
    name: 'Android',
    icon: Smartphone,
    requiredVersion: 'Android 9.0+ (Pie, 10, 11, 12, 13, 14, 15)',
    architectures: ['arm64-v8a (Modern 64-bit Phones)', 'armeabi-v7a (32-bit Legacy)'],
    recommendedPackage: 'GhostDownloader-v3.0.0-arm64-v8a.apk',
    downloads: [
      {
        arch: 'arm64-v8a',
        label: 'Android ARM64-v8a Universal APK',
        file: 'GhostDownloader-v3.0.0-arm64-v8a.apk',
        size: '28.4 MB',
        checksum: 'sha256: 91029384019283019284019283019284',
      },
      {
        arch: 'armeabi-v7a',
        label: 'Android 32-bit ARMv7 APK',
        file: 'GhostDownloader-v3.0.0-armeabi-v7a.apk',
        size: '24.9 MB',
        checksum: 'sha256: 81920394801928301928401928301928',
      },
      {
        arch: 'remote-companion',
        label: 'Ghost Remote Controller Companion PWA',
        file: 'Install via Browser Web App',
        size: 'Instant Launch',
        checksum: 'TLS 1.3 Verified',
      },
    ],
  },
];

export function MultiPlatformModal({
  isOpen,
  onClose,
  onShowToast,
}: MultiPlatformModalProps) {
  const [selectedPlatform, setSelectedPlatform] = useState<string>('windows');
  const [copiedCommand, setCopiedCommand] = useState(false);

  if (!isOpen) return null;

  const currentPlatform = PLATFORMS.find((p) => p.id === selectedPlatform) || PLATFORMS[0];

  const handleCopyCmd = (cmd: string) => {
    navigator.clipboard.writeText(cmd);
    setCopiedCommand(true);
    onShowToast('Package install command copied!', 'success');
    setTimeout(() => setCopiedCommand(false), 2000);
  };

  const handleSimulateDownload = (pkgFile: string) => {
    onShowToast(`Downloading ${pkgFile}...`, 'success');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-150">
      <div 
        className="relative w-full max-w-3xl bg-white dark:bg-neutral-900 rounded-2xl shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50/70 dark:bg-neutral-800/40">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-teal-500/10 text-teal-600 dark:text-teal-400">
              <Layers className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <span>Multi-Platform Support & Architecture Matrix</span>
                <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-full bg-teal-100 dark:bg-teal-950 text-teal-700 dark:text-teal-300 font-semibold border border-teal-300 dark:border-teal-800">
                  v3.0.0 Native
                </span>
              </h2>
              <p className="text-xs text-neutral-500 dark:text-neutral-400">
                Official builds for Windows, macOS, Linux, and Android across x86_64, ARM64, and ARMv7
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

        {/* Platform Selector Tabs */}
        <div className="grid grid-cols-4 p-3 gap-2 bg-neutral-100/60 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-800">
          {PLATFORMS.map((plat) => {
            const Icon = plat.icon;
            const isSelected = plat.id === selectedPlatform;
            return (
              <button
                key={plat.id}
                onClick={() => setSelectedPlatform(plat.id)}
                className={`flex items-center justify-center space-x-2 py-2.5 px-3 rounded-xl font-bold text-xs transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-white dark:bg-neutral-700 text-teal-600 dark:text-teal-300 shadow-sm border border-teal-500/30'
                    : 'text-neutral-600 dark:text-neutral-400 hover:bg-neutral-200/60 dark:hover:bg-neutral-700/50'
                }`}
              >
                <Icon className="w-4 h-4" />
                <span>{plat.name}</span>
              </button>
            );
          })}
        </div>

        {/* Platform Details & Downloads */}
        <div className="p-6 overflow-y-auto space-y-5 text-xs">
          {/* OS Requirement & Architecture Header */}
          <div className="p-4 rounded-xl border border-neutral-200 dark:border-neutral-700/80 bg-neutral-50 dark:bg-neutral-800/40 space-y-2">
            <div className="flex items-center justify-between">
              <span className="font-bold text-sm text-neutral-900 dark:text-neutral-100 flex items-center space-x-2">
                <currentPlatform.icon className="w-4 h-4 text-teal-500" />
                <span>{currentPlatform.name} System Requirements</span>
              </span>
              <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-teal-50 dark:bg-teal-950 text-teal-700 dark:text-teal-300 font-bold border border-teal-300 dark:border-teal-800">
                {currentPlatform.requiredVersion}
              </span>
            </div>

            <div className="text-[11px] text-neutral-500 dark:text-neutral-400 space-y-1">
              <div className="flex items-center space-x-2">
                <Cpu className="w-3.5 h-3.5 text-neutral-400" />
                <span className="font-semibold text-neutral-700 dark:text-neutral-300">Supported Architectures:</span>
                <span>{currentPlatform.architectures.join(' • ')}</span>
              </div>
            </div>
          </div>

          {/* Package Manager Command (if available) */}
          {currentPlatform.packageCommand && (
            <div className="space-y-1.5">
              <span className="font-semibold text-neutral-700 dark:text-neutral-300 block">
                Command Line Installation:
              </span>
              <div className="flex items-center justify-between p-2.5 rounded-xl bg-neutral-900 text-neutral-200 font-mono text-[11px] border border-neutral-800">
                <span className="select-all">{currentPlatform.packageCommand}</span>
                <button
                  onClick={() => handleCopyCmd(currentPlatform.packageCommand!)}
                  className="p-1 rounded hover:bg-neutral-800 text-neutral-400 hover:text-white transition-colors cursor-pointer"
                  title="Copy command"
                >
                  {copiedCommand ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                </button>
              </div>
            </div>
          )}

          {/* Downloads List */}
          <div className="space-y-2.5">
            <span className="font-bold text-neutral-800 dark:text-neutral-200 block">
              Direct Package Downloads ({currentPlatform.downloads.length}):
            </span>

            {currentPlatform.downloads.map((pkg, idx) => (
              <div
                key={idx}
                className="flex items-center justify-between p-3.5 rounded-xl border border-neutral-200 dark:border-neutral-700/70 bg-white dark:bg-neutral-800/60 hover:border-teal-500/50 transition-all"
              >
                <div className="space-y-1 min-w-0 pr-3">
                  <div className="flex items-center space-x-2">
                    <span className="font-bold text-neutral-900 dark:text-neutral-100">
                      {pkg.label}
                    </span>
                    <span className="px-1.5 py-0.5 rounded text-[10px] font-mono bg-neutral-100 dark:bg-neutral-700 text-neutral-600 dark:text-neutral-300">
                      {pkg.arch}
                    </span>
                  </div>

                  <p className="font-mono text-[10px] text-neutral-400 truncate">
                    {pkg.file} • {pkg.size} • {pkg.checksum}
                  </p>
                </div>

                <button
                  onClick={() => handleSimulateDownload(pkg.file)}
                  className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-teal-600 hover:bg-teal-500 active:scale-95 text-white font-bold text-xs shadow-sm transition-all cursor-pointer shrink-0"
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>Download</span>
                </button>
              </div>
            ))}
          </div>

          {/* Verification Callout */}
          <div className="p-3 rounded-xl bg-teal-50/60 dark:bg-teal-950/20 border border-teal-200 dark:border-teal-800/60 text-[11px] text-teal-800 dark:text-teal-300 flex items-center space-x-2.5">
            <ShieldCheck className="w-4 h-4 text-teal-600 dark:text-teal-400 shrink-0" />
            <span>
              All release binaries are signed with official EV Code Signing Certificates and verified with GPG signatures.
            </span>
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-6 py-3.5 border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50/80 dark:bg-neutral-800/40">
          <div className="text-xs text-neutral-400">
            Current Browser: <span className="font-semibold text-neutral-700 dark:text-neutral-200">Native Web Application</span>
          </div>

          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-xs font-medium text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 dark:hover:bg-neutral-800 transition-colors cursor-pointer"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
