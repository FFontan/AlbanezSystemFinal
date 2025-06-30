package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Endereco;
import br.edu.umfg.teste.spring.entities.Fornecedor;
import br.edu.umfg.teste.spring.repositories.EnderecoRepository;
import br.edu.umfg.teste.spring.repositories.FornecedorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
public class FornecedorController {

    private static final Logger logger = LoggerFactory.getLogger(FornecedorController.class);

    private FornecedorRepository fornecedorRepository;
    private EnderecoRepository enderecoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public FornecedorController(FornecedorRepository fornecedorRepository, EnderecoRepository enderecoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/cadastroFornecedor")
    public String mostrarCadastro(Model model) {
        logger.info("Acessando página de cadastro de fornecedor");
        model.addAttribute("fornecedor", new Fornecedor());
        return "cadastroFornecedor";
    }

    @GetMapping("/listaFornecedor")
    public String listarFornecedor(@RequestParam(value = "filtro", required = false) String filtro, Model model) {
        logger.info("Listando fornecedores com filtro: {}", filtro);
        List<Fornecedor> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = fornecedorRepository.findAll();
        } else {
            Set<Fornecedor> set = new LinkedHashSet<>();
            try {
                Long id = Long.parseLong(filtro);
                fornecedorRepository.findById(id).ifPresent(set::add);
            } catch (NumberFormatException e) {
                logger.warn("Filtro não numérico ao buscar fornecedor por ID: {}", filtro);
            }

            set.addAll(fornecedorRepository
                    .findByNomeFantasiaContainingIgnoreCaseOrDocumentoContainingIgnoreCase(filtro, filtro));
            lista = new ArrayList<>(set);
        }

        model.addAttribute("listaFornecedores", lista);
        model.addAttribute("filtro", filtro);
        return "listaFornecedor";
    }

    @PostMapping("/fornecedores")
    public String cadastrarFornecedor(@Valid @ModelAttribute Fornecedor fornecedor, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Tentando cadastrar fornecedor: {}", fornecedor.getNomeFantasia());
        if (result.hasErrors()) {
            logger.warn("Erro de validação ao cadastrar fornecedor: {}", result.getAllErrors());
            model.addAttribute("fornecedor", fornecedor);
            return "cadastroFornecedor";
        }

        enderecoRepository.save(fornecedor.getEndereco());
        fornecedorRepository.save(fornecedor);

        logger.info("Fornecedor cadastrado com sucesso: ID {}", fornecedor.getId());
        redirectAttributes.addFlashAttribute("confirmacao", "Fornecedor cadastrado com sucesso!");
        return "redirect:/listaFornecedor";
    }

    @GetMapping("/editarFornecedor/{id}")
    public String editarFornecedor(@PathVariable Long id, Model model) {
        logger.info("Editando fornecedor ID: {}", id);
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Fornecedor não encontrado para edição: {}", id);
                    return new IllegalArgumentException("Fornecedor não encontrado");
                });
        model.addAttribute("fornecedor", fornecedor);
        return "editarFornecedor";
    }

    @PostMapping("/editarFornecedor/{id}")
    public String atualizarFornecedor(@PathVariable Long id, @Valid @ModelAttribute Fornecedor fornecedor,
                                      BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Atualizando fornecedor ID: {}", id);
        if (result.hasErrors()) {
            logger.warn("Erro de validação ao atualizar fornecedor: {}", result.getAllErrors());
            model.addAttribute("fornecedor", fornecedor);
            return "editarFornecedor";
        }

        if (fornecedor.getEndereco() != null && fornecedor.getEndereco().getId() == null) {
            enderecoRepository.save(fornecedor.getEndereco());
        }

        fornecedorRepository.save(fornecedor);
        logger.info("Fornecedor atualizado com sucesso: ID {}", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Fornecedor atualizado com sucesso!");
        return "redirect:/listaFornecedor";
    }

    @GetMapping("/excluirFornecedor/{id}")
    public String excluirFornecedor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Tentando excluir fornecedor ID: {}", id);
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Pedido p WHERE p.fornecedorId = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();

        if (count > 0) {
            logger.warn("Fornecedor ID {} vinculado a pedidos. Não pode ser excluído.", id);
            redirectAttributes.addFlashAttribute("error", "Não é possível excluir um fornecedor vinculado a pedidos.");
            return "redirect:/listaFornecedor";
        }

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Fornecedor não encontrado para exclusão: {}", id);
                    return new IllegalArgumentException("Fornecedor não encontrado");
                });

        fornecedorRepository.delete(fornecedor);
        logger.info("Fornecedor excluído com sucesso: ID {}", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Fornecedor excluído com sucesso.");
        return "redirect:/listaFornecedor";
    }
}