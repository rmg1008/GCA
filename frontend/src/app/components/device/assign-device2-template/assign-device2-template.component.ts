import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { DeviceDTO } from '../../../models/device.dto';
import { TemplateDTO } from '../../../models/template.dto';
import { TemplateService } from '../../../services/template/template.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssignedCommandsDTO } from '../../../models/assignedCommands';

@Component({
  selector: 'app-assign-device2-template',
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-device2-template.component.html',
  styleUrl: './assign-device2-template.component.css'
})
export class AssignDevice2TemplateComponent {
  @Input() device!: DeviceDTO; // Recibe un dispositivo si se trata de modificar
  @Output() assigned = new EventEmitter<number>(); // Evento para indicar al padre la acción realizada
  @ViewChild('modalRef') modalRef!: ElementRef<HTMLDialogElement>;

  templates: TemplateDTO[] = [];
  selectedTemplateId!: number;
  selectedTemplateCommands: AssignedCommandsDTO[] = [];

  constructor(private templateService: TemplateService) {}

  asignada?: TemplateDTO;

  /**
   * Carga todas las plantillas que se pueden asignar
   */
  open(): void {
    this.selectedTemplateCommands = [];
    this.templateService.searchTemplates('', 0, 100).subscribe(res => {
      const currentId = this.device.templateId;
      this.asignada = res.content.find(t => t.id === currentId);
      this.templates = res.content.filter(t => t.id !== currentId);
      // Tras cargar el componente, muestra la plantilla asignada y los comandos que la componen
      if (this.asignada) {
        this.templateService.getAssignedCommands(this.asignada.id).subscribe(assigned => {
          this.selectedTemplateCommands = assigned.map(dto => {
            const value = dto.commandValue || '';
            const placeholders = value.match(/{{(.*?)}}/g)?.map(p => p.replace(/[{}]/g, '')) || [];

            return {
              command: {
                id: dto.commandId,
                name: dto.commandName || '',
                description: dto.commandDescription || '',
                value,
                placeholders
              },
              order: dto.executionOrder,
              parameterValues: dto.parameterValues
            };
          });
        });
      }
      this.selectedTemplateId = null as any;
      this.modalRef.nativeElement.showModal(); // Se abre el modal
    });
  }

  unassign(): void {
    this.assigned.emit();
    this.close();
  }

  close(): void {
    this.modalRef.nativeElement.close();
  }

  assign(): void {
    this.assigned.emit(this.selectedTemplateId); // Indica al padre que se ha asignado una plantilla al dispositivo
    this.close();
  }


  /**
   * Muestra la información de la plantilla seleccionada
   * @param templateId Template seleccionada
   * @returns Comandos que pertenecen a esa template
   */
  onTemplateChange(templateId: number): void {
    if (!templateId) {
      this.selectedTemplateCommands = [];
      return;
    }

    this.templateService.getAssignedCommands(templateId).subscribe(assigned => {
      this.selectedTemplateCommands = assigned.map(dto => {
        const value = dto.commandValue || '';
        const placeholders = value.match(/{{(.*?)}}/g)?.map(p => p.replace(/[{}]/g, '')) || [];

        return {
          command: {
            id: dto.commandId,
            name: dto.commandName || '',
            description: dto.commandDescription || '',
            value,
            placeholders
          },
          order: dto.executionOrder,
          parameterValues: dto.parameterValues
        };
      });
    });
  }

  /**
   * 
   * @param command comando
   * @param values valores para ese comando
   * @returns Comando con los valores sustituidos
   */
  renderCommand(command: string, values: { [key: string]: string } = {}): string {
    return command.replace(/{{(.*?)}}/g, (_, key) => values[key] || `[${key}]`);
  }
}
