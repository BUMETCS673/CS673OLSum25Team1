package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUsername(String username);

    @Query(value = "SELECT COUNT(*) > 0 FROM Users u WHERE u.email = ?1 AND u.username = ?2", nativeQuery = true)
    boolean existsByEmailAndUserName(String email, String username);

}
