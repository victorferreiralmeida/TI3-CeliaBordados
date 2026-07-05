package com.celiabordados.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celiabordados.Administrador;
import com.celiabordados.Cliente;
import com.celiabordados.model.AuthResponse;
import com.celiabordados.service.AdministradorService;
import com.celiabordados.service.ClienteService;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AdministradorService administradorService;

    public AuthResponse authenticateCliente(String email, String senha) {
        logger.info("Tentativa de autenticação de cliente: {}", email);
        
        Cliente cliente = clienteService.autenticar(email, senha);
        
        if (cliente != null) {
            logger.info("Cliente autenticado com sucesso: {}", email);
            String token = jwtUtil.generateToken(email, "CLIENTE");
            return new AuthResponse(token, "CLIENTE", cliente.getId(), cliente.getEmail(), cliente.getNome());
        }
        
        logger.warn("Falha na autenticação de cliente: {}", email);
        return new AuthResponse(false);
    }

    public AuthResponse authenticateAdmin(String email, String senha) {
        logger.info("Tentativa de autenticação de administrador: {}", email);
        
        boolean autenticado = administradorService.autenticar(email, senha);
        
        if (autenticado) {
            logger.info("Administrador autenticado com sucesso: {}", email);
            // Buscar o administrador novamente do banco para garantir dados atualizados
            Administrador admin = administradorService.getAdministrador(email);
            logger.info("Administrador recuperado do banco: {} ({})", admin.getNome(), admin.getEmail());
            String token = jwtUtil.generateToken(email, "ADMIN");
            return new AuthResponse(token, "ADMIN", admin.getId(), admin.getEmail(), admin.getNome(), admin.getUltimoLogin());
        }
        
        logger.warn("Falha na autenticação de administrador: {}", email);
        return new AuthResponse(false);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }

    public String getRoleFromToken(String token) {
        return jwtUtil.extractRole(token);
    }
}
