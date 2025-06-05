import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

/**
 * Servicio de autenticación del usuario
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Realiza el inicio de sesión
   * @param credentials Credenciales del usuario (correo y contraseña)
   * @returns JWT Token
   */
  login(credentials: { username: string; password: string }) {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => localStorage.setItem('token', response.token))
    );
  }

  /**
   * Elimina el token 
   */
  logout() {
    localStorage.removeItem('token');
  }

/**
 * Comprueba si hay una sesión activa
 * @returns True si existe token, False si no
 */
  isLoggedIn(): boolean {
    return localStorage.getItem('token') !== null;
  }
}
