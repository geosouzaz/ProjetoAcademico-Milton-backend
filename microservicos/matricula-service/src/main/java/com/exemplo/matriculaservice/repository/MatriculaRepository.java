package com.exemplo.matriculaservice.repository;

import com.exemplo.matriculaservice.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA do microserviço de Matrículas.
 * Herda todos os métodos CRUD de JpaRepository.
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    // Consulta customizada: buscar todas as matrículas de uma pessoa
    List<Matricula> findByPessoaId(Long pessoaId);

    // Consulta customizada: buscar todas as matrículas de um curso
    List<Matricula> findByCursoId(Long cursoId);

    // Consulta customizada: buscar matrículas ativas
    List<Matricula> findByAtivo(boolean ativo);
}
