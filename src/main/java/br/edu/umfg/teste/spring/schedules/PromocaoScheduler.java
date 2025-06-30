package br.edu.umfg.teste.spring.schedules;

import br.edu.umfg.teste.spring.entities.Promocao;
import br.edu.umfg.teste.spring.entities.PromocaoProduto;
import br.edu.umfg.teste.spring.entities.Produto;
import br.edu.umfg.teste.spring.repositories.PromocaoRepository;
import br.edu.umfg.teste.spring.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PromocaoScheduler {

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Scheduled(fixedRate = 60000) // Roda a cada 60 segundos (60000 ms)
    @Transactional
    public void verificarPromocoesExpiradas() {
        List<Promocao> promocoes = promocaoRepository.findAll();

        for (Promocao promocao : promocoes) {
            if (promocao.getStatus() == Promocao.StatusPromocao.ATIVO
                    && promocao.getDataFim().isBefore(LocalDateTime.now())) {

                // Encerra a promoção
                for (PromocaoProduto promocaoProduto : promocao.getProdutos()) {
                    Produto produto = promocaoProduto.getProduto();
                    if (produto != null) {
                        produto.setPrecoVenda(promocaoProduto.getPrecoOriginal().doubleValue());
                        produtoRepository.save(produto);
                    }
                }

                promocao.setStatus(Promocao.StatusPromocao.ENCERRADO);
                promocaoRepository.save(promocao);

                System.out.println("Promoção encerrada: " + promocao.getNome());
            }
        }
    }
}
