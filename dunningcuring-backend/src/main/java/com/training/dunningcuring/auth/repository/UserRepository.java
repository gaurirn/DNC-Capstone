package com.training.dunningcuring.auth.repository;

import com.training.dunningcuring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    // --- DELETE THIS METHOD ---
    // Boolean existsByEmail(String email);
}