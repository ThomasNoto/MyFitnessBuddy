package com.myfitnessbuddy.repository;

import com.myfitnessbuddy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Spring Data JPA provides basic CRUD operations
    // don't need to put anything here!
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
