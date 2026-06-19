package com.ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entity.Pagamento;
import com.ecommerce.entity.Pedido;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.PagamentoRepository;
import com.ecommerce.repository.PedidoRepository;
import com.ecommerce.repository.ProdutoRepository;
import com.ecommerce.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/dashboard", usuario);
            return "redirect:/login";
        }
        
        // Estatísticas do dashboard
        long totalProdutos = produtoRepository.count();
        long totalPedidos = pedidoRepository.count();
        
        // Calcular total de vendas
        List<Pedido> todosPedidos = pedidoRepository.findAll();
        double totalVendas = todosPedidos.stream()
            .filter(p -> p.getStatusPedido() != Pedido.StatusPedido.CANCELADO)
            .mapToDouble(p -> p.getValorTotal().doubleValue())
            .sum();
        
        // Pedidos recentes (últimos 10)
        List<Pedido> pedidosRecentes = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        
        model.addAttribute("totalProdutos", totalProdutos);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalVendas", totalVendas);
        model.addAttribute("pedidosRecentes", pedidosRecentes);
        
        return "admin-dashboard";
    }
    
    @GetMapping("/pedidos")
    public String adminPedidos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/pedidos", usuario);
            return "redirect:/login";
        }
        
        List<Pedido> pedidos = pedidoRepository.findAll();
        model.addAttribute("pedidos", pedidos);
        
        return "admin-pedidos";
    }
    
    @PostMapping("/pedidos/{id}/status")
    @Transactional
    @CacheEvict(value = "pedidos", allEntries = true)
    public String atualizarStatusPedido(@PathVariable Long id, 
                                       @RequestParam String status,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        System.out.println("\n=== DEBUG ATUALIZAR STATUS ===");
        System.out.println("ID do Pedido: " + id);
        System.out.println("Novo Status: " + status);
        System.out.println("Usuário: " + (usuario != null ? usuario.getNome() : "NULL"));
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            System.out.println("ERRO: Acesso negado!");
            registrarAcessoNegado("/admin/pedidos/" + id + "/status", usuario);
            redirectAttributes.addFlashAttribute("erro", "Acesso negado!");
            return "redirect:/login";
        }
        
        try {
            // Verificar se pedido existe
            if (!pedidoRepository.existsById(id)) {
                System.out.println("ERRO: Pedido #" + id + " não encontrado!");
                redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado!");
                return "redirect:/admin/pedidos";
            }
            
            Pedido.StatusPedido novoStatus = Pedido.StatusPedido.valueOf(status);
            System.out.println("Novo status convertido: " + novoStatus);
            
            // UPDATE direto no banco usando query customizada
            pedidoRepository.updateStatusPedido(id, novoStatus);
            System.out.println("Update executado no banco");
            
            // Flush para garantir commit
            pedidoRepository.flush();
            System.out.println("Flush executado");
            
            // Verificar se realmente foi atualizado
            Pedido pedidoVerificacao = pedidoRepository.findById(id).orElse(null);
            if (pedidoVerificacao != null) {
                System.out.println("Verificação no banco - Status: " + pedidoVerificacao.getStatusPedido());
            }
            
            System.out.println("✓ SUCCESS: Pedido #" + id + " atualizado para " + novoStatus.getDescricao());
            redirectAttributes.addFlashAttribute("sucesso", "Status do pedido #" + id + " atualizado para: " + novoStatus.getDescricao());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ ERRO: Status inválido: " + status);
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Status inválido: " + status);
        } catch (Exception e) {
            System.out.println("✗ ERRO: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status: " + e.getMessage());
        }
        
        System.out.println("=== FIM DEBUG ===\n");
        return "redirect:/admin/pedidos";
    }
    
    @PostMapping("/pagamentos/{id}/status")
    public String atualizarStatusPagamento(@PathVariable Long id, 
                                          @RequestParam String status,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null || usuario.getTipoUsuario() != Usuario.TipoUsuario.ADMIN) {
            registrarAcessoNegado("/admin/pagamentos/" + id + "/status", usuario);
            redirectAttributes.addFlashAttribute("erro", "Acesso negado!");
            return "redirect:/login";
        }
        
        try {
            Pagamento pagamento = pagamentoRepository.findById(id).orElse(null);
            if (pagamento != null) {
                pagamento.setStatusPagamento(Pagamento.StatusPagamento.valueOf(status));
                pagamentoRepository.save(pagamento);
                redirectAttributes.addFlashAttribute("sucesso", "Status do pagamento atualizado!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Pagamento não encontrado!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status do pagamento: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos";
    }

    private void registrarAcessoNegado(String rota, Usuario usuario) {
        String login = usuario == null ? "anonimo" : usuario.getLogin();
        log.warn("Acesso administrativo negado: rota={} usuario={}", rota, login);
    }
}
