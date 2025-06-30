package br.edu.umfg.teste.spring.repositories;

import br.edu.umfg.teste.spring.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Pessoa findByid(Long id);
}

