package com.ecommerce.controller;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CarrinhoService carrinhoService;

    @GetMapping
    public String checkout(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            return "redirect:/login?redirect=/checkout";
        }
        
        if (carrinhoService.getItens(session).isEmpty()) {
            return "redirect:/carrinho";
        }
        
        model.addAttribute("itens", carrinhoService.getItens(session));
        model.addAttribute("total", carrinhoService.getTotal(session));
        model.addAttribute("usuario", usuario);
        
        return "checkout";
    }

    @GetMapping("/frete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calcularFretePorCep(@RequestParam String cep,
                                                                   @RequestParam(required = false) String uf) {
        String cepLimpo = cep == null ? "" : cep.replaceAll("\\D", "");
        if (!cepLimpo.matches("\\d{8}")) {
            return ResponseEntity.badRequest().body(Map.of("erro", "CEP inválido."));
        }

        String ufResolvida = isBlank(uf) ? inferirUfPorCep(cepLimpo) : uf.trim().toUpperCase();
        BigDecimal valor = calcularFretePorUf(ufResolvida);

        return ResponseEntity.ok(Map.of(
            "cep", formatarCep(cepLimpo),
            "uf", ufResolvida,
            "valor", valor,
            "prazoDias", calcularPrazoEntrega(ufResolvida)
        ));
    }

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PostMapping("/finalizar")
    @Transactional
    public String finalizarPedido(@RequestParam String cep,
                                 @RequestParam String endereco,
                                 @RequestParam String cidade,
                                 @RequestParam String uf,
                                 @RequestParam(required = false) String complemento,
                                 @RequestParam(required = false) String tipoPagamento,
                                 @RequestParam(required = false) String numeroCartao,
                                 @RequestParam(required = false) String parcelas,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            return "redirect:/login?redirect=/checkout";
        }
        
        List<CarrinhoService.ItemCarrinho> itens = carrinhoService.getItens(session);
        if (itens.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Carrinho vazio!");
            return "redirect:/carrinho";
        }

        if (tipoPagamento == null || tipoPagamento.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Selecione uma forma de pagamento!");
            return "redirect:/checkout";
        }

        String tipoPagamentoNormalizado = tipoPagamento.trim().toUpperCase();
        Pagamento.TipoPagamento tipoPagamentoEnum;
        String numeroCartaoLimpo = null;

        try {
            tipoPagamentoEnum = Pagamento.TipoPagamento.valueOf(tipoPagamentoNormalizado);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Forma de pagamento inválida!");
            return "redirect:/checkout";
        }

        if (tipoPagamentoEnum == Pagamento.TipoPagamento.CARTAO) {
            numeroCartaoLimpo = numeroCartao == null ? "" : numeroCartao.replaceAll("\\D", "");
            if (numeroCartaoLimpo.length() < 4) {
                redirectAttributes.addFlashAttribute("erro", "Número do cartão inválido!");
                return "redirect:/checkout";
            }

            if (isCartaoSandboxRecusado(numeroCartaoLimpo)) {
                redirectAttributes.addFlashAttribute("erro", "Pagamento recusado pelo sandbox. Use outro cartão ou forma de pagamento.");
                return "redirect:/checkout";
            }
        }
        
        try {
            Usuario usuarioAtualizado = usuarioRepository.findById(usuario.getIdUsuario()).orElse(usuario);
            atualizarEnderecoUsuario(usuarioAtualizado, cep, endereco, cidade, uf, complemento);
            usuarioAtualizado = usuarioRepository.save(usuarioAtualizado);
            session.setAttribute("usuario", usuarioAtualizado);

            for (CarrinhoService.ItemCarrinho item : itens) {
                Produto produtoAtual = produtoRepository.findById(item.getProduto().getIdProduto()).orElse(null);
                if (produtoAtual == null) {
                    redirectAttributes.addFlashAttribute("erro", "Produto não encontrado: " + item.getProduto().getNome());
                    return "redirect:/carrinho";
                }

                if (produtoAtual.getEstoqueAtual() < item.getQuantidade()) {
                    redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente para: " + produtoAtual.getNome());
                    return "redirect:/carrinho";
                }

                item.setProduto(produtoAtual);
            }

            // Criar pedido
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuarioAtualizado);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setEnderecoEntrega(endereco + ", " + cidade + " - " + uf + " CEP: " + cep);
            pedido.setStatusPedido(Pedido.StatusPedido.AGUARDANDO);
            
            BigDecimal total = carrinhoService.getTotal(session);
            BigDecimal frete = calcularFretePorUf(uf);
            BigDecimal desconto = "PIX".equals(tipoPagamentoNormalizado) ? total.multiply(new BigDecimal("0.05")) : BigDecimal.ZERO;
            
            pedido.setValorFrete(frete);
            pedido.setValorTotal(total.add(frete).subtract(desconto));
            
            pedido = pedidoRepository.save(pedido);
            
            // Criar itens do pedido
            for (CarrinhoService.ItemCarrinho item : itens) {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPedido(pedido);
                itemPedido.setProduto(item.getProduto());
                itemPedido.setQuantidade(item.getQuantidade());
                itemPedido.setPrecoUnitario(item.getProduto().getPrecoUnitario());
                itemPedido.setValorTotal(item.getSubtotal());
                itemPedido.setTamanho(item.getTamanho());
                itemPedido.setCor(item.getCor());
                
                itemPedidoRepository.save(itemPedido);

                Produto produto = item.getProduto();
                produto.setEstoqueAtual(produto.getEstoqueAtual() - item.getQuantidade());
                produtoRepository.save(produto);
            }
            
            // Criar pagamento
            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setTipoPagamento(tipoPagamentoEnum);
            pagamento.setValor(pedido.getValorTotal());
            pagamento.setStatusPagamento(Pagamento.StatusPagamento.PENDENTE);
            
            if (tipoPagamentoEnum == Pagamento.TipoPagamento.CARTAO) {
                pagamento.setCartaoUltimosDigitos(numeroCartaoLimpo.substring(numeroCartaoLimpo.length() - 4));
                pagamento.setStatusPagamento(Pagamento.StatusPagamento.APROVADO);
                pagamento.setDataPagamento(LocalDateTime.now());
            }
            
            pagamentoRepository.save(pagamento);
            
            carrinhoService.limparCarrinho(session);
            
            return "redirect:/pedidos/" + pedido.getIdPedido() + "/acompanhar";
            
        } catch (Exception e) {
            org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            redirectAttributes.addFlashAttribute("erro", "Erro ao finalizar pedido: " + e.getMessage());
            return "redirect:/carrinho";
        }
    }
    
    private BigDecimal calcularFretePorUf(String uf) {
        return switch (uf == null ? "" : uf.trim().toUpperCase()) {
            case "SP" -> new BigDecimal("15.00");
            case "RJ" -> new BigDecimal("18.00");
            case "MG" -> new BigDecimal("20.00");
            case "RS" -> new BigDecimal("25.00");
            case "PR" -> new BigDecimal("22.00");
            case "SC" -> new BigDecimal("24.00");
            default -> new BigDecimal("30.00");
        };
    }

    private int calcularPrazoEntrega(String uf) {
        return switch (uf == null ? "" : uf.trim().toUpperCase()) {
            case "SP" -> 2;
            case "RJ", "MG" -> 3;
            case "PR", "SC", "RS" -> 5;
            default -> 7;
        };
    }

    private String inferirUfPorCep(String cepLimpo) {
        int prefixo = Integer.parseInt(cepLimpo.substring(0, 2));
        if (prefixo <= 19) {
            return "SP";
        }
        if (prefixo <= 28) {
            return "RJ";
        }
        if (prefixo <= 39) {
            return "MG";
        }
        if (prefixo >= 80 && prefixo <= 87) {
            return "PR";
        }
        if (prefixo >= 88 && prefixo <= 89) {
            return "SC";
        }
        if (prefixo >= 90) {
            return "RS";
        }
        return "OUTROS";
    }

    private String formatarCep(String cepLimpo) {
        return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
    }

    private boolean isCartaoSandboxRecusado(String numeroCartaoLimpo) {
        return "4000000000000002".equals(numeroCartaoLimpo);
    }

    private void atualizarEnderecoUsuario(Usuario usuario, String cep, String endereco, String cidade, String uf, String complemento) {
        usuario.setCep(limpar(cep));
        usuario.setLogradouro(limpar(endereco));
        usuario.setCidade(limpar(cidade));
        usuario.setEstado(limpar(uf));
        usuario.setComplemento(limpar(complemento));
    }

    private String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
