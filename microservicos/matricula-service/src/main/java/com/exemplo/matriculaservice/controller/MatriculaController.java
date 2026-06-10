package com.exemplo.matriculaservice.controller;

import com.exemplo.matriculaservice.model.Matricula;
import com.exemplo.matriculaservice.dto.MatriculaDetalhadaDTO;
import com.exemplo.matriculaservice.service.MatriculaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST do microservi�o de Matr�culas.
 *
 * Base URL: http://localhost:8081/api/matriculas
 *
 * Endpoints dispon�veis:
 *   GET    /api/matriculas              ? lista todas
 *   GET    /api/matriculas/{id}         ? busca por ID
 *   GET    /api/matriculas/pessoa/{id}  ? lista por pessoa
 *   GET    /api/matriculas/curso/{id}   ? lista por curso
 *   POST   /api/matriculas              ? cria nova
 *   PUT    /api/matriculas/{id}         ? atualiza
 *   PATCH  /api/matriculas/{id}/desativar ? desativa (soft delete)
 *   DELETE /api/matriculas/{id}         ? remove permanentemente
 */
@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*")
public class MatriculaController {

    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Matricula> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDetalhadaDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(service::montarDetalhada)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pessoa/{pessoaId}")
    public List<Matricula> listarPorPessoa(@PathVariable Long pessoaId) {
        return service.listarPorPessoa(pessoaId);
    }

    @GetMapping("/curso/{cursoId}")
    public List<Matricula> listarPorCurso(@PathVariable Long cursoId) {
        return service.listarPorCurso(cursoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula criar(@RequestBody Matricula matricula) {
        return service.salvar(matricula);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizar(@PathVariable Long id,
                                               @RequestBody Matricula matricula) {
        try {
            return ResponseEntity.ok(service.atualizar(id, matricula));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        try {
            service.desativar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
