package com.celiabordados.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.celiabordados.Produto;
import com.celiabordados.ProdutoCarrinhoAdicao;

@Repository
public interface ProdutoCarrinhoAdicaoRepository extends JpaRepository<ProdutoCarrinhoAdicao, Long> {
    
    // Contar adições ao carrinho por produto
    long countByProduto(Produto produto);
    
    // Buscar adições ao carrinho por produto
    List<ProdutoCarrinhoAdicao> findByProduto(Produto produto);
    
    // Buscar adições ao carrinho por produto ordenadas por data (mais recentes primeiro)
    List<ProdutoCarrinhoAdicao> findByProdutoOrderByDataAdicaoDesc(Produto produto);
    
    // Buscar produtos mais adicionados ao carrinho (últimos 30 dias)
    @Query("SELECT p.nome as nomeProduto, COUNT(a) as total " +
           "FROM ProdutoCarrinhoAdicao a " +
           "JOIN a.produto p " +
           "WHERE a.dataAdicao >= CURRENT_DATE - 30 " +
           "GROUP BY p.nome " +
           "ORDER BY total DESC")
    List<Object[]> findProdutosMaisAdicionadosAoCarrinho();
}
