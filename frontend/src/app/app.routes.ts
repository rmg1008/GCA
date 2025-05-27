import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './services/auth/auth.guard';
import { CommandComponent } from './components/command/command.component';
import { TemplateComponent } from './components/template/template.component';
import { AssignCommands2TemplateComponent } from './components/template/assign-commands2-template/assign-commands2-template.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] } ,
    { path: 'comandos', component: CommandComponent, canActivate: [AuthGuard] },
    { path: 'plantillas', component: TemplateComponent, canActivate: [AuthGuard] },
    { path: 'plantillas/:id/asignar-comandos', component: AssignCommands2TemplateComponent, canActivate: [AuthGuard]},    
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    { path: '**', redirectTo: 'dashboard' }
  ];
