package com.ecommerce.util;

/**
 * Classe legada mantida apenas por compatibilidade com chamadas antigas.
 * O schema é gerenciado pelo Hibernate/JPA (spring.jpa.hibernate.ddl-auto=update).
 */
@Deprecated
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void criarTodasTabelas() {
        System.out.println("DatabaseInitializer desabilitado: schema gerenciado por Hibernate/JPA.");
    }
}
