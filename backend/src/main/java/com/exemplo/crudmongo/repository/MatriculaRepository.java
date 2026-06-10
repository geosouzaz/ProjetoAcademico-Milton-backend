package com.exemplo.crudmongo.repository;

import com.exemplo.crudmongo.Model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {}
