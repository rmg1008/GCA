import { ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { TemplateDTO } from '../../models/template.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TemplateService } from '../../services/template/template.service';
import { ToastService } from '../../services/toast/toast.service';
import { TemplateModalComponent } from './template-modal/template-modal.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-template',
  imports: [CommonModule, FormsModule, TemplateModalComponent],
  templateUrl: './template.component.html',
  styleUrl: './template.component.css'
})
export class TemplateComponent {
  filter: string = '';
  templates: TemplateDTO[] = [];
  isEdit: boolean = false;
  // Valores por defecto para la paginación
  page = 0;
  size = 10;
  totalPages = 0;

  @ViewChild('templateModal') templateModal!: TemplateModalComponent;
  private templateService = inject(TemplateService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);
  selectedTemplate?: TemplateDTO;
  private router: Router = new Router;

  /**
   * Carga todas las plantillas al inicio
   */
  ngOnInit(): void {
    this.searchCommands();
  }
  
  searchCommands(): void {
    this.templateService.searchTemplates(this.filter, this.page, this.size).subscribe({
      next: (res) => {
        this.templates = res.content;
        this.totalPages = res.totalPages;
      },
      error: (err) => console.error('Error al buscar templates', err)
    });
  }
  
  goToPage(p: number): void {
    if (p >= 0 && p < this.totalPages) {
      this.page = p;
      this.searchCommands();
    }
  }

  resetAndSearch(): void {
    this.page = 0; // Resetea la página al buscar por literal
    this.searchCommands();
  }
  
  delete(template: TemplateDTO): void{
    if (confirm("¿Estás seguro que quieres eliminar el template")) {
      this.templateService.deleteTemplateById(template.id).subscribe({
        next: () => {
          this.toastService.show("success", "Template eliminado")
          this.searchCommands()
        },
        error: (err) => {
          this.toastService.show("error", "Error al eliminar el template")
        }
      });
      
    }
  }
  
  openForm() {
    this.isEdit = false;
  }

  /**
   * Crea o modifica una plantilla
   * @param template Plantilla que se desea añadir o modificar
   */
  handleSave(template: TemplateDTO): void {
    const isEdit = template.id ? true : false
    const call = isEdit ? this.templateService.updateTemplate(template) : this.templateService.addTemplate(template);
  
    call.subscribe({
      next: () => { 
        if (isEdit) {
          this.toastService.show("success", "Template modificado");
        } else {
          this.toastService.show("success", "Template añadido");
        }
        this.searchCommands();
        },
      error: (err) =>  this.toastService.show("error", 'No se ha podido guardar el template')
    });
  }

   openModal(template?: TemplateDTO): void {
      if (template) {
        this.selectedTemplate = template;
      } else {
        this.selectedTemplate = undefined;
      }
  
      this.cdr.detectChanges();
      this.templateModal.open();
    }

    /**
     * Regirige a la asignación de comandos para una plantilla
     * @param id Plantilla a la que se quiere asignar los comandos
     */
    assignCommands(id: number): void {
      this.router.navigate(['/plantillas', id, 'asignar-comandos']);
    }
    
  
}
