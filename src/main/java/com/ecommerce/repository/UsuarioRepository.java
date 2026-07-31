package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByLogin(String login);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByLogin(String login);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
}