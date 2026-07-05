package com.celiabordados.service;

import com.celiabordados.Personalizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalizacaoRepository extends JpaRepository<Personalizacao, Long> {

}
