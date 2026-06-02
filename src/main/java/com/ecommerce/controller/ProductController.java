package com.ecommerce.controller;

import com.ecommerce.entity.Categoria;
import com.ecommerce.entity.Produto;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.ProdutoRepository;
import com.ecommerce.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CarrinhoService carrinhoService;

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
    public String addToCart(@RequestParam(name = "productId") Long productId,
                           @RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Produto produto = produtoRepository.findById(productId).orElse(null);
        if (produto == null) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/produtos";
        }

        if (quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("erro", "Quantidade inválida.");
            return "redirect:/produtos";
        }

        if (produto.getEstoqueAtual() < quantity) {
            redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente.");
            return "redirect:/produtos";
        }

        carrinhoService.adicionarItem(session, produto, quantity, null, null);
        redirectAttributes.addFlashAttribute("sucesso", "Produto adicionado ao carrinho.");
        return "redirect:/carrinho";
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
    public String saveProduct(@RequestParam(required = false) String nome,
                             @RequestParam(required = false) String descricao,
                             @RequestParam(required = false) BigDecimal preco,
                             @RequestParam(required = false) BigDecimal precoUnitario,
                             @RequestParam(required = false) Integer estoque,
                             @RequestParam(required = false) Integer estoqueAtual,
                             @RequestParam(required = false) Integer estoqueMinimo,
                             @RequestParam(required = false) Long categoriaId,
                             @RequestParam(required = false) String categoria,
                             @RequestParam(required = false) String urlImagem,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado.");
            return "redirect:/login";
        }

        BigDecimal valor = precoUnitario != null ? precoUnitario : preco;
        Integer quantidade = estoqueAtual != null ? estoqueAtual : estoque;

        if (isBlank(nome) || isBlank(descricao) || valor == null || quantidade == null) {
            redirectAttributes.addFlashAttribute("erro", "Preencha nome, descrição, preço e estoque do produto.");
            return "redirect:/admin/produtos/novo";
        }

        Categoria categoriaProduto = resolverCategoria(categoriaId, categoria);
        if (categoriaProduto == null) {
            redirectAttributes.addFlashAttribute("erro", "Categoria não encontrada.");
            return "redirect:/admin/produtos/novo";
        }

        Produto produto = new Produto();
        produto.setNome(nome.trim());
        produto.setDescricao(descricao.trim());
        produto.setPrecoUnitario(valor);
        produto.setEstoqueAtual(quantidade);
        produto.setEstoqueMinimo(estoqueMinimo != null ? estoqueMinimo : 0);
        produto.setCategoria(categoriaProduto);
        produto.setUrlImagem(limpar(urlImagem));

        produtoRepository.save(produto);
        redirectAttributes.addFlashAttribute("sucesso", "Produto salvo na tabela produto.");
        return "redirect:/admin/produtos";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String editProductForm(@PathVariable Long id) {
        return "redirect:/admin/produtos/editar/" + id;
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado.");
            return "redirect:/login";
        }

        try {
            if (!produtoRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                return "redirect:/admin/produtos";
            }

            produtoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído da tabela produto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir produto: " + e.getMessage());
        }
        return "redirect:/admin/produtos";
    }

    private Categoria resolverCategoria(Long categoriaId, String nomeCategoria) {
        if (categoriaId != null) {
            return categoriaRepository.findById(categoriaId).orElse(null);
        }

        String nome = isBlank(nomeCategoria) ? "Geral" : nomeCategoria.trim();
        return categoriaRepository.findByNome(nome)
            .orElseGet(() -> categoriaRepository.save(new Categoria(nome, "Categoria criada por rota legada")));
    }

    private static String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private static boolean isAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && usuario.getTipoUsuario() == Usuario.TipoUsuario.ADMIN;
    }
}
