package com.exemplo.matriculaservice.dto;

public class MatriculaDetalhadaDTO {

    private Long id;
    private Long pessoaId;
    private String nomePessoa;
    private Long cursoId;
    private String nomeCurso;
    private String dataMatricula;
    private boolean ativo;

    public MatriculaDetalhadaDTO(Long id, Long pessoaId, String nomePessoa, Long cursoId, String nomeCurso, String dataMatricula, boolean ativo) {
        this.id = id;
        this.pessoaId = pessoaId;
        this.nomePessoa = nomePessoa;
        this.cursoId = cursoId;
        this.nomeCurso = nomeCurso;
        this.dataMatricula = dataMatricula;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public String getDataMatricula() {
        return dataMatricula;
    }

    public boolean isAtivo() {
        return ativo;
    }
}