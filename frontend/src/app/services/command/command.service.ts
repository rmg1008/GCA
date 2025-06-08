import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommandDTO } from '../../models/command.dto';
import { Page } from '../../models/page';

/**
 * Servicio para la gestión de los comandos
 */
@Injectable({
  providedIn: 'root'
})
export class CommandService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/command/';

  /**
   * 
   * @param literal texto utilizado para buscar por nombre o descripción
   * @param page página a mostrar
   * @param size tamaño por página
   * @returns Comandos paginados
   */
  searchCommands(literal: string = '', page: number = 0, size: number = 10): Observable<Page<CommandDTO>> {
    let url = `${this.baseUrl}search?page=${page}&size=${size}`;
    if (literal.trim()) {
      url += `&literal=${encodeURIComponent(literal.trim())}`;
    }
    return this.http.get<Page<CommandDTO>>(url);
  }

  /**
   * 
   * @param command Comando a añadir
   * @returns 
   */
  addCommand(command: CommandDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.baseUrl + "register", command, { headers })
  }

  /**
   * 
   * @param command Comando a modificar
   * @returns 
   */
  updateCommand(command: CommandDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.baseUrl + "update", command, { headers })
  }

  /**
   * 
   * @param id Identificador del comando a eliminar
   * @returns 
   */
  deleteCommandById(id:number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + "delete/" + id)
  }
}
