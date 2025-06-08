import { ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommandService } from '../../services/command/command.service';
import { CommandModalComponent } from './command-modal/command-modal.component';
import { ToastService } from '../../services/toast/toast.service';
import { CommandDTO } from '../../models/command.dto';

@Component({
  selector: 'app-command',
  imports: [CommonModule, FormsModule, CommandModalComponent],
  templateUrl: './command.component.html',
  styleUrl: './command.component.css'
})
export class CommandComponent {
  filtro: string = '';
  comandos: CommandDTO[] = [];
  modoEdicion: boolean = false;
  comandoSeleccionado: CommandDTO | null = null;
  // Valores por defecto para la paginación
  page = 0;
  size = 5;
  totalPages = 0;

  @ViewChild('commandModal') commandModal!: CommandModalComponent;
  private commandService = inject(CommandService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);
  selectedCommand?: CommandDTO;

  /**
   * Se obtienen todos los comandos al cargar el componente
   */
  ngOnInit(): void {
    this.searchCommands();
  }
  
  searchCommands(): void {
    this.commandService.searchCommands(this.filtro, this.page, this.size).subscribe({
      next: (res) => {
        this.comandos = res.content;
        this.totalPages = res.totalPages;
      },
      error: (err) => console.error('Error al buscar comandos', err)
    });
  }
  
  goToPage(p: number): void {
    if (p >= 0 && p < this.totalPages) {
      this.page = p;
      this.searchCommands();
    }
  }

  resetAndSearch(): void {
    this.page = 0;
    this.searchCommands();
  }
  
  delete(command: CommandDTO): void{
    if (confirm("¿Desea eliminar el comando?")) {
      this.commandService.deleteCommandById(command.id).subscribe({
        next: () => {
          this.toastService.show("success", "Comando eliminado")
          this.searchCommands()
        },
        error: (err) => {
          this.toastService.show("error", "Error al eliminar el comando")
        }
      });
    }
  }
  
  abrirFormulario() {
    this.modoEdicion = false;
    this.comandoSeleccionado = null;
  }

  /**
   * Es invocada cuando se añade o edita un comando desde el modal
   * @param cmd Comando a añadir o modificar
   */
  handleSave(cmd: CommandDTO): void {
    const isEdit = cmd.id ? true : false
    const call = isEdit ? this.commandService.updateCommand(cmd) : this.commandService.addCommand(cmd);
  
    call.subscribe({
      next: () => { 
        if (isEdit) {
          this.toastService.show("success", "Comando modificado");
        } else {
          this.toastService.show("success", "Comando añadido");
        }
        this.searchCommands();
        },
      error: (err) => this.toastService.show("error", "No se ha podido guardar el comando")
    });
  }

  openModal(command?: CommandDTO): void {
    if (command) {
      this.selectedCommand = command;
    } else {
      this.selectedCommand = undefined;
    }
    this.cdr.detectChanges();
    this.commandModal.open();
  }
}
