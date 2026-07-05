package com.celiabordados.service;

import com.celiabordados.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Administrador findByEmail(String email);
    Administrador findByEmailAndSenha(String email, String senha);
}
