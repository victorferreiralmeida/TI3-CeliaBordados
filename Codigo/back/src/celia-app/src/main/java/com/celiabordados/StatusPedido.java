package com.celiabordados;

public enum StatusPedido {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    CANCELADO("Cancelado"),
    PROCESSANDO("Processando"),
    ENVIADO("Enviado"),
    EM_PRODUCAO("Em Produção"),
    ENTREGUE("Entregue"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusPedido fromString(String text) {
        for (StatusPedido status : StatusPedido.values()) {
            if (status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + text);
    }
} 