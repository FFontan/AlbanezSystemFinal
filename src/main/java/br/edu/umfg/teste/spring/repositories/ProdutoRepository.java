package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Produto findByid(int id);

    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);

    @Query("SELECT p FROM Produto p WHERE p.id = :id OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Produto> findByIdOrDescricaoContainingIgnoreCase(@Param("id") Long id, @Param("descricao") String descricao);

}

