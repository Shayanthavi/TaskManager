package com.example.task_manager_app.dto;

public class AuthenticationResponse {
    private String token;
    private String username;
    private Long userId;

    // Default constructor
    public AuthenticationResponse() {
    }

    // Parameterized constructor
    public AuthenticationResponse(String token, String username, Long userId) {
        this.token = token;
        this.username = username;
        this.userId = userId;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
