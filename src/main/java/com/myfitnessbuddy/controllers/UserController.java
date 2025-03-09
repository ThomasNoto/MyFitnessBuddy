package com.myfitnessbuddy.controllers;

import com.myfitnessbuddy.entities.User; // imports uers entity
import com.myfitnessbuddy.repository.UserRepository; // imports user repo
// import org.springframework.beans.factory.annotation.Autowired;
// provides web API functionality in spring boot
// handles GET, POST, PUT, DELETE requests
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    // @Autowired
    // private UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    // CREATE NEW USER
    // should automatically set an id for each new entry
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedUser.getName());
            user.setAge(updatedUser.getAge());
            user.setWeight(updatedUser.getWeight());
            user.setHeight(updatedUser.getHeight());
            user.setFitnessGoals(updatedUser.getFitnessGoals());
            user.setGender(updatedUser.getGender());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // DELETE USER
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted successfully!";
    }
}
