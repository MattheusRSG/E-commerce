package com.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entity.Pedido;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.ItemPedidoRepository;
import com.ecommerce.repository.PagamentoRepository;
import com.ecommerce.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;

    @GetMapping
    public String pedidos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            return "redirect:/login?redirect=/pedidos";
        }
        
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByDataPedidoDesc(usuario);
        model.addAttribute("pedidos", pedidos);
        
        return "pedidos";
    }
    
    
    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id, 
                                 @RequestParam String status,
                                 RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setStatusPedido(Pedido.StatusPedido.valueOf(status));
            pedidoRepository.save(pedido);
            redirectAttributes.addFlashAttribute("sucesso", "Status atualizado!");
        }
        return "redirect:/admin/pedidos";
    }
}