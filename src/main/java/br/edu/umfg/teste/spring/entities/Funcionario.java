package br.edu.umfg.teste.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Funcionario extends Pessoa{

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "NOME", nullable = false)
    private String nome;

    @NotNull(message = "O salário é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "O salário deve ser maior que zero")
    @DecimalMax(value = "999999.00", inclusive = true, message = "O salário não pode ultrapassar R$ 999.999,00")
    @Column(name = "SALARIO", nullable = false)
    private Double salario = 0.0;

    @Past(message = "A data de nascimento deve estar no passado")
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    @Column(name = "DATA_NASC", nullable = true)
    private LocalDate data_nasc;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public LocalDate getData_nasc() {
        return data_nasc;
    }

    public void setData_nasc(LocalDate data_nasc) {
        this.data_nasc = data_nasc;
    }
}
