package com.celiabordados.service;

import com.celiabordados.Cliente;
import com.celiabordados.security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordUtils passwordUtils;
    
    // RowMapper para Cliente
    private final RowMapper<Cliente> clienteRowMapper = (rs, rowNum) -> {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setEmail(rs.getString("email"));
        cliente.setSenha(rs.getString("senha"));
        cliente.setEnderecoCompleto(rs.getString("endereco_completo"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setDataCadastro(rs.getTimestamp("data_cadastro") != null ? rs.getTimestamp("data_cadastro").toLocalDateTime() : null);
        return cliente;
    };
    
    public List<Cliente> getClientes() {
        try {
            // Consulta JDBC direta
            return jdbcTemplate.query(
                "SELECT id, nome, email, senha, endereco_completo, telefone, data_cadastro FROM clientes",
                clienteRowMapper
            );
        } catch (Exception e) {
            System.err.println("Erro ao listar clientes via JDBC: " + e.getMessage());
            e.printStackTrace();
            return clienteRepository.findAll();
        }
    }

    public boolean adicionarCliente(Cliente cliente) {
        System.out.println("Tentando adicionar cliente: " + cliente.getNome() + " com email: " + cliente.getEmail());
        
        try {
            // Verificar existência via JDBC
            Integer countEmail = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM clientes WHERE email = ?", 
                Integer.class, 
                cliente.getEmail()
            );
            
            if (countEmail != null && countEmail > 0) {
                System.out.println("Email já cadastrado: " + cliente.getEmail());
                return false;
            }
            
            if (cliente.getTelefone() != null && !cliente.getTelefone().isEmpty()) {
                Integer countTelefone = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM clientes WHERE telefone = ?", 
                    Integer.class, 
                    cliente.getTelefone()
                );
                
                if (countTelefone != null && countTelefone > 0) {
                    System.out.println("Telefone já cadastrado: " + cliente.getTelefone());
                    return false;
                }
            }
            
            // Criptografar a senha antes de salvar
            String senhaCriptografada = passwordUtils.encode(cliente.getSenha());
            cliente.setSenha(senhaCriptografada);
            
            // Salvar via repositório
            cliente.setDataCadastro(LocalDateTime.now());
            Cliente clienteSalvo = clienteRepository.save(cliente);
            System.out.println("Cliente salvo com sucesso! ID: " + clienteSalvo.getId());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Cliente autenticar(String email, String senha) {
        System.out.println("Tentando autenticar cliente com email: " + email);
        
        try {
            // Consulta direta JDBC para buscar senha
            Cliente cliente = buscarPorEmail(email);
            
            if (cliente == null) {
                System.out.println("Cliente não encontrado: " + email);
                return null;
            }
            
            String senhaArmazenada = cliente.getSenha();
            
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
                    atualizarSenhaParaBcrypt(cliente.getId(), senha);
                }
            }
            
            if (senhaCorreta) {
                System.out.println("Cliente autenticado com sucesso!");
                return cliente;
            } else {
                System.out.println("Senha incorreta para cliente: " + email);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar cliente: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Atualiza a senha de um cliente para o formato BCrypt
     */
    private void atualizarSenhaParaBcrypt(Long clienteId, String senhaTexto) {
        try {
            String senhaBcrypt = passwordUtils.encode(senhaTexto);
            jdbcTemplate.update(
                "UPDATE clientes SET senha = ? WHERE id = ?",
                senhaBcrypt,
                clienteId
            );
            System.out.println("Senha do cliente atualizada para BCrypt. ID: " + clienteId);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar senha para BCrypt: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Cliente buscarPorEmail(String email) {
        try {
            // Consulta JDBC direta
            return jdbcTemplate.queryForObject(
                "SELECT id, nome, email, senha, endereco_completo, telefone, data_cadastro FROM clientes WHERE email = ?",
                clienteRowMapper,
                email
            );
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente por email via JDBC: " + e.getMessage());
            e.printStackTrace();
            return clienteRepository.findByEmail(email);
        }
    }
    
    public Optional<Cliente> getClienteById(Long id) {
        try {
            // Consulta JDBC direta
            Cliente cliente = jdbcTemplate.queryForObject(
                "SELECT id, nome, email, senha, endereco_completo, telefone, data_cadastro FROM clientes WHERE id = ?",
                clienteRowMapper,
                id
            );
            return Optional.ofNullable(cliente);
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente por ID via JDBC: " + e.getMessage());
            e.printStackTrace();
            return clienteRepository.findById(id);
        }
    }
}
