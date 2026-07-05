package com.celiabordados.controller;

import com.celiabordados.Cliente;
import com.celiabordados.ItemCarrinho;
import com.celiabordados.service.ClienteService;
import com.celiabordados.service.ItemCarrinhoService;
import com.celiabordados.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/carrinho")
@CrossOrigin(origins = "*")
public class ItemCarrinhoController {

    @Autowired
    private ItemCarrinhoService itemCarrinhoService;
    
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getCarrinhoByCliente(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Token de autenticação não fornecido"));
        }
        
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            
            Cliente cliente = clienteService.buscarPorEmail(email);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Cliente não encontrado"));
            }
            
            List<ItemCarrinho> itens = itemCarrinhoService.getCarrinhoByCliente(cliente);
            
            // Desconectar o cliente dos itens para evitar problemas de serialização
            itens.forEach(item -> {
                if (item.getCliente() != null) {
                    Cliente clienteDesconectado = new Cliente();
                    clienteDesconectado.setId(cliente.getId());
                    clienteDesconectado.setNome(cliente.getNome());
                    clienteDesconectado.setEmail(cliente.getEmail());
                    // Não enviar a senha
                    clienteDesconectado.setEnderecoCompleto(cliente.getEnderecoCompleto());
                    clienteDesconectado.setTelefone(cliente.getTelefone());
                    
                    item.setCliente(clienteDesconectado);
                }
            });
            
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar a requisição: " + e.getMessage()));
        }
    }

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarItemAoCarrinho(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String personalizacao,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Token de autenticação não fornecido"));
        }
        
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            
            Cliente cliente = clienteService.buscarPorEmail(email);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Cliente não encontrado"));
            }
            
            ItemCarrinho item = itemCarrinhoService.addItemToCarrinho(produtoId, quantidade, personalizacao, cliente.getId());
            
            if (item != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(item);
            }
            
            return ResponseEntity.badRequest().body(Map.of("erro", "Não foi possível adicionar o item ao carrinho"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar a requisição: " + e.getMessage()));
        }
    }

    @PutMapping("/quantidade/{id}")
    public ResponseEntity<?> atualizarQuantidade(@PathVariable Long id, @RequestParam Integer quantidade) {
        ItemCarrinho item = itemCarrinhoService.updateItemQuantidade(id, quantidade);
        
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/personalizacao/{id}")
    public ResponseEntity<?> atualizarPersonalizacao(@PathVariable Long id, @RequestParam String personalizacao) {
        ItemCarrinho item = itemCarrinhoService.updateItemPersonalizacao(id, personalizacao);
        
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerItem(@PathVariable Long id) {
        try {
            itemCarrinhoService.removeItem(id);
            return ResponseEntity.ok(Map.of("mensagem", "Item removido com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Não foi possível remover o item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/limpar/{clienteId}")
    public ResponseEntity<?> limparCarrinho(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.getClienteById(clienteId);
        
        if (clienteOpt.isPresent()) {
            itemCarrinhoService.limparCarrinho(clienteOpt.get());
            return ResponseEntity.ok(Map.of("mensagem", "Carrinho limpo com sucesso"));
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/total/{clienteId}")
    public ResponseEntity<?> calcularTotalCarrinho(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.getClienteById(clienteId);
        
        if (clienteOpt.isPresent()) {
            Double total = itemCarrinhoService.calcularTotalCarrinho(clienteOpt.get());
            return ResponseEntity.ok(Map.of("total", total));
        }
        
        return ResponseEntity.notFound().build();
    }
}
