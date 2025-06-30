package br.edu.umfg.teste.spring.entities;

import br.edu.umfg.teste.spring.validators.DocumentoValido;
import br.edu.umfg.teste.spring.validators.TelefoneValido;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table (name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
public class Pessoa{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Valid
    @ManyToOne(optional = false)
    @JoinColumn(name = "ENDERECO_ID", referencedColumnName = "ID")
    private Endereco endereco = new Endereco();

    @NotBlank(message = "O documento é obrigatório")
    @DocumentoValido
    @Column(name = "DOCUMENTO", nullable = false)
    private String documento;

    @NotBlank(message = "O telefone é obrigatório")
    @TelefoneValido
    @Column(name = "FONE", nullable = true)
    private String telefone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
       this.endereco = endereco;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @PrePersist
    @PreUpdate
    private void limparDocumento() {
        if (this.documento != null) {
            this.documento = this.documento.replaceAll("[^\\d]", "");
        }
    }
}
