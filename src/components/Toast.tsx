import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export interface ToastMessage {
  id: string;
  text: string;
  type?: 'success' | 'warning' | 'info';
}

interface ToastProps {
  toasts: ToastMessage[];
  onDismiss: (id: string) => void;
}

export function ToastContainer({ toasts, onDismiss }: ToastProps) {
  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col space-y-2 pointer-events-none">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className="pointer-events-auto flex items-center space-x-2 px-3.5 py-2.5 rounded-xl shadow-lg bg-neutral-900/95 dark:bg-neutral-800/95 text-white text-xs border border-white/10 backdrop-blur-md animate-in slide-in-from-bottom-2 fade-in duration-200"
        >
          {toast.type === 'success' && <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />}
          {toast.type === 'warning' && <AlertCircle className="w-4 h-4 text-amber-400 flex-shrink-0" />}
          {(!toast.type || toast.type === 'info') && <Info className="w-4 h-4 text-sky-400 flex-shrink-0" />}

          <span className="font-medium">{toast.text}</span>

          <button
            onClick={() => onDismiss(toast.id)}
            className="p-0.5 rounded text-neutral-400 hover:text-white transition-colors cursor-pointer"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      ))}
    </div>
  );
}
