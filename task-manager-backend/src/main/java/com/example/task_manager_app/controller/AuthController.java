package com.example.task_manager_app.controller;

import com.example.task_manager_app.dto.AuthenticationRequest;
import com.example.task_manager_app.dto.AuthenticationResponse;
import com.example.task_manager_app.model.User;
import com.example.task_manager_app.security.JwtUtil;
import com.example.task_manager_app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserServiceImpl userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            response.put("error", "Username is already taken");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            response.put("error", "Email is already in use");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode the password
        User savedUser = userService.registerUser(user);
        
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId().toString());
        response.put("username", savedUser.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest request) {
        try {
            Optional<User> userOptional = userService.findByUsername(request.getUsername());
            
            if (userOptional.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            User user = userOptional.get();
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            
            AuthenticationResponse authResponse = new AuthenticationResponse(
                token,
                user.getUsername(),
                user.getId()
            );
            
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(token, userDetails)) {
                Optional<User> user = userService.findByUsername(username);
                if (user.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("userId", user.get().getId());
                    response.put("username", user.get().getUsername());
                    return ResponseEntity.ok(response);
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}