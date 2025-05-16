package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAllByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, int id);
    boolean existsByEmailAndIdNot(String email, int id);
}
