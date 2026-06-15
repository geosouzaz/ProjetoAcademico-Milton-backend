import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Disciplina {
  id?: number;
  nome: string;
  codigo: string;
  ativo: boolean;
}

@Component({
  selector: 'app-disciplinas',
  templateUrl: './disciplinas.component.html',
  styleUrls: ['./disciplinas.component.css']
})
export class DisciplinasComponent implements OnInit {
  items: Disciplina[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Disciplina = { nome: '', codigo: '', ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Disciplina[]>('/api/disciplinas').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar disciplinas.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { nome: '', codigo: '', ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Disciplina) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/disciplinas/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Disciplina atualizada!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/disciplinas', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Disciplina criada!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Disciplina) {
    if (!confirm(`Excluir "${item.nome}"?`)) return;
    this.http.delete(`/api/disciplinas/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Disciplina excluída!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
