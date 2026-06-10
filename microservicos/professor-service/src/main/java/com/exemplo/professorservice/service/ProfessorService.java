package com.exemplo.professorservice.service;

import com.exemplo.professorservice.model.Professor;
import com.exemplo.professorservice.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    public List<Professor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Professor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Professor> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Professor salvar(Professor professor) {
        return repository.save(professor);
    }

    public Professor atualizar(Long id, Professor dados) {
        Professor existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado: " + id));

        existente.setNome(dados.getNome());
        existente.setEspecialidade(dados.getEspecialidade());
        existente.setAtivo(dados.isAtivo());

        return repository.save(existente);
    }

    public void desativar(Long id) {
        Professor professor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado: " + id));

        professor.setAtivo(false);

        repository.save(professor);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}