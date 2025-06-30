package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByClienteNomeContainingIgnoreCase(String nome);

    @Query("SELECT MONTH(v.dataVenda), SUM(v.total) FROM Venda v WHERE YEAR(v.dataVenda) = :ano GROUP BY MONTH(v.dataVenda) ORDER BY MONTH(v.dataVenda)")
    List<Object[]> totalVendasPorMes(@Param("ano") int ano);

    @Query("SELECT MONTH(v.dataVenda), COUNT(v) FROM Venda v WHERE YEAR(v.dataVenda) = :ano GROUP BY MONTH(v.dataVenda) ORDER BY MONTH(v.dataVenda)")
    List<Object[]> numeroVendasPorMes(@Param("ano") int ano);
}
