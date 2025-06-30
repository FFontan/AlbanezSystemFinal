package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.dtos.VendaForm;
import br.edu.umfg.teste.spring.entities.Venda;
import br.edu.umfg.teste.spring.repositories.ClienteRepository;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import br.edu.umfg.teste.spring.services.VendaService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class VendaController {
    private static final Logger logger = LoggerFactory.getLogger(VendaController.class);

    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private ProdutoRepository produtoRepo;
    @Autowired
    private VendaService vendaService;

    @GetMapping("/realizaVenda")
    public String form(Model model) {
        logger.info("Acessando formulário de realização de venda.");
        // Cria o form e já adiciona um item vazio para evitar binding em índices null
        VendaForm vendaForm = new VendaForm();
        vendaForm.getItens().add(new VendaForm.ItemForm());

        model.addAttribute("vendaForm", vendaForm);
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("produtos", produtoRepo.findAll());
        return "realizaVenda";
    }

    @PostMapping("/realizaVenda")
    public String realiza(
            @Valid @ModelAttribute("vendaForm") VendaForm vendaForm,
            BindingResult result,
            RedirectAttributes ra,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        request.getParameterMap().keySet().forEach(System.out::println);

        logger.info("Iniciando processamento da venda.");

        if (result.hasErrors()) {
            logger.warn("Erro de validação ao processar venda: {}", result.getAllErrors());
            model.addAttribute("clientes", clienteRepo.findAll());
            model.addAttribute("produtos", produtoRepo.findAll());
            return "realizaVenda";
        }

        vendaService.salvarVenda(vendaForm);
        logger.info("Venda salva com sucesso para cliente ID: {}", vendaForm.getClienteId());

        redirectAttributes.addFlashAttribute("confirmacao", "Venda realizada com sucesso.");
        return "redirect:/listaVendas";
    }

    @GetMapping("/listaVendas")
    public String listarVendas(@RequestParam(name = "filtro", required = false) String filtro, Model model) {
        logger.info("Listando vendas. Filtro: {}", filtro != null ? filtro : "nenhum");

        List<Venda> vendas;
        if (filtro != null && !filtro.isEmpty()) {
            // Supondo que vendaService tenha método para filtro por id ou nome cliente
            vendas = vendaService.buscarPorIdOuNomeCliente(filtro);
        } else {
            vendas = vendaService.listarTodas();
        }
        logger.info("Total de vendas encontradas: {}", vendas.size());

        model.addAttribute("vendas", vendas);
        model.addAttribute("filtro", filtro);  // para manter o valor no input do filtro
        return "listaVendas";
    }

    @GetMapping("/grafico/total-vendas/{ano}")
    @ResponseBody
    public Map<String, BigDecimal> getTotalVendasPorMes(@PathVariable int ano) {
        return vendaService.getTotalVendasPorMes(ano);
    }

    @GetMapping("/grafico/numero-vendas/{ano}")
    @ResponseBody
    public Map<String, Long> getNumeroVendasPorMes(@PathVariable int ano) {
        return vendaService.getNumeroVendasPorMes(ano);
    }
}
