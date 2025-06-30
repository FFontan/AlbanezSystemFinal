package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Funcionario;
import br.edu.umfg.teste.spring.repositories.EnderecoRepository;
import br.edu.umfg.teste.spring.repositories.FuncionarioRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
public class FuncionarioController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioController.class);

    private FuncionarioRepository funcionarioRepository;
    private EnderecoRepository enderecoRepository;

    @Autowired
    public FuncionarioController(FuncionarioRepository funcionarioRepository, EnderecoRepository enderecoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/cadastroFuncionario")
    public String mostrarCadastro(Model model) {
        logger.info("Acessando página de cadastro de funcionário");
        model.addAttribute("funcionario", new Funcionario());
        return "cadastroFuncionario";
    }

    @GetMapping("/listaFuncionario")
    public String listarFuncionario(@RequestParam(value = "filtro", required = false) String filtro, Model model) {
        logger.info("Listando funcionários com filtro: {}", filtro);
        List<Funcionario> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = funcionarioRepository.findAll();
        } else {
            Set<Funcionario> set = new LinkedHashSet<>();
            try {
                Long id = Long.parseLong(filtro);
                funcionarioRepository.findById(id).ifPresent(set::add);
            } catch (NumberFormatException e) {
                logger.warn("Filtro inválido para ID numérico: {}", filtro);
            }
            set.addAll(funcionarioRepository
                    .findByNomeContainingIgnoreCaseOrDocumentoContainingIgnoreCase(filtro, filtro));
            lista = new ArrayList<>(set);
        }
        model.addAttribute("listaFuncionarios", lista);
        model.addAttribute("filtro", filtro);
        return "listaFuncionario";
    }

    @PostMapping("/funcionarios")
    public String cadastrarFuncionarios(@Valid @ModelAttribute Funcionario funcionario,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        logger.info("Tentando cadastrar funcionário: {}", funcionario.getNome());
        if (result.hasErrors()) {
            logger.warn("Erro de validação ao cadastrar funcionário: {}", result.getAllErrors());
            model.addAttribute("funcionario", funcionario);
            return "cadastroFuncionario";
        }

        enderecoRepository.save(funcionario.getEndereco());
        funcionarioRepository.save(funcionario);
        logger.info("Funcionário cadastrado com sucesso: ID {}", funcionario.getId());
        redirectAttributes.addFlashAttribute("confirmacao", "Funcionário cadastrado com sucesso!");
        return "redirect:/listaFuncionario";
    }

    @GetMapping("/editarFuncionario/{id}")
    public String mostrarEditarFuncionario(@PathVariable Long id, Model model) {
        logger.info("Editando funcionário ID: {}", id);
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Funcionário inválido para edição: {}", id);
                    return new IllegalArgumentException("Funcionário inválido: " + id);
                });
        model.addAttribute("funcionario", funcionario);
        return "editarFuncionario";
    }

    @PostMapping("/editarFuncionario/{id}")
    public String editarFuncionario(@PathVariable Long id,
                                    @Valid @ModelAttribute Funcionario funcionario,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        logger.info("Atualizando funcionário ID: {}", id);
        if (result.hasErrors()) {
            logger.warn("Erro de validação ao editar funcionário: {}", result.getAllErrors());
            model.addAttribute("funcionario", funcionario);
            return "editarFuncionario";
        }

        funcionario.setId(id);
        enderecoRepository.save(funcionario.getEndereco());
        funcionarioRepository.save(funcionario);
        logger.info("Funcionário atualizado com sucesso: ID {}", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Funcionário atualizado com sucesso!");
        return "redirect:/listaFuncionario";
    }

    @GetMapping("/excluirFuncionario/{id}")
    public String excluirFuncionario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Tentando excluir funcionário ID: {}", id);
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Funcionário inválido para exclusão: {}", id);
                    return new IllegalArgumentException("Funcionário inválido: " + id);
                });
        funcionarioRepository.delete(funcionario);
        logger.info("Funcionário excluído com sucesso: ID {}", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Funcionário excluído com sucesso.");
        return "redirect:/listaFuncionario";
    }
}
