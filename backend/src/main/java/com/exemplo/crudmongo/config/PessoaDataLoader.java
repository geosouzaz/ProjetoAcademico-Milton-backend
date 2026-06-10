package com.exemplo.crudmongo.config;

import com.exemplo.crudmongo.Model.Pessoa;
import com.exemplo.crudmongo.repository.PessoaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.javafaker.Faker;

import java.util.Locale;

@Configuration
public class PessoaDataLoader {

    @Bean
    CommandLineRunner loadPessoaDatabase(PessoaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Faker faker = new Faker(new Locale("pt-BR"));

                for (int i = 0; i < 200; i++) {
                    Pessoa pessoa = new Pessoa();
                    pessoa.setNome(faker.name().fullName());
                    pessoa.setEmail(faker.internet().emailAddress());
                    pessoa.setAtivo(faker.bool().bool());
                    repository.save(pessoa);
                }

                System.out.println("✅ Banco de pessoas populado com 200 registros!");
            } else {
                System.out.println("ℹ️ Banco de pessoas já contém dados, não foi necessário repopular.");
            }
        };
    }
}
