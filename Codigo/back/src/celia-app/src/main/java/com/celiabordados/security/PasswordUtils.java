package com.celiabordados.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utilitário para criptografia de senhas usando BCrypt
 */
@Component
public class PasswordUtils {
    
    private final BCryptPasswordEncoder bcryptEncoder;
    
    public PasswordUtils() {
        // Força do trabalho 12 (2^12 iterações)
        this.bcryptEncoder = new BCryptPasswordEncoder(12);
    }
    
    /**
     * Criptografa uma senha usando BCrypt
     * 
     * @param rawPassword a senha em texto puro
     * @return a senha criptografada
     */
    public String encode(String rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }
    
    /**
     * Verifica se uma senha em texto puro corresponde a uma senha criptografada
     * 
     * @param rawPassword a senha em texto puro
     * @param encodedPassword a senha criptografada
     * @return true se as senhas corresponderem, false caso contrário
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bcryptEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Verifica se uma senha está criptografada com BCrypt
     * 
     * @param password a senha a ser verificada
     * @return true se a senha já está criptografada com BCrypt
     */
    public boolean isBCryptEncoded(String password) {
        return password != null && password.startsWith("$2a$");
    }
} 