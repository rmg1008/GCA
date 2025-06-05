import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TreeNodeDTO } from '../../models/tree-node.dto';
import { GroupDTO } from '../../models/group.dto';

/**
 * Servicio para la gestión de grupos
 */
@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/groups';

  /**
   * 
   * @param group Grupo a añadir
   * @returns 
   */
  addGroup(group: GroupDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.apiUrl + "/registerGroup", group, { headers })
  }

  /**
   * 
   * @param group Grupo a modificar
   * @returns 
   */
  updateGroup(group: GroupDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.apiUrl + "/updateGroup", group, { headers })
  }

  /**
   * 
   * @returns Devuelve todos los grupos
   */
  getAllGroups(): Observable<TreeNodeDTO[]> {
    return this.http.get<TreeNodeDTO[]>(this.baseUrl);
  }

  /**
   * 
   * @param id Identificador del grupo a eliminar
   * @returns 
   */
  deleteGroup(id:number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + "/" + id)
  }

  /**
   * Asigna / Elimina una plantilla a un determinado grupo
   * @param groupId Identificador del grupo
   * @param templateId Identificador de la plantilla
   * @returns 
   */
  asignTemplateToGroup(groupId:number, templateId:number): Observable<void> {
    if (templateId) {
      return this.http.post<void>(this.baseUrl + "/" +groupId + "/assign-template/" + templateId, null)
    } else {
      return this.http.delete<void>(this.baseUrl + "/" + groupId + "/unassign-template" )
    }
  }
}