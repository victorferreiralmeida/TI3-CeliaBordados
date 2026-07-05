package com.celiabordados.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celiabordados.Produto;

@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }
    
    public List<Produto> getProdutosDisponiveis() {
        return produtoRepository.findByDisponivel(true);
    }
    
    public List<Produto> getProdutosByCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }
    
    public List<Produto> searchProdutos(String termo) {
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }
    
    public Optional<Produto> getProdutoById(Long id) {
        return produtoRepository.findById(id);
    }
    
    public Produto saveProduto(Produto produto) {
        return produtoRepository.save(produto);
    }
    
    public void deleteProduto(Long id) {
        produtoRepository.deleteById(id);
    }
    
    public Produto updateProduto(Long id, Produto produtoDetails) {
        Optional<Produto> optionalProduto = produtoRepository.findById(id);
        
        if (optionalProduto.isPresent()) {
            Produto produto = optionalProduto.get();
            produto.setNome(produtoDetails.getNome());
            produto.setDescricao(produtoDetails.getDescricao());
            produto.setImagemUrl(produtoDetails.getImagemUrl());
            produto.setCategoria(produtoDetails.getCategoria());
            produto.setPrecoBase(produtoDetails.getPrecoBase());
            produto.setDisponivel(produtoDetails.isDisponivel());
            
            return produtoRepository.save(produto);
        }
        
        return null;
    }
}
