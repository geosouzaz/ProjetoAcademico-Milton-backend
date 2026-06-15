import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Curso {
  id?: number;
  nome: string;
  cargaHoraria: number;
  ativo: boolean;
}

@Component({
  selector: 'app-cursos',
  templateUrl: './cursos.component.html',
  styleUrls: ['./cursos.component.css']
})
export class CursosComponent implements OnInit {
  items: Curso[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Curso = { nome: '', cargaHoraria: 0, ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Curso[]>('/api/cursos').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar cursos.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { nome: '', cargaHoraria: 0, ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Curso) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/cursos/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Curso atualizado!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/cursos', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Curso criado!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Curso) {
    if (!confirm(`Excluir "${item.nome}"?`)) return;
    this.http.delete(`/api/cursos/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Curso excluído!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
