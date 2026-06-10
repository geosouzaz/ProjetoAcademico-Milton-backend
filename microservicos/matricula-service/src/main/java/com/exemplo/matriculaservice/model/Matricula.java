package com.exemplo.matriculaservice.model;

import jakarta.persistence.*;

/**
 * Entidade Matricula ? pertence exclusivamente a este microserviço.
 *
 * IMPORTANTE: em microserviços, NÃO usamos @ManyToOne com outras entidades
 * de outros serviços. Em vez disso, guardamos apenas os IDs (pessoaId, cursoId).
 * A consulta ao nome da pessoa ou curso é feita via chamada REST ao
 * respectivo serviço, se necessário.
 */
@Entity
@Table(name = "matricula")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referência ao microserviço de Pessoas (apenas o ID, sem @ManyToOne)
    private Long pessoaId;

    // Referência ao microserviço de Cursos (apenas o ID, sem @ManyToOne)
    private Long cursoId;

    private String dataMatricula;

    private boolean ativo;

    public Matricula() {}

    public Matricula(Long pessoaId, Long cursoId, String dataMatricula, boolean ativo) {
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
