package com.myfitnessbuddy.repository;

import com.myfitnessbuddy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // No need to write SQL queries—Spring Data JPA provides basic CRUD operations.
}
