package com.ecommerce.util;

import com.ecommerce.entity.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Gerenciador de sessão singleton para controle de usuários logados
 * e conexões com banco de dados
 */
public final class SessionManager {
    
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
    private static final String URL = System.getenv().getOrDefault(
        "SUPABASE_DB_URL",
        "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0"
    );
    private static final String USER = System.getenv().getOrDefault(
        "SUPABASE_DB_USER",
        "postgres.uqkzrlgtqvffxwcnfazy"
    );
    private static final String PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");
    private static final String DRIVER = "org.postgresql.Driver";
    
    private static volatile SessionManager instance;
    private static Usuario currentUser;
    
    private SessionManager() {
        // Singleton - construtor privado
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }
    
    public Usuario getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(Usuario user) {
        currentUser = user;
        LOGGER.info("Usuário logado: " + (user != null ? user.getLogin() : "null"));
    }
    
    public void login(Usuario user) {
        setCurrentUser(user);
    }
    
    public void logout() {
        LOGGER.info("Logout do usuário: " + (currentUser != null ? currentUser.getLogin() : "null"));
        currentUser = null;
    }
    
    public boolean isAdmin() {
        return currentUser != null && currentUser.getTipoUsuario() == Usuario.TipoUsuario.ADMIN;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado", e);
        }
    }
    
    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            LOGGER.info("Conexão com PostgreSQL estabelecida com sucesso");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erro na conexão com PostgreSQL", e);
            return false;
        }
    }
}
