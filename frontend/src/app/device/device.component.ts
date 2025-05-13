import { ChangeDetectorRef, Component, EventEmitter, inject, Input, Output, ViewChild } from '@angular/core';
import { DeviceDTO } from '../models/device.dto';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../services/device/device.service';
import { TreeNodeDTO } from '../models/tree-node.dto';
import { ToastService } from '../services/toast/toast.service';
import { DeviceFormModalComponent } from './device-form-modal/device-form-modal.component';

@Component({
  selector: 'app-device',
  imports: [CommonModule, DeviceFormModalComponent],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css'
})
export class DeviceComponent {
  @Input() devices: DeviceDTO[] = [];
  @Input() selectedPath: TreeNodeDTO[] = [];
  @Output() deleted = new EventEmitter<void>();
  @Output() pathClicked = new EventEmitter<TreeNodeDTO>();
  @ViewChild('deviceFormModal') deviceFormModal!: DeviceFormModalComponent;
  selectedDevice?: DeviceDTO;
  private deviceService = inject(DeviceService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  deleteDevice(id: number): void{
    if (confirm("¿Estás seguro que quieres eliminar el dispositivo")) {
      console.log("Entra")
      this.deviceService.deleteDeviceById(id).subscribe({
        next: () => {
          this.toastService.show("success", "Dispositivo eliminado")
          this.deleted.emit();
        },
        error: (err) => {
          this.toastService.show("error", "Error al eliminar el dispositivo")
        }
      });
      
    }
  }
  
  openModal(device?: DeviceDTO): void {
    if (device) {
      this.selectedDevice = device;
    } else {
      this.selectedDevice = undefined;
    }

    this.cdr.detectChanges();
    this.deviceFormModal.open();
  }

  handleSave(device: DeviceDTO): void {
      if (device.id && device.id !== 0) {
        this.deviceService.updateDevice(device).subscribe({          
          next: () => {
          this.toastService.show("success", "Dispositivo modificado con éxito");
          this.deleted.emit();
        },
        error: (err) => {
          this.toastService.show("error", "Error al modificar el dispositivo");
        }
        
      });
      } else {
        this.deviceService.addDevice(device).subscribe({
          next: () => {
            this.toastService.show("success", "Dispositivo registrado con éxito");
            this.deleted.emit();
          },
          error: (err) => {
            this.toastService.show("error", "Error al registrar el dispositivo");
          }
        
        });
      }
    }

  onBreadcrumbClick(node: TreeNodeDTO): void {
    this.pathClicked.emit(node);
  }
}
