package com.exemplo.pessoaservice.repository;

import com.exemplo.pessoaservice.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    List<Pessoa> findByAtivo(boolean ativo);

    List<Pessoa> findByNomeContainingIgnoreCase(String nome);
}