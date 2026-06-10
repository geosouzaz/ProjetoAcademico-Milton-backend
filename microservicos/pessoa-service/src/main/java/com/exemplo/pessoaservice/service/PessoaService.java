package com.exemplo.pessoaservice.service;

import com.exemplo.pessoaservice.model.Pessoa;
import com.exemplo.pessoaservice.repository.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    public List<Pessoa> listarTodas() {
        return repository.findAll();
    }

    public Optional<Pessoa> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Pessoa> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Pessoa salvar(Pessoa pessoa) {
        return repository.save(pessoa);
    }

    public Pessoa atualizar(Long id, Pessoa dados) {

        Pessoa existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setAtivo(dados.isAtivo());

        return repository.save(existente);
    }

    public void desativar(Long id) {

        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoa.setAtivo(false);

        repository.save(pessoa);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}