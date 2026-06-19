package com.ecommerce.controller;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    private static final Logger log = LoggerFactory.getLogger(AdminProductController.class);

    @GetMapping("/test")
    public String test() {
        return "admin-test";
    }

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;



    @GetMapping("/produtos")
    public String produtos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos", usuario);
            return "redirect:/login";
        }
        
        List<Produto> produtos = produtoRepository.findAll();
        model.addAttribute("produtos", produtos);
        
        return "admin-produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos/novo", usuario);
            return "redirect:/login";
        }
        
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        model.addAttribute("produto", new Produto());
        
        return "admin-produto-form";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@RequestParam String nome,
                               @RequestParam String descricao,
                               @RequestParam BigDecimal precoUnitario,
                               @RequestParam Integer estoqueAtual,
                               @RequestParam Integer estoqueMinimo,
                               @RequestParam Long categoriaId,
                               @RequestParam(required = false) String tamanhos,
                               @RequestParam(required = false) String cores,
                               @RequestParam(required = false) String urlImagem,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos/salvar", usuario);
            redirectAttributes.addFlashAttribute("erro", "Acesso negado!");
            return "redirect:/login";
        }
        
        try {
            Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
            if (categoria == null) {
                redirectAttributes.addFlashAttribute("erro", "Categoria não encontrada!");
                return "redirect:/admin/produtos/novo";
            }
            
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPrecoUnitario(precoUnitario);
            produto.setEstoqueAtual(estoqueAtual);
            produto.setEstoqueMinimo(estoqueMinimo);
            produto.setCategoria(categoria);
            produto.setTamanhos(tamanhos);
            produto.setCores(cores);
            produto.setUrlImagem(urlImagem);
            
            produtoRepository.save(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar produto: " + e.getMessage());
        }
        
        return "redirect:/admin/produtos";
    }

    @GetMapping("/produtos/editar/{id}")
    public String editarProduto(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos/editar/" + id, usuario);
            return "redirect:/login";
        }
        
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) {
            return "redirect:/admin/produtos";
        }
        
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        model.addAttribute("produto", produto);
        
        return "admin-produto-form";
    }

    @PostMapping("/produtos/atualizar/{id}")
    public String atualizarProduto(@PathVariable Long id,
                                  @RequestParam String nome,
                                  @RequestParam String descricao,
                                  @RequestParam BigDecimal precoUnitario,
                                  @RequestParam Integer estoqueAtual,
                                  @RequestParam Integer estoqueMinimo,
                                  @RequestParam Long categoriaId,
                                  @RequestParam(required = false) String tamanhos,
                                  @RequestParam(required = false) String cores,
                                  @RequestParam(required = false) String urlImagem,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos/atualizar/" + id, usuario);
            redirectAttributes.addFlashAttribute("erro", "Acesso negado!");
            return "redirect:/login";
        }
        
        try {
            Produto produto = produtoRepository.findById(id).orElse(null);
            if (produto == null) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado!");
                return "redirect:/admin/produtos";
            }
            
            Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
            if (categoria == null) {
                redirectAttributes.addFlashAttribute("erro", "Categoria não encontrada!");
                return "redirect:/admin/produtos/editar/" + id;
            }
            
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPrecoUnitario(precoUnitario);
            produto.setEstoqueAtual(estoqueAtual);
            produto.setEstoqueMinimo(estoqueMinimo);
            produto.setCategoria(categoria);
            produto.setTamanhos(tamanhos);
            produto.setCores(cores);
            produto.setUrlImagem(urlImagem);
            
            produtoRepository.save(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar produto: " + e.getMessage());
        }
        
        return "redirect:/admin/produtos";
    }

    @PostMapping("/produtos/excluir/{id}")
    public String excluirProduto(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/produtos/excluir/" + id, usuario);
            redirectAttributes.addFlashAttribute("erro", "Acesso negado!");
            return "redirect:/login";
        }

        try {
            Produto produto = produtoRepository.findById(id).orElse(null);
            if (produto == null) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado!");
                return "redirect:/admin/produtos";
            }
            
            produtoRepository.delete(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir produto: " + e.getMessage());
        }
        
        return "redirect:/admin/produtos";
    }

    private void registrarAcessoNegado(String rota, Usuario usuario) {
        String login = usuario == null ? "anonimo" : usuario.getLogin();
        log.warn("Acesso administrativo negado: rota={} usuario={}", rota, login);
    }
}
