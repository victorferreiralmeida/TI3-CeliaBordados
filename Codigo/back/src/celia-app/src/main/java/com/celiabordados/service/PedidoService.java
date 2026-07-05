package com.celiabordados.service;

import com.celiabordados.Cliente;
import com.celiabordados.Pedido;
import com.celiabordados.PedidoItem;
import com.celiabordados.Produto;
import com.celiabordados.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PedidoItemRepository pedidoItemRepository;
    
    @Autowired
    private ProdutoService produtoService;
    
    // Buscar todos os pedidos
    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }
    
    // Buscar pedido por ID
    public Optional<Pedido> getPedidoById(Long id) {
        return pedidoRepository.findById(id);
    }
    
    // Buscar pedidos por cliente
    public List<Pedido> getPedidosByCliente(Cliente cliente) {
        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
        // Validar e limpar dados inconsistentes
        for (Pedido pedido : pedidos) {
            if (pedido.getItens() != null) {
                pedido.getItens().removeIf(item -> item == null || item.getProduto() == null);
                pedido.recalcularTotal();
            }
        }
        return pedidos;
    }
    
    // Buscar pedidos por status
    public List<Pedido> getPedidosByStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }
    
    // Criar novo pedido
    @Transactional
    public Pedido criarPedido(Cliente cliente, List<PedidoItem> itens) {
        Pedido pedido = new Pedido(cliente);
        pedido = pedidoRepository.save(pedido);
        
        if (itens != null && !itens.isEmpty()) {
            for (PedidoItem item : itens) {
                item.setPedido(pedido);
                pedidoItemRepository.save(item);
            }
            pedido.recalcularTotal();
            pedido = pedidoRepository.save(pedido);
        }
        
        return pedido;
    }
    
    // Adicionar item ao pedido
    @Transactional
    public PedidoItem adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        Optional<Produto> produtoOpt = produtoService.getProdutoById(produtoId);
        
        if (pedidoOpt.isPresent() && produtoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            Produto produto = produtoOpt.get();
            
            // Verificar se já existe um item com este produto neste pedido
            List<PedidoItem> itensExistentes = pedidoItemRepository.findByPedidoAndProduto(pedido, produto);
            
            if (!itensExistentes.isEmpty()) {
                // Atualizar a quantidade do item existente
                PedidoItem itemExistente = itensExistentes.get(0);
                itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
                pedidoItemRepository.save(itemExistente);
                
                // Recalcular o total do pedido
                pedido.recalcularTotal();
                pedidoRepository.save(pedido);
                
                return itemExistente;
            } else {
                // Criar um novo item
                PedidoItem novoItem = new PedidoItem(pedido, produto, quantidade);
                novoItem = pedidoItemRepository.save(novoItem);
                
                // Adicionar o item ao pedido e recalcular o total
                pedido.adicionarItem(novoItem);
                pedidoRepository.save(pedido);
                
                return novoItem;
            }
        }
        
        return null;
    }
    
    // Remover item do pedido
    @Transactional
    public boolean removerItemDoPedido(Long pedidoId, Long itemId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        Optional<PedidoItem> itemOpt = pedidoItemRepository.findById(itemId);
        
        if (pedidoOpt.isPresent() && itemOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            PedidoItem item = itemOpt.get();
            
            // Verificar se o item pertence ao pedido
            if (item.getPedido().getId().equals(pedido.getId())) {
                pedido.removerItem(item);
                pedidoItemRepository.delete(item);
                pedidoRepository.save(pedido);
                return true;
            }
        }
        
        return false;
    }
    
    // Atualizar status do pedido
    @Transactional
    public boolean atualizarStatusPedido(Long pedidoId, StatusPedido novoStatus) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setStatus(novoStatus);
            pedidoRepository.save(pedido);
            return true;
        }
        return false;
    }
    
    // Cancelar pedido
    @Transactional
    public boolean cancelarPedido(Long pedidoId) {
        return atualizarStatusPedido(pedidoId, StatusPedido.CANCELADO);
    }
    
    // Confirmar pagamento do pedido
    @Transactional
    public boolean confirmarPagamento(Long pedidoId) {
        return atualizarStatusPedido(pedidoId, StatusPedido.PAGO);
    }
    
    // Buscar os 10 pedidos mais recentes diretamente no banco de dados
    public List<Pedido> getPedidosRecentes(int quantidade) {
        return pedidoRepository.findAllByOrderByDataPedidoDesc(PageRequest.of(0, quantidade));
    }
    
    // Buscar apenas os IDs dos pedidos mais recentes
    public List<Long> getPedidosRecentesIds(int quantidade) {
        return pedidoRepository.findRecentPedidoIds(PageRequest.of(0, quantidade));
    }
    
    // Método para salvar/atualizar um pedido
    public Pedido savePedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
} 