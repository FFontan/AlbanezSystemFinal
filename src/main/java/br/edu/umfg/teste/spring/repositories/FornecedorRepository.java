package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Fornecedor;
import br.edu.umfg.teste.spring.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Fornecedor findByid(Long id);

    List<Fornecedor> findByNomeFantasiaContainingIgnoreCaseOrDocumentoContainingIgnoreCase(
            String nomeFantasia,
            String documento
    );
}



