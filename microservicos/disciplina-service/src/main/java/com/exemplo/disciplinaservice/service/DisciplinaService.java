package com.exemplo.disciplinaservice.service;

import com.exemplo.disciplinaservice.model.Disciplina;
import com.exemplo.disciplinaservice.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaService {

    private final DisciplinaRepository repository;

    public DisciplinaService(DisciplinaRepository repository) {
        this.repository = repository;
    }

    public List<Disciplina> listarTodas() {
        return repository.findAll();
    }

    public Optional<Disciplina> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Disciplina> listarAtivas() {
        return repository.findByAtivo(true);
    }

    public Disciplina salvar(Disciplina disciplina) {
        return repository.save(disciplina);
    }

    public Disciplina atualizar(Long id, Disciplina dados) {
        Disciplina existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        existente.setNome(dados.getNome());
        existente.setCodigo(dados.getCodigo());
        existente.setAtivo(dados.isAtivo());

        return repository.save(existente);
    }

    public void desativar(Long id) {
        Disciplina disciplina = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        disciplina.setAtivo(false);

        repository.save(disciplina);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}