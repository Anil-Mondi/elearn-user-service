package com.cts.elearn.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cts.elearn.entity.User;
import com.cts.elearn.repository.UserRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository repo,
                               BCryptPasswordEncoder encoder) {

        return args -> {

            if (repo.count() == 0) {

                User admin = new User();

                admin.setName("Admin");
                admin.setEmail("admin@elearn.com");
                admin.setPassword(
                        encoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setStatus(User.Status.Active);

                repo.save(admin);
            }
        };
    }
}