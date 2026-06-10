package com.exemplo.crudmongo.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pessoaId;
    private Long disciplinaId;
    private double nota;
    private String data;
    private boolean ativo;

    public Avaliacao() {}

    public Avaliacao(Long id, Long pessoaId, Long disciplinaId, double nota, String data, boolean ativo) {
        this.id = id;
        this.pessoaId = pessoaId;
        this.disciplinaId = disciplinaId;
        this.nota = nota;
        this.data = data;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPessoaId() { return pessoaId; }
    public void setPessoaId(Long pessoaId) { this.pessoaId = pessoaId; }
    public Long getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(Long disciplinaId) { this.disciplinaId = disciplinaId; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
