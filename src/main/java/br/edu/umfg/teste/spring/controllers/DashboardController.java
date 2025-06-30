package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Promocao;
import br.edu.umfg.teste.spring.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping("/contadores")
    public Map<String, Object> getContadores() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("totalProdutos", produtoRepository.count());
        dados.put("promocoesAtivas", promocaoRepository.countByStatus(Promocao.StatusPromocao.ATIVO));
        dados.put("totalClientes", clienteRepository.count());
        dados.put("totalFornecedores", fornecedorRepository.count());
        dados.put("totalFuncionarios", funcionarioRepository.count());
        return dados;
    }
}
