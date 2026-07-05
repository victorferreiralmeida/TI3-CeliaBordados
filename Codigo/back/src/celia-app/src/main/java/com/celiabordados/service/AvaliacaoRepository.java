package com.celiabordados.service;

import com.celiabordados.Avaliacao;
import com.celiabordados.Produto;
import com.celiabordados.Pedido;
import com.celiabordados.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByProduto(Produto produto);
    List<Avaliacao> findByPedido(Pedido pedido);
    List<Avaliacao> findByCliente(Cliente cliente);
} 