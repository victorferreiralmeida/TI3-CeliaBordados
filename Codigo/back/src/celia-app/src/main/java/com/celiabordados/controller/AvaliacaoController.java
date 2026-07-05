package com.celiabordados.controller;

import com.celiabordados.Avaliacao;
import com.celiabordados.Cliente;
import com.celiabordados.Pedido;
import com.celiabordados.Produto;
import com.celiabordados.StatusPedido;
import com.celiabordados.service.AvaliacaoService;
import com.celiabordados.service.ClienteService;
import com.celiabordados.service.PedidoService;
import com.celiabordados.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoService avaliacaoService;
    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Avaliacao> listarTodas() {
        return avaliacaoService.listarTodas();
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<?> buscarPorProduto(@PathVariable Long produtoId) {
        Optional<Produto> produto = produtoService.getProdutoById(produtoId);
        if (produto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Avaliacao> avaliacoes = avaliacaoService.buscarPorProduto(produto.get());
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Avaliacao> buscarPorPedido(@PathVariable Long pedidoId) {
        Optional<Pedido> pedido = pedidoService.getPedidoById(pedidoId);
        return pedido.map(avaliacaoService::buscarPorPedido).orElse(List.of());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Avaliacao> buscarPorCliente(@PathVariable Long clienteId) {
        Optional<Cliente> cliente = clienteService.getClienteById(clienteId);
        return cliente.map(avaliacaoService::buscarPorCliente).orElse(List.of());
    }

    @PostMapping
    public ResponseEntity<Avaliacao> salvar(@RequestBody Avaliacao avaliacao) {
        Avaliacao salva = avaliacaoService.salvar(avaliacao);
        return ResponseEntity.ok(salva);
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> salvarMultiplasAvaliacoes(@PathVariable Long pedidoId, @RequestBody AvaliacoesRequest request) {
        Optional<Pedido> pedidoOpt = pedidoService.getPedidoById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Pedido não encontrado");
        }
        Pedido pedido = pedidoOpt.get();
        Optional<Cliente> clienteOpt = clienteService.getClienteById(pedido.getCliente().getId());
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Cliente não encontrado");
        }
        Cliente cliente = clienteOpt.get();
        
        // Salvar cada avaliação
        for (AvaliacaoDTO dto : request.getAvaliacoes()) {
            Optional<Produto> produtoOpt = produtoService.getProdutoById(dto.getProdutoId());
            if (produtoOpt.isEmpty()) continue;
            Produto produto = produtoOpt.get();
            
            // Verificar se já existe uma avaliação para este produto e cliente neste pedido
            // List<Avaliacao> avaliacoesExistentes = avaliacaoService.buscarPorPedido(pedido);
            // boolean jaAvaliado = avaliacoesExistentes.stream()
            //                         .anyMatch(a -> a.getProduto().getId().equals(produto.getId()) && a.getCliente().getId().equals(cliente.getId()));

            // if (!jaAvaliado) {
                Avaliacao avaliacao = new Avaliacao();
                avaliacao.setPedido(pedido);
                avaliacao.setProduto(produto);
                avaliacao.setCliente(cliente);
                avaliacao.setNota(dto.getNota());
                avaliacao.setComentario(dto.getComentario());
                avaliacao.setDataAvaliacao(dto.getData() != null ? dto.getData() : java.time.LocalDateTime.now());
                avaliacaoService.salvar(avaliacao);
            // }
        }

        // Verificar se todos os itens do pedido foram avaliados
        List<Avaliacao> avaliacoesDoPedido = avaliacaoService.buscarPorPedido(pedido);
        java.util.Set<Long> produtosAvaliadosIds = avaliacoesDoPedido.stream()
                .map(avaliacao -> avaliacao.getProduto().getId())
                .collect(java.util.stream.Collectors.toSet());

        java.util.Set<Long> produtosDoPedidoIds = pedido.getItens().stream()
                .map(item -> item.getProduto().getId())
                .collect(java.util.stream.Collectors.toSet());

        if (produtosAvaliadosIds.containsAll(produtosDoPedidoIds) && produtosDoPedidoIds.containsAll(produtosAvaliadosIds)) {
            // Todos os produtos do pedido foram avaliados
            if (!pedido.isAvaliado()) {
                pedido.setAvaliado(true);
                pedido.setStatus(StatusPedido.CONCLUIDO);
                pedidoService.savePedido(pedido);
            }
        }

        return ResponseEntity.ok(java.util.Map.of("message", "Avaliações salvas com sucesso!"));
    }

    // DTOs auxiliares
    public static class AvaliacoesRequest {
        private Long pedidoId;
        private java.util.List<AvaliacaoDTO> avaliacoes;
        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        public java.util.List<AvaliacaoDTO> getAvaliacoes() { return avaliacoes; }
        public void setAvaliacoes(java.util.List<AvaliacaoDTO> avaliacoes) { this.avaliacoes = avaliacoes; }
    }
    public static class AvaliacaoDTO {
        private Long produtoId;
        private String nome;
        private Integer nota;
        private String comentario;
        private java.time.LocalDateTime data;
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public Integer getNota() { return nota; }
        public void setNota(Integer nota) { this.nota = nota; }
        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
        public java.time.LocalDateTime getData() { return data; }
        public void setData(java.time.LocalDateTime data) { this.data = data; }
    }
} 