package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entity.Produto;
import com.ecommerce.repository.ProdutoRepository;
import com.ecommerce.service.CarrinhoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String carrinho(HttpSession session, Model model) {
        model.addAttribute("itens", carrinhoService.getItens(session));
        model.addAttribute("total", carrinhoService.getTotal(session));
        return "carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionarItem(@RequestParam Long produtoId,
                               @RequestParam(defaultValue = "1") Integer quantidade,
                               @RequestParam(required = false) String tamanho,
                               @RequestParam(required = false) String cor,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Produto produto = produtoRepository.findById(produtoId).orElse(null);
        if (produto == null) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado!");
            return "redirect:/produtos";
        }
        
        if (produto.getEstoqueAtual() < quantidade) {
            redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente!");
            return "redirect:/produtos";
        }
        
        carrinhoService.adicionarItem(session, produto, quantidade, tamanho, cor);
        redirectAttributes.addFlashAttribute("sucesso", "Produto adicionado ao carrinho!");
        return "redirect:/carrinho";
    }

    @PostMapping("/remover")
    public String removerItem(@RequestParam Long produtoId, HttpSession session) {
        carrinhoService.removerItem(session, produtoId);
        return "redirect:/carrinho";
    }

    @PostMapping("/atualizar")
    public String atualizarQuantidade(@RequestParam Long produtoId,
                                     @RequestParam Integer quantidade,
                                     HttpSession session) {
        carrinhoService.atualizarQuantidade(session, produtoId, quantidade);
        return "redirect:/carrinho";
    }

    @PostMapping("/limpar")
    public String limparCarrinho(HttpSession session) {
        carrinhoService.limparCarrinho(session);
        return "redirect:/carrinho";
    }
}