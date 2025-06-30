package br.edu.umfg.teste.spring.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class VendaForm {
    @NotNull(message = "Cliente deve ser selecionado")
    private Long clienteId;

    @NotNull
    private List<@Valid ItemForm> itens = new ArrayList<>();

    public static class ItemForm {
        @NotNull(message = "Produto deve ser selecionado")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        private Integer quantidade;

        public Long getProdutoId() {
            return produtoId;
        }

        public void setProdutoId(Long produtoId) {
            this.produtoId = produtoId;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemForm> getItens() {
        return itens;
    }

    public void setItens(List<ItemForm> itens) {
        this.itens = itens;
    }
}


