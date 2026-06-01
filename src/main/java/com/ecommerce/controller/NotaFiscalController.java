package com.ecommerce.controller;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import com.ecommerce.util.PDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
@RequestMapping("/nota-fiscal")
public class NotaFiscalController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/gerar/{pedidoId}")
    public void gerarNotaFiscal(@PathVariable Long pedidoId, HttpSession session, HttpServletResponse response) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            
            if (usuario == null) {
                response.sendRedirect("/login");
                return;
            }

            Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
            
            if (pedido == null) {
                response.sendError(404, "Pedido não encontrado");
                return;
            }

            // Verificar se o usuário pode acessar este pedido
            if (!usuario.getTipoUsuario().equals(Usuario.TipoUsuario.ADMIN) && 
                !pedido.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                response.sendError(403, "Acesso negado");
                return;
            }

            // Converter Pedido para Order (compatibilidade com PDFGenerator)
            Order order = convertPedidoToOrder(pedido);
            
            File pdfFile = PDFGenerator.generateInvoice(order);
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Nota_Fiscal_Pedido_" + pedidoId + ".pdf\"");
            
            FileInputStream fis = new FileInputStream(pdfFile);
            OutputStream os = response.getOutputStream();
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            
            fis.close();
            os.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(500, "Erro ao gerar nota fiscal: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Order convertPedidoToOrder(Pedido pedido) {
        Order order = new Order();
        order.setId(pedido.getIdPedido());
        order.setOrderDate(pedido.getDataPedido());
        order.setTotal(pedido.getValorTotal());
        order.setPaymentMethod(pedido.getPagamento() != null ? pedido.getPagamento().getTipoPagamento().toString() : "Não informado");
        order.setDeliveryAddress(pedido.getUsuario().getLogradouro() + ", " + pedido.getUsuario().getNumero() + " - " + pedido.getUsuario().getCidade());
        
        // Criar usuário simplificado
        Order.SimpleUser user = new Order.SimpleUser();
        user.setNomeCompleto(pedido.getUsuario().getNome());
        user.setCpf(pedido.getUsuario().getCpf());
        user.setTelefone(pedido.getUsuario().getTelefone());
        user.setLogradouro(pedido.getUsuario().getLogradouro());
        user.setNumero(pedido.getUsuario().getNumero());
        user.setComplemento(pedido.getUsuario().getComplemento());
        user.setCidade(pedido.getUsuario().getCidade());
        user.setEstado(pedido.getUsuario().getEstado());
        user.setCep(pedido.getUsuario().getCep());
        order.setUser(user);
        
        // Converter itens
        java.util.List<OrderItem> items = new java.util.ArrayList<>();
        for (ItemPedido item : pedido.getItens()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(item.getQuantidade());
            orderItem.setPrice(item.getPrecoUnitario());
            orderItem.setSubtotal(item.getPrecoUnitario().multiply(java.math.BigDecimal.valueOf(item.getQuantidade())));
            
            // Criar produto simplificado
            OrderItem.SimpleProduct product = new OrderItem.SimpleProduct();
            product.setNome(item.getProduto().getNome());
            orderItem.setProduct(product);
            
            items.add(orderItem);
        }
        order.setItems(items);
        
        return order;
    }
}
