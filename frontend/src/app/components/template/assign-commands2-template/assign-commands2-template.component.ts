import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TemplateService } from '../../../services/template/template.service';
import { CommandService } from '../../../services/command/command.service';
import { CommandDTO } from '../../../models/command.dto';
import { TemplateCommandDTO } from '../../../models/templateCommand.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssignedCommandsDTO } from '../../../models/assignedCommands';
import { ToastService } from '../../../services/toast/toast.service';

@Component({
  selector: 'app-assign-commands-to-template',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-commands2-template.component.html',
  styleUrls: ['./assign-commands2-template.component.css'],
})
export class AssignCommands2TemplateComponent implements OnInit {
  plantillaId!: number;
  availableCommands: CommandDTO[] = [];
  assignedCommands: AssignedCommandsDTO[] = [];
  objectKeys = Object.keys;

  searchTerm: string = '';
  totalPages = 0;
  page = 0;
  size = 5;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private templateService: TemplateService,
    private commandService: CommandService,
    private toastService: ToastService
  ) {}

  /**
   * Comprueba que se ha selecciondo una plantilla, de lo contrario
   * redirige a la gestión de plantillas de nueuvo
   */
  ngOnInit(): void {
    this.plantillaId = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(this.plantillaId)) {
      this.router.navigate(['/plantillas']);
      return;
    }
    this.loadCommands();
  }

  onSearch(): void {
    this.page = 0; // Resetea la página actual al realizar una búsqueda
    this.loadCommands();
  }

  /**
   * Rellena las dos columnas, de comandos disponibles y seleccionados
   */
  loadCommands(): void {
    this.templateService.getAssignedCommands(this.plantillaId).subscribe(assigned => {
      if (this.assignedCommands.length == 0 ) {
          this.assignedCommands = assigned.map(dto => {
          const value = dto.commandValue || '';
          const placeholders = value.match(/{{(.*?)}}/g)?.map(p => p.replace(/[{}]/g, '')) || [];

          const parameterValues: { [key: string]: string } = {};
          placeholders.forEach(p => parameterValues[p] = '');

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
      }

      this.commandService.searchCommands(this.searchTerm, this.page, this.size).subscribe(result => {
        this.totalPages = result.totalPages;
        this.availableCommands = result.content
          .map(cmd => {
            const placeholders = cmd.value.match(/{{(.*?)}}/g)?.map(p => p.replace(/[{}]/g, '')) || [];
            return { ...cmd, placeholders };
          });
      });

    });
  }

  assign(command: CommandDTO): void {
    const maxOrder = this.assignedCommands.length > 0
      ? Math.max(...this.assignedCommands.map(c => c.order)) + 1
      : 1;

    const placeholders = command.placeholders || [];
    const parameterValues: { [key: string]: string } = {};
    placeholders.forEach(p => parameterValues[p] = '');

    this.assignedCommands.push({ command, order: maxOrder, parameterValues });
    this.availableCommands = this.availableCommands.filter(c => c.id !== command.id);
  }

  delete(index: number): void {
    const removed = this.assignedCommands.splice(index, 1)[0];
    this.availableCommands.push(removed.command);
    this.recalculateOrder();
  }

  /**
   * Aumenta la prioridad del comando
   * @param index nuevo orden para el comando 
   */
  up(index: number): void {
    if (index > 0) {
      [this.assignedCommands[index - 1], this.assignedCommands[index]] = [this.assignedCommands[index], this.assignedCommands[index - 1]];
      this.recalculateOrder();
    }
  }

    /**
   * Disminuye la prioridad del comando
   * @param index nuevo orden para el comando 
   */
  down(index: number): void {
    if (index < this.assignedCommands.length - 1) {
      [this.assignedCommands[index + 1], this.assignedCommands[index]] = [this.assignedCommands[index], this.assignedCommands[index + 1]];
      this.recalculateOrder();
    }
  }

  /**
   * Recalcula el orden de todos los comandos
   */
  recalculateOrder(): void {
    this.assignedCommands.forEach((cmd, idx) => cmd.order = idx + 1);
  }

  save(): void {
    const payload: TemplateCommandDTO[] = this.assignedCommands.map(ac => ({
      commandId: ac.command.id,
      executionOrder: ac.order,
      parameterValues: ac.parameterValues
    }));

    this.templateService.assignCommandsToTemplate(this.plantillaId, payload).subscribe({
      next: () => { 
        this.toastService.show("success", "Plantilla modificada");
        this.back();
        },
      error: (err) => this.toastService.show("error", "No se ha podido guardar las asignaciones de comandos")
    });
  }

  back(): void {
    this.router.navigate(['/plantillas']);
  }

  get filteredAvailableCommands(): CommandDTO[] {
    if (!this.searchTerm) {
      return this.availableCommands;
    }
    const lowerSearchTerm = this.searchTerm.toLowerCase();
    return this.availableCommands.filter(cmd =>
      cmd.name.toLowerCase().includes(lowerSearchTerm) ||
      cmd.description.toLowerCase().includes(lowerSearchTerm)
    );
  }

  /**
   * Comprueba que los comandos dinámicos se han asignado correctamente
   * @returns True si no se han rellenado todos los valores
   */
  isInvalid(): boolean {
     if (this.assignedCommands.length === 0) return false;
      return this.assignedCommands.some(command => {
        const values = Object.values(command.parameterValues || {})
        return values.some(val => !val || val.trim() === '');
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