import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommandDTO } from '../../models/command.dto';
import { Page } from '../../models/page';

@Injectable({
  providedIn: 'root'
})
export class CommandService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/command/';

  searchCommands(literal: string = '', page: number = 0, size: number = 10): Observable<Page<CommandDTO>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
  
    let url = `${this.baseUrl}search?page=${page}&size=${size}`;
    if (literal.trim()) {
      url += `&literal=${encodeURIComponent(literal.trim())}`;
    }
    return this.http.get<Page<CommandDTO>>(url, { headers });
  }

  addCommand(command: CommandDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    console.log(command)
    return this.http.post<void>(this.baseUrl + "register", command, { headers })
  }

  updateCommand(command: CommandDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(this.baseUrl + "update", command, { headers })
  }

  deleteCommandById(id:number): Observable<void> {
    console.log(this.baseUrl + id)
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<void>(this.baseUrl + "delete/" + id, { headers })
  }
}
