package com.training.dunningcuring.auth.repository;
import com.training.dunningcuring.auth.entity.ERole;
import com.training.dunningcuring.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}