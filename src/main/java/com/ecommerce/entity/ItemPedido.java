package com.ecommerce.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_pedido")
    private Long idItemPedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;
    
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    @Column(nullable = false)
    private Integer quantidade;
    
    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;
    
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    // Campos opcionais para variações
    private String tamanho;
    private String cor;
    
    // Constructors
    public ItemPedido() {}
    
    public ItemPedido(Pedido pedido, Produto produto, Integer quantidade, BigDecimal precoUnitario) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
    
    // Getters and Setters
    public Long getIdItemPedido() { return idItemPedido; }
    public void setIdItemPedido(Long idItemPedido) { this.idItemPedido = idItemPedido; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { 
        this.quantidade = quantidade;
        if (this.precoUnitario != null) {
            this.valorTotal = this.precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }
    
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { 
        this.precoUnitario = precoUnitario;
        if (this.quantidade != null) {
            this.valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }
    
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}