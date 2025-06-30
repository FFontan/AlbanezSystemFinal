package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Cliente;
import br.edu.umfg.teste.spring.repositories.EnderecoRepository;
import br.edu.umfg.teste.spring.repositories.ClienteRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public ClienteController(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/cadastroCliente")
    public String mostrarCadastro(Model model) {
        logger.info("Acessando tela de cadastro de cliente.");
        model.addAttribute("cliente", new Cliente());
        return "cadastroCliente";
    }

    @GetMapping("/listaCliente")
    public String listarCliente(
            @RequestParam(value = "filtro", required = false) String filtro,
            Model model) {

        List<Cliente> lista;
        if (filtro != null && !filtro.isBlank()) {
            logger.info("Listando clientes com filtro: '{}'", filtro);
            lista = clienteRepository.findByNomeContainingIgnoreCaseOrId(filtro, parseIdOuZero(filtro));
        } else {
            logger.info("Listando todos os clientes.");
            lista = clienteRepository.findAll();
        }

        model.addAttribute("clientes", lista);
        model.addAttribute("filtro", filtro);
        return "listaCliente";
    }

    private Long parseIdOuZero(String filtro) {
        try {
            return Long.parseLong(filtro);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @PostMapping("/clientes")
    public String cadastrarCliente(
            @Valid @ModelAttribute Cliente cliente,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("Erro de validação ao cadastrar cliente: {}", result.getAllErrors());
            model.addAttribute("cliente", cliente);
            return "cadastroCliente";
        }

        enderecoRepository.save(cliente.getEndereco());
        clienteRepository.save(cliente);
        logger.info("Cliente cadastrado com sucesso: {}", cliente.getNome());

        redirectAttributes.addFlashAttribute("confirmacao", "Cliente cadastrado com sucesso!");
        return "redirect:/listaCliente";
    }

    @GetMapping("/editarCliente/{id}")
    public String mostrarEditarCliente(@PathVariable Long id, Model model) {
        logger.info("Acessando edição do cliente com ID {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente inválido: " + id));
        model.addAttribute("cliente", cliente);
        return "editarCliente";
    }

    @PostMapping("/editarCliente/{id}")
    public String editarCliente(
            @PathVariable Long id,
            @Valid @ModelAttribute Cliente cliente,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("Erro de validação ao editar cliente ID {}: {}", id, result.getAllErrors());
            model.addAttribute("cliente", cliente);
            return "editarCliente";
        }

        cliente.setId(id);
        enderecoRepository.save(cliente.getEndereco());
        clienteRepository.save(cliente);
        logger.info("Cliente atualizado com sucesso: ID {}, Nome {}", id, cliente.getNome());

        redirectAttributes.addFlashAttribute("confirmacao", "Cliente atualizado com sucesso!");
        return "redirect:/listaCliente";
    }

    @GetMapping("/excluirCliente/{id}")
    public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente inválido: " + id));
            clienteRepository.delete(cliente);
            logger.info("Cliente excluído com sucesso: ID {}", id);
            redirectAttributes.addFlashAttribute("confirmacao", "Cliente excluído com sucesso.");
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao excluir cliente ID {}: Cliente vinculado a vendas.", id);
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir o cliente. Ele está vinculado a uma ou mais vendas.");
        }
        return "redirect:/listaCliente";
    }
}
