package com.celiabordados.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.celiabordados.Cliente;
import com.celiabordados.Produto;
import com.celiabordados.security.JwtUtil;
import com.celiabordados.service.ClienteService;
import com.celiabordados.service.ProdutoEngajamentoService;
import com.celiabordados.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoEngajamentoService produtoEngajamentoService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Produto>> getAllProdutos() {
        return ResponseEntity.ok(produtoService.getAllProdutos());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Produto>> getAllProdutosAdmin() {
        return ResponseEntity.ok(produtoService.getAllProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProdutoById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);
        if (!produtoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Produto produto = produtoOpt.get();
        
        Long clienteId = null;
        // Se houver um token, tentar extrair o clienteId
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                Cliente cliente = clienteService.buscarPorEmail(email);
                
                if (cliente != null) {
                    clienteId = cliente.getId();
                }
            } catch (Exception e) {
                // Log do erro mas continua retornando o produto
                System.err.println("Erro ao extrair cliente do token: " + e.getMessage());
            }
        }
        
        // Sempre registrar a visualização, passando clienteId ou null
        produtoEngajamentoService.registrarVisualizacao(id, clienteId);
        
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> getProdutosByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.getProdutosByCategoria(categoria));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Produto>> searchProdutos(@RequestParam String termo) {
        return ResponseEntity.ok(produtoService.searchProdutos(termo));
    }

    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        Produto savedProduto = produtoService.saveProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduto(@PathVariable Long id, @RequestBody Produto produtoDetails) {
        Produto updatedProduto = produtoService.updateProduto(id, produtoDetails);
        
        if (updatedProduto != null) {
            return ResponseEntity.ok(updatedProduto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduto(@PathVariable Long id) {
        try {
            produtoService.deleteProduto(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto removido com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Não foi possível remover o produto: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/adicionar-ao-carrinho")
    public ResponseEntity<?> registrarAdicaoAoCarrinho(
            @PathVariable Long id,
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
            
            produtoEngajamentoService.registrarAdicaoAoCarrinho(id, cliente.getId());
            return ResponseEntity.ok(Map.of("mensagem", "Adição ao carrinho registrada com sucesso"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao registrar adição ao carrinho: " + e.getMessage()));
        }
    }
}
