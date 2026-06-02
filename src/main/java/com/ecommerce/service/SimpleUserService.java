package com.ecommerce.service;

import com.ecommerce.model.SimpleUser;
import java.util.*;

@Deprecated
public class SimpleUserService {
    
    private final Map<String, SimpleUser> users = new HashMap<>();
    
    public SimpleUserService() {
        // Criar usuários padrão
        createDefaultUsers();
    }
    
    private void createDefaultUsers() {
        // Admin user
        SimpleUser admin = new SimpleUser(
            "admin", 
            "admin123", 
            "Administrador do Sistema", 
            "admin@ecommerce.com", 
            SimpleUser.UserType.ADMIN
        );
        users.put("admin", admin);
        
        // Cliente user
        SimpleUser cliente = new SimpleUser(
            "cliente", 
            "123456", 
            "Cliente Teste", 
            "cliente@ecommerce.com", 
            SimpleUser.UserType.CLIENTE
        );
        users.put("cliente", cliente);
        
        System.out.println("✅ Usuário ADMIN criado: admin/admin123");
        System.out.println("✅ Usuário CLIENTE criado: cliente/123456");
    }
    
    public SimpleUser authenticate(String username, String password) {
        System.out.println("🔍 Tentativa de login: " + username + "/" + password);
        System.out.println("🔍 Usuários disponíveis: " + users.keySet());
        
        SimpleUser user = users.get(username);
        if (user != null) {
            System.out.println("🔍 Usuário encontrado: " + user.getUsername());
            System.out.println("🔍 Senha armazenada: " + user.getPassword());
            System.out.println("🔍 Senha fornecida: " + password);
            
            if (user.getPassword().equals(password)) {
                System.out.println("✅ Login bem-sucedido!");
                return user;
            } else {
                System.out.println("❌ Senha incorreta!");
            }
        } else {
            System.out.println("❌ Usuário não encontrado!");
        }
        return null;
    }
    
    public List<SimpleUser> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public int getTotalUsers() {
        return users.size();
    }
}
