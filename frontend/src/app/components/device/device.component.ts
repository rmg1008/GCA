import { ChangeDetectorRef, Component, EventEmitter, inject, Input, Output, ViewChild } from '@angular/core';
import { DeviceDTO } from '../../models/device.dto';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../services/device/device.service';
import { TreeNodeDTO } from '../../models/tree-node.dto';
import { ToastService } from '../../services/toast/toast.service';
import { DeviceFormModalComponent } from './device-form-modal/device-form-modal.component';
import { AssignDevice2TemplateComponent } from './assign-device2-template/assign-device2-template.component';

@Component({
  selector: 'app-device',
  imports: [CommonModule, DeviceFormModalComponent, AssignDevice2TemplateComponent],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css'
})
export class DeviceComponent {
  @Input() devices: DeviceDTO[] = []; // Recibe la lista de dispositivos
  @Input() selectedPath: TreeNodeDTO[] = []; // Ruta del grupo al que pertenece
  @Output() deleted = new EventEmitter<void>(); // Evento para indicar que se ha eliminado el dispositivo 
  @Output() pathClicked = new EventEmitter<TreeNodeDTO>(); // Evento para navegar entre grupos
  @ViewChild('deviceFormModal') deviceFormModal!: DeviceFormModalComponent;
  @ViewChild('assignTemplateModal') assignTemplateModal!: AssignDevice2TemplateComponent;
  selectedDevice?: DeviceDTO;
  selectedDeviceToAssign!: DeviceDTO;
  private deviceService = inject(DeviceService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  deleteDevice(id: number): void{
    if (confirm("¿Estás seguro que quieres eliminar el dispositivo")) {
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

  openAssignModal(device: DeviceDTO): void {
    this.selectedDeviceToAssign = device;
    this.cdr.detectChanges();
    this.assignTemplateModal.open();
  }

  /**
   * Crea o modifica el dispositivo
   * @param device Dispositivo a guardar
   */
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

    /**
     * Lanza el evento para navegar hacia el nodo seleccionado
     * @param node Nodo al que queremos ir
     */
  onBreadcrumbClick(node: TreeNodeDTO): void {
    this.pathClicked.emit(node);
  }

  /**
   * Asigna o elimina la asignación del dispositivo
   * @param templateId Recibe template o no segun se asigne o se elimine la asignación
   */
  handleTemplateAssigned(templateId: number): void {
  this.deviceService.asignTemplateToDevice(this.selectedDeviceToAssign.id, templateId)
    .subscribe(() => {
      if (templateId) {
        this.toastService.show('success', 'Plantilla asignada correctamente');
        this.selectedDeviceToAssign.templateId = templateId;
      } else {
        this.toastService.show('success', 'Asignación eliminada correctamente');
        this.selectedDeviceToAssign.templateId = null as any;
      }
      this.deleted.emit();
    });
}

}
