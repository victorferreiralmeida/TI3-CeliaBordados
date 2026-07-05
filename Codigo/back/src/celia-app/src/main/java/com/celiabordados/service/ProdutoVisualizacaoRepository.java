package com.celiabordados.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.celiabordados.Produto;
import com.celiabordados.ProdutoVisualizacao;

@Repository
public interface ProdutoVisualizacaoRepository extends JpaRepository<ProdutoVisualizacao, Long> {
    
    // Contar visualizações por produto
    long countByProduto(Produto produto);
    
    // Buscar visualizações por produto
    List<ProdutoVisualizacao> findByProduto(Produto produto);
    
    // Buscar visualizações por produto ordenadas por data (mais recentes primeiro)
    List<ProdutoVisualizacao> findByProdutoOrderByDataVisualizacaoDesc(Produto produto);
    
    // Buscar produtos mais visualizados (últimos 30 dias)
    @Query("SELECT p.nome as nomeProduto, COUNT(v) as total " +
           "FROM ProdutoVisualizacao v " +
           "JOIN v.produto p " +
           "WHERE v.dataVisualizacao >= CURRENT_DATE - 30 " +
           "GROUP BY p.nome " +
           "ORDER BY total DESC")
    List<Object[]> findProdutosMaisVisualizados();
}
