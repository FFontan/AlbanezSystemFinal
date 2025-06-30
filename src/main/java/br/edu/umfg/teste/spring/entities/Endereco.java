package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "O logradouro é obrigatório")
    @Column(name = "LOGRADOURO", nullable = false)
    private String logradouro;

    @Min(value = 1, message = "O número do endereço deve ser maior que zero")
    @Column(name = "NUMERO_END", nullable = false)
    private int numeroEndereco;

    @NotBlank(message = "O bairro é obrigatório")
    @Column(name = "BAIRRO", nullable = false)
    private String bairro;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido (ex: 12345-678)")
    @Column(name = "CEP", nullable = false)
    private String cep;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(name = "CIDADE", nullable = false)
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Pattern(regexp = "^(?i)(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO)$",
            message = "UF inválida.")
    @Column(name = "UF", nullable = false)
    private String uf;

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public int getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(int numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        if (uf != null) {
            this.uf = uf.toUpperCase();
        } else {
            this.uf = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
