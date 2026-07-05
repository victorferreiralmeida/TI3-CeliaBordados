package com.celiabordados;

import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "personalizacao")
@Component
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Personalizacao{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto tipoProduto;
    
    @Column(nullable = false)
    private String corProduto;
    
    @Column(nullable = false)
    private String textoBordado;

    @Column(nullable = false)
    private String fonteBordado;

    @Column(nullable = false)
    private String corBordado;

    @Column(nullable = false)
    private String observacao;
    
    @Transient
    private static final String ARQUIVO_CSV = "clientes.csv"; // Mantido para compatibilidade

    public Personalizacao() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(Produto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getCor() {
        return corProduto;
    }

    public void setCor(String cor) {
        this.corProduto = cor;
    }

    public String getTextoBordado() {
        return textoBordado;
    }

    public void setTextoBordado(String textoBordado) {
        this.textoBordado = textoBordado;
    }

    public String getFonteBordado() {
        return fonteBordado;
    }

    public void setFonteBordado(String fonteBordado) {
        this.fonteBordado = fonteBordado;
    }

    public String getCorBordado() {
        return corBordado;
    }

    public void setCorBordado(String corBordado) {
        this.corBordado = corBordado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
