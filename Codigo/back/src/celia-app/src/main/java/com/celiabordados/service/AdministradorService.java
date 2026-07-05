package com.celiabordados.service;

import com.celiabordados.Administrador;
import com.celiabordados.security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class AdministradorService {
    
    @Autowired
    private AdministradorRepository administradorRepository;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordUtils passwordUtils;
    
    // Lista de senhas padrão que serão consideradas inseguras
    private static final List<String> SENHAS_PADRAO = List.of("admin", "admin1234");
    
    // RowMapper para Administrador
    private final RowMapper<Administrador> administradorRowMapper = (rs, rowNum) -> {
        Administrador admin = new Administrador();
        admin.setId(rs.getLong("id"));
        admin.setNome(rs.getString("nome"));
        admin.setEmail(rs.getString("email"));
        admin.setSenha(rs.getString("senha"));
        // Recupera o último login, que pode ser nulo
        try {
            admin.setUltimoLogin(rs.getTimestamp("ultimo_login") != null ? 
                rs.getTimestamp("ultimo_login").toLocalDateTime() : null);
        } catch (Exception e) {
            admin.setUltimoLogin(null);
        }
        return admin;
    };
    
    public boolean autenticar(String email, String senha) {
        System.out.println("Tentando autenticar administrador com email: " + email);
        
        try {
            // Buscar o administrador
            Administrador admin = getAdministrador(email);
            
            if (admin == null) {
                System.out.println("Administrador não encontrado: " + email);
                return false;
            }
            
            String senhaArmazenada = admin.getSenha();
            
            // Verificar se a senha está no formato BCrypt
            boolean senhaCorreta;
            if (passwordUtils.isBCryptEncoded(senhaArmazenada)) {
                // Verificar usando BCrypt
                senhaCorreta = passwordUtils.matches(senha, senhaArmazenada);
            } else {
                // Verificação legada (texto puro)
                senhaCorreta = senhaArmazenada.equals(senha);
                
                // Se a senha estiver correta, atualize-a para BCrypt
                if (senhaCorreta) {
                    atualizarSenhaParaBcrypt(admin.getId(), senha);
                }
            }
            
            if (senhaCorreta) {
                System.out.println("Administrador autenticado com sucesso");
                // Atualiza o último login
                atualizarUltimoLogin(admin.getId());
                return true;
            } else {
                System.out.println("Senha incorreta para administrador: " + email);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar administrador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Atualiza o último login do administrador para a data e hora atual
     */
    private void atualizarUltimoLogin(Long adminId) {
        try {
            jdbcTemplate.update(
                "UPDATE administradores SET ultimo_login = ? WHERE id = ?",
                java.sql.Timestamp.valueOf(LocalDateTime.now()),
                adminId
            );
            System.out.println("Último login do administrador atualizado. ID: " + adminId);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar último login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Atualiza a senha de um administrador para o formato BCrypt
     */
    private void atualizarSenhaParaBcrypt(Long adminId, String senhaTexto) {
        try {
            String senhaBcrypt = passwordUtils.encode(senhaTexto);
            jdbcTemplate.update(
                "UPDATE administradores SET senha = ? WHERE id = ?",
                senhaBcrypt,
                adminId
            );
            System.out.println("Senha do administrador atualizada para BCrypt. ID: " + adminId);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar senha para BCrypt: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica se a senha do administrador é uma das senhas padrão consideradas inseguras
     */
    public boolean isSenhaPadrao(String email) {
        try {
            Administrador admin = getAdministrador(email);
            if (admin == null) {
                return false;
            }
            
            String senhaArmazenada = admin.getSenha();
            
            // Verificar se a senha está em texto plano
            if (!passwordUtils.isBCryptEncoded(senhaArmazenada)) {
                return SENHAS_PADRAO.contains(senhaArmazenada);
            }
            
            // Se a senha estiver criptografada, verificar se corresponde a alguma das senhas padrão
            for (String senhaPadrao : SENHAS_PADRAO) {
                if (passwordUtils.matches(senhaPadrao, senhaArmazenada)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao verificar se a senha é padrão: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Altera a senha do administrador após verificar a senha atual
     */
    public boolean alterarSenha(String email, String senhaAtual, String novaSenha) {
        try {
            Administrador admin = getAdministrador(email);
            if (admin == null) {
                return false;
            }
            
            String senhaArmazenada = admin.getSenha();
            
            // Verificar se a senha atual está correta
            boolean senhaCorreta;
            if (passwordUtils.isBCryptEncoded(senhaArmazenada)) {
                senhaCorreta = passwordUtils.matches(senhaAtual, senhaArmazenada);
            } else {
                senhaCorreta = senhaArmazenada.equals(senhaAtual);
            }
            
            if (!senhaCorreta) {
                return false;
            }
            
            // Criptografar e atualizar a nova senha
            String novaSenhaBcrypt = passwordUtils.encode(novaSenha);
            jdbcTemplate.update(
                "UPDATE administradores SET senha = ? WHERE id = ?",
                novaSenhaBcrypt,
                admin.getId()
            );
            
            System.out.println("Senha do administrador alterada com sucesso. ID: " + admin.getId());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao alterar senha do administrador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Administrador getAdministrador(String email) {
        try {
            // Consulta JDBC direta ao banco, ignorando qualquer cache
            return jdbcTemplate.queryForObject(
                "SELECT id, nome, email, senha, ultimo_login FROM administradores WHERE email = ?",
                administradorRowMapper,
                email
            );
        } catch (Exception e) {
            System.err.println("Erro ao buscar administrador via JDBC: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Método para inicializar um administrador padrão se não existir nenhum
    @PostConstruct
    public void inicializarAdminPadrao() {
        try {
            // Verificar se já existe algum administrador diretamente via JDBC
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM administradores", Integer.class);
            
            if (count != null && count == 0) {
                System.out.println("Criando administrador padrão...");
                Administrador admin = new Administrador("Célia", "admin@celiabordados.com", "admin");
                
                // Criptografar a senha antes de salvar
                String senhaCriptografada = passwordUtils.encode(admin.getSenha());
                admin.setSenha(senhaCriptografada);
                
                administradorRepository.save(admin);
                System.out.println("Administrador padrão criado com sucesso!");
            } else {
                // Não atualiza a senha do administrador existente para permitir alterações manuais
                System.out.println("Administrador já existe, mantendo configurações atuais.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar administradores existentes: " + e.getMessage());
            // Em caso de erro, tenta usar o repositório normal
            if (administradorRepository.count() == 0) {
                System.out.println("Criando administrador padrão...");
                Administrador admin = new Administrador("Célia", "admin@celiabordados.com", "admin1234");
                
                // Criptografar a senha antes de salvar
                String senhaCriptografada = passwordUtils.encode(admin.getSenha());
                admin.setSenha(senhaCriptografada);
                
                administradorRepository.save(admin);
                System.out.println("Administrador padrão criado com sucesso!");
            }
        }
    }
    
    // Método para salvar um novo administrador
    public Administrador salvarAdministrador(Administrador administrador) {
        // Criptografar a senha antes de salvar
        String senhaCriptografada = passwordUtils.encode(administrador.getSenha());
        administrador.setSenha(senhaCriptografada);
        
        return administradorRepository.save(administrador);
    }
    
    // Método para listar todos os administradores
    public List<Administrador> getAllAdministradores() {
        try {
            // Consulta JDBC direta ao banco, ignorando qualquer cache
            return jdbcTemplate.query(
                "SELECT id, nome, email, senha, ultimo_login FROM administradores",
                administradorRowMapper
            );
        } catch (Exception e) {
            System.err.println("Erro ao listar administradores via JDBC: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, use o repositório
            return administradorRepository.findAll();
        }
    }
}
