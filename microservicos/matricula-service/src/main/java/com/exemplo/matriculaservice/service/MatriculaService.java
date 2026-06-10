package com.exemplo.matriculaservice.service;

import com.exemplo.matriculaservice.dto.MatriculaDetalhadaDTO;
import com.exemplo.matriculaservice.model.Matricula;
import com.exemplo.matriculaservice.repository.MatriculaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;
import java.util.Optional;

/**
 * Camada de negocio do microservico de Matriculas.
 * Toda a logica de negocio fica aqui - o controller so delega.
 */
@Service
public class MatriculaService {

    private final MatriculaRepository repository;
    private final RestTemplate restTemplate;

    /**
     * URLs configuradas no application.properties.
     * Em Docker: http://pessoa-service:8082 / http://curso-service:8083
     * Em dev local: http://localhost:8082 / http://localhost:8083
     */
    @Value("${pessoa.service.url}")
    private String pessoaServiceUrl;

    @Value("${curso.service.url}")
    private String cursoServiceUrl;

    public MatriculaService(MatriculaRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    /** Lista todas as matriculas */
    public List<Matricula> listarTodas() {
        return repository.findAll();
    }

    /** Busca uma matricula pelo ID */
    public Optional<Matricula> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /** Monta a resposta detalhada com nome da pessoa e nome do curso */
    public MatriculaDetalhadaDTO montarDetalhada(Matricula matricula) {
        String nomePessoa = buscarNomePessoa(matricula.getPessoaId());
        String nomeCurso = buscarNomeCurso(matricula.getCursoId());

        return new MatriculaDetalhadaDTO(
                matricula.getId(),
                matricula.getPessoaId(),
                nomePessoa,
                matricula.getCursoId(),
                nomeCurso,
                matricula.getDataMatricula(),
                matricula.isAtivo()
        );
    }

    /** Lista matriculas de uma pessoa especifica */
    public List<Matricula> listarPorPessoa(Long pessoaId) {
        return repository.findByPessoaId(pessoaId);
    }

    /** Lista matriculas de um curso especifico */
    public List<Matricula> listarPorCurso(Long cursoId) {
        return repository.findByCursoId(cursoId);
    }

    /** Cria uma nova matricula */
    public Matricula salvar(Matricula matricula) {
        return repository.save(matricula);
    }

    /** Atualiza uma matricula existente */
    public Matricula atualizar(Long id, Matricula dados) {
        Matricula existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada: " + id));
        existente.setPessoaId(dados.getPessoaId());
        existente.setCursoId(dados.getCursoId());
        existente.setDataMatricula(dados.getDataMatricula());
        existente.setAtivo(dados.isAtivo());
        return repository.save(existente);
    }

    /** Desativa uma matricula (soft delete) */
    public void desativar(Long id) {
        Matricula existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada: " + id));
        existente.setAtivo(false);
        repository.save(existente);
    }

    /** Remove permanentemente uma matricula */
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private String buscarNomePessoa(Long pessoaId) {
        return buscarNomeCampo(pessoaServiceUrl + "/api/pessoas/" + pessoaId);
    }

    private String buscarNomeCurso(Long cursoId) {
        return buscarNomeCampo(cursoServiceUrl + "/api/cursos/" + cursoId);
    }

    private String buscarNomeCampo(String url) {
        try {
            Map<?, ?> resposta = restTemplate.getForObject(url, Map.class);
            if (resposta == null) {
                return "indisponivel";
            }
            Object nome = resposta.get("nome");
            return nome != null ? nome.toString() : "indisponivel";
        } catch (HttpClientErrorException.NotFound e) {
            return "indisponivel";
        } catch (RestClientException e) {
            // Fallback: retorna "indisponivel" em vez de propagar a excecao
            // Assim o matricula-service nao cai quando pessoa/curso-service estao offline
            return "indisponivel";
        }
    }
}
