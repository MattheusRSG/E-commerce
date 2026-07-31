package com.ecommerce.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long idPagamento;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    // Campos específicos para PIX
    @Column(name = "pix_tx_id")
    private String pixTxId;
    
    @Column(name = "pix_chave")
    private String pixChave;
    
    // Campos específicos para Boleto
    @Column(name = "boleto_codigo_barras")
    private String boletoCodigoBarras;
    
    @Column(name = "boleto_vencimento")
    private LocalDateTime boletoVencimento;
    
    // Campos específicos para Cartão
    @Column(name = "cartao_token")
    private String cartaoToken;
    
    @Column(name = "cartao_ultimos_digitos")
    private String cartaoUltimosDigitos;
    
    @Column(name = "cartao_bandeira")
    private String cartaoBandeira;
    
    public enum TipoPagamento {
        PIX("Pix"),
        BOLETO("Boleto"),
        CARTAO("Cartão de Crédito");
        
        private final String descricao;
        
        TipoPagamento(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    public enum StatusPagamento {
        PENDENTE("Pendente"),
        APROVADO("Aprovado"),
        REJEITADO("Rejeitado"),
        CANCELADO("Cancelado");
        
        private final String descricao;
        
        StatusPagamento(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    // Constructors
    public Pagamento() {}
    
    public Pagamento(Pedido pedido, TipoPagamento tipoPagamento, BigDecimal valor) {
        this.pedido = pedido;
        this.tipoPagamento = tipoPagamento;
        this.valor = valor;
    }
    
    // Getters and Setters
    public Long getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Long idPagamento) { this.idPagamento = idPagamento; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public TipoPagamento getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(TipoPagamento tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(StatusPagamento statusPagamento) { this.statusPagamento = statusPagamento; }
    
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public String getPixTxId() { return pixTxId; }
    public void setPixTxId(String pixTxId) { this.pixTxId = pixTxId; }
    
    public String getPixChave() { return pixChave; }
    public void setPixChave(String pixChave) { this.pixChave = pixChave; }
    
    public String getBoletoCodigoBarras() { return boletoCodigoBarras; }
    public void setBoletoCodigoBarras(String boletoCodigoBarras) { this.boletoCodigoBarras = boletoCodigoBarras; }
    
    public LocalDateTime getBoletoVencimento() { return boletoVencimento; }
    public void setBoletoVencimento(LocalDateTime boletoVencimento) { this.boletoVencimento = boletoVencimento; }
    
    public String getCartaoToken() { return cartaoToken; }
    public void setCartaoToken(String cartaoToken) { this.cartaoToken = cartaoToken; }
    
    public String getCartaoUltimosDigitos() { return cartaoUltimosDigitos; }
    public void setCartaoUltimosDigitos(String cartaoUltimosDigitos) { this.cartaoUltimosDigitos = cartaoUltimosDigitos; }
    
    public String getCartaoBandeira() { return cartaoBandeira; }
    public void setCartaoBandeira(String cartaoBandeira) { this.cartaoBandeira = cartaoBandeira; }
}