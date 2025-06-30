package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class PromocaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // <<-- novo: referência de volta para Promocao
    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Alterar para 'optional = true'
    @JoinColumn(name = "PROMOCAO_ID", nullable = true)  // Permitir NULL no banco de dados
    private Promocao promocao;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "PRODUTO_ID",
            nullable = true,
            foreignKey = @ForeignKey(
                    name = "FK_promocao_produto_produto",
                    foreignKeyDefinition =
                            "FOREIGN KEY (PRODUTO_ID) REFERENCES produto(id) ON DELETE SET NULL"
            )
    )
    private Produto produto;

    @Column(nullable = false)
    private BigDecimal precoOriginal;

    @Column(nullable = false)
    private BigDecimal precoPromocional;

    @Column(nullable = false)
    private BigDecimal valorDesconto;

    // GETTERS / SETTERS

    public Long getId() {
        return id;
    }

    public Promocao getPromocao() {
        return promocao;
    }

    public void setPromocao(Promocao promocao) {
        this.promocao = promocao;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getPrecoOriginal() {
        return precoOriginal;
    }

    public void setPrecoOriginal(BigDecimal precoOriginal) {
        this.precoOriginal = precoOriginal;
    }

    public BigDecimal getPrecoPromocional() {
        return precoPromocional;
    }

    public void setPrecoPromocional(BigDecimal precoPromocional) {
        this.precoPromocional = precoPromocional;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }
}
