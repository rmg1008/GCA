import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Protege rutas que requieren autenticación.
 * Verifica que el token JWT exista y no haya expirado.
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Si el token es válido, permite el acceso.
   * Si ha expirado, redirige al login y lo elimina
   */
  canActivate(): boolean {
    const token = localStorage.getItem('token');
    if (token && !this.isTokenExpired(token)) {
      return true;
    }
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
    return false;
  }
  
  /**
   * 
   * @param token Token a verificar
   * @returns True si ha expirado, False si no
   */
  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1])); // Extrae la segunda parte del payload
      const exp = payload.exp;
      return Date.now() >= exp * 1000;
    } catch (e) {
      return true; // En caso de que no esté bien formado
    }
  }
}