package br.edu.umfg.teste.spring.entities;

import br.edu.umfg.teste.spring.validators.InscricaoEstadualValid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Fornecedor extends Pessoa{

    @NotBlank(message = "Nome Fantasia é obrigatório")
    @Column(name = "NOME_FANTASIA", nullable = false)
    private String nomeFantasia;

    @InscricaoEstadualValid
    @Column(name = "INSCRICAO_ESTADUAL", nullable = false)
    private String inscricaoEstadual;

    @NotBlank(message = "Razão Social é obrigatória")
    @Column(name = "RAZAO_SOCIAL", nullable = false)
    private String razaoSocial;

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

}
