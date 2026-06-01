package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    /**
     * Compatibilidade com rotas legadas em inglês.
     * Redireciona para os endpoints atuais em português.
     */
    @GetMapping
    public String listProducts(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return "redirect:/produtos?busca=" + search.trim();
        }
        return "redirect:/produtos";
    }
    
    @PostMapping("/add-to-cart")
    public String addToCart() {
        return "redirect:/produtos";
    }
    
    @GetMapping("/admin")
    public String adminProducts() {
        return "redirect:/admin/produtos";
    }
    
    @GetMapping("/admin/new")
    public String newProductForm() {
        return "redirect:/admin/produtos/novo";
    }
    
    @PostMapping("/admin/save")
    public String saveProduct() {
        return "redirect:/admin/produtos";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String editProductForm(@PathVariable Long id) {
        return "redirect:/admin/produtos/editar/" + id;
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return "redirect:/admin/produtos";
    }
}
