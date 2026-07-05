package com.celiabordados.controller;

import com.celiabordados.Administrador;
import com.celiabordados.Cliente;
import com.celiabordados.model.AuthResponse;
import com.celiabordados.security.AuthService;

import com.celiabordados.service.AdministradorService;
import com.celiabordados.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private AdministradorService administradorService;

    @PostMapping("/cliente/login")
    public ResponseEntity<?> clienteLogin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");
        
        // Log para debug
        System.out.println("Tentativa de login de cliente: " + email);
        
        // Autenticação direta para verificar se há problemas no serviço
        Cliente cliente = clienteService.autenticar(email, senha);
        if (cliente != null) {
            System.out.println("Cliente encontrado diretamente: " + cliente.getNome());
        } else {
            System.out.println("Cliente não encontrado diretamente");
        }
        
        // Usando o serviço de autenticação
        AuthResponse response = authService.authenticateCliente(email, senha);
        
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Email ou senha incorretos.", "authenticated", false));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");
        
        // Log para debug
        System.out.println("Tentativa de login de administrador: " + email);
        
        // Autenticação direta para verificar se há problemas no serviço
        boolean autenticado = administradorService.autenticar(email, senha);
        if (autenticado) {
            System.out.println("Administrador autenticado diretamente");
        } else {
            System.out.println("Administrador não autenticado diretamente");
        }
        
        // Usando o serviço de autenticação
        AuthResponse response = authService.authenticateAdmin(email, senha);
        
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Email ou senha incorretos.", "authenticated", false));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Log para depuração
        System.out.println("Logout solicitado");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = authService.getEmailFromToken(token);
            String role = authService.getRoleFromToken(token);
            
            System.out.println("Logout para usuário: " + email + " (" + role + ")");
        }
        
        // JWT é stateless, então o logout é gerenciado pelo cliente
        // O frontend deve remover o token do localStorage
        return ResponseEntity.ok(Map.of(
            "mensagem", "Logout realizado com sucesso",
            "success", true
        ));
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }
        
        String token = authHeader.substring(7);
        
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }
        
        String email = authService.getEmailFromToken(token);
        String role = authService.getRoleFromToken(token);
        
        if ("CLIENTE".equals(role)) {
            Cliente cliente = clienteService.buscarPorEmail(email);
            if (cliente != null) {
                return ResponseEntity.ok(new AuthResponse(token, role, cliente.getId(), email, cliente.getNome()));
            }
        } else if ("ADMIN".equals(role)) {
            Administrador admin = administradorService.getAdministrador(email);
            if (admin != null) {
                return ResponseEntity.ok(new AuthResponse(token, role, admin.getId(), email, admin.getNome()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("authenticated", false));
    }
}
