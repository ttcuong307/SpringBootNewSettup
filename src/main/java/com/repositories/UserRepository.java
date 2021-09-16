package com.repositories;

import com.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndEmail(String username, String email);

    boolean existsByIdNotAndUsername(Long id, String username);

//    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
