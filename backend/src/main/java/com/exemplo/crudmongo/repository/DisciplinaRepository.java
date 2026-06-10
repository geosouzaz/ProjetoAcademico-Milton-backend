package com.exemplo.crudmongo.repository;

import com.exemplo.crudmongo.Model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {}
