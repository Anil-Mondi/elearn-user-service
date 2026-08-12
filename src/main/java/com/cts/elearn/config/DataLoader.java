package com.cts.elearn.config;

import com.cts.elearn.entity.Role;
import com.cts.elearn.entity.Status;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cts.elearn.entity.User;
import com.cts.elearn.repository.UserRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            UserRepository repo,
            BCryptPasswordEncoder encoder) {

        return args -> {

            if(repo.count() == 0) {

                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@elearn.com");
                admin.setContactNumber("9999999999");
                admin.setPassword(
                        encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.ACTIVE);

                repo.save(admin);

                User learner = new User();
                learner.setName("Anil");
                learner.setEmail("anil@elearn.com");
                learner.setContactNumber("8888888888");
                learner.setPassword(
                        encoder.encode("anil123"));
                learner.setRole(Role.LEARNER);
                learner.setStatus(Status.ACTIVE);

                repo.save(learner);

                User instructor = new User();
                instructor.setName("Instructor");
                instructor.setEmail("instructor@elearn.com");
                instructor.setContactNumber("7777777777");
                instructor.setPassword(
                        encoder.encode("instructor123"));
                instructor.setRole(Role.INSTRUCTOR);
                instructor.setStatus(Status.ACTIVE);

                repo.save(instructor);
            }
        };
    }
}