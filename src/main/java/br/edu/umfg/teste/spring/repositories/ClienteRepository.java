package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findBydocumento(String documento);

    List<Cliente> findByNomeContainingIgnoreCaseOrId(String nome, Long id);

}

