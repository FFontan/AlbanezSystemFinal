package br.edu.umfg.teste.spring.services;

import br.edu.umfg.teste.spring.dtos.VendaForm;
import br.edu.umfg.teste.spring.entities.ItemVenda;
import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.entities.Venda;
import br.edu.umfg.teste.spring.repositories.ClienteRepository;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import br.edu.umfg.teste.spring.repositories.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ProdutoRepository produtoRepo;

    @Autowired
    private VendaRepository vendaRepo;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VendaService.class);

    @Transactional
    public void salvarVenda(VendaForm form) {
        log.info("Iniciando processo de salvamento da venda para cliente ID: {}", form.getClienteId());

        Venda venda = new Venda();
        venda.setCliente(clienteRepo.findById(form.getClienteId()).orElseThrow());

        BigDecimal total = BigDecimal.ZERO;
        for (VendaForm.ItemForm iform : form.getItens()) {
            log.info("Processando item - Produto ID: {}, Quantidade: {}", iform.getProdutoId(), iform.getQuantidade());

            Produto prod = produtoRepo.findById(iform.getProdutoId()).orElseThrow();

            if (prod.getQuantidade() < iform.getQuantidade()) {
                log.warn("Estoque insuficiente para o produto: {}", prod.getDescricao());
                throw new RuntimeException("Estoque insuficiente para o produto: " + prod.getDescricao());
            }

            prod.setQuantidade(prod.getQuantidade() - iform.getQuantidade());
            produtoRepo.save(prod);
            log.debug("Estoque atualizado para produto {}: novo estoque = {}", prod.getDescricao(), prod.getQuantidade());

            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setProduto(prod);
            item.setQuantidade(iform.getQuantidade());

            BigDecimal preco = BigDecimal.valueOf(prod.getPrecoFinal());
            BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(iform.getQuantidade()));

            item.setSubtotal(subtotal);
            total = total.add(subtotal);
            venda.getItens().add(item);
        }
        venda.setTotal(total);
        vendaRepo.save(venda);
        log.info("Venda salva com sucesso. Total: {}", total);
    }

    public List<Venda> listarTodas() {
        return vendaRepo.findAll();
    }

    public List<Venda> buscarPorIdOuNomeCliente(String filtro) {
        if (filtro.matches("\\d+")) {
            Optional<Venda> venda = vendaRepo.findById(Long.parseLong(filtro));
            return venda.map(Collections::singletonList).orElse(Collections.emptyList());
        } else {
            return vendaRepo.findByClienteNomeContainingIgnoreCase(filtro);
        }
    }

    public Map<String, BigDecimal> getTotalVendasPorMes(int ano) {
        List<Object[]> resultados = vendaRepo.totalVendasPorMes(ano);
        return resultados.stream().collect(Collectors.toMap(
                r -> Month.of((int) r[0]).getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")),
                r -> BigDecimal.valueOf(((Number) r[1]).doubleValue()),
                (a, b) -> a,
                LinkedHashMap::new
        ));
    }

    public Map<String, Long> getNumeroVendasPorMes(int ano) {
        List<Object[]> resultados = vendaRepo.numeroVendasPorMes(ano);
        return resultados.stream().collect(Collectors.toMap(
                r -> Month.of((int) r[0]).getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")),
                r -> ((Number) r[1]).longValue(),
                (a, b) -> a,
                LinkedHashMap::new
        ));
    }
}


