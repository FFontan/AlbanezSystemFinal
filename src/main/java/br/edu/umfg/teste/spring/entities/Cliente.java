package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Cliente extends Pessoa{

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "NOME", nullable = false)
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
