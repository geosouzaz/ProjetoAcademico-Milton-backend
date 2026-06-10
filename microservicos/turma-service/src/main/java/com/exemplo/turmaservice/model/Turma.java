package com.exemplo.turmaservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String semestre;

    private boolean ativo;

    public Turma() {
    }

    public Turma(String nome, String semestre, boolean ativo) {
        this.nome = nome;
        this.semestre = semestre;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSemestre() {
        return semestre;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}