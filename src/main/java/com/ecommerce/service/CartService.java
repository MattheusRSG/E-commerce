package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@Deprecated
public class CartService {
    
    @Autowired
    private ProductService productService;
    
    private static final String CART_SESSION_KEY = "cart";
    
    @SuppressWarnings("unchecked")
    public List<CartItem> getCartItems(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
    
    public void addToCart(HttpSession session, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId);
        if (product == null || product.getEstoque() < quantity) {
            return;
        }
        
        List<CartItem> cart = getCartItems(session);
        
        // Verificar se o produto já está no carrinho
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity <= product.getEstoque()) {
                item.setQuantity(newQuantity);
            }
        } else {
            cart.add(new CartItem(product, quantity));
        }
    }
    
    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCartItems(session);
        cart.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    
    public void updateQuantity(HttpSession session, Long productId, Integer quantity) {
        List<CartItem> cart = getCartItems(session);
        
        if (quantity <= 0) {
            removeFromCart(session, productId);
            return;
        }
        
        cart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    Product product = item.getProduct();
                    if (quantity <= product.getEstoque()) {
                        item.setQuantity(quantity);
                    }
                });
    }
    
    public BigDecimal getCartTotal(HttpSession session) {
        return getCartItems(session).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getCartItemCount(HttpSession session) {
        return getCartItems(session).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
