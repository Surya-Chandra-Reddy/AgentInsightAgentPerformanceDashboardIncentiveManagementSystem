package com.AgentInsight.controller;

import com.AgentInsight.dto.UserDto;
import com.AgentInsight.entity.Users;
import com.AgentInsight.enums.UserRole;
import com.AgentInsight.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/agent")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    private final UserService userService;

    public AgentController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/loadagents")
    public ResponseEntity<List<UserDto>> getAllAgents() {
        logger.info("Fetching all agents...");
        List<UserDto> users = userService.getAllUsers();

        List<UserDto> agents = users.stream()
                .filter(user -> user.getRole() == UserRole.AGENT)
                .collect(Collectors.toList());

        logger.info("Retrieved {} agents", agents.size());
        return ResponseEntity.ok(agents);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addagent")
    public ResponseEntity<Map<String,String>> createAgent(@RequestBody Users user) {
        logger.info("Attempting to create agent with email: {}", user.getEmail());
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

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{agentid}")
    public ResponseEntity<?> updateUser(@PathVariable String agentid, @RequestBody Users user) {
        logger.info("Updating agent with agentId: {}", agentid);
        try {
            userService.updateUser(agentid, user);
            UserDto updatedUser = userService.getUserById(agentid);
            logger.info("Agent updated successfully with agentId: {}", agentid);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Update failed for agentId: {}", agentid, e);
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{agentid}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String agentid) {
        logger.info("Attempting to delete agent with agentId: {}", agentid);
        try {
            userService.deleteUser(agentid);
            logger.info("Agent deleted successfully with agentId: {}", agentid);

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
}
