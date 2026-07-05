package com.celiabordados.model;

public class PixPaymentRequest {
    private Double valor;
    private String email;
    private Long pedidoId;

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
} 