import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../../models/page';
import { TemplateDTO } from '../../models/template.dto';
import { TemplateCommandDTO } from '../../models/templateCommand.dto';
import { CommandDTO } from '../../models/command.dto';

/**
 * Servicio encargado de la gestión de las plantillas
 */
@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/template/';

  /**
   * 
   * @param literal texto utilizado para buscar por nombre o descripción
   * @param page página a mostrar
   * @param size tamaño por página
   * @returns Plantillas paginadas
   */
  searchTemplates(literal: string = '', page: number = 0, size: number = 10): Observable<Page<TemplateDTO>> {
    let url = `${this.baseUrl}search?page=${page}&size=${size}`;
    if (literal.trim()) {
      url += `&literal=${encodeURIComponent(literal.trim())}`;
    }
    return this.http.get<Page<TemplateDTO>>(url);
  }

  /**
   * 
   * @param template Plantilla a añadir
   * @returns 
   */
  addTemplate(template: TemplateDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<void>(this.baseUrl + "register", template, { headers })
  }

  /**
   * 
   * @param template Plantilla a modificar
   * @returns 
   */
  updateTemplate(template: TemplateDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<void>(this.baseUrl + "update", template, { headers })
  }

  /**
   * 
   * @param id Identidicador de plantilla a eliminar
   * @returns 
   */
  deleteTemplateById(id:number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + "delete/" + id)
  }

  /**
   * 
   * @param templateId Identificador de plantilla
   * @returns Comandos asignados a un plantilla
   */
  getAssignedCommands(templateId: number): Observable<TemplateCommandDTO[]> {
    return this.http.get<TemplateCommandDTO[]>(`${this.baseUrl}${templateId}/commands`);
  }
  
  /**
   * 
   * @param templateId Identificador de plantilla
   * @returns Comandos disponibles para asignar a la plantilla
   */
  getAvailableCommands(templateId: number): Observable<CommandDTO[]> {
    return this.http.get<CommandDTO[]>(`${this.baseUrl}${templateId}/available-commands`);
  }
  
  /**
   * 
   * @param templateId Identificador de plantilla
   * @param commands Comandos a asignar a la plantilla
   * @returns 
   */
  assignCommandsToTemplate(templateId: number, commands: TemplateCommandDTO[]): Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<void>(`${this.baseUrl}${templateId}/assign-commands`, commands, { headers });
  }
}
