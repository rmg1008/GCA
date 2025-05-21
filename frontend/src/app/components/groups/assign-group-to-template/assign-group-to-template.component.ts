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
  @Input() selectedPath: TreeNodeDTO[] = [];
  @Output() pathClicked = new EventEmitter<TreeNodeDTO>();
  @Output() changed = new EventEmitter<void>();
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

  renderCommand(template: string, values: { [key: string]: string } = {}): string {
    return template.replace(/{{(.*?)}}/g, (_, key) => values[key] || `[${key}]`);
  }

}
