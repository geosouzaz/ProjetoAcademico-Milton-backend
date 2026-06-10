package com.exemplo.pessoaservice.config;

import com.exemplo.pessoaservice.model.Pessoa;
import com.exemplo.pessoaservice.repository.PessoaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PessoaDataLoader {

    @Bean
    CommandLineRunner carregarDados(PessoaRepository repository) {

        return args -> {

            repository.save(new Pessoa(
                    "Ana Silva",
                    "ana@email.com",
                    true
            ));

            repository.save(new Pessoa(
                    "Carlos Souza",
                    "carlos@email.com",
                    true
            ));

            repository.save(new Pessoa(
                    "Maria Oliveira",
                    "maria@email.com",
                    true
            ));

            System.out.println("Dados iniciais carregados.");
        };
    }
}