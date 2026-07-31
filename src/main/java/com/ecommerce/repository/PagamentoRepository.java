package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.Pagamento;
import com.ecommerce.entity.Pedido;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPedido(Pedido pedido);
}