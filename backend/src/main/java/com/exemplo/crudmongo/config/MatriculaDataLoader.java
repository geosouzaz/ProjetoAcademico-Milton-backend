package com.exemplo.crudmongo.config;

import com.exemplo.crudmongo.Model.Matricula;
import com.exemplo.crudmongo.repository.MatriculaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatriculaDataLoader {
    @Bean
    CommandLineRunner initMatricula(MatriculaRepository repository) {
        return args -> {
            repository.save(new Matricula(null, 1L, 1L, "2024-01-01", true));
            repository.save(new Matricula(null, 2L, 2L, "2024-01-02", true));
        };
    }
}
