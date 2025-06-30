package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {
    Promocao findByid(Long id);

    List<Promocao> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Promocao p WHERE p.id = :id OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Promocao> findByIdOrNomeContainingIgnoreCase(@Param("id") Long id, @Param("nome") String nome);

    long countByStatus(Promocao.StatusPromocao status);
}


