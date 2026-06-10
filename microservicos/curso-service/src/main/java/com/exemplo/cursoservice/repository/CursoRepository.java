package com.exemplo.cursoservice.repository;

import com.exemplo.cursoservice.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByAtivo(boolean ativo);

    List<Curso> findByNomeContainingIgnoreCase(String nome);
}