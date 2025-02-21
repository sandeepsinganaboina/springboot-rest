package com.spring.rest.empapp.prorestapi.controller;

import com.spring.rest.empapp.prorestapi.model.User;
import com.spring.rest.empapp.prorestapi.repo.UserRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    //Allow new users to register without authentication
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Username already exists
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        User savedUser = userRepo.save(user);
        logger.info("New user saved ");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Get user by ID (requires authentication)
    @Operation(summary = "Get all user for given id", description = "Fetches a user details for given id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        logger.info("New user logged in");
        Optional<User> user = userRepo.findById(Math.toIntExact(id));
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all users (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetch all users for admin");
        return ResponseEntity.ok(userRepo.findAll());
    }

    // Update user details (only authenticated users can update)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepo.findById(Math.toIntExact(id))
                .map(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Encrypt new password
                    existingUser.setRole(updatedUser.getRole());
                    userRepo.save(existingUser);
                    return ResponseEntity.ok(existingUser);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete user (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(Math.toIntExact(id))) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(Math.toIntExact(id));
        return ResponseEntity.noContent().build();
    }
}
