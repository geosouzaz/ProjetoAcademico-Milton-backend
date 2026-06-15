import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { PessoasComponent } from './pessoas/pessoas.component';
import { CursosComponent } from './cursos/cursos.component';
import { DisciplinasComponent } from './disciplinas/disciplinas.component';
import { ProfessoresComponent } from './professores/professores.component';
import { TurmasComponent } from './turmas/turmas.component';
import { MatriculasComponent } from './matriculas/matriculas.component';
import { AvaliacoesComponent } from './avaliacoes/avaliacoes.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard',    component: DashboardComponent,    canActivate: [AuthGuard] },
  { path: 'pessoas',      component: PessoasComponent,      canActivate: [AuthGuard] },
  { path: 'cursos',       component: CursosComponent,       canActivate: [AuthGuard] },
  { path: 'disciplinas',  component: DisciplinasComponent,  canActivate: [AuthGuard] },
  { path: 'professores',  component: ProfessoresComponent,  canActivate: [AuthGuard] },
  { path: 'turmas',       component: TurmasComponent,       canActivate: [AuthGuard] },
  { path: 'matriculas',   component: MatriculasComponent,   canActivate: [AuthGuard] },
  { path: 'avaliacoes',   component: AvaliacoesComponent,   canActivate: [AuthGuard] },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
