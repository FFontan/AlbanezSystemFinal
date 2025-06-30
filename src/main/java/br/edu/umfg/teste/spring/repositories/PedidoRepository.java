package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Pedido findByid(Long id);

    @Query("SELECT p FROM Pedido p WHERE " +
            "CAST(p.id AS string) LIKE %:filtro% OR " +
            "LOWER(p.fornecedor.nomeFantasia) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Pedido> buscarPorIdOuFornecedor(@Param("filtro") String filtro);

}

