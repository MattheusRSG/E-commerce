package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principal da aplicação E-Commerce Spring Boot
 * Responsável pela inicialização do sistema web
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
public class ECommerceApp {
    
    public static void main(String[] args) {
        SpringApplication.run(ECommerceApp.class, args);
        System.out.println("\n=== Estilo Feminino - E-commerce Iniciado ===");
        System.out.println("Acesse: http://localhost:8080");
        System.out.println("============================================\n");
    }
}