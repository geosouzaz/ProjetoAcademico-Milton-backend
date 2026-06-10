package com.exemplo.crudmongo.repository;

import com.exemplo.crudmongo.Model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {}
