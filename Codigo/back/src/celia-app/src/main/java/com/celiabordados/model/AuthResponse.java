package com.celiabordados.model;

import java.time.LocalDateTime;

public class AuthResponse {
    private String token;
    private String role;
    private Long userId;
    private String email;
    private String nome;
    private boolean authenticated;
    private LocalDateTime ultimoLogin;

    public AuthResponse() {
    }

    public AuthResponse(String token, String role, Long userId, String email, String nome) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.email = email;
        this.nome = nome;
        this.authenticated = true;
    }
    
    public AuthResponse(String token, String role, Long userId, String email, String nome, LocalDateTime ultimoLogin) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.email = email;
        this.nome = nome;
        this.ultimoLogin = ultimoLogin;
        this.authenticated = true;
    }

    // Constructor for failed authentication
    public AuthResponse(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
    
    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
}
