import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  title: string;
  message?: string;
  duration?: number;
  removing?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);

  private add(toast: Omit<Toast, 'id'>) {
    const id = Date.now().toString(36) + Math.random().toString(36).slice(2);
    this.toasts.update(list => [...list, { ...toast, id }]);
    setTimeout(() => this.remove(id), toast.duration ?? 4000);
  }

  remove(id: string) {
    this.toasts.update(list =>
      list.map(t => t.id === id ? { ...t, removing: true } : t)
    );
    setTimeout(() =>
      this.toasts.update(list => list.filter(t => t.id !== id)),
    300);
  }

  success(title: string, message?: string) {
    this.add({ type: 'success', title, message });
  }
  error(title: string, message?: string) {
    this.add({ type: 'error', title, message, duration: 6000 });
  }
  info(title: string, message?: string) {
    this.add({ type: 'info', title, message });
  }
  warning(title: string, message?: string) {
    this.add({ type: 'warning', title, message });
  }
}
