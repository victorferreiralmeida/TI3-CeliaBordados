package com.celiabordados.service;

import com.celiabordados.Cliente;
import com.celiabordados.ItemCarrinho;
import com.celiabordados.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemCarrinhoService {
    
    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<ItemCarrinho> getCarrinhoByCliente(Cliente cliente) {
        return itemCarrinhoRepository.findByCliente(cliente);
    }
    
    public ItemCarrinho addItemToCarrinho(Long produtoId, Integer quantidade, String personalizacao, Long clienteId) {
        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        
        if (produtoOpt.isPresent() && clienteOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Cliente cliente = clienteOpt.get();
            
            if (!produto.isDisponivel()) {
                return null; // Produto não está disponível
            }
            
            ItemCarrinho item = new ItemCarrinho(produto, quantidade, personalizacao, cliente);
            return itemCarrinhoRepository.save(item);
        }
        
        return null;
    }
    
    public Optional<ItemCarrinho> getItemById(Long id) {
        return itemCarrinhoRepository.findById(id);
    }
    
    public ItemCarrinho updateItemQuantidade(Long id, Integer quantidade) {
        Optional<ItemCarrinho> itemOpt = itemCarrinhoRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            ItemCarrinho item = itemOpt.get();
            item.setQuantidade(quantidade);
            return itemCarrinhoRepository.save(item);
        }
        
        return null;
    }
    
    public ItemCarrinho updateItemPersonalizacao(Long id, String personalizacao) {
        Optional<ItemCarrinho> itemOpt = itemCarrinhoRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            ItemCarrinho item = itemOpt.get();
            item.setPersonalizacao(personalizacao);
            return itemCarrinhoRepository.save(item);
        }
        
        return null;
    }
    
    public void removeItem(Long id) {
        itemCarrinhoRepository.deleteById(id);
    }
    
    @Transactional
    public void limparCarrinho(Cliente cliente) {
        itemCarrinhoRepository.deleteByCliente(cliente);
    }
    
    public Double calcularTotalCarrinho(Cliente cliente) {
        List<ItemCarrinho> itens = itemCarrinhoRepository.findByCliente(cliente);
        return itens.stream()
                .mapToDouble(ItemCarrinho::getSubtotal)
                .sum();
    }
}
