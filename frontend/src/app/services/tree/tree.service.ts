import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TreeNodeDTO } from '../../models/tree-node.dto';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TreeService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/tree';

  getTree(): Observable<TreeNodeDTO> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.get<TreeNodeDTO>(this.baseUrl, { headers });
  }
}
