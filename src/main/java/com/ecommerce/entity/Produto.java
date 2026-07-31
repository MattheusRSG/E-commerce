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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "produto")
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, length = 1000)
    private String descricao;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;
    
    @Min(value = 0, message = "Estoque atual não pode ser negativo")
    @Column(name = "estoque_atual", nullable = false)
    private Integer estoqueAtual = 0;
    
    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
    
    // Campos adicionais para melhor experiência
    @Column(length = 500)
    private String nome;
    
    @Column(name = "url_imagem", length = 1000)
    private String urlImagem;
    
    @Column(length = 500)
    private String tamanhos; // P,M,G,GG
    
    @Column(length = 500)
    private String cores; // Preto,Branco,Rosa
    
    // Constructors
    public Produto() {}
    
    public Produto(String nome, String descricao, BigDecimal precoUnitario, Integer estoqueAtual, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.estoqueAtual = estoqueAtual;
        this.categoria = categoria;
    }
    
    // Getters and Setters
    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    
    public Integer getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(Integer estoqueAtual) { this.estoqueAtual = estoqueAtual; }
    
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }
    
    public String getTamanhos() { return tamanhos; }
    public void setTamanhos(String tamanhos) { this.tamanhos = tamanhos; }
    
    public String getCores() { return cores; }
    public void setCores(String cores) { this.cores = cores; }
    
    public boolean isEstoqueBaixo() {
        return estoqueAtual <= estoqueMinimo;
    }
    
    public boolean isDisponivel() {
        return estoqueAtual > 0;
    }
}