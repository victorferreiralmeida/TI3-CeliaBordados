package com.celiabordados.service;

import com.celiabordados.ItemCarrinho;
import com.celiabordados.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    List<ItemCarrinho> findByCliente(Cliente cliente);
    void deleteByCliente(Cliente cliente);
}
