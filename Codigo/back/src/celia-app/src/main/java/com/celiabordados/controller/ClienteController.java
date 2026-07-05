package com.celiabordados.controller;

import com.celiabordados.Cliente;
import com.celiabordados.model.AuthResponse;
import com.celiabordados.security.AuthService;
import com.celiabordados.service.ClienteService;
import com.celiabordados.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Verificar se o usuário é um administrador
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(clienteService.getClientes());
            }
        }
        
        // Se não for admin, retorna não autorizado
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        try {
            // A criptografia será aplicada no ClienteService
            boolean sucesso = clienteService.adicionarCliente(cliente);
            
            if (sucesso) {
                Cliente clienteSalvo = clienteService.buscarPorEmail(cliente.getEmail());
                // Não retorne a senha no response
                clienteSalvo.setSenha(null);
                return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
            } else {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("erro", "Email ou telefone já existente."));
            }
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("erro", "Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }

    // Mantido para compatibilidade, usando diretamente o AuthService
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            System.out.println("Login de cliente via endpoint legado");
            String email = credentials.get("email");
            String senha = credentials.get("senha");
            
            // Usar diretamente o serviço de autenticação em vez de fazer uma requisição HTTP
            AuthResponse response = authService.authenticateCliente(email, senha);
            
            if (response.isAuthenticated()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Email ou senha incorretos.", "authenticated", false));
            }
        } catch (Exception e) {
            System.err.println("Erro no login de cliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar login: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{email}")
    public ResponseEntity<?> getClienteByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Verificar se o usuário está autenticado e tem permissão
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String tokenEmail = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            
            // Apenas o próprio cliente ou um admin pode ver os detalhes
            if (email.equals(tokenEmail) || "ADMIN".equals(role)) {
                Cliente cliente = clienteService.buscarPorEmail(email);
                if (cliente != null) {
                    return ResponseEntity.ok(cliente);
                }
                return ResponseEntity.notFound().build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getClienteById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            String email = jwtUtil.extractEmail(token);
            
            Optional<Cliente> clienteOpt = clienteService.getClienteById(id);
            
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                
                // Apenas o próprio cliente ou um admin pode ver os detalhes
                if (cliente.getEmail().equals(email) || "ADMIN".equals(role)) {
                    return ResponseEntity.ok(cliente);
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Logout movido para AuthController
    
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPerfil(@RequestBody Map<String, Object> dadosPerfil,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Não autorizado"));
        }
        
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);
        
        // Verifica se é um cliente fazendo a atualização
        if (!"CLIENTE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Apenas clientes podem atualizar seus perfis"));
        }
        
        // Obtém os dados para atualização
        Long id = ((Number) dadosPerfil.get("id")).longValue();
        String telefone = (String) dadosPerfil.get("telefone");
        String enderecoCompleto = (String) dadosPerfil.get("enderecoCompleto");
        
        Optional<Cliente> clienteOpt = clienteService.getClienteById(id);
        
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Cliente não encontrado"));
        }
        
        Cliente cliente = clienteOpt.get();
        
        // Verificar se é o próprio cliente atualizando seus dados
        if (!cliente.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Você só pode atualizar seu próprio perfil"));
        }
        
        try {
            // Verificar se o telefone já está em uso por outro cliente
            if (telefone != null && !telefone.isEmpty()) {
                Integer countTelefone = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM clientes WHERE telefone = ? AND id != ?", 
                    Integer.class, 
                    telefone, id
                );
                
                if (countTelefone != null && countTelefone > 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("erro", "Este número de telefone já está cadastrado para outro cliente"));
                }
            }
            
            // Atualiza os dados do cliente
            cliente.setTelefone(telefone);
            cliente.setEnderecoCompleto(enderecoCompleto);
            
            // Salva o cliente atualizado
            jdbcTemplate.update(
                "UPDATE clientes SET telefone = ?, endereco_completo = ? WHERE id = ?",
                telefone, enderecoCompleto, id
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Perfil atualizado com sucesso",
                "cliente", Map.of(
                    "id", cliente.getId(),
                    "nome", cliente.getNome(),
                    "email", cliente.getEmail(),
                    "telefone", cliente.getTelefone(),
                    "enderecoCompleto", cliente.getEnderecoCompleto()
                )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar perfil: " + e.getMessage()));
        }
    }
}
