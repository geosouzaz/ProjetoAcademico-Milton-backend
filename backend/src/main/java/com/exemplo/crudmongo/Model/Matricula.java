package com.exemplo.crudmongo.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "matricula")
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pessoaId;
    private Long cursoId;
    private String dataMatricula;
    private boolean ativo;

    public Matricula() {}

    public Matricula(Long id, Long pessoaId, Long cursoId, String dataMatricula, boolean ativo) {
        this.id = id;
        this.pessoaId = pessoaId;
        this.cursoId = cursoId;
        this.dataMatricula = dataMatricula;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPessoaId() { return pessoaId; }
    public void setPessoaId(Long pessoaId) { this.pessoaId = pessoaId; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
    public String getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(String dataMatricula) { this.dataMatricula = dataMatricula; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
