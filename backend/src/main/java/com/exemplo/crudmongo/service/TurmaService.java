package com.exemplo.crudmongo.service;

import com.exemplo.crudmongo.Model.Turma;
import com.exemplo.crudmongo.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TurmaService {
    @Autowired
    private TurmaRepository repository;

    public List<Turma> findAll() { return repository.findAll(); }
    public Optional<Turma> findById(Long id) { return repository.findById(id); }
    public Turma save(Turma turma) { return repository.save(turma); }
    public void deleteById(Long id) { repository.deleteById(id); }
}
