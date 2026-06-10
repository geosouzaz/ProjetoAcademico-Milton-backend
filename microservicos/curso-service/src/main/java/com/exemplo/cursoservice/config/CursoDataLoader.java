package com.exemplo.cursoservice.config;

import com.exemplo.cursoservice.model.Curso;
import com.exemplo.cursoservice.repository.CursoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CursoDataLoader {

    @Bean
    CommandLineRunner carregarDados(CursoRepository repository) {

        return args -> {

            repository.save(new Curso(
                    "Engenharia de Software",
                    3600,
                    true
            ));

            repository.save(new Curso(
                    "Direito",
                    4000,
                    true
            ));

            repository.save(new Curso(
                    "Administração",
                    3000,
                    true
            ));

            System.out.println("Dados iniciais carregados.");
        };
    }
}