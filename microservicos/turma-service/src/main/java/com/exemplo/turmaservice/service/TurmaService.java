package com.exemplo.turmaservice.service;

import com.exemplo.turmaservice.model.Turma;
import com.exemplo.turmaservice.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaService {

    private final TurmaRepository repository;

    public TurmaService(TurmaRepository repository) {
        this.repository = repository;
    }

    public List<Turma> listarTodos() {
        return repository.findAll();
    }

    public Optional<Turma> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Turma> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Turma salvar(Turma turma) {
        return repository.save(turma);
    }

    public Turma atualizar(Long id, Turma dados) {

        Turma existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada: " + id));

        existente.setNome(dados.getNome());
        existente.setSemestre(dados.getSemestre());
        existente.setAtivo(dados.isAtivo());

        return repository.save(existente);
    }

    public void desativar(Long id) {

        Turma turma = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada: " + id));

        turma.setAtivo(false);

        repository.save(turma);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}