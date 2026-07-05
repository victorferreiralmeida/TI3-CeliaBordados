package com.celiabordados.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.celiabordados.Pedido;
import com.celiabordados.PedidoItem;
import com.celiabordados.Produto;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    
    // Buscar itens por pedido
    List<PedidoItem> findByPedido(Pedido pedido);
    
    // Buscar itens por produto
    List<PedidoItem> findByProduto(Produto produto);
    
    // Buscar itens por pedido e produto
    List<PedidoItem> findByPedidoAndProduto(Pedido pedido, Produto produto);
} 