package com.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.entity.ItemPedido;
import com.ecommerce.entity.Pedido;
import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.ItemPedidoRepository;
import com.ecommerce.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AcompanharPedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @GetMapping("/pedidos/{id}/acompanhar")
    public String acompanharPedido(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        
        if (pedido == null) {
            return "redirect:/pedidos";
        }

        // Verificar se o usuário pode acessar este pedido
        if (!usuario.getTipoUsuario().equals(Usuario.TipoUsuario.ADMIN) && 
            !pedido.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            return "redirect:/pedidos";
        }

        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itens);
        model.addAttribute("pagamento", pedido.getPagamento());
        model.addAttribute("isAdmin", usuario.getTipoUsuario().equals(Usuario.TipoUsuario.ADMIN));
        
        return "acompanhar-pedido";
    }
}