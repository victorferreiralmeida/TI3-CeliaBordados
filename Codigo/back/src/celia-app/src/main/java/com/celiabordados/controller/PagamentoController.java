package com.celiabordados.controller;

import com.celiabordados.model.PixPaymentRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/pagamento")
public class PagamentoController {
    private final String MERCADO_PAGO_ACCESS_TOKEN = "TEST-5838009196574661-060220-d93f6b7972ab839cecc917b7144905b5-2472128017";
    private final String MERCADO_PAGO_API_URL = "https://api.mercadopago.com";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/criar-preferencia")
    public ResponseEntity<?> criarPreferenciaPagamento(@RequestBody PixPaymentRequest request) {
        try {
            String url = MERCADO_PAGO_API_URL + "/checkout/preferences";

            // Preparar lista de itens (simplificada por enquanto, com base na solicitação), idealmente deve vir dos itens do carrinho
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("title", "Compra em Célia Bordados");
            item.put("quantity", 1);
            item.put("unit_price", request.getValor());
            items.add(item);

            // Preparar o corpo da preferência de pagamento
            Map<String, Object> body = new HashMap<>();
            body.put("items", items);
            body.put("payer", Map.of("email", request.getEmail()));

            // Adicionar o ID do pedido como external_reference
            if (request.getPedidoId() != null) {
                body.put("external_reference", request.getPedidoId().toString());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(MERCADO_PAGO_ACCESS_TOKEN);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode responseJson = objectMapper.readTree(response.getBody());

            Map<String, String> preferenceResponse = new HashMap<>();
            preferenceResponse.put("preference_id", responseJson.path("id").asText());
            preferenceResponse.put("init_point", responseJson.path("init_point").asText()); // URL to redirect

            return ResponseEntity.ok(preferenceResponse);

        } catch (HttpClientErrorException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro na comunicação com Mercado Pago ao criar preferência");
            error.put("details", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao criar preferência de pagamento");
            error.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
} 