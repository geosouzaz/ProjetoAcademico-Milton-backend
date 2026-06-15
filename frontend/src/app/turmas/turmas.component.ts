import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Turma {
  id?: number;
  nome: string;
  semestre: string;
  ativo: boolean;
}

@Component({
  selector: 'app-turmas',
  templateUrl: './turmas.component.html',
  styleUrls: ['./turmas.component.css']
})
export class TurmasComponent implements OnInit {
  items: Turma[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Turma = { nome: '', semestre: '', ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Turma[]>('/api/turmas').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar turmas.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { nome: '', semestre: '', ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Turma) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/turmas/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Turma atualizada!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/turmas', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Turma criada!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Turma) {
    if (!confirm(`Excluir "${item.nome}"?`)) return;
    this.http.delete(`/api/turmas/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Turma excluída!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
