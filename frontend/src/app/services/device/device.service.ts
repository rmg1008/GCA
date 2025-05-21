import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DeviceDTO } from '../../models/device.dto';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/device/';
  private readonly groupUrl = 'group/';

  getDevicesByGroup(group:number): Observable<DeviceDTO[]> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.get<DeviceDTO[]>(this.baseUrl + this.groupUrl + group, { headers });
  }

  addDevice(device: DeviceDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    console.log(device)
    return this.http.post<void>(this.apiUrl + "/registerDevice", device, { headers })
  }

  updateDevice(device: DeviceDTO) : Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(this.apiUrl + "/updateDevice", device, { headers })
  }

  deleteDeviceById(id:number): Observable<void> {
    console.log(this.baseUrl + id)
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<void>(this.baseUrl + id, { headers })
  }

  asignTemplateToDevice(deviceId:number, templateId:number): Observable<void> {
    const token = localStorage.getItem('token');
    console.log('Token:', token);
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    console.log(this.baseUrl + deviceId + "/assign-template/" + templateId)
    if (templateId) {
      return this.http.post<void>(this.baseUrl + deviceId + "/assign-template/" + templateId, null, { headers })
    } else {
      return this.http.delete<void>(this.baseUrl + deviceId + "/unassign-template" , { headers })
    }
  }
}