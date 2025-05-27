import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { DeviceDTO } from '../../models/device.dto';
import { TemplateDTO } from '../../models/template.dto';
import { TemplateService } from '../../services/template/template.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssignedCommandsDTO } from '../../models/assignedCommands';

@Component({
  selector: 'app-assign-device2-template',
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-device2-template.component.html',
  styleUrl: './assign-device2-template.component.css'
})
export class AssignDevice2TemplateComponent {
  @Input() device!: DeviceDTO;
  @Output() assigned = new EventEmitter<number>();
  @ViewChild('modalRef') modalRef!: ElementRef<HTMLDialogElement>;

  templates: TemplateDTO[] = [];
  selectedTemplateId!: number;
  selectedTemplateCommands: AssignedCommandsDTO[] = [];

  constructor(private templateService: TemplateService) {}

  asignada?: TemplateDTO;

  open(): void {
    this.selectedTemplateCommands = [];
    this.templateService.searchTemplates('', 0, 100).subscribe(res => {
      const currentId = this.device.templateId;
      this.asignada = res.content.find(t => t.id === currentId);
      this.templates = res.content.filter(t => t.id !== currentId);
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
      this.modalRef.nativeElement.showModal();
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
    this.assigned.emit(this.selectedTemplateId);
    this.close();
  }

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

  renderCommand(template: string, values: { [key: string]: string } = {}): string {
    return template.replace(/{{(.*?)}}/g, (_, key) => values[key] || `[${key}]`);
  }
}
