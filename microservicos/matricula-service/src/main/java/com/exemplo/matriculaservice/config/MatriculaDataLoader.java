package com.exemplo.matriculaservice.config;

import com.exemplo.matriculaservice.model.Matricula;
import com.exemplo.matriculaservice.repository.MatriculaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Carrega dados iniciais no banco H2 ao subir o microserviço.
 * Útil para testes e demonstrações.
 */
@Configuration
public class MatriculaDataLoader {

    @Bean
    CommandLineRunner carregarDados(MatriculaRepository repository) {
        return args -> {
            repository.save(new Matricula(1L, 1L, "2024-01-10", true));
            repository.save(new Matricula(2L, 1L, "2024-01-12", true));
            repository.save(new Matricula(1L, 2L, "2024-02-05", true));
            repository.save(new Matricula(3L, 2L, "2024-02-08", false));
            System.out.println("? [matricula-service] Dados iniciais carregados.");
        };
    }
}
