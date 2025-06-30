function exportarTabelaPDF(config) {
    const { titulo, colunas, seletorTabela } = config;
    const linhas = document.querySelectorAll(`${seletorTabela} tbody tr`);
    const dados = [];

    linhas.forEach(tr => {
        const linha = {};
        colunas.forEach(col => {
            const celula = tr.querySelector(`td[data-coluna="${col.nome}"]`);
            linha[col.nome] = celula ? celula.innerText.trim() : "";
        });
        dados.push(linha);
    });

    fetch("/exportar/pdf", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ titulo, colunas, dados })
    })
    .then(res => res.blob())
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "exportacao.pdf";
        a.click();
        window.URL.revokeObjectURL(url);
    });
}

function exportarTabelaProdutos() {
    exportarTabelaPDF({
        titulo: "Lista de Produtos",
        seletorTabela: "#tabela-produtos",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "descricao", label: "Descrição" },
            { nome: "precoCusto", label: "Preço de Custo" },
            { nome: "precoVenda", label: "Preço de Venda" },
            { nome: "precoFinal", label: "Preço com Desconto" },
            { nome: "quantidade", label: "Quantidade" }
        ]
    });
}

function exportarTabelaClientes() {
    exportarTabelaPDF({
        titulo: "Lista de Clientes",
        seletorTabela: "#tabela-clientes",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "documento", label: "CPF" },
            { nome: "nome", label: "Nome" },
            { nome: "telefone", label: "Fone" },
            { nome: "logradouro", label: "Logradouro" },
            { nome: "numeroEndereco", label: "Numero" },
            { nome: "bairro", label: "Bairro" },
            { nome: "cep", label: "CEP" },
            { nome: "cidade", label: "Cidade" },
            { nome: "uf", label: "UF" }
        ]
    });
}

function exportarTabelaFornecedores() {
    exportarTabelaPDF({
        titulo: "Lista de Fornecedores",
        seletorTabela: "#tabela-fornecedores",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "documento", label: "CNPJ" },
            { nome: "nomeFantasia", label: "Nome Fantasia" },
            { nome: "inscricaoEstadual", label: "Inscrição Estadual" },
            { nome: "razaoSocial", label: "Razão Social" },
            { nome: "telefone", label: "Telefone" },
            { nome: "logradouro", label: "Logradouro" },
            { nome: "numeroEndereco", label: "Número" },
            { nome: "bairro", label: "Bairro" },
            { nome: "cep", label: "CEP" },
            { nome: "cidade", label: "Cidade" },
            { nome: "uf", label: "UF" }
        ]
    });
}

function exportarTabelaFuncionarios() {
    exportarTabelaPDF({
        titulo: "Lista de Funcionários",
        seletorTabela: "#tabela-funcionarios",
        colunas: [
            { nome: "id",           label: "ID" },
            { nome: "documento",    label: "CPF" },
            { nome: "nome",         label: "Nome" },
            { nome: "telefone",     label: "Fone" },
            { nome: "dataNasc",     label: "Data de Nascimento" },
            { nome: "salario",      label: "Salário" },
            { nome: "logradouro",   label: "Logradouro" },
            { nome: "numeroEndereco", label: "Número" },
            { nome: "bairro",       label: "Bairro" },
            { nome: "cep",          label: "CEP" },
            { nome: "cidade",       label: "Cidade" },
            { nome: "uf",           label: "UF" }
        ]
    });
}

function exportarTabelaPedidos() {
    exportarTabelaPDF({
        titulo: "Lista de Pedidos",
        seletorTabela: "#tabela-pedidos",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "dataCadastro", label: "Data de Cadastro" },
            { nome: "fornecedor", label: "Fornecedor" },
            { nome: "valorTotal", label: "Valor Total" }
        ]
    });
}

function exportarTabelaPromocoes() {
    exportarTabelaPDF({
        titulo: "Lista de Promoções",
        seletorTabela: "#tabela-promocoes",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "nome", label: "Nome" },
            { nome: "dataInicio", label: "Data Início" },
            { nome: "dataFim", label: "Data Fim" },
            { nome: "status", label: "Status" }
        ]
    });
}

function exportarTabelaVendas() {
    exportarTabelaPDF({
        titulo: "Lista de Vendas",
        seletorTabela: "#tabela-vendas",
        colunas: [
            { nome: "id", label: "ID" },
            { nome: "cliente", label: "Cliente" },
            { nome: "data", label: "Data" },
            { nome: "total", label: "Total" }
        ]
    });
}

