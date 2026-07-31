package com.ecommerce.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pedido")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false)
    private StatusPedido statusPedido = StatusPedido.AGUARDANDO;
    
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPedido> itens;
    
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pagamento pagamento;
    
    // Campos de entrega
    private String enderecoEntrega;
    private BigDecimal valorFrete = BigDecimal.ZERO;
    
    public enum StatusPedido {
        AGUARDANDO("Aguardando Pagamento"),
        FATURADO("Faturado"),
        EM_SEPARACAO("Em Separação"),
        ENVIADO("Enviado"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado");
        
        private final String descricao;
        
        StatusPedido(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    // Constructors
    public Pedido() {
        this.dataPedido = LocalDateTime.now();
    }
    
    public Pedido(Usuario usuario, BigDecimal valorTotal) {
        this();
        this.usuario = usuario;
        this.valorTotal = valorTotal;
    }
    
    // Getters and Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    
    public StatusPedido getStatusPedido() { return statusPedido; }
    public void setStatusPedido(StatusPedido statusPedido) { this.statusPedido = statusPedido; }
    
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    
    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
    
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    
    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }
    
    public BigDecimal getValorTotalComFrete() {
        return valorTotal.add(valorFrete != null ? valorFrete : BigDecimal.ZERO);
    }
}