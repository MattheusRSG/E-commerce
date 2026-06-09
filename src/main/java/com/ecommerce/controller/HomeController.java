package com.ecommerce.controller;

import com.ecommerce.entity.Categoria;
import com.ecommerce.entity.Produto;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.ProdutoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<Produto> produtosDestaque = produtoRepository.findTop8ByOrderByIdProdutoDesc();
        
        model.addAttribute("categorias", categorias);
        model.addAttribute("produtos", produtosDestaque);
        return "home";
    }

    @GetMapping("/produtos")
    public String produtos(@RequestParam(required = false) Long categoria,
                          @RequestParam(required = false) String busca,
                          Model model, HttpSession session) {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<Produto> produtos;

        if (categoria != null) {
            produtos = produtoRepository.findByCategoria_IdCategoria(categoria);
        } else if (busca != null && !busca.trim().isEmpty()) {
            produtos = produtoRepository.findByNomeContainingIgnoreCase(busca);
        } else {
            produtos = produtoRepository.findAll();
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        boolean isAdmin = usuario != null && usuario.getTipoUsuario() == Usuario.TipoUsuario.ADMIN;
        
        model.addAttribute("categorias", categorias);
        model.addAttribute("produtos", produtos);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("busca", busca);
        model.addAttribute("isAdmin", isAdmin);
        return "produtos";
    }
}
