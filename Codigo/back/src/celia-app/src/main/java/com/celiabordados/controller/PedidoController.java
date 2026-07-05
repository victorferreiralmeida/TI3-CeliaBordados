package com.celiabordados.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.celiabordados.Pedido;
import com.celiabordados.PedidoItem;
import com.celiabordados.security.JwtUtil;
import com.celiabordados.service.ClienteService;
import com.celiabordados.service.PedidoService;
import com.celiabordados.service.ProdutoEngajamentoService;
import com.celiabordados.service.ProdutoService;
import com.celiabordados.StatusPedido;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ProdutoEngajamentoService produtoEngajamentoService;
    
    // Obter dados para o dashboard (apenas para administradores)
    @GetMapping
    public ResponseEntity<?> getDashboardData(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                Map<String, Object> dashboardData = new HashMap<>();
                
                // Pedidos Hoje
                dashboardData.put("pedidosHoje", getPedidosHoje());
                
                // // Receita Mensal
                dashboardData.put("receitaMensal", getReceitaMensal());
                
                // // Clientes Novos (cadastrados no mês atual)
                dashboardData.put("clientesNovos", getClientesNovos());
                
                // // Itens em Estoque
                dashboardData.put("itensEmEstoque", getItensEmEstoque());
                
                // // Vendas por Categoria
                dashboardData.put("vendasPorCategoria", getVendasPorCategoria());
                
                // // Vendas Mensais (últimos 6 meses)
                dashboardData.put("vendasMensais", getVendasMensais());
                
                // // Pedidos Recentes (últimos 10 pedidos) - apenas IDs
                dashboardData.put("pedidosRecentesIds", getPedidosRecentes());
                
                return ResponseEntity.ok(dashboardData);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Pedidos hoje
    @GetMapping("/pedidos-hoje")
    public ResponseEntity<?> getPedidosHoje(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(Map.of("quantidade", getPedidosHoje()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Receita mensal
    @GetMapping("/receita-mensal")
    public ResponseEntity<?> getReceitaMensal(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(Map.of("valor", getReceitaMensal()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Clientes novos
    @GetMapping("/clientes-novos")
    public ResponseEntity<?> getClientesNovos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(Map.of("quantidade", getClientesNovos()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Itens em estoque
    @GetMapping("/itens-estoque")
    public ResponseEntity<?> getItensEmEstoque(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(Map.of("quantidade", getItensEmEstoque()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Vendas por categoria
    @GetMapping("/vendas-por-categoria")
    public ResponseEntity<?> getVendasPorCategoria(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(getVendasPorCategoria());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Vendas mensais
    @GetMapping("/vendas-mensais")
    public ResponseEntity<?> getVendasMensais(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(getVendasMensais());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Pedidos recentes (apenas IDs)
    @GetMapping("/pedidos-recentes")
    public ResponseEntity<?> getPedidosRecentesEndpoint(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(Map.of("ids", getPedidosRecentes()));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Buscar detalhes de um pedido específico pelo ID
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<?> getPedidoById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                Optional<Pedido> pedidoOpt = pedidoService.getPedidoById(id);
                
                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    
                    // Criar um DTO simplificado para evitar problemas de serialização
                    Map<String, Object> pedidoDTO = new HashMap<>();
                    pedidoDTO.put("id", pedido.getId());
                    
                    // Dados do cliente
                    Map<String, Object> clienteDTO = new HashMap<>();
                    clienteDTO.put("id", pedido.getCliente().getId());
                    clienteDTO.put("nome", pedido.getCliente().getNome());
                    clienteDTO.put("email", pedido.getCliente().getEmail());
                    pedidoDTO.put("cliente", clienteDTO);
                    
                    pedidoDTO.put("dataPedido", pedido.getDataPedido());
                    pedidoDTO.put("status", pedido.getStatus());
                    pedidoDTO.put("total", pedido.getTotal());
                    
                    // Itens do pedido
                    List<Map<String, Object>> itensDTO = new ArrayList<>();
                    for (PedidoItem item : pedido.getItens()) {
                        Map<String, Object> itemDTO = new HashMap<>();
                        itemDTO.put("id", item.getId());
                        itemDTO.put("quantidade", item.getQuantidade());
                        itemDTO.put("precoUnitario", item.getPrecoUnitario());
                        
                        // Dados do produto
                        Map<String, Object> produtoDTO = new HashMap<>();
                        produtoDTO.put("id", item.getProduto().getId());
                        produtoDTO.put("nome", item.getProduto().getNome());
                        produtoDTO.put("categoria", item.getProduto().getCategoria());
                        produtoDTO.put("imagem_url", item.getProduto().getImagemUrl());
                        itemDTO.put("produto", produtoDTO);
                        
                        itensDTO.add(itemDTO);
                    }
                    pedidoDTO.put("itens", itensDTO);
                    
                    return ResponseEntity.ok(pedidoDTO);
                }
                
                return ResponseEntity.notFound().build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // NOVO ENDPOINT PARA LISTAR TODOS OS PEDIDOS PARA O ADMIN
    @GetMapping(path = "/api/pedidos", produces = "application/json")
    public ResponseEntity<?> getAllPedidos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(pedidoService.getAllPedidos());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }
    
    // Obter dados de engajamento dos produtos
    @GetMapping("/engajamento-produtos")
    public ResponseEntity<?> getEngajamentoProdutos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            
            if ("ADMIN".equals(role)) {
                Map<String, Object> engajamentoData = new HashMap<>();
                
                // Dados de produtos mais visitados
                engajamentoData.put("produtosVisitados", getProdutosVisitados());
                
                // Dados de taxa de conversão
                engajamentoData.put("taxaConversao", getTaxasConversao());
                
                // Dados de produtos populares
                engajamentoData.put("produtosPopulares", getProdutosPopulares());
                
                return ResponseEntity.ok(engajamentoData);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }

    // Métodos auxiliares para obter os dados do dashboard
    
    private int getPedidosHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.plusDays(1).atStartOfDay();
        
        return (int) pedidoService.getAllPedidos().stream()
                .filter(p -> p.getDataPedido().isAfter(inicioDia) && p.getDataPedido().isBefore(fimDia))
                .count();
    }
    
    private double getReceitaMensal() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = YearMonth.from(hoje).atDay(1).atStartOfDay();
        LocalDateTime fimMes = YearMonth.from(hoje).atEndOfMonth().plusDays(1).atStartOfDay();
        
        return pedidoService.getAllPedidos().stream()
                .filter(p -> p.getDataPedido().isAfter(inicioMes) && p.getDataPedido().isBefore(fimMes))
                .filter(p -> p.getStatus() == StatusPedido.PAGO || p.getStatus() == StatusPedido.CONCLUIDO)
                .mapToDouble(Pedido::getTotal)
                .sum();
    }
    
    private int getClientesNovos() {
        // Esta implementação é simplificada. Para uma versão mais precisa,
        // seria necessário armazenar a data de cadastro dos clientes.
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = YearMonth.from(hoje).atDay(1).atStartOfDay();
        
        // Aqui estamos apenas contando todos os clientes como exemplo.
        // Idealmente, você teria um campo cadastradoEm na entidade Cliente.
        return clienteService.getClientes().size();
    }
    
    private int getItensEmEstoque() {
        // Considerando que todos os produtos disponíveis estão em estoque
        return (int) produtoService.getAllProdutos().stream()
                .filter(p -> p.isDisponivel())
                .count();
    }
    
    private Map<String, Integer> getVendasPorCategoria() {
        Map<String, Integer> vendasPorCategoria = new HashMap<>();
        
        // Contagem simplificada. Para uma versão mais precisa, seria necessário
        // consultar os itens de pedido pagos e agrupar por categoria.
        pedidoService.getAllPedidos().stream()
                .filter(p -> p.getStatus() == StatusPedido.PAGO || p.getStatus() == StatusPedido.CONCLUIDO)
                .flatMap(p -> p.getItens().stream())
                .forEach(item -> {
                    String categoria = item.getProduto().getCategoria();
                    vendasPorCategoria.put(categoria,
                        vendasPorCategoria.getOrDefault(categoria, 0) + item.getQuantidade());
                });
        
        return vendasPorCategoria;
    }
    
    private Map<String, Double> getVendasMensais() {
        Map<String, Double> vendasMensais = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        
        // Gerar os últimos 6 meses
        LocalDate hoje = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(hoje).minusMonths(i);
            String chave = mes.format(formatter);
            vendasMensais.put(chave, 0.0);
        }
        
        // Calcular as vendas para cada mês
        pedidoService.getAllPedidos().stream()
                .filter(p -> p.getStatus() == StatusPedido.PAGO || p.getStatus() == StatusPedido.CONCLUIDO)
                .forEach(p -> {
                    String mesAno = p.getDataPedido().format(formatter);
                    if (vendasMensais.containsKey(mesAno)) {
                        vendasMensais.put(mesAno, vendasMensais.get(mesAno) + p.getTotal());
                    }
                });
        
        return vendasMensais;
    }
    
    private List<Long> getPedidosRecentes() {
        return pedidoService.getPedidosRecentesIds(10);
    }
    
    // Métodos para análise de engajamento dos produtos
    
    private Map<String, Integer> getProdutosVisitados() {
        return produtoEngajamentoService.getProdutosMaisVisitados();
    }
    
    private Map<String, Double> getTaxasConversao() {
        return produtoEngajamentoService.getTaxasConversao();
    }
    
    private List<Map<String, Object>> getProdutosPopulares() {
        return produtoEngajamentoService.getProdutosPopulares();
    }
}