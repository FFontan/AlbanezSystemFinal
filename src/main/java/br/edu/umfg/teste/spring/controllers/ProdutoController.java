package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.entities.Promocao;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import br.edu.umfg.teste.spring.repositories.PromocaoProdutoRepository;
import com.lowagie.text.Font;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.persistence.EntityManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

@Controller
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    private final ProdutoRepository produtoRepository;
    private final PromocaoProdutoRepository promocaoProdutoRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public ProdutoController(ProdutoRepository produtoRepository,
                             PromocaoProdutoRepository promocaoProdutoRepository,
                             Cloudinary cloudinary) {
        this.produtoRepository = produtoRepository;
        this.promocaoProdutoRepository = promocaoProdutoRepository;
        this.cloudinary = cloudinary;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/cadastroProduto")
    public String mostrarCadastro(Model model) {
        logger.info("Exibindo tela de cadastro de produto.");
        model.addAttribute("produto", new Produto());
        return "cadastroProduto";
    }

    @GetMapping("/listaProduto")
    public String listarProduto(@RequestParam(value = "filtro", required = false) String filtro, Model model) {
        logger.info("Listando produtos com filtro: {}", filtro);
        List<Produto> produtos;

        if (filtro != null && !filtro.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(filtro);
                produtos = produtoRepository.findByIdOrDescricaoContainingIgnoreCase(id, filtro);
            } catch (NumberFormatException e) {
                produtos = produtoRepository.findByDescricaoContainingIgnoreCase(filtro);
            }
        } else {
            produtos = produtoRepository.findAll();
        }

        String imagemPadrao = "/images-produtos/sem-imagem.png";
        produtos.forEach(produto -> {
            if (produto.getImagem() == null || produto.getImagem().isEmpty()) {
                produto.setImagem(imagemPadrao);
            }
        });

        List<Long> idsEmPromoAtiva = promocaoProdutoRepository
                .findProdutoIdsByPromocaoStatus(Promocao.StatusPromocao.ATIVO);
        Set<Long> bloqueados = new HashSet<>(idsEmPromoAtiva);

        model.addAttribute("produtos", produtos);
        model.addAttribute("produtosEmPromoAtiva", bloqueados);

        return "listaProduto";
    }

    @PostMapping("/produtos")
    public String cadastrarProdutos(@Valid @ModelAttribute Produto produto, BindingResult result,
                                    @RequestParam("imagemFile") MultipartFile imagemFile, Model model,
                                    RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("Erro ao cadastrar produto: {}", result.getAllErrors());
            model.addAttribute("produto", produto);
            return "cadastroProduto";
        }

        if (!imagemFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imagemFile.getBytes(), ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");
                produto.setImagem(url);
                logger.info("Imagem enviada para o Cloudinary com sucesso: {}", url);
            } catch (IOException e) {
                logger.error("Erro ao enviar imagem para o Cloudinary: {}", e.getMessage(), e);
            }
        }

        produtoRepository.save(produto);
        logger.info("Produto cadastrado com sucesso: {}", produto.getDescricao());

        redirectAttributes.addFlashAttribute("confirmacao", "Produto cadastrado com sucesso!");
        return "redirect:/listaProduto";
    }

    @GetMapping("/editarProduto/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        model.addAttribute("produto", produto);
        logger.info("Abrindo formulário de edição para produto ID {}", id);
        return "editarProduto";
    }

    @PostMapping("/editarProduto/{id}")
    public String atualizarProduto(@PathVariable Long id, @Valid @ModelAttribute Produto produto,
                                   BindingResult result, @RequestParam("imagemFile") MultipartFile imagemFile,
                                   @RequestParam(value = "excluirImagem", required = false) String excluirImagem,
                                   RedirectAttributes redirectAttributes) {

        produto.setId(id);

        if (produto.getDesconto() != null && produto.getPrecoVenda() != null &&
                produto.getPrecoVenda().compareTo(produto.getDesconto()) < 0) {
            result.rejectValue("precoVenda", "erro.precoVenda", "Preço de venda não pode ser menor que o desconto ativo.");
        }

        if (result.hasErrors()) {
            logger.warn("Erro ao atualizar produto ID {}: {}", id, result.getAllErrors());
            Produto produtoExistente = produtoRepository.findById(id).orElseThrow();
            produto.setImagem(produtoExistente.getImagem());
            return "editarProduto";
        }

        Produto produtoExistente = produtoRepository.findById(id).orElseThrow();

        if ("true".equals(excluirImagem)) {
            if (produtoExistente.getImagem() != null && !produtoExistente.getImagem().isEmpty()) {
                String imagem = produtoExistente.getImagem();
                if (!imagem.startsWith("http")) { // só tenta excluir se for local
                    String nomeArquivo = imagem.replace("/images-produtos/", "");
                    Path caminhoImagem = Paths.get(System.getProperty("user.dir"), "images-produtos", nomeArquivo);
                    try {
                        Files.deleteIfExists(caminhoImagem);
                        logger.info("Imagem local excluída: {}", nomeArquivo);
                    } catch (IOException e) {
                        logger.error("Erro ao excluir imagem local: {}", e.getMessage(), e);
                    }
                } else {
                    logger.info("Imagem armazenada no Cloudinary, não será excluída localmente: {}", imagem);
                }
            }
            produto.setImagem(null);
        }

        if (!imagemFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imagemFile.getBytes(), ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");
                produto.setImagem(url);
                logger.info("Nova imagem enviada para o Cloudinary: {}", url);
            } catch (IOException e) {
                logger.error("Erro ao enviar nova imagem para o Cloudinary: {}", e.getMessage(), e);
            }
        }

        produtoRepository.save(produto);
        logger.info("Produto ID {} atualizado com sucesso", id);

        redirectAttributes.addFlashAttribute("confirmacao", "Produto atualizado com sucesso!");
        return "redirect:/listaProduto";
    }

    @GetMapping("/excluirProduto/{id}")
    public String excluirProduto(@PathVariable Long id, RedirectAttributes attrs) {
        if (promocaoProdutoRepository.findProdutoIdsByPromocaoStatus(Promocao.StatusPromocao.ATIVO).contains(id)) {
            logger.warn("Tentativa de excluir produto em promoção ativa. ID: {}", id);
            attrs.addFlashAttribute("error", "Não é possível excluir produto que está em promoção ativa.");
            return "redirect:/listaProduto";
        }

        Long count = entityManager.createQuery(
                        "SELECT COUNT(ip) FROM ItemPedido ip WHERE ip.produtoId.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();

        if (count > 0) {
            logger.warn("Tentativa de excluir produto vinculado a pedido. ID: {}", id);
            attrs.addFlashAttribute("error", "Não é possível excluir produto que está vinculado a pedidos realizados.");
            return "redirect:/listaProduto";
        }

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produtoRepository.delete(produto);

        logger.info("Produto excluído com sucesso. ID: {}", id);
        attrs.addFlashAttribute("confirmacao", "Produto excluído com sucesso.");
        return "redirect:/listaProduto";
    }
}