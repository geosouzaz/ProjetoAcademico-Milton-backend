package com.exemplo.disciplinaservice.config;

import com.exemplo.disciplinaservice.model.Disciplina;
import com.exemplo.disciplinaservice.repository.DisciplinaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisciplinaDataLoader {

    @Bean
    CommandLineRunner carregarDados(DisciplinaRepository repository) {

        return args -> {

            repository.save(new Disciplina(
                    "Banco de Dados",
                    "BD01",
                    true
            ));

            repository.save(new Disciplina(
                    "Programação Java",
                    "JAVA01",
                    true
            ));

            repository.save(new Disciplina(
                    "Estrutura de Dados",
                    "ED01",
                    true
            ));

            System.out.println("Dados iniciais carregados.");
        };
    }
}