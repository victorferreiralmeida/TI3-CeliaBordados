package com.celiabordados.service;

import com.celiabordados.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Cliente findByEmailAndSenha(String email, String senha);
}
