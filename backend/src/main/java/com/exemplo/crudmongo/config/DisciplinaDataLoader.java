package com.exemplo.crudmongo.config;

import com.exemplo.crudmongo.Model.Disciplina;
import com.exemplo.crudmongo.repository.DisciplinaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisciplinaDataLoader {
    @Bean
    CommandLineRunner initDisciplina(DisciplinaRepository repository) {
        return args -> {
            repository.save(new Disciplina(null, "Matemática", "MAT01", true));
            repository.save(new Disciplina(null, "História", "HIS01", true));
        };
    }
}
