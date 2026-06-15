import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Matricula {
  id?: number;
  pessoaId: number;
  cursoId: number;
  dataMatricula: string;
  ativo: boolean;
}

@Component({
  selector: 'app-matriculas',
  templateUrl: './matriculas.component.html',
  styleUrls: ['./matriculas.component.css']
})
export class MatriculasComponent implements OnInit {
  items: Matricula[] = [];
  loading = false;
  showModal = false;
  editing = false;
  form: Matricula = { pessoaId: 0, cursoId: 0, dataMatricula: '', ativo: true };
  toast: { msg: string; type: string } | null = null;

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.http.get<Matricula[]>('/api/matriculas').subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.showToast('Erro ao carregar matrículas.', 'error'); this.loading = false; }
    });
  }

  openNew() {
    this.form = { pessoaId: 0, cursoId: 0, dataMatricula: '', ativo: true };
    this.editing = false;
    this.showModal = true;
  }

  openEdit(item: Matricula) {
    this.form = { ...item };
    this.editing = true;
    this.showModal = true;
  }

  save() {
    if (this.editing && this.form.id != null) {
      this.http.put(`/api/matriculas/${this.form.id}`, this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Matrícula atualizada!', 'success'); },
        error: () => this.showToast('Erro ao atualizar.', 'error')
      });
    } else {
      this.http.post('/api/matriculas', this.form).subscribe({
        next: () => { this.showModal = false; this.load(); this.showToast('Matrícula criada!', 'success'); },
        error: () => this.showToast('Erro ao criar.', 'error')
      });
    }
  }

  remove(item: Matricula) {
    if (!confirm(`Excluir matrícula #${item.id}?`)) return;
    this.http.delete(`/api/matriculas/${item.id}`).subscribe({
      next: () => { this.load(); this.showToast('Matrícula excluída!', 'success'); },
      error: () => this.showToast('Erro ao excluir.', 'error')
    });
  }

  showToast(msg: string, type: string) {
    this.toast = { msg, type };
    setTimeout(() => this.toast = null, 3500);
  }
}
