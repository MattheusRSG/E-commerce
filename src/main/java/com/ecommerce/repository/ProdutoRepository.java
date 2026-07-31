package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    List<Produto> findByCategoria_IdCategoria(Long categoriaId);
    
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    @Query("SELECT p FROM Produto p WHERE p.nome LIKE %:busca% OR p.descricao LIKE %:busca%")
    List<Produto> buscarPorTexto(@Param("busca") String busca);
    
    List<Produto> findTop8ByOrderByIdProdutoDesc();
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueAtual > 0")
    List<Produto> findProdutosComEstoque();
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueAtual <= p.estoqueMinimo")
    List<Produto> findProdutosEstoqueBaixo();
}