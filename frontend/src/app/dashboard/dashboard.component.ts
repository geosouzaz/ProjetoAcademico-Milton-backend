import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  cards = [
    { icon: '👥', label: 'Pessoas',     route: '/pessoas'     },
    { icon: '📚', label: 'Cursos',      route: '/cursos'      },
    { icon: '📋', label: 'Disciplinas', route: '/disciplinas' },
    { icon: '👨‍🏫', label: 'Professores', route: '/professores' },
    { icon: '🏫', label: 'Turmas',      route: '/turmas'      },
    { icon: '📝', label: 'Matrículas',  route: '/matriculas'  },
    { icon: '⭐', label: 'Avaliações',  route: '/avaliacoes'  },
  ];

  constructor(public auth: AuthService, private router: Router) {}

  navigate(route: string) {
    this.router.navigate([route]);
  }
}
