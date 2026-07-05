package com.celiabordados.controller;

import com.celiabordados.service.PedidoService;
import com.celiabordados.security.JwtUtil;
import com.celiabordados.StatusPedido;
import com.celiabordados.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoAdminController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.celiabordados.service.ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<?> getAllPedidos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            if ("ADMIN".equals(role)) {
                // Montar lista de DTOs
                List<com.celiabordados.Pedido> pedidos = pedidoService.getAllPedidos();
                List<Map<String, Object>> pedidosDTO = new java.util.ArrayList<>();
                for (com.celiabordados.Pedido pedido : pedidos) {
                    Map<String, Object> dto = new java.util.HashMap<>();
                    dto.put("id", pedido.getId());
                    dto.put("clienteNome", pedido.getCliente() != null ? pedido.getCliente().getNome() : "");
                    // Concatenar nomes dos produtos
                    String produtos = pedido.getItens() != null
                        ? pedido.getItens().stream()
                            .map(item -> item.getProduto() != null ? item.getProduto().getNome() : "")
                            .reduce((a, b) -> a + ", " + b).orElse("")
                        : "";
                    dto.put("produtoNome", produtos);
                    dto.put("valor", pedido.getTotal());
                    dto.put("data", pedido.getDataPedido() != null ? pedido.getDataPedido().toLocalDate().toString() : "");
                    dto.put("status", pedido.getStatus().name());
                    pedidosDTO.add(dto);
                }
                return ResponseEntity.ok(pedidosDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<?> getPedidosByCliente(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            String email = jwtUtil.extractEmail(token);

            // Permitir acesso ao próprio cliente ou admin
            com.celiabordados.Cliente cliente = clienteService.getClienteById(id).orElse(null);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Cliente não encontrado"));
            }
            if (!cliente.getEmail().equals(email) && !"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Não autorizado"));
            }

            List<com.celiabordados.Pedido> pedidos = pedidoService.getPedidosByCliente(cliente);
            List<Map<String, Object>> pedidosDTO = new java.util.ArrayList<>();
            for (com.celiabordados.Pedido pedido : pedidos) {
                Map<String, Object> pedidoDTO = new java.util.HashMap<>();
                pedidoDTO.put("id", pedido.getId());
                pedidoDTO.put("dataPedido", pedido.getDataPedido());
                pedidoDTO.put("status", pedido.getStatus());
                pedidoDTO.put("valorTotal", pedido.getTotal());
                // Itens
                List<Map<String, Object>> itensDTO = new java.util.ArrayList<>();
                if (pedido.getItens() != null) {
                    for (com.celiabordados.PedidoItem item : pedido.getItens()) {
                        if (item == null || item.getProduto() == null) {
                            System.out.println("[WARN] Item de pedido nulo ou sem produto associado encontrado no pedido " + pedido.getId());
                            continue; // Pular este item
                        }
                        Map<String, Object> itemDTO = new java.util.HashMap<>();
                        itemDTO.put("quantidade", item.getQuantidade());
                        itemDTO.put("precoUnitario", item.getPrecoUnitario());
                        Map<String, Object> produtoDTO = new java.util.HashMap<>();
                        produtoDTO.put("nome", item.getProduto().getNome());
                        produtoDTO.put("precoUnitario", item.getProduto().getPrecoBase());
                        produtoDTO.put("imagem_url", item.getProduto().getImagemUrl());
                        produtoDTO.put("id", item.getProduto().getId());
                        itemDTO.put("produto", produtoDTO);
                        itensDTO.add(itemDTO);
                    }
                }
                pedidoDTO.put("itens", itensDTO);
                pedidosDTO.add(pedidoDTO);
            }
            return ResponseEntity.ok(pedidosDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Não autorizado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestBody Map<String, Object> dados,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            if ("ADMIN".equals(role)) {
                String novoStatusStr = (String) dados.get("status");
                if (novoStatusStr != null) {
                    try {
                        StatusPedido novoStatus = StatusPedido.fromString(novoStatusStr.trim());
                        System.out.println("[DEBUG] Atualizando status do pedido " + id + " para: " + novoStatus);
                        boolean atualizado = pedidoService.atualizarStatusPedido(id, novoStatus);
                        if (atualizado) {
                            return ResponseEntity.ok(Map.of("mensagem", "Status atualizado com sucesso"));
                        } else {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(Map.of("erro", "Pedido não encontrado ou não foi possível atualizar"));
                        }
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("erro", "Status inválido: " + novoStatusStr));
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }

    @GetMapping("/relatorio")
    public ResponseEntity<?> getRelatorioVendas(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String produto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            if ("ADMIN".equals(role)) {
                List<com.celiabordados.Pedido> pedidos = pedidoService.getAllPedidos();
                List<Map<String, Object>> pedidosDTO = new java.util.ArrayList<>();
                for (com.celiabordados.Pedido pedido : pedidos) {
                    // Filtro por data
                    boolean dataOk = true;
                    if (dataInicial != null && !dataInicial.isEmpty()) {
                        dataOk = pedido.getDataPedido() != null &&
                                !pedido.getDataPedido().toLocalDate().isBefore(java.time.LocalDate.parse(dataInicial));
                    }
                    if (dataFinal != null && !dataFinal.isEmpty()) {
                        dataOk = dataOk && pedido.getDataPedido() != null &&
                                !pedido.getDataPedido().toLocalDate().isAfter(java.time.LocalDate.parse(dataFinal));
                    }
                    // Filtro por status
                    boolean statusOk = true;
                    if (status != null && !status.isEmpty()) {
                        String[] statusArr = status.split(",");
                        statusOk = java.util.Arrays.stream(statusArr)
                                .anyMatch(s -> s.equalsIgnoreCase(pedido.getStatus().name()));
                    }
                    // Filtro por produto
                    boolean produtoOk = true;
                    if (produto != null && !produto.isEmpty()) {
                        String[] produtosArr = produto.split(",");
                        String nomesProdutos = pedido.getItens() != null
                                ? pedido.getItens().stream()
                                    .map(item -> item.getProduto() != null ? item.getProduto().getNome() : "")
                                    .reduce((a, b) -> a + "," + b).orElse("")
                                : "";
                        produtoOk = java.util.Arrays.stream(produtosArr)
                                .anyMatch(p -> nomesProdutos.contains(p));
                    }
                    if (dataOk && statusOk && produtoOk) {
                        Map<String, Object> dto = new java.util.HashMap<>();
                        dto.put("data", pedido.getDataPedido() != null ? pedido.getDataPedido().toLocalDate().toString() : "");
                        String produtos = pedido.getItens() != null
                                ? pedido.getItens().stream()
                                    .map(item -> item.getProduto() != null ? item.getProduto().getNome() : "")
                                    .reduce((a, b) -> a + ", " + b).orElse("")
                                : "";
                        dto.put("produto", produtos);
                        dto.put("valor", pedido.getTotal());
                        dto.put("status", pedido.getStatus().name());
                        pedidosDTO.add(dto);
                    }
                }
                return ResponseEntity.ok(pedidosDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Não autorizado"));
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(
            @RequestBody Map<String, Object> pedidoData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Token de autenticação não fornecido"));
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            com.celiabordados.Cliente cliente = clienteService.buscarPorEmail(email);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Cliente não encontrado"));
            }

            // Criação do pedido
            com.celiabordados.Pedido pedido = new com.celiabordados.Pedido();
            pedido.setCliente(cliente);
            pedido.setStatus(com.celiabordados.StatusPedido.PENDENTE);
            // Conversão segura do total
            Object totalObj = pedidoData.getOrDefault("total", 0.0);
            double total;
            if (totalObj instanceof Integer) {
                total = ((Integer) totalObj).doubleValue();
            } else if (totalObj instanceof Double) {
                total = (Double) totalObj;
            } else if (totalObj instanceof String) {
                total = Double.parseDouble((String) totalObj);
            } else {
                total = 0.0;
            }
            pedido.setTotal(total);
            pedido.setAvaliado(false);

            // Itens do pedido (espera-se um array de objetos)
            List<Map<String, Object>> itens = (List<Map<String, Object>>) pedidoData.get("itens");
            List<com.celiabordados.PedidoItem> itensPedido = new java.util.ArrayList<>();
            if (itens != null) {
                for (Map<String, Object> item : itens) {
                    Long produtoId = Long.valueOf(item.get("produtoId").toString());
                    Integer quantidade = Integer.valueOf(item.get("quantidade").toString());
                    com.celiabordados.Produto produto = produtoService.getProdutoById(produtoId).orElse(null);
                    if (produto != null) {
                        com.celiabordados.PedidoItem pedidoItem = new com.celiabordados.PedidoItem();
                        pedidoItem.setProduto(produto);
                        pedidoItem.setQuantidade(quantidade);
                        pedidoItem.setPrecoUnitario(produto.getPrecoBase());
                        pedidoItem.setPedido(pedido);
                        itensPedido.add(pedidoItem);
                    }
                }
            }
            pedido.setItens(itensPedido);

            pedidoService.savePedido(pedido);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", pedido.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao criar pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPedidoById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractRole(token);
            String email = jwtUtil.extractEmail(token);

            com.celiabordados.Pedido pedido = pedidoService.getPedidoById(id).orElse(null);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Pedido não encontrado"));
            }
            // Permitir acesso ao admin ou ao cliente dono do pedido
            if (!"ADMIN".equals(role) && (pedido.getCliente() == null || !pedido.getCliente().getEmail().equals(email))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Não autorizado"));
            }

            Map<String, Object> pedidoDTO = new java.util.HashMap<>();
            pedidoDTO.put("id", pedido.getId());
            pedidoDTO.put("dataPedido", pedido.getDataPedido());
            pedidoDTO.put("status", pedido.getStatus());
            pedidoDTO.put("total", pedido.getTotal());
            // Itens
            java.util.List<java.util.Map<String, Object>> itensDTO = new java.util.ArrayList<>();
            if (pedido.getItens() != null) {
                for (com.celiabordados.PedidoItem item : pedido.getItens()) {
                    if (item == null || item.getProduto() == null) continue;
                    java.util.Map<String, Object> itemDTO = new java.util.HashMap<>();
                    itemDTO.put("quantidade", item.getQuantidade());
                    itemDTO.put("precoUnitario", item.getPrecoUnitario());
                    java.util.Map<String, Object> produtoDTO = new java.util.HashMap<>();
                    produtoDTO.put("nome", item.getProduto().getNome());
                    produtoDTO.put("precoUnitario", item.getProduto().getPrecoBase());
                    produtoDTO.put("imagem_url", item.getProduto().getImagemUrl());
                    produtoDTO.put("id", item.getProduto().getId());
                    itemDTO.put("produto", produtoDTO);
                    itensDTO.add(itemDTO);
                }
            }
            pedidoDTO.put("itens", itensDTO);
            return ResponseEntity.ok(pedidoDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Não autorizado"));
    }
} 