import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../../models/page';
import { TemplateDTO } from '../../models/template.dto';
import { TemplateCommandDTO } from '../../models/templateCommand.dto';
import { CommandDTO } from '../../models/command.dto';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/template/';

  searchTemplates(literal: string = '', page: number = 0, size: number = 10): Observable<Page<TemplateDTO>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
  
    let url = `${this.baseUrl}search?page=${page}&size=${size}`;
    if (literal.trim()) {
      url += `&literal=${encodeURIComponent(literal.trim())}`;
    }
  
    return this.http.get<Page<TemplateDTO>>(url, { headers });
  }


  addTemplate(template: TemplateDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    console.log(template)
    return this.http.post<void>(this.baseUrl + "register", template, { headers })
  }

  updateTemplate(template: TemplateDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(this.baseUrl + "update", template, { headers })
  }

  deleteTemplateById(id:number): Observable<void> {
    console.log(this.baseUrl + id)
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<void>(this.baseUrl + "delete/" + id, { headers })
  }

  getAssignedCommands(templateId: number): Observable<TemplateCommandDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.get<TemplateCommandDTO[]>(`${this.baseUrl}${templateId}/commands`, { headers });
  }
  
  getAvailableCommands(templateId: number): Observable<CommandDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.get<CommandDTO[]>(`${this.baseUrl}${templateId}/available-commands`, { headers });
  }
  
  assignCommandsToTemplate(templateId: number, commands: TemplateCommandDTO[]): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(`${this.baseUrl}${templateId}/assign-commands`, commands, { headers });
  }

  asignTemplateToGroup(groupId:number, templateId:number): Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(this.baseUrl + groupId + "/assign-template/" + templateId, { headers })
  }
}
