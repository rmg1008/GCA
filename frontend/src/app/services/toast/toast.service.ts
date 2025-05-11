import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
  id: number;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toastsSubject.asObservable();
  private counter = 0;

  show(type: Toast['type'], message: string) {
    const id = this.counter++;
    const newToast: Toast = { type, message, id };
    this.toastsSubject.next([...this.toastsSubject.value, newToast]);
    setTimeout(() => this.remove(id), 3000);
  }

  remove(id: number) {
    this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
  }
}
