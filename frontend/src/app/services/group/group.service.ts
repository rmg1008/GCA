import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TreeNodeDTO } from '../../models/tree-node.dto';
import { GroupDTO } from '../../models/group.dto';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/groups';

  addGroup(group: GroupDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    console.log(group)
    return this.http.post<void>(this.apiUrl + "/registerGroup", group, { headers })
  }

    updateGroup(group: GroupDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    console.log(group)
    return this.http.post<void>(this.apiUrl + "/updateGroup", group, { headers })
  }

  getAllGroups(): Observable<TreeNodeDTO[]> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.get<TreeNodeDTO[]>(this.baseUrl, { headers });
  }


  deleteGroup(id:number): Observable<void> {
    console.log(this.baseUrl + id)
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<void>(this.baseUrl + "/" + id, { headers })
  }

  asignTemplateToGroup(groupId:number, templateId:number): Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    console.log(this.baseUrl + groupId + "/assign-template/" + templateId)
    if (templateId) {
      return this.http.post<void>(this.baseUrl + "/" +groupId + "/assign-template/" + templateId, null, { headers })
    } else {
      return this.http.delete<void>(this.baseUrl + "/" + groupId + "/unassign-template" , { headers })
    }
  }
}