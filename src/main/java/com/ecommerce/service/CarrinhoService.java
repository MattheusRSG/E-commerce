package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Produto;

import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    public static final String CARRINHO_SESSION = "carrinho";

    public static class ItemCarrinho {
        private Produto produto;
        private Integer quantidade;
        private String tamanho;
        private String cor;
        private BigDecimal subtotal;

        public ItemCarrinho(Produto produto, Integer quantidade, String tamanho, String cor) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.tamanho = tamanho;
            this.cor = cor;
            this.subtotal = produto.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));
        }

        // Getters e Setters
        public Produto getProduto() { return produto; }
        public void setProduto(Produto produto) { this.produto = produto; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { 
            this.quantidade = quantidade;
            this.subtotal = produto.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));
        }
        public String getTamanho() { return tamanho; }
        public void setTamanho(String tamanho) { this.tamanho = tamanho; }
        public String getCor() { return cor; }
        public void setCor(String cor) { this.cor = cor; }
        public BigDecimal getSubtotal() { return subtotal; }
    }

    @SuppressWarnings("unchecked")
    private Map<Long, ItemCarrinho> getCarrinho(HttpSession session) {
        Map<Long, ItemCarrinho> carrinho = (Map<Long, ItemCarrinho>) session.getAttribute(CARRINHO_SESSION);
        if (carrinho == null) {
            carrinho = new HashMap<>();
            session.setAttribute(CARRINHO_SESSION, carrinho);
        }
        return carrinho;
    }

    public void adicionarItem(HttpSession session, Produto produto, Integer quantidade, String tamanho, String cor) {
        Map<Long, ItemCarrinho> carrinho = getCarrinho(session);
        
        if (carrinho.containsKey(produto.getIdProduto())) {
            ItemCarrinho item = carrinho.get(produto.getIdProduto());
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            carrinho.put(produto.getIdProduto(), new ItemCarrinho(produto, quantidade, tamanho, cor));
        }
    }

    public void removerItem(HttpSession session, Long produtoId) {
        Map<Long, ItemCarrinho> carrinho = getCarrinho(session);
        carrinho.remove(produtoId);
    }

    public void atualizarQuantidade(HttpSession session, Long produtoId, Integer quantidade) {
        Map<Long, ItemCarrinho> carrinho = getCarrinho(session);
        if (carrinho.containsKey(produtoId)) {
            if (quantidade <= 0) {
                carrinho.remove(produtoId);
            } else {
                carrinho.get(produtoId).setQuantidade(quantidade);
            }
        }
    }

    public List<ItemCarrinho> getItens(HttpSession session) {
        return new ArrayList<>(getCarrinho(session).values());
    }

    public BigDecimal getTotal(HttpSession session) {
        return getItens(session).stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getQuantidadeTotal(HttpSession session) {
        return getItens(session).stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
    }

    public void limparCarrinho(HttpSession session) {
        session.removeAttribute(CARRINHO_SESSION);
    }
}