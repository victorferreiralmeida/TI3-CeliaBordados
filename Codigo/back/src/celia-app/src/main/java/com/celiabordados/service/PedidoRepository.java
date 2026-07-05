package com.celiabordados.service;

import com.celiabordados.Cliente;
import com.celiabordados.Pedido;
import com.celiabordados.StatusPedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Buscar pedidos por cliente
    List<Pedido> findByCliente(Cliente cliente);
    
    // Buscar pedidos por status
    List<Pedido> findByStatus(StatusPedido status);
    
    // Buscar pedidos por cliente e status
    List<Pedido> findByClienteAndStatus(Cliente cliente, StatusPedido status);
    
    // Buscar pedidos recentes ordenados por data
    List<Pedido> findAllByOrderByDataPedidoDesc(Pageable pageable);
    
    // Buscar apenas os IDs dos pedidos mais recentes
    @Query("SELECT p.id FROM Pedido p ORDER BY p.dataPedido DESC")
    List<Long> findRecentPedidoIds(Pageable pageable);
} 