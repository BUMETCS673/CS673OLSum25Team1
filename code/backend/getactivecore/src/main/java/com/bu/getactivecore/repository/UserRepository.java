package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for managing {@link Users} entities.
 */
public interface UserRepository extends JpaRepository<Users, String> {

    @Query(value = "SELECT * FROM users u WHERE u.username = ?1", nativeQuery = true)
    Optional<Users> findByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE u.email = ?1 OR u.username = ?2", nativeQuery = true)
    Optional<Users> findByEmailOrUserName(String email, String username);

}
