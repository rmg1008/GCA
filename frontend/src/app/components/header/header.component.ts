import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  standalone: true,
  selector: 'app-header',
  imports: [RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Cierra la sesión y redirige a login
   */
  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  /**
   * 
   * @returns True si el usuario ha iniciado sesión
   */
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

}
