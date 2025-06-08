import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DeviceDTO } from '../../models/device.dto';

/**
 * Servicio encargado de la gestión de los dispositivos
 */
@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private readonly baseUrl = this.apiUrl + '/device/';
  private readonly groupUrl = 'group/';

  /**
   * Obtiene todos los dispositivos de un determinado grupo
   * @param group Identificador del grupo
   * @returns Array de dispositivos
   */
  getDevicesByGroup(group:number): Observable<DeviceDTO[]> {
    return this.http.get<DeviceDTO[]>(this.baseUrl + this.groupUrl + group);
  }

  /**
   * 
   * @param device Dispositivo a añadie
   * @returns 
   */
  addDevice(device: DeviceDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.apiUrl + "/registerDevice", device, { headers })
  }

  /**
   * 
   * @param device Dispositivo a modificar
   * @returns 
   */
  updateDevice(device: DeviceDTO) : Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<void>(this.apiUrl + "/updateDevice", device, { headers })
  }

  /**
   * 
   * @param id Identificador del dispositivo a eliminar
   * @returns 
   */
  deleteDeviceById(id:number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + id)
  }

  /**
   * Asigna / Elimina una plantilla a un dispositivo determinado
   * @param deviceId Identificador de dispositivo
   * @param templateId Identificador de plantilla
   * @returns 
   */
  asignTemplateToDevice(deviceId:number, templateId:number): Observable<void> {
    if (templateId) {
      return this.http.post<void>(this.baseUrl + deviceId + "/assign-template/" + templateId, null,)
    } else {
      return this.http.delete<void>(this.baseUrl + deviceId + "/unassign-template" )
    }
  }
}