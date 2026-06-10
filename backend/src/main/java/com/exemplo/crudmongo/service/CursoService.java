package com.exemplo.crudmongo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.exemplo.crudmongo.Model.Curso;
import com.exemplo.crudmongo.repository.CursoRepository;

@Service
public class CursoService {
    private final CursoRepository repository; // Repositório para acesso ao banco de dados
    
    /**
     * Injeta o repositório PessoaRepository via construtor.
     */
    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }


    /**
     * Retorna todas as pessoas cadastradas no banco de dados.
     * @return Lista de pessoas
     */
    public List<Curso> listarTodas() {
        return repository.findAll();
    }

    /**
     * Salva uma nova pessoa no banco de dados.
     * @param pessoa Objeto Pessoa a ser salvo
     * @return Pessoa salva
     */
    public Curso salvar(Curso curso) {
        return repository.save(curso);
    }

    /**
     * Atualiza uma pessoa existente pelo ID.
     * @param id Identificador da pessoa a ser atualizada
     * @param novaPessoa Dados atualizados da pessoa
     * @return Pessoa atualizada
     */
    public Curso atualizar(@PathVariable Long id,  Curso novoCurso) {
        return repository.findById(id).map(c -> {
            c.setNome(novoCurso.getNome());
            c.setCargaHoraria(novoCurso.getCargaHoraria());
            c.setAtivo(novoCurso.isAtivo());
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Curso não encontrada"));
    }

    /**
     * Exclui uma pessoa pelo ID.
     * @param id Identificador da pessoa a ser excluída
     */
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}