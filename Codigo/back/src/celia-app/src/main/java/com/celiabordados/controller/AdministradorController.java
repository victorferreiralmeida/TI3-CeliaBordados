package com.celiabordados.controller;

import com.celiabordados.Administrador;
import com.celiabordados.model.AuthResponse;
import com.celiabordados.security.AuthService;
import com.celiabordados.service.AdministradorService;
import com.celiabordados.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthService authService;

    // Mantido para compatibilidade, usando diretamente o AuthService
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            System.out.println("Login de administrador via endpoint legado");
            String email = credentials.get("email");
            String senha = credentials.get("senha");
            
            // Usar diretamente o serviço de autenticação em vez de fazer uma requisição HTTP
            AuthResponse response = authService.authenticateAdmin(email, senha);
            
            if (response.isAuthenticated()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Email ou senha incorretos.", "authenticated", false));
            }
        } catch (Exception e) {
            System.err.println("Erro no login de administrador: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar login: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{email}")
    public ResponseEntity<?> getAdministrador(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            // Apenas o próprio admin ou outro admin pode ver os detalhes
            if ("ADMIN".equals(role)) {
                Administrador admin = administradorService.getAdministrador(email);
                if (admin != null) {
                    return ResponseEntity.ok(admin);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    @GetMapping
    public ResponseEntity<List<Administrador>> getAllAdministradores(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            // Apenas administradores podem ver a lista de administradores
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(administradorService.getAllAdministradores());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
    }
    
    @GetMapping("/verificar-senha-padrao")
    public ResponseEntity<?> verificarSenhaPadrao(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            
            // Apenas o próprio admin pode verificar sua senha
            if ("ADMIN".equals(role) && email != null) {
                boolean senhaPadrao = administradorService.isSenhaPadrao(email);
                return ResponseEntity.ok(Map.of("senhaPadrao", senhaPadrao));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    @PostMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> senhaData) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            
            // Apenas o próprio admin pode alterar sua senha
            if ("ADMIN".equals(role) && email != null) {
                String senhaAtual = senhaData.get("senhaAtual");
                String novaSenha = senhaData.get("novaSenha");
                
                if (senhaAtual == null || novaSenha == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("sucesso", false, "mensagem", "Senha atual e nova senha são obrigatórias"));
                }
                
                // Validar nova senha (não pode ser igual à padrão)
                if (novaSenha.equals("admin") || novaSenha.equals("admin1234")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("sucesso", false, "mensagem", "A nova senha não pode ser igual à senha padrão"));
                }
                
                // Validar nova senha (mínimo 6 caracteres)
                if (novaSenha.length() < 6) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("sucesso", false, "mensagem", "A nova senha deve ter pelo menos 6 caracteres"));
                }
                
                boolean alterado = administradorService.alterarSenha(email, senhaAtual, novaSenha);
                if (alterado) {
                    return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Senha alterada com sucesso"));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("sucesso", false, "mensagem", "Senha atual incorreta"));
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("sucesso", false, "mensagem", "Não autorizado"));
    }
    
    // Logout movido para AuthController
    
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarAdministrador(
            @RequestBody Administrador administrador,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            // Apenas administradores podem cadastrar novos administradores
            if ("ADMIN".equals(role)) {
                try {
                    // Verificar se já existe um administrador com o mesmo email
                    if (administradorService.getAdministrador(administrador.getEmail()) != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("erro", "Email já cadastrado"));
                    }
                    
                    // Salvar o novo administrador (a senha será criptografada no serviço)
                    Administrador adminSalvo = administradorService.salvarAdministrador(administrador);
                    
                    // Não retornar a senha no response
                    adminSalvo.setSenha(null);
                    
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(adminSalvo);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("erro", "Erro ao cadastrar administrador: " + e.getMessage()));
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
}
