// toast.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 space-y-2">
      <div
        *ngFor="let toast of toastService.toasts$ | async"
        class="alert shadow-lg animate-fade"
        [ngClass]="{
          'alert-success': toast.type === 'success',
          'alert-error': toast.type === 'error',
          'alert-info': toast.type === 'info',
          'alert-warning': toast.type === 'warning'
        }"
      >
        <span>{{ toast.message }}</span>
        <button class="btn btn-sm btn-ghost" (click)="toastService.remove(toast.id)">✕</button>
      </div>
    </div>

  `,
})
export class ToastComponent {
  toastService = inject(ToastService);

  getToastClass(type: string) {
    return {
      'alert-success': type === 'success',
      'alert-error': type === 'error',
      'alert-info': type === 'info',
      'alert-warning': type === 'warning',
    };
  }
}
