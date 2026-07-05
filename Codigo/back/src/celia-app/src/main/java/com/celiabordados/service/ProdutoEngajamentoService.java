package com.celiabordados.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.celiabordados.Avaliacao;
import com.celiabordados.Cliente;
import com.celiabordados.Produto;
import com.celiabordados.ProdutoCarrinhoAdicao;
import com.celiabordados.ProdutoVisualizacao;

@Service
public class ProdutoEngajamentoService {
    
    @Autowired
    private ProdutoVisualizacaoRepository visualizacaoRepository;
    
    @Autowired
    private ProdutoCarrinhoAdicaoRepository carrinhoAdicaoRepository;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private AvaliacaoService avaliacaoService;
    
    @Transactional
    public void registrarVisualizacao(Long produtoId, Long clienteId) {
        Optional<Produto> produtoOpt = produtoService.getProdutoById(produtoId);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            ProdutoVisualizacao visualizacao = new ProdutoVisualizacao(produto, clienteId != null ? new Cliente(clienteId) : null);
            visualizacaoRepository.save(visualizacao);
        }
    }
    
    @Transactional
    public void registrarAdicaoAoCarrinho(Long produtoId, Long clienteId) {
        Optional<Produto> produtoOpt = produtoService.getProdutoById(produtoId);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Cliente cliente = new Cliente(clienteId);
            ProdutoCarrinhoAdicao adicao = new ProdutoCarrinhoAdicao(produto, cliente);
            carrinhoAdicaoRepository.save(adicao);
        }
    }
    
    public Map<String, Integer> getProdutosMaisVisitados() {
        List<Object[]> resultados = visualizacaoRepository.findProdutosMaisVisualizados();
        Map<String, Integer> produtosVisitados = new LinkedHashMap<>();
        
        for (Object[] resultado : resultados) {
            String nomeProduto = (String) resultado[0];
            Long visualizacoes = (Long) resultado[1];
            produtosVisitados.put(nomeProduto, visualizacoes.intValue());
        }
        
        return produtosVisitados;
    }
    
    public Map<String, Double> getTaxasConversao() {
        List<Produto> produtos = produtoService.getAllProdutos();
        Map<String, Double> taxasConversao = new LinkedHashMap<>();
        
        for (Produto produto : produtos) {
            if (produto.isDisponivel()) {
                long visualizacoes = visualizacaoRepository.countByProduto(produto);
                long adicoesCarrinho = carrinhoAdicaoRepository.countByProduto(produto);
                
                double taxa = visualizacoes > 0 ? (double) adicoesCarrinho / visualizacoes : 0.0;
                taxasConversao.put(produto.getNome(), taxa);
            }
        }
        
        return taxasConversao;
    }
    
    public List<Map<String, Object>> getProdutosPopulares() {
        List<Produto> produtos = produtoService.getAllProdutos();
        List<Map<String, Object>> produtosPopulares = new ArrayList<>();
        
        for (Produto produto : produtos) {
            if (produto.isDisponivel()) {
                Map<String, Object> dadosProduto = new HashMap<>();
                dadosProduto.put("id", produto.getId());
                dadosProduto.put("nome", produto.getNome());
                dadosProduto.put("categoria", produto.getCategoria());
                dadosProduto.put("preco", produto.getPrecoBase());
                dadosProduto.put("visualizacoes", visualizacaoRepository.countByProduto(produto));
                dadosProduto.put("adicoesCarrinho", carrinhoAdicaoRepository.countByProduto(produto));

                // Adicionar média de avaliações e número de comentários
                List<Avaliacao> avaliacoesProduto = avaliacaoService.buscarPorProduto(produto);
                double mediaAvaliacoes = avaliacoesProduto.stream()
                    .mapToInt(Avaliacao::getNota)
                    .average()
                    .orElse(0.0);
                long numeroComentarios = avaliacoesProduto.stream()
                    .filter(a -> a.getComentario() != null && !a.getComentario().trim().isEmpty())
                    .count();

                dadosProduto.put("mediaAvaliacoes", String.format("%.1f", mediaAvaliacoes));
                dadosProduto.put("numeroComentarios", numeroComentarios);
                
                produtosPopulares.add(dadosProduto);
            }
        }
        
        // Ordenar por número de visualizações e adições ao carrinho
        produtosPopulares.sort((a, b) -> {
            long totalA = ((Long) a.get("visualizacoes")) + ((Long) a.get("adicoesCarrinho"));
            long totalB = ((Long) b.get("visualizacoes")) + ((Long) b.get("adicoesCarrinho"));
            return Long.compare(totalB, totalA);
        });
        
        // Limitar aos 10 produtos mais populares
        return produtosPopulares.stream()
                .limit(10)
                .collect(Collectors.toList());
    }
} 