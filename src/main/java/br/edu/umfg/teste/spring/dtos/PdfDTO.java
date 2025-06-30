package br.edu.umfg.teste.spring.dtos;

import java.util.List;
import java.util.Map;

public class PdfDTO {

    public static class ExportacaoPdfDTO {
        private String titulo;
        private List<ColunaDTO> colunas;
        private List<Map<String, String>> dados;

        // Getters e setters
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public List<ColunaDTO> getColunas() { return colunas; }
        public void setColunas(List<ColunaDTO> colunas) { this.colunas = colunas; }

        public List<Map<String, String>> getDados() { return dados; }
        public void setDados(List<Map<String, String>> dados) { this.dados = dados; }
    }

    public static class ColunaDTO {
        private String nome;
        private String label;

        // Getters e setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }
}
