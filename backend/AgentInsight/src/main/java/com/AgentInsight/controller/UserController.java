package com.AgentInsight.controller;

import com.AgentInsight.CustomException.EmailAlreadyExistsException;
import com.AgentInsight.dto.UserDto;
import com.AgentInsight.entity.Users;
import com.AgentInsight.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody Users user) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        try {
            userService.addUser(user);
            logger.info("User registered successfully: {}", user.getEmail());
            Map<String,String> response=new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("status", "success");
            response.put("role", user.getRole().name());
            return ResponseEntity.ok(response);
        } catch (EmailAlreadyExistsException e){
            logger.warn("Registration failed - email already exists: {}", user.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status","Error registering");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", user.getEmail(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed");
            error.put("status", "error");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String,String>> createAgent(@RequestBody Users user) {
        logger.info("Creating agent with email: {}", user.getEmail());
        try {
            if (user.getAgentid() != null && user.getAgentid().trim().isEmpty()) {
                user.setAgentid(null);
            }
            Users savedUser = userService.createUser(user);
            logger.info("Agent created successfully with agentId: {}", savedUser.getAgentid());

            Map<String,String> response = new HashMap<>();
            response.put("message", "Agent created successfully");
            response.put("status", "success");
            response.put("role", savedUser.getRole().name());
            response.put("agentid", savedUser.getAgentid());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to create agent with email: {}", user.getEmail(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create agent. Please check your input.");
            error.put("status", "error");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
        logger.info("Login attempt for email: {}", user.getEmail());
        try {
            Map<String,String> token = userService.verify(user);

            if (token != null && !token.isEmpty() && !"failed".equalsIgnoreCase(token.getOrDefault("error", ""))) {
                logger.info("Login successful for email: {}", user.getEmail());
                return ResponseEntity.ok(token);
            }

            logger.warn("Login failed for email: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        } catch (Exception e) {
            logger.error("Login error for email: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Fetching all users...");
        try {
            List<UserDto> users = userService.getAllUsers();
            logger.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(users != null ? users : Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @DeleteMapping("/{agentid}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String agentid) {
        logger.info("Attempting to delete user with agentId: {}", agentid);
        try {
            userService.deleteUser(agentid);
            logger.info("User deleted successfully with agentId: {}", agentid);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Deletion failed for agentId: {}", agentid, e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Deletion failed: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{agentid}")
    public ResponseEntity<?> updateUser(@PathVariable String agentid, @RequestBody Users user) {
        logger.info("Updating user with agentId: {}", agentid);
        try {
            userService.updateUser(agentid, user);
            UserDto updatedUser = userService.getUserById(agentid);
            logger.info("User updated successfully with agentId: {}", agentid);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Update failed for agentId: {}", agentid, e);
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @GetMapping("/{agentid}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String agentid) {
        logger.info("Fetching user by agentId: {}", agentid);
        try {
            UserDto userDto = userService.getUserById(agentid);
            logger.info("Successfully retrieved user with agentId: {}", agentid);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            logger.error("Failed to fetch user with agentId: {}", agentid, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
