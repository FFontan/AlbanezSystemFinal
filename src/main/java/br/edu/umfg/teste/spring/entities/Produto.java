package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IMAGEM")
    private String imagem;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 100, message = "Descrição deve ter entre 3 e 100 caracteres")
    @Column(name = "DESCRICAO")
    private String descricao;

    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Max(value = 999999, message = "Quantidade não pode ser maior que 999999")
    @Column(name = "QUANTIDADE")
    private int quantidade;

    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de custo deve ser maior que zero")
    @DecimalMax(value = "999999.00", message = "Preço de custo inválido")
    @Column(name = "PRECO_CUSTO")
    private Double precoCusto = 0.0;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
    @DecimalMax(value = "999999.00", message = "Preço de venda inválido")
    @Column(name = "PRECO_VENDA")
    private Double precoVenda = 0.0;

    @Transient
    private double precoFinal;

    // Novo campo para o desconto
    @Column(name = "DESCONTO")
    private Double desconto = 0.0; // Inicializa como 0, sem desconto

    @AssertTrue(message = "O preço de venda não pode ser menor que o desconto")
    public boolean isPrecoValido() {
        if (precoVenda == null || desconto == null) {
            return true;
        }
        return precoVenda >= desconto;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(Double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public Double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public double getPrecoFinal() {
        if (desconto == null || desconto == 0.0) {
            return precoVenda;
        }
        return precoVenda - desconto;
    }
}
