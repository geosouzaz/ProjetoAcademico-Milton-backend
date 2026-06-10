package com.exemplo.crudmongo.service;

import com.exemplo.crudmongo.Model.Matricula;
import com.exemplo.crudmongo.repository.MatriculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MatriculaService {
    @Autowired
    private MatriculaRepository repository;

    public List<Matricula> findAll() { return repository.findAll(); }
    public Optional<Matricula> findById(Long id) { return repository.findById(id); }
    public Matricula save(Matricula matricula) { return repository.save(matricula); }
    public void deleteById(Long id) { repository.deleteById(id); }
}
