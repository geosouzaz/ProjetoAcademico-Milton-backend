import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Avaliacao {
  id?: number;
  pessoaId: number;
  disciplinaId: number;
  nota: number;
  data: string;
  ativo: boolean;
}

@Component({
  selector: 'app-avaliacoes',
  templateUrl: './avaliacoes.component.html',
  styleUrls: ['./avaliacoes.component.css']
})
export class AvaliacoesComponent implements OnInit {
  items: Avaliacao[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Avaliacao = { pessoaId: 0, disciplinaId: 0, nota: 0, data: '', ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Avaliacao[]>('/api/avaliacoes').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar avaliações.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { pessoaId: 0, disciplinaId: 0, nota: 0, data: '', ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Avaliacao) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/avaliacoes/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Avaliação atualizada!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/avaliacoes', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Avaliação criada!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Avaliacao) {
    if (!confirm(`Excluir avaliação #${item.id}?`)) return;
    this.http.delete(`/api/avaliacoes/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Avaliação excluída!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
