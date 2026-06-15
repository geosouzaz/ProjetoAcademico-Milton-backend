import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { JwtInterceptor } from './core/jwt.interceptor';

import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { PessoasComponent } from './pessoas/pessoas.component';
import { CursosComponent } from './cursos/cursos.component';
import { DisciplinasComponent } from './disciplinas/disciplinas.component';
import { ProfessoresComponent } from './professores/professores.component';
import { TurmasComponent } from './turmas/turmas.component';
import { MatriculasComponent } from './matriculas/matriculas.component';
import { AvaliacoesComponent } from './avaliacoes/avaliacoes.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    PessoasComponent,
    CursosComponent,
    DisciplinasComponent,
    ProfessoresComponent,
    TurmasComponent,
    MatriculasComponent,
    AvaliacoesComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
