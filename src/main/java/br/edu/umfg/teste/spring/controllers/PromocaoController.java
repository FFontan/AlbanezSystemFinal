package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.entities.Promocao;
import br.edu.umfg.teste.spring.entities.PromocaoProduto;
import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.repositories.PromocaoProdutoRepository;
import br.edu.umfg.teste.spring.repositories.PromocaoRepository;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PromocaoController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PromocaoProdutoRepository promocaoProdutoRepository;

    private static final Logger log = LoggerFactory.getLogger(PromocaoController.class);

    @GetMapping("/cadastroPromocao")
    public String exibirFormularioCadastro(Model model) {
        log.info("Acessando página de cadastro de promoção.");

        model.addAttribute("promocao", new Promocao());

        List<Produto> todosProdutos = produtoRepository.findAll();
        log.debug("Produtos encontrados no banco: {}", todosProdutos.size());
        model.addAttribute("produtos", todosProdutos);

        List<Long> produtosEmPromocao = promocaoProdutoRepository
                .findProdutoIdsByPromocaoStatus(Promocao.StatusPromocao.ATIVO);
        log.debug("Produtos com promoções ativas: {}", produtosEmPromocao.size());
        model.addAttribute("produtosEmPromocao", produtosEmPromocao);

        log.info("Formulário de promoção preparado com dados de produtos.");
        return "cadastroPromocao";
    }

    @PostMapping("/promocao/salvar")
    @Transactional
    public String salvarPromocao(
            @ModelAttribute Promocao promocao,
            @RequestParam("produtoIds") List<Long> produtoIds,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Iniciando salvamento de nova promoção.");

        if (produtoIds.isEmpty()) {
            log.warn("Tentativa de salvar promoção sem produtos selecionados.");
            return "redirect:/cadastroPromocao?erro=sem_produtos";
        }

        List<PromocaoProduto> itens = new ArrayList<>();

        for (Long id : produtoIds) {
            String raw = request.getParameter("valoresDesconto[" + id + "]");
            if (raw == null || raw.isBlank()) {
                log.debug("Desconto vazio ou nulo para o produto ID {}", id);
                continue;
            }

            BigDecimal desconto;
            try {
                desconto = new BigDecimal(raw);
            } catch (NumberFormatException e) {
                log.warn("Valor de desconto inválido para o produto ID {}: {}", id, raw);
                continue;
            }

            if (desconto.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("Desconto zero ou negativo ignorado para o produto ID {}", id);
                continue;
            }

            Produto p = produtoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Produto com ID {} não encontrado no banco.", id);
                        return new RuntimeException("Produto não encontrado: " + id);
                    });

            BigDecimal precoOrig = BigDecimal.valueOf(p.getPrecoVenda());
            BigDecimal precoPromo = precoOrig.subtract(desconto);

            PromocaoProduto pp = new PromocaoProduto();
            pp.setPromocao(promocao);
            pp.setProduto(p);
            pp.setPrecoOriginal(precoOrig);
            pp.setPrecoPromocional(precoPromo);
            pp.setValorDesconto(desconto);
            itens.add(pp);

            p.setDesconto(desconto.doubleValue());
            produtoRepository.save(p);

            log.debug("Produto ID {} vinculado à promoção com desconto de R$ {}", id, desconto);
        }

        promocao.setProdutos(itens);
        promocao.setStatus(Promocao.StatusPromocao.ATIVO);
        promocaoRepository.save(promocao);

        log.info("Promoção '{}' salva com {} produto(s).", promocao.getNome(), itens.size());

        redirectAttributes.addFlashAttribute("confirmacao", "Promoção salva com sucesso!");
        return "redirect:/listaPromocao";
    }

    // 🟰 GET - Lista de promoções
    @GetMapping("/listaPromocao")
    public String listarPromocoes(@RequestParam(required = false) String filtro, Model model) {
        log.info("Listando promoções. Filtro recebido: {}", filtro);

        List<Promocao> promocoes;

        if (filtro != null && !filtro.isBlank()) {
            try {
                Long id = Long.parseLong(filtro);
                promocoes = promocaoRepository.findByIdOrNomeContainingIgnoreCase(id, filtro);
                log.debug("Filtro numérico aplicado. ID ou nome contendo '{}'", filtro);
            } catch (NumberFormatException e) {
                promocoes = promocaoRepository.findByNomeContainingIgnoreCase(filtro);
                log.debug("Filtro textual aplicado. Nome contendo '{}'", filtro);
            }
        } else {
            promocoes = promocaoRepository.findAll();
            log.debug("Nenhum filtro aplicado. Todas promoções carregadas.");
        }

        // Encerrar promoções expiradas automaticamente
        int encerradas = 0;
        for (Promocao promocao : promocoes) {
            if (promocao.getStatus() == Promocao.StatusPromocao.ATIVO
                    && promocao.getDataFim().isBefore(LocalDateTime.now())) {
                log.info("Encerrando promoção expirada: ID {} - {}", promocao.getId(), promocao.getNome());
                encerrarPromocao(promocao);
                encerradas++;
            }
        }

        if (encerradas > 0) {
            log.info("{} promoção(ões) encerradas automaticamente por expiração.", encerradas);
        }

        model.addAttribute("promocoes", promocoes);
        model.addAttribute("filtro", filtro); // manter preenchido no campo de busca
        return "listaPromocao";
    }

    // 🟰 GET - Detalhes de uma promoção
    @GetMapping("/promocao/{id}")
    public String detalhesPromocao(@PathVariable Long id, Model model) {
        log.info("Acessando detalhes da promoção com ID: {}", id);

        Promocao promocao = promocaoRepository.findByid(id);
        if (promocao == null) {
            log.warn("Promoção com ID {} não encontrada.", id);
            return "redirect:/listaPromocao";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        model.addAttribute("promocao", promocao);
        model.addAttribute("dataInicioFormatada", promocao.getDataInicio().format(formatter));
        model.addAttribute("dataFimFormatada", promocao.getDataFim().format(formatter));

        log.debug("Promoção carregada: {} (Início: {}, Fim: {})",
                promocao.getNome(),
                promocao.getDataInicio().format(formatter),
                promocao.getDataFim().format(formatter));

        return "detalhesPromocao";
    }

    @GetMapping("/promocao/editar/{id}")
    public String editarPromocao(@PathVariable Long id, Model model) {
        log.info("Acessando tela de edição da promoção com ID: {}", id);

        Promocao promocao = promocaoRepository.findByid(id);
        if (promocao == null) {
            log.warn("Promoção com ID {} não encontrada para edição.", id);
            return "redirect:/listaPromocao";
        }

        List<PromocaoProduto> produtosPromocao = promocao.getProdutos();
        List<Produto> todosProdutos = produtoRepository.findAll();

        // 1) Todos exceto os já nesta promoção:
        List<Produto> produtosDisponiveisAll = new ArrayList<>(todosProdutos);
        for (PromocaoProduto pp : produtosPromocao) {
            produtosDisponiveisAll.remove(pp.getProduto());
        }

        // 2) IDs em outras promoções ativas:
        List<Long> produtosEmOutraPromocao = promocaoProdutoRepository
                .findProdutoIdsByPromocaoStatus(Promocao.StatusPromocao.ATIVO);
        for (PromocaoProduto pp : produtosPromocao) {
            produtosEmOutraPromocao.remove(pp.getProduto().getId());
        }

        String dataFimFormatada = promocao.getDataFim()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        model.addAttribute("promocao", promocao);
        model.addAttribute("produtosPromocao", produtosPromocao);
        model.addAttribute("produtosDisponiveisAll", produtosDisponiveisAll);
        model.addAttribute("produtosEmOutraPromocao", produtosEmOutraPromocao);
        model.addAttribute("dataFimFormatada", dataFimFormatada);

        log.debug("Promoção '{}' pronta para edição. Produtos em promoção: {}, disponíveis: {}",
                promocao.getNome(),
                produtosPromocao.size(),
                produtosDisponiveisAll.size());

        return "editarPromocao";
    }

    @PostMapping("/promocao/atualizar/{id}")
    @Transactional
    public String atualizarPromocao(
            @PathVariable Long id,
            @ModelAttribute Promocao promocaoAtualizada,

            @RequestParam(name = "removerProdutoIds", required = false)
            List<Long> removerProdutoIds,

            @RequestParam(name = "produtosExistentesIds", required = false)
            List<Long> produtosExistentesIds,

            @RequestParam(name = "valoresDescontoExistentes", required = false)
            List<BigDecimal> valoresDescontoExistentes,

            @RequestParam(name = "novoProdutoIds", required = false)
            List<Long> novoProdutoIds,

            @RequestParam(name = "valoresDescontoNovos", required = false)
            List<BigDecimal> valoresDescontoNovos,

            RedirectAttributes redirectAttributes
    ) {
        log.info("Iniciando atualização da promoção ID: {}", id);
        // 0) Carrega a promoção do DB
        Promocao promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoção não encontrada: " + id));

        // 1) REMOÇÃO (sem lambdas que capturem 'promocao' não-final)
        if (removerProdutoIds != null) {
            log.debug("Removendo produtos da promoção: {}", removerProdutoIds);
            // acumula os PromocaoProduto que devem ser removidos
            List<PromocaoProduto> toRemove = new ArrayList<>();

            for (Long pid : removerProdutoIds) {
                Produto produto = produtoRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + pid));

                Optional<PromocaoProduto> ppOpt = promocaoProdutoRepository
                        .findByPromocaoAndProduto(promocao, produto);

                if (ppOpt.isPresent()) {
                    PromocaoProduto pp = ppOpt.get();

                    // desfaz o desconto no produto
                    produto.setDesconto(0.0);
                    produtoRepository.save(produto);

                    // marca para remoção
                    toRemove.add(pp);
                }
            }

            // efetiva a remoção fora do for acima
            for (PromocaoProduto pp : toRemove) {
                promocao.getProdutos().remove(pp);
                promocaoProdutoRepository.delete(pp);
            }

            // recarrega promoção (reatribuição aqui é ok, pois não estamos mais dentro de lambda)
            promocao = promocaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Promoção não encontrada: " + id));
        }

        // 2) Atualizar descontos existentes
        if (produtosExistentesIds != null && valoresDescontoExistentes != null) {
            log.debug("Atualizando descontos existentes.");
            for (int i = 0; i < produtosExistentesIds.size(); i++) {
                Long pid = produtosExistentesIds.get(i);
                BigDecimal novoDesconto = valoresDescontoExistentes.get(i);

                // só atualiza se o produto ainda faz parte da promoção
                Optional<PromocaoProduto> optPP = promocao.getProdutos().stream()
                        .filter(pp -> pp.getProduto().getId().equals(pid))
                        .findFirst();

                if (optPP.isEmpty()) {
                    continue;
                }

                PromocaoProduto pp = optPP.get();
                if (novoDesconto.compareTo(pp.getValorDesconto()) != 0) {
                    BigDecimal precoOrig = pp.getPrecoOriginal();
                    pp.setValorDesconto(novoDesconto);
                    pp.setPrecoPromocional(precoOrig.subtract(novoDesconto));

                    // atualizar também no Produto
                    Produto produto = pp.getProduto();
                    produto.setDesconto(novoDesconto.doubleValue());
                    entityManager.merge(produto);

                    // persistir a mudança no relacionamento
                    promocaoProdutoRepository.save(pp);
                }
            }
        }

        // 3) Adicionar novos produtos
        if (novoProdutoIds != null && valoresDescontoNovos != null) {
            log.debug("Adicionando novos produtos à promoção.");
            for (int i = 0; i < novoProdutoIds.size(); i++) {
                Long pid = novoProdutoIds.get(i);
                BigDecimal desconto = valoresDescontoNovos.get(i);

                Produto produto = produtoRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + pid));

                BigDecimal precoOrig = BigDecimal.valueOf(produto.getPrecoVenda());
                BigDecimal precoPromo = precoOrig.subtract(desconto);

                PromocaoProduto pp = new PromocaoProduto();
                pp.setPromocao(promocao);
                pp.setProduto(produto);
                pp.setPrecoOriginal(precoOrig);
                pp.setPrecoPromocional(precoPromo);
                pp.setValorDesconto(desconto);

                promocao.getProdutos().add(pp);
                produto.setDesconto(desconto.doubleValue());
                produtoRepository.save(produto);
            }
        }

        // 4) Atualizar dados gerais da promoção
        promocao.setNome(promocaoAtualizada.getNome());
        promocao.setDataFim(promocaoAtualizada.getDataFim());
        promocaoRepository.save(promocao);

        log.info("Promoção ID {} atualizada com sucesso.", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Promoção atualizada com sucesso!");
        return "redirect:/listaPromocao";
    }

    // 🟰 POST - Encerrar promoção manualmente
    @PostMapping("/promocao/encerrar/{id}")
    public String encerrarPromocaoManual(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Solicitada finalização manual da promoção com ID: {}", id);

        Promocao promocao = promocaoRepository.findByid(id);
        if (promocao == null) {
            log.warn("Promoção com ID {} não encontrada para encerramento manual.", id);
            redirectAttributes.addFlashAttribute("erro", "Promoção não encontrada.");
            return "redirect:/listaPromocao";
        }

        encerrarPromocao(promocao);

        log.info("Promoção com ID {} encerrada manualmente com sucesso.", id);
        redirectAttributes.addFlashAttribute("confirmacao", "Promoção encerrada com sucesso!");
        return "redirect:/listaPromocao";
    }

    @Transactional
    private void encerrarPromocao(Promocao promocao) {
        log.info("Iniciando encerramento da promoção: ID={}, Nome='{}'", promocao.getId(), promocao.getNome());

        // 1) Restaurar o desconto dos produtos que estavam na promoção
        for (PromocaoProduto pp : promocao.getProdutos()) {
            Produto produto = pp.getProduto();
            if (produto != null) {
                log.debug("Removendo desconto do produto ID={}, Nome='{}'", produto.getId(), produto.getDescricao());
                produto.setDesconto(0.0);
                produtoRepository.save(produto);
            }
        }

        // 2) Marcar a promoção como encerrada (mantendo as tuplas em promocao_produto)
        promocao.setStatus(Promocao.StatusPromocao.ENCERRADO);
        promocao.setDataFim(LocalDateTime.now());
        promocaoRepository.save(promocao);

        log.info("Promoção ID={} encerrada com status ENCERRADO e dataFim atualizada para {}", promocao.getId(), promocao.getDataFim());
    }
}
