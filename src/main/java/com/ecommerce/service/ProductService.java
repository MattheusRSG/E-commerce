package com.ecommerce.service;

import com.ecommerce.model.Product;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class ProductService {
    
    private final Map<Long, Product> products = new HashMap<>();
    private Long nextId = 1L;
    
    public ProductService() {
        createSampleProducts();
    }
    
    private void createSampleProducts() {
        addProduct(new Product(nextId++, "Smartphone Samsung Galaxy", "Smartphone com tela de 6.1 polegadas", new BigDecimal("1299.99"), 10, "Eletrônicos"));
        addProduct(new Product(nextId++, "Notebook Dell Inspiron", "Notebook com processador Intel i5", new BigDecimal("2499.99"), 5, "Eletrônicos"));
        addProduct(new Product(nextId++, "Camiseta Polo", "Camiseta polo masculina 100% algodão", new BigDecimal("79.90"), 25, "Roupas"));
        addProduct(new Product(nextId++, "Tênis Nike Air", "Tênis esportivo para corrida", new BigDecimal("299.99"), 15, "Calçados"));
        addProduct(new Product(nextId++, "Livro Java Programming", "Livro sobre programação Java", new BigDecimal("89.90"), 20, "Livros"));
        addProduct(new Product(nextId++, "Fone de Ouvido Bluetooth", "Fone sem fio com cancelamento de ruído", new BigDecimal("199.99"), 30, "Eletrônicos"));
        
        System.out.println("✅ " + products.size() + " produtos criados");
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public List<Product> getAvailableProducts() {
        return products.values().stream()
                .filter(p -> p.getEstoque() > 0)
                .collect(Collectors.toList());
    }
    
    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
                .filter(p -> p.getCategoria().equals(category) && p.getEstoque() > 0)
                .collect(Collectors.toList());
    }
    
    public List<Product> searchProducts(String search) {
        return products.values().stream()
                .filter(p -> p.getNome().toLowerCase().contains(search.toLowerCase()) && p.getEstoque() > 0)
                .collect(Collectors.toList());
    }
    
    public List<String> getAllCategories() {
        return products.values().stream()
                .map(Product::getCategoria)
                .distinct()
                .collect(Collectors.toList());
    }
    
    public Product getProductById(Long id) {
        return products.get(id);
    }
    
    public void addProduct(Product product) {
        if (product.getId() == null) {
            product.setId(nextId++);
        }
        products.put(product.getId(), product);
    }
    
    public void updateProduct(Product product) {
        products.put(product.getId(), product);
    }
    
    public void deleteProduct(Long id) {
        products.remove(id);
    }
    
    public boolean updateStock(Long productId, int quantity) {
        Product product = products.get(productId);
        if (product != null && product.getEstoque() >= quantity) {
            product.setEstoque(product.getEstoque() - quantity);
            return true;
        }
        return false;
    }
}
