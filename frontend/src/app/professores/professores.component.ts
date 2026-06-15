import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Professor {
  id?: number;
  nome: string;
  especialidade: string;
  ativo: boolean;
}

@Component({
  selector: 'app-professores',
  templateUrl: './professores.component.html',
  styleUrls: ['./professores.component.css']
})
export class ProfessoresComponent implements OnInit {
  items: Professor[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Professor = { nome: '', especialidade: '', ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Professor[]>('/api/professores').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar professores.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { nome: '', especialidade: '', ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Professor) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/professores/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Professor atualizado!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/professores', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Professor criado!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Professor) {
    if (!confirm(`Excluir "${item.nome}"?`)) return;
    this.http.delete(`/api/professores/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Professor excluído!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
