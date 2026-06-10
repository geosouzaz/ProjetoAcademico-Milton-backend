package com.exemplo.crudmongo.controller;

import com.exemplo.crudmongo.Model.Disciplina;
import com.exemplo.crudmongo.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {
    @Autowired
    private DisciplinaService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public List<Disciplina> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public Optional<Disciplina> getById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public Disciplina create(@RequestBody Disciplina disciplina) { return service.save(disciplina); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public Disciplina update(@PathVariable Long id, @RequestBody Disciplina disciplina) {
        disciplina.setId(id);
        return service.save(disciplina);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
