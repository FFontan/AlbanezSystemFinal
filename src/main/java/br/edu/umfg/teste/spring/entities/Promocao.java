package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Promocao {

    public enum StatusPromocao {
        ATIVO,
        ENCERRADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    private StatusPromocao status = StatusPromocao.ATIVO;

    @OneToMany(mappedBy = "promocao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromocaoProduto> produtos = new ArrayList<>();

    @Column
    private BigDecimal descontoPorcentagem;

    @Column
    private BigDecimal descontoValor;

    @PrePersist
    protected void onCreate() {
        this.dataInicio = LocalDateTime.now();
    }

    @PreUpdate
    public void verificarStatus() {
        if (dataFim.isBefore(LocalDateTime.now())) {
            this.status = StatusPromocao.ENCERRADO;
        }
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public LocalDateTime getDataInicio() { return dataInicio; }

    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }

    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusPromocao getStatus() { return status; }

    public void setStatus(StatusPromocao status) { this.status = status; }

    public List<PromocaoProduto> getProdutos() { return produtos; }

    public void setProdutos(List<PromocaoProduto> produtos) { this.produtos = produtos; }

    public BigDecimal getDescontoPorcentagem() { return descontoPorcentagem; }

    public void setDescontoPorcentagem(BigDecimal descontoPorcentagem) { this.descontoPorcentagem = descontoPorcentagem; }

    public BigDecimal getDescontoValor() { return descontoValor; }

    public void setDescontoValor(BigDecimal descontoValor) { this.descontoValor = descontoValor; }
}
