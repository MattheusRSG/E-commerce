package com.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entity.Categoria;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.CategoriaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/categorias")
public class CategoryManagementController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listarCategorias(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            return "redirect:/login";
        }
        
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        
        return "admin-categorias";
    }

    @PostMapping("/excluir/{id}")
    public String excluirCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Categoria excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir categoria: " + e.getMessage());
        }
        return "redirect:/admin/categorias";
    }
}