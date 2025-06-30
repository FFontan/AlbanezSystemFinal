package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.entities.Promocao;
import br.edu.umfg.teste.spring.entities.Promocao.StatusPromocao;
import br.edu.umfg.teste.spring.entities.PromocaoProduto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromocaoProdutoRepository extends JpaRepository<PromocaoProduto, Long> {

    Optional<PromocaoProduto> findByPromocaoAndProduto(Promocao promocao, Produto produto);

    @Query("SELECT pp.produto.id FROM PromocaoProduto pp WHERE pp.promocao.status = :status")
    List<Long> findProdutoIdsByPromocaoStatus(@Param("status") StatusPromocao status);

    @Modifying
    @Transactional
    @Query("DELETE FROM PromocaoProduto pp WHERE pp.promocao.id = :promocaoId")
    void deleteByPromocaoId(@Param("promocaoId") Long promocaoId);
}
