package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Fornecedor;
import br.edu.umfg.teste.spring.entities.ItemPedido;
import br.edu.umfg.teste.spring.entities.Pedido;
import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.repositories.FornecedorRepository;
import br.edu.umfg.teste.spring.repositories.PedidoRepository;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Pedido pedidoAtual = new Pedido();

    @GetMapping("/cadastroPedido")
    public String exibirFormulario(Model model) {
        logger.info("Exibindo formulário de novo pedido.");

        model.addAttribute("pedido", pedidoAtual);
        model.addAttribute("fornecedores", fornecedorRepository.findAll());

        List<Produto> produtosDisponiveis = produtoRepository.findAll()
                .stream()
                .filter(produto -> pedidoAtual.getItens().stream()
                        .noneMatch(item -> item.getProdutoId().getId().equals(produto.getId())))
                .toList();

        model.addAttribute("produtos", produtosDisponiveis);
        return "cadastroPedido";
    }

    @PostMapping("/pedido/adicionarProduto")
    public String adicionarProduto(@RequestParam Long produtoId, @RequestParam int quantidade) {
        logger.info("Adicionando produto ID={} com quantidade={}", produtoId, quantidade);

        Produto produto = produtoRepository.findById(produtoId).orElseThrow();

        boolean jaExiste = pedidoAtual.getItens().stream()
                .anyMatch(item -> item.getProdutoId().getId().equals(produtoId));

        if (!jaExiste) {
            ItemPedido item = new ItemPedido();
            item.setProdutoId(produto);
            item.setPreco(BigDecimal.valueOf(produto.getPrecoCusto()));
            item.setQuantidade(quantidade);
            pedidoAtual.getItens().add(item);
            logger.debug("Produto adicionado com sucesso.");
        } else {
            logger.warn("Produto ID={} já estava no pedido. Nenhuma ação tomada.", produtoId);
        }

        return "redirect:/cadastroPedido";
    }

    @PostMapping("/pedido/removerProduto")
    public String removerProduto(@RequestParam Long produtoId) {
        logger.info("Removendo produto ID={} do pedido atual.", produtoId);
        pedidoAtual.getItens().removeIf(item -> item.getProdutoId().getId().equals(produtoId));
        return "redirect:/cadastroPedido";
    }

    @GetMapping("/listaPedido")
    public String listarPedido(@RequestParam(name = "filtro", required = false) String filtro, Model model) {
        logger.info("Listando pedidos. Filtro aplicado: {}", filtro);

        if (filtro != null && !filtro.isEmpty()) {
            model.addAttribute("pedido", pedidoRepository.buscarPorIdOuFornecedor(filtro));
        } else {
            model.addAttribute("pedido", pedidoRepository.findAll());
        }

        model.addAttribute("filtro", filtro);
        return "listaPedido";
    }

    @PostMapping("/salvarPedido")
    public String salvarPedido(@ModelAttribute @Valid Pedido pedido, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Iniciando processo de salvamento de pedido.");

        if (pedidoAtual.getItens().isEmpty()) {
            logger.warn("Tentativa de salvar pedido vazio. Bloqueando operação.");
            result.rejectValue("itens", "pedido.itens.vazio", "O pedido deve conter ao menos um produto.");
        }

        if (result.hasErrors()) {
            logger.warn("Erro de validação ao salvar pedido. Retornando para o formulário.");
            model.addAttribute("pedido", pedido);
            model.addAttribute("fornecedores", fornecedorRepository.findAll());

            List<Produto> produtosDisponiveis = produtoRepository.findAll()
                    .stream()
                    .filter(produto -> pedidoAtual.getItens().stream()
                            .noneMatch(item -> item.getProdutoId().getId().equals(produto.getId())))
                    .toList();
            model.addAttribute("produtos", produtosDisponiveis);
            return "cadastroPedido";
        }

        Fornecedor fornecedor = fornecedorRepository.findById(pedido.getFornecedorId())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        pedido.setFornecedor(fornecedor);

        pedido.setDataCadastro(LocalDateTime.now());
        pedido.setValorTotal(
                pedidoAtual.getItens().stream()
                        .map(ItemPedido::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        pedido.setItens(pedidoAtual.getItens());

        pedidoRepository.save(pedido);
        logger.info("Pedido salvo. ID={}, Total={}, Itens={}",
                pedido.getId(), pedido.getValorTotal(), pedido.getItens().size());

        for (ItemPedido item : pedidoAtual.getItens()) {
            Produto produto = item.getProdutoId();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
            logger.debug("Atualizado estoque do produto ID={} para quantidade={}", produto.getId(), produto.getQuantidade());
        }

        pedidoAtual = new Pedido();

        redirectAttributes.addFlashAttribute("confirmacao", "Pedido cadastrado com sucesso!");
        return "redirect:/listaPedido";
    }
}
