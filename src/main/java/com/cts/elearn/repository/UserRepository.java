package com.cts.elearn.repository;

import com.cts.elearn.entity.Role;
import com.cts.elearn.entity.Status;
import com.cts.elearn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByStatus(Status status);

    List<User> findByRole(Role role);

}