package com.exemplo.crudmongo.repository;

import com.exemplo.crudmongo.Model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {}
