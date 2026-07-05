package com.celiabordados.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    // Lista de endpoints que não precisam de autenticação
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/cliente/login",
            "/api/auth/admin/login",
            "/api/auth/logout",
            "/api/auth/check",
            "/api/clientes/cadastro",
            "/api/produtos",
            "/api/avaliacoes/produto/",
            // Adicionando os endpoints antigos para compatibilidade
            "/api/clientes/login",
            "/api/admin/login"
    );

    // Lista de endpoints que só podem ser acessados por administradores
    private static final List<String> ADMIN_ONLY_ENDPOINTS = Arrays.asList(
            "/api/admin",
            "/api/produtos/admin",
            "/api/produtos/POST",
            "/api/produtos/PUT",
            "/api/produtos/DELETE",
            "/api/pedidos/admin"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Configurando CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Para requisições OPTIONS (pre-flight), apenas retorna OK
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String requestPath = request.getRequestURI();
        final String method = request.getMethod();
        
        logger.debug("Requisição para: {} {}", method, requestPath);

        // Verificar se é um endpoint público
        if (isPublicEndpoint(requestPath)) {
            logger.debug("Endpoint público: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        // Extrair o token do cabeçalho Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    role = jwtUtil.extractRole(token);
                }
            } catch (Exception e) {
                // Token inválido
            }
        }

        // Verificar se o usuário tem permissão para acessar o endpoint
        if (token == null || role == null || (isAdminOnlyEndpoint(requestPath, method) && !"ADMIN".equals(role))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"erro\":\"Acesso não autorizado\",\"authenticated\":false}");
            return;
        }

        // Continuar com a requisição
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            logger.debug("Endpoint público detectado: {}", path);
        }
        return isPublic;
    }

    private boolean isAdminOnlyEndpoint(String path, String method) {
        // Verifica endpoints específicos de admin
        if (ADMIN_ONLY_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return true;
        }
        
        // Verifica operações de modificação em produtos (POST, PUT, DELETE)
        if (path.startsWith("/api/produtos") && 
            ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method))) {
            return true;
        }
        
        return false;
    }
}
