package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.dtos.PdfDTO;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @PostMapping("/exportar/pdf")
    public void exportarPdfGenerico(@RequestBody PdfDTO.ExportacaoPdfDTO dto, HttpServletResponse response) throws IOException, DocumentException {
        logger.info("Iniciando exportação de PDF para: '{}'", dto.getTitulo());
        logger.info("Colunas: {} | Total de registros: {}", dto.getColunas().size(), dto.getDados().size());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"exportacao.pdf\"");

        Document document = new Document(PageSize.A4, 40, 40, 60, 50);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Font rodapeFonte = new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(100, 100, 100));
                Phrase rodape = new Phrase("Exportado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), rodapeFonte);
                Phrase pagina = new Phrase("Página " + writer.getPageNumber(), rodapeFonte);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, rodape, document.leftMargin(), document.bottomMargin() - 10, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, pagina, document.right() - document.rightMargin(), document.bottomMargin() - 10, 0);
            }
        });

        try {
            document.open();

            // ✅ LOGOMARCA
            // ✅ LOGOMARCA
            try {
                com.lowagie.text.Image logo = com.lowagie.text.Image.getInstance(
                        Objects.requireNonNull(getClass().getClassLoader().getResource("static/images/logo-albanez.png"))
                );

                logo.scaleToFit(120, 80);
                logo.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception imgEx) {
                logger.warn("Não foi possível carregar a logomarca: {}", imgEx.getMessage());
            }



            // ✅ TÍTULO
            Font fonteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(74, 151, 165));
            Paragraph titulo = new Paragraph(dto.getTitulo(), fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Font fonteCabecalho = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            Font fonteCelula = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);

            List<String> colunas = dto.getColunas().stream().map(PdfDTO.ColunaDTO::getLabel).toList();
            PdfPTable tabela = new PdfPTable(colunas.size());
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(10f);
            tabela.setSpacingAfter(10f);

            for (String label : colunas) {
                PdfPCell headerCell = new PdfPCell(new Phrase(label, fonteCabecalho));
                headerCell.setBackgroundColor(new Color(74, 151, 165));
                headerCell.setPadding(8);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabela.addCell(headerCell);
            }

            boolean alternar = false;
            for (Map<String, String> linha : dto.getDados()) {
                for (PdfDTO.ColunaDTO coluna : dto.getColunas()) {
                    String valor = linha.getOrDefault(coluna.getNome(), "");
                    PdfPCell cell = new PdfPCell(new Phrase(valor, fonteCelula));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(6);
                    if (alternar) {
                        cell.setBackgroundColor(new Color(240, 240, 240));
                    }
                    tabela.addCell(cell);
                }
                alternar = !alternar;
            }

            document.add(tabela);
            logger.info("Exportação de PDF concluída com sucesso: {}", dto.getTitulo());

        } catch (Exception e) {
            logger.error("Erro durante exportação de PDF: {}", e.getMessage(), e);
            throw e;
        } finally {
            document.close();
        }
    }
}