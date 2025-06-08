import { Component, EventEmitter, Input, OnChanges, Output, inject } from '@angular/core';
import { TemplateDTO } from '../../../models/template.dto';
import { TemplateService } from '../../../services/template/template.service';
import { TreeNodeDTO } from '../../../models/tree-node.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GroupService } from '../../../services/group/group.service';
import { ToastService } from '../../../services/toast/toast.service';
import { AssignedCommandsDTO } from '../../../models/assignedCommands';

@Component({
  selector: 'app-assign-group-to-template',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-group-to-template.component.html',
  styleUrl: './assign-group-to-template.component.css'
})
export class AssignGroupToTemplateComponent implements OnChanges {
  @Input() selectedPath: TreeNodeDTO[] = []; // Recibe la ruta completa del grupo
  @Output() pathClicked = new EventEmitter<TreeNodeDTO>(); // Evento para navegar por la ruta de grupos
  @Output() changed = new EventEmitter<void>(); // Evento para indicar que ha habido una modificación
  templates: TemplateDTO[] = [];
  selectedTemplateId!: number;
  selectedTemplateCommands: AssignedCommandsDTO[] = [];
  groupId!: number;
  asignada?: TemplateDTO;
  objectKeys = Object.keys;
  private toastService = inject(ToastService);
  constructor(
    private templateService: TemplateService,
    private groupService: GroupService
  ) {}

  /**
   * Carga las plantillas disponibles para el grupo
   */
  ngOnChanges(): void {
      this.templates = [];
      this.selectedTemplateCommands = [];
      this.asignada = undefined;
      const selected = this.selectedPath[this.selectedPath.length - 1];
      const currentId = selected.templateId;
      this.groupId = selected.id;
      this.selectedTemplateId = null as any;
      this.searchTemplates(currentId);
  }

  /**
   * Lanza el evento para navegar al nodo seleccionado
   * @param node Nodo destino
   */
  onBreadcrumbClick(node: TreeNodeDTO): void {
    this.pathClicked.emit(node);
  }

  unassign(): void {
    this.groupService.asignTemplateToGroup(this.groupId, null as any).subscribe(() => {
      this.toastService.show('success', 'Asignación eliminada correctamente');
      this.selectedPath[this.selectedPath.length - 1].templateId = null as any;
      this.changed.emit();
    });
  }

  assign(): void {
    this.groupService.asignTemplateToGroup(this.groupId, this.selectedTemplateId).subscribe(() => {
      this.toastService.show('success', 'Plantilla asignada correctamente');
      this.selectedPath[this.selectedPath.length - 1].templateId = this.selectedTemplateId;

      this.searchTemplates(this.selectedTemplateId)
      this.changed.emit();
    });
  }

  /**
   * Muestra la configuración al seleccionar la plantilla
   * @param templateId Plantilla seleccionada
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


  private searchTemplates(currentId: number) {
    this.templateService.searchTemplates('', 0, 100).subscribe(res => {
      this.asignada = res.content.find(t => t.id == currentId);
      this.templates = res.content.filter(t => t.id != currentId);

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
