package com.ecommerce.util;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDFGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static File generateInvoice(Order order) throws Exception {
        if (order == null) {
            throw new IllegalArgumentException("Order nao pode ser nulo");
        }

        File pdfDir = new File("pdfs");
        if (!pdfDir.exists() && !pdfDir.mkdirs()) {
            throw new IllegalStateException("Nao foi possivel criar diretorio de PDFs");
        }

        long numeroNotaLong = order.getId() != null ? order.getId() : 0L;
        String numeroNota = String.format("%06d", numeroNotaLong);
        String serie = "1";

        LocalDateTime emissao = order.getOrderDate() != null ? order.getOrderDate() : LocalDateTime.now();
        String protocolo = buildProtocol(numeroNotaLong, emissao);
        String chaveAcesso = buildAccessKey(numeroNotaLong, emissao);

        Order.SimpleUser user = order.getUser();
        String clienteNome = safeText(user != null ? user.getNomeCompleto() : "Cliente", 68);
        String clienteCpf = safeText(user != null ? user.getCpf() : "", 20);
        if (clienteCpf.isBlank()) {
            clienteCpf = "NAO INFORMADO";
        }

        String endereco = buildAddress(user, order.getDeliveryAddress());
        String municipio = safeText(user != null ? user.getCidade() : "", 28);
        if (municipio.isBlank()) {
            municipio = "NAO INFORMADO";
        }

        String uf = safeText(user != null ? user.getEstado() : "", 2).toUpperCase();
        if (uf.isBlank()) {
            uf = "SP";
        }

        String cep = digitsOnly(user != null ? user.getCep() : "");
        if (!cep.isBlank() && cep.length() == 8) {
            cep = cep.substring(0, 5) + "-" + cep.substring(5);
        } else if (cep.isBlank()) {
            cep = "00000-000";
        }

        String telefone = safeText(user != null ? user.getTelefone() : "", 20);
        if (telefone.isBlank()) {
            telefone = "NAO INFORMADO";
        }

        List<OrderItem> items = order.getItems() != null ? order.getItems() : Collections.emptyList();
        BigDecimal totalProdutos = BigDecimal.ZERO;
        int quantidadeTotal = 0;
        for (OrderItem item : items) {
            if (item == null) {
                continue;
            }
            int qtd = item.getQuantity() != null ? item.getQuantity() : 0;
            quantidadeTotal += qtd;
            BigDecimal subtotal = item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO;
            totalProdutos = totalProdutos.add(subtotal);
        }

        BigDecimal totalNota = order.getTotal() != null ? order.getTotal() : totalProdutos;
        BigDecimal valorFrete = BigDecimal.ZERO;
        BigDecimal baseIcms = totalNota;
        BigDecimal valorIcms = BigDecimal.ZERO;
        BigDecimal valorIpi = BigDecimal.ZERO;

        String paymentMethod = safeText(order.getPaymentMethod(), 40);
        if (paymentMethod.isBlank()) {
            paymentMethod = "NAO INFORMADO";
        }

        String fileName = "Nota_Fiscal_Pedido_" + numeroNotaLong + ".pdf";
        File pdfFile = new File(pdfDir, fileName);

        StringBuilder stream = new StringBuilder();
        stream.append("0.8 w\n");
        stream.append("1 J\n");
        stream.append("1 j\n");

        final int left = 24;
        final int right = 571;
        final int width = right - left;

        int top = 816;

        int reciboHeight = 48;
        int reciboBottom = top - reciboHeight;
        rect(stream, left, reciboBottom, width, reciboHeight);

        int reciboNfX = right - 95;
        line(stream, reciboNfX, top, reciboNfX, reciboBottom);
        line(stream, left, top - 24, reciboNfX, top - 24);

        text(stream, "F2", 7, left + 4, top - 11, "RECEBEMOS DE E-COMMERCE LTDA OS PRODUTOS CONSTANTES DA NOTA FISCAL INDICADA AO LADO");
        text(stream, "F2", 7, left + 4, top - 35, "DATA DE RECEBIMENTO");
        text(stream, "F2", 7, left + 110, top - 35, "IDENTIFICACAO E ASSINATURA DO RECEBEDOR");

        centerText(stream, "F1", 12, reciboNfX, right, top - 18, "NF-e");
        centerText(stream, "F1", 12, reciboNfX, right, top - 32, "No " + numeroNota);
        centerText(stream, "F2", 9, reciboNfX, right, top - 44, "Serie " + serie);

        int dashY = reciboBottom - 8;
        stream.append("[3 3] 0 d\n");
        line(stream, left, dashY, right, dashY);
        stream.append("[] 0 d\n");

        int headerTop = dashY - 8;
        int headerHeight = 102;
        int headerBottom = headerTop - headerHeight;
        rect(stream, left, headerBottom, width, headerHeight);

        int headerCol1 = left + 138;
        int headerCol2 = left + 292;
        line(stream, headerCol1, headerTop, headerCol1, headerBottom);
        line(stream, headerCol2, headerTop, headerCol2, headerBottom);

        text(stream, "F1", 11, left + 10, headerTop - 22, "E-COMMERCE LTDA");
        text(stream, "F2", 8, left + 10, headerTop - 37, "CNPJ 12.345.678/0001-90");
        text(stream, "F2", 8, left + 10, headerTop - 50, "RUA DO COMERCIO, 123 - CENTRO");
        text(stream, "F2", 8, left + 10, headerTop - 63, "SAO PAULO - SP");
        text(stream, "F2", 8, left + 10, headerTop - 76, "www.ecommerce.com.br");

        centerText(stream, "F1", 14, headerCol1, headerCol2, headerTop - 18, "DANFE");
        centerText(stream, "F2", 9, headerCol1, headerCol2, headerTop - 32, "Documento Auxiliar da");
        centerText(stream, "F2", 9, headerCol1, headerCol2, headerTop - 44, "Nota Fiscal Eletronica");

        text(stream, "F2", 8, headerCol1 + 8, headerTop - 57, "0 - ENTRADA");
        text(stream, "F2", 8, headerCol1 + 8, headerTop - 69, "1 - SAIDA");
        rect(stream, headerCol2 - 26, headerTop - 74, 20, 20);
        centerText(stream, "F1", 10, headerCol2 - 26, headerCol2 - 6, headerTop - 61, "1");

        text(stream, "F1", 13, headerCol1 + 8, headerTop - 88, "N " + numeroNota);
        text(stream, "F2", 9, headerCol1 + 8, headerTop - 100, "SERIE " + serie + "  FOLHA 1/1");

        text(stream, "F2", 8, headerCol2 + 8, headerTop - 13, "Controle do Fisco");
        int barcodeX = headerCol2 + 8;
        int barcodeY = headerBottom + 18;
        int barcodeWidth = right - barcodeX - 8;
        int barcodeHeight = 52;
        rect(stream, barcodeX, barcodeY, barcodeWidth, barcodeHeight);
        fakeBarcode(stream, barcodeX + 2, barcodeY + 2, barcodeWidth - 4, barcodeHeight - 4, chaveAcesso);

        int operacaoTop = headerBottom - 8;
        int operacaoHeight = 62;
        int operacaoBottom = operacaoTop - operacaoHeight;
        rect(stream, left, operacaoBottom, width, operacaoHeight);
        line(stream, left, operacaoTop - 24, right, operacaoTop - 24);
        line(stream, left + 355, operacaoTop, left + 355, operacaoTop - 24);
        line(stream, left + 95, operacaoTop - 24, left + 95, operacaoBottom);
        line(stream, left + 225, operacaoTop - 24, left + 225, operacaoBottom);
        line(stream, left + 305, operacaoTop - 24, left + 305, operacaoBottom);

        text(stream, "F2", 7, left + 4, operacaoTop - 10, "Natureza da operacao");
        text(stream, "F2", 9, left + 4, operacaoTop - 21, "Venda de mercadorias");
        text(stream, "F2", 7, left + 360, operacaoTop - 10, "Protocolo de autorizacao de uso da NF-e");
        text(stream, "F1", 9, left + 360, operacaoTop - 21, protocolo);

        text(stream, "F2", 7, left + 4, operacaoTop - 33, "Inscricao Estadual");
        text(stream, "F2", 8, left + 4, operacaoTop - 45, "123456789");
        text(stream, "F2", 7, left + 100, operacaoTop - 33, "Insc. Est. Subst. Trib.");
        text(stream, "F2", 8, left + 100, operacaoTop - 45, "--");
        text(stream, "F2", 7, left + 230, operacaoTop - 33, "CNPJ");
        text(stream, "F2", 8, left + 230, operacaoTop - 45, "12.345.678/0001-90");

        text(stream, "F2", 7, left + 310, operacaoTop - 33, "Chave de acesso da NF-e");
        text(stream, "F1", 8, left + 310, operacaoTop - 45, formatAccessKey(chaveAcesso));

        int destinatarioTop = operacaoBottom - 8;
        int destinatarioHeight = 104;
        int destinatarioBottom = destinatarioTop - destinatarioHeight;
        rect(stream, left, destinatarioBottom, width, destinatarioHeight);
        line(stream, left, destinatarioTop - 14, right, destinatarioTop - 14);
        line(stream, left, destinatarioTop - 32, right, destinatarioTop - 32);
        line(stream, left, destinatarioTop - 50, right, destinatarioTop - 50);
        line(stream, left, destinatarioTop - 68, right, destinatarioTop - 68);
        line(stream, left, destinatarioTop - 86, right, destinatarioTop - 86);

        text(stream, "F1", 9, left + 4, destinatarioTop - 11, "Destinatario / Remetente");
        text(stream, "F2", 8, left + 4, destinatarioTop - 27, "Nome / Razao Social: " + clienteNome);
        text(stream, "F2", 8, left + 4, destinatarioTop - 45, "CPF/CNPJ: " + clienteCpf + "   Inscricao Estadual: ISENTO");
        text(stream, "F2", 8, left + 4, destinatarioTop - 63, "Endereco: " + endereco);
        text(stream, "F2", 8, left + 4, destinatarioTop - 81, "Municipio: " + municipio + "   UF: " + uf + "   CEP: " + cep);
        text(stream, "F2", 8, left + 4, destinatarioTop - 99, "Data emissao: " + emissao.format(DATE_FORMAT) + "   Hora emissao: " + emissao.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "   Fone/Fax: " + telefone);

        int faturasTop = destinatarioBottom - 6;
        int faturasHeight = 44;
        int faturasBottom = faturasTop - faturasHeight;
        rect(stream, left, faturasBottom, width, faturasHeight);
        line(stream, left, faturasTop - 14, right, faturasTop - 14);
        line(stream, left, faturasTop - 30, right, faturasTop - 30);

        int quarter1 = left + (width / 4);
        int quarter2 = left + (width / 2);
        int quarter3 = left + ((width * 3) / 4);
        line(stream, quarter1, faturasTop - 14, quarter1, faturasBottom);
        line(stream, quarter2, faturasTop - 14, quarter2, faturasBottom);
        line(stream, quarter3, faturasTop - 14, quarter3, faturasBottom);

        text(stream, "F1", 9, left + 4, faturasTop - 11, "Faturas");
        text(stream, "F2", 8, left + 4, faturasTop - 26, "Numero");
        text(stream, "F2", 8, quarter1 + 4, faturasTop - 26, "Vencimento");
        text(stream, "F2", 8, quarter2 + 4, faturasTop - 26, "Valor");
        text(stream, "F2", 8, quarter3 + 4, faturasTop - 26, "Forma Pagto");

        text(stream, "F2", 8, left + 4, faturasTop - 40, numeroNota + "/1");
        text(stream, "F2", 8, quarter1 + 4, faturasTop - 40, emissao.plusDays(7).format(DATE_FORMAT));
        text(stream, "F2", 8, quarter2 + 4, faturasTop - 40, formatMoney(totalNota));
        text(stream, "F2", 8, quarter3 + 4, faturasTop - 40, paymentMethod);

        int impostoTop = faturasBottom - 6;
        int impostoHeight = 58;
        int impostoBottom = impostoTop - impostoHeight;
        rect(stream, left, impostoBottom, width, impostoHeight);
        line(stream, left, impostoTop - 14, right, impostoTop - 14);
        line(stream, left, impostoTop - 30, right, impostoTop - 30);

        int imp1 = left + 91;
        int imp2 = left + 182;
        int imp3 = left + 273;
        int imp4 = left + 364;
        int imp5 = left + 455;
        line(stream, imp1, impostoTop - 14, imp1, impostoBottom);
        line(stream, imp2, impostoTop - 14, imp2, impostoBottom);
        line(stream, imp3, impostoTop - 14, imp3, impostoBottom);
        line(stream, imp4, impostoTop - 14, imp4, impostoBottom);
        line(stream, imp5, impostoTop - 14, imp5, impostoBottom);

        text(stream, "F1", 9, left + 4, impostoTop - 11, "Calculo do imposto");
        text(stream, "F2", 7, left + 4, impostoTop - 26, "Base calc. ICMS");
        text(stream, "F2", 7, imp1 + 4, impostoTop - 26, "Valor do ICMS");
        text(stream, "F2", 7, imp2 + 4, impostoTop - 26, "Base calc. ICMS ST");
        text(stream, "F2", 7, imp3 + 4, impostoTop - 26, "Valor ICMS ST");
        text(stream, "F2", 7, imp4 + 4, impostoTop - 26, "Valor IPI");
        text(stream, "F2", 7, imp5 + 4, impostoTop - 26, "Valor total produtos");

        text(stream, "F2", 8, left + 4, impostoTop - 43, formatMoney(baseIcms));
        text(stream, "F2", 8, imp1 + 4, impostoTop - 43, formatMoney(valorIcms));
        text(stream, "F2", 8, imp2 + 4, impostoTop - 43, "0,00");
        text(stream, "F2", 8, imp3 + 4, impostoTop - 43, "0,00");
        text(stream, "F2", 8, imp4 + 4, impostoTop - 43, formatMoney(valorIpi));
        text(stream, "F2", 8, imp5 + 4, impostoTop - 43, formatMoney(totalProdutos));

        int transporteTop = impostoBottom - 6;
        int transporteHeight = 72;
        int transporteBottom = transporteTop - transporteHeight;
        rect(stream, left, transporteBottom, width, transporteHeight);
        line(stream, left, transporteTop - 14, right, transporteTop - 14);
        line(stream, left, transporteTop - 30, right, transporteTop - 30);
        line(stream, left, transporteTop - 48, right, transporteTop - 48);

        int tr1 = left + 265;
        int tr2 = left + 338;
        int tr3 = left + 426;
        int tr4 = left + 480;
        line(stream, tr1, transporteTop - 14, tr1, transporteBottom);
        line(stream, tr2, transporteTop - 14, tr2, transporteBottom);
        line(stream, tr3, transporteTop - 14, tr3, transporteBottom);
        line(stream, tr4, transporteTop - 14, tr4, transporteBottom);

        text(stream, "F1", 9, left + 4, transporteTop - 11, "Transportador / Volumes transportados");
        text(stream, "F2", 7, left + 4, transporteTop - 26, "Nome");
        text(stream, "F2", 7, tr1 + 4, transporteTop - 26, "Frete por conta");
        text(stream, "F2", 7, tr2 + 4, transporteTop - 26, "Codigo ANTT");
        text(stream, "F2", 7, tr3 + 4, transporteTop - 26, "Placa do veiculo");
        text(stream, "F2", 7, tr4 + 4, transporteTop - 26, "UF");

        text(stream, "F2", 8, left + 4, transporteTop - 43, "TRANSPORTADORA PADRAO");
        text(stream, "F2", 8, tr1 + 4, transporteTop - 43, "1-Emitente");
        text(stream, "F2", 8, tr2 + 4, transporteTop - 43, "--");
        text(stream, "F2", 8, tr3 + 4, transporteTop - 43, "AAA0A00");
        text(stream, "F2", 8, tr4 + 4, transporteTop - 43, uf);

        text(stream, "F2", 7, left + 4, transporteTop - 60, "Quantidade");
        text(stream, "F2", 7, left + 105, transporteTop - 60, "Especie");
        text(stream, "F2", 7, left + 215, transporteTop - 60, "Marca");
        text(stream, "F2", 7, left + 330, transporteTop - 60, "Numeracao");
        text(stream, "F2", 7, left + 435, transporteTop - 60, "Peso bruto");
        text(stream, "F2", 7, left + 510, transporteTop - 60, "Peso liquido");

        text(stream, "F2", 8, left + 4, transporteTop - 70, String.valueOf(Math.max(1, quantidadeTotal)));
        text(stream, "F2", 8, left + 105, transporteTop - 70, "VOLUMES");
        text(stream, "F2", 8, left + 215, transporteTop - 70, "CAIXAS");
        text(stream, "F2", 8, left + 435, transporteTop - 70, formatMoney(totalNota));
        text(stream, "F2", 8, left + 510, transporteTop - 70, formatMoney(totalNota));

        int itensTop = transporteBottom - 6;
        int itensHeight = 134;
        int itensBottom = itensTop - itensHeight;
        rect(stream, left, itensBottom, width, itensHeight);
        line(stream, left, itensTop - 14, right, itensTop - 14);
        line(stream, left, itensTop - 30, right, itensTop - 30);

        int c1 = left + 36;
        int c2 = left + 245;
        int c3 = left + 288;
        int c4 = left + 324;
        int c5 = left + 360;
        int c6 = left + 390;
        int c7 = left + 425;
        int c8 = left + 476;
        int c9 = left + 526;

        line(stream, c1, itensTop - 14, c1, itensBottom);
        line(stream, c2, itensTop - 14, c2, itensBottom);
        line(stream, c3, itensTop - 14, c3, itensBottom);
        line(stream, c4, itensTop - 14, c4, itensBottom);
        line(stream, c5, itensTop - 14, c5, itensBottom);
        line(stream, c6, itensTop - 14, c6, itensBottom);
        line(stream, c7, itensTop - 14, c7, itensBottom);
        line(stream, c8, itensTop - 14, c8, itensBottom);
        line(stream, c9, itensTop - 14, c9, itensBottom);

        text(stream, "F1", 9, left + 4, itensTop - 11, "Itens da nota fiscal");
        text(stream, "F2", 7, left + 3, itensTop - 26, "Codigo");
        text(stream, "F2", 7, c1 + 3, itensTop - 26, "Descricao do produto/servico");
        text(stream, "F2", 7, c2 + 3, itensTop - 26, "NCM/SH");
        text(stream, "F2", 7, c3 + 3, itensTop - 26, "CST");
        text(stream, "F2", 7, c4 + 3, itensTop - 26, "CFOP");
        text(stream, "F2", 7, c5 + 3, itensTop - 26, "UN");
        text(stream, "F2", 7, c6 + 3, itensTop - 26, "Qtde");
        text(stream, "F2", 7, c7 + 3, itensTop - 26, "Preco un");
        text(stream, "F2", 7, c8 + 3, itensTop - 26, "Preco total");

        int itemRowTop = itensTop - 30;
        int rowHeight = 18;
        int maxRows = (itensHeight - 30) / rowHeight;

        for (int i = 0; i < maxRows; i++) {
            int rowBottom = itemRowTop - ((i + 1) * rowHeight);
            line(stream, left, rowBottom, right, rowBottom);

            if (i < items.size()) {
                OrderItem item = items.get(i);
                String codigo = String.format("%03d", i + 1);
                String descricao = safeText(item != null && item.getProduct() != null ? item.getProduct().getNome() : "Produto", 33);
                String quantidade = String.valueOf(item != null && item.getQuantity() != null ? item.getQuantity() : 0);
                BigDecimal precoUnit = item != null && item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                BigDecimal precoTotal = item != null && item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO;

                int yText = rowBottom + 6;
                text(stream, "F2", 7, left + 4, yText, codigo);
                text(stream, "F2", 7, c1 + 3, yText, descricao);
                text(stream, "F2", 7, c2 + 3, yText, "00000000");
                text(stream, "F2", 7, c3 + 3, yText, "000");
                text(stream, "F2", 7, c4 + 3, yText, "5.102");
                text(stream, "F2", 7, c5 + 3, yText, "UN");
                text(stream, "F2", 7, c6 + 3, yText, quantidade);
                text(stream, "F2", 7, c7 + 3, yText, formatMoney(precoUnit));
                text(stream, "F2", 7, c8 + 3, yText, formatMoney(precoTotal));
            }
        }

        int issqnTop = itensBottom - 6;
        int issqnHeight = 40;
        int issqnBottom = issqnTop - issqnHeight;
        rect(stream, left, issqnBottom, width, issqnHeight);
        line(stream, left, issqnTop - 14, right, issqnTop - 14);
        line(stream, left, issqnTop - 28, right, issqnTop - 28);

        int is1 = left + 182;
        int is2 = left + 364;
        line(stream, is1, issqnTop - 14, is1, issqnBottom);
        line(stream, is2, issqnTop - 14, is2, issqnBottom);

        text(stream, "F1", 9, left + 4, issqnTop - 11, "Calculo do ISSQN");
        text(stream, "F2", 7, left + 4, issqnTop - 24, "Inscricao Municipal");
        text(stream, "F2", 7, is1 + 4, issqnTop - 24, "Valor total dos servicos");
        text(stream, "F2", 7, is2 + 4, issqnTop - 24, "Base de calculo do ISSQN");

        text(stream, "F2", 8, left + 4, issqnTop - 37, "--");
        text(stream, "F2", 8, is1 + 4, issqnTop - 37, "0,00");
        text(stream, "F2", 8, is2 + 4, issqnTop - 37, "0,00");

        int adicionaisTop = issqnBottom - 6;
        int adicionaisHeight = 90;
        int adicionaisBottom = adicionaisTop - adicionaisHeight;
        rect(stream, left, adicionaisBottom, width, adicionaisHeight);
        line(stream, left, adicionaisTop - 14, right, adicionaisTop - 14);

        int splitAdicional = left + (int) (width * 0.65);
        line(stream, splitAdicional, adicionaisTop - 14, splitAdicional, adicionaisBottom);

        text(stream, "F1", 9, left + 4, adicionaisTop - 11, "Dados adicionais");
        text(stream, "F2", 8, left + 4, adicionaisTop - 26, "Informacoes complementares:");

        List<String> infoLines = new ArrayList<>();
        infoLines.add("Pedido #" + numeroNotaLong + " - Documento auxiliar para conferencia interna.");
        infoLines.add("Total produtos: R$ " + formatMoney(totalProdutos) + " | Frete: R$ " + formatMoney(valorFrete));
        infoLines.add("Valor total da nota: R$ " + formatMoney(totalNota));
        infoLines.add("Forma de pagamento: " + paymentMethod);
        infoLines.add("Gerado em: " + LocalDateTime.now().format(DATE_TIME_FORMAT));

        int infoY = adicionaisTop - 38;
        for (String line : infoLines) {
            text(stream, "F2", 7, left + 6, infoY, line);
            infoY -= 11;
        }

        text(stream, "F2", 8, splitAdicional + 4, adicionaisTop - 26, "Reservado ao Fisco");
        text(stream, "F2", 7, splitAdicional + 4, adicionaisTop - 40, "Sem observacoes fiscais.");

        text(stream, "F2", 7, left + 4, adicionaisBottom - 10, "Obtenha o arquivo digital em www.ecommerce.com.br/nfe");

        stream.append("1 0 0 rg\n");
        centerText(stream, "F1", 8, left, right, adicionaisBottom - 10, "AMBIENTE DE HOMOLOGACAO - DOCUMENTO SEM VALOR FISCAL");
        stream.append("0 0 0 rg\n");

        text(stream, "F2", 7, right - 120, adicionaisBottom - 10, LocalDateTime.now().format(DATE_TIME_FORMAT));

        String streamContent = stream.toString();
        byte[] streamBytes = streamContent.getBytes(StandardCharsets.ISO_8859_1);

        StringBuilder content = new StringBuilder();
        List<Integer> objectPositions = new ArrayList<>();

        content.append("%PDF-1.4\n");

        objectPositions.add(content.length());
        content.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        objectPositions.add(content.length());
        content.append("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        objectPositions.add(content.length());
        content.append("3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources 4 0 R /MediaBox [0 0 595 842] /Contents 5 0 R >>\nendobj\n");

        objectPositions.add(content.length());
        content.append("4 0 obj\n<< /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >> /F2 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >>\nendobj\n");

        objectPositions.add(content.length());
        content.append("5 0 obj\n<< /Length ").append(streamBytes.length).append(" >>\nstream\n");
        content.append(streamContent);
        content.append("\nendstream\nendobj\n");

        int xrefPos = content.length();
        content.append("xref\n");
        content.append("0 6\n");
        content.append("0000000000 65535 f \n");
        for (Integer position : objectPositions) {
            content.append(String.format("%010d 00000 n \n", position));
        }

        content.append("trailer\n<< /Size 6 /Root 1 0 R >>\n");
        content.append("startxref\n");
        content.append(xrefPos).append("\n");
        content.append("%%EOF\n");

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(content.toString().getBytes(StandardCharsets.ISO_8859_1));
        }

        return pdfFile;
    }

    private static void rect(StringBuilder stream, int x, int y, int width, int height) {
        stream.append(x).append(' ').append(y).append(' ').append(width).append(' ').append(height).append(" re S\n");
    }

    private static void line(StringBuilder stream, int x1, int y1, int x2, int y2) {
        stream.append(x1).append(' ').append(y1).append(" m ").append(x2).append(' ').append(y2).append(" l S\n");
    }

    private static void text(StringBuilder stream, String font, int size, int x, int y, String value) {
        String sanitized = safeText(value, 180);
        if (sanitized.isBlank()) {
            return;
        }

        stream.append("BT /").append(font).append(' ').append(size).append(" Tf 1 0 0 1 ")
              .append(x).append(' ').append(y).append(" Tm (")
              .append(escapePdf(sanitized)).append(") Tj ET\n");
    }

    private static void centerText(StringBuilder stream, String font, int size, int startX, int endX, int y, String value) {
        String sanitized = safeText(value, 180);
        if (sanitized.isBlank()) {
            return;
        }

        double estimatedWidth = sanitized.length() * size * 0.48;
        int x = (int) Math.round(startX + ((endX - startX) - estimatedWidth) / 2.0);
        if (x < startX + 2) {
            x = startX + 2;
        }

        text(stream, font, size, x, y, sanitized);
    }

    private static void fakeBarcode(StringBuilder stream, int x, int y, int width, int height, String seed) {
        String numericSeed = digitsOnly(seed);
        if (numericSeed.isBlank()) {
            numericSeed = "12345678901234567890";
        }

        int cursor = x;
        int i = 0;
        while (cursor < x + width - 2) {
            int digit = numericSeed.charAt(i % numericSeed.length()) - '0';
            int barWidth = (digit % 2 == 0) ? 1 : 2;
            int gap = (digit % 3 == 0) ? 2 : 1;
            int barHeight = height - 2 - ((digit % 4 == 0) ? 4 : 0);
            if (barHeight < 8) {
                barHeight = 8;
            }

            stream.append(cursor).append(' ').append(y + 1).append(' ').append(barWidth).append(' ').append(barHeight).append(" re f\n");
            cursor += barWidth + gap;
            i++;
        }
    }

    private static String buildAddress(Order.SimpleUser user, String fallback) {
        if (user != null) {
            String street = safeText(user.getLogradouro(), 40);
            String number = safeText(user.getNumero(), 8);
            String complement = safeText(user.getComplemento(), 20);
            StringBuilder address = new StringBuilder();

            if (!street.isBlank()) {
                address.append(street);
            }
            if (!number.isBlank()) {
                if (address.length() > 0) {
                    address.append(", ");
                }
                address.append(number);
            }
            if (!complement.isBlank()) {
                if (address.length() > 0) {
                    address.append(" - ");
                }
                address.append(complement);
            }

            if (address.length() > 0) {
                return safeText(address.toString(), 72);
            }
        }

        String fallbackAddress = safeText(fallback, 72);
        return fallbackAddress.isBlank() ? "NAO INFORMADO" : fallbackAddress;
    }

    private static String buildProtocol(long numeroNota, LocalDateTime emissao) {
        long base = Math.abs((numeroNota * 999_983L) + (emissao.getDayOfYear() * 10_007L) + emissao.getSecond());
        long protocol = base % 1_000_000_000_000_000L;
        return String.format("%015d", protocol);
    }

    private static String buildAccessKey(long numeroNota, LocalDateTime emissao) {
        String base = emissao.format(DateTimeFormatter.ofPattern("yyMM"))
            + "12345678000190"
            + "35"
            + "55"
            + String.format("%03d", 1)
            + String.format("%09d", Math.abs(numeroNota % 1_000_000_000L))
            + "1";

        StringBuilder key = new StringBuilder(base);
        int seed = Math.abs((int) (numeroNota * 31 + emissao.getDayOfYear() * 17));
        while (key.length() < 43) {
            key.append(Math.abs(seed % 10));
            seed = (seed * 37) + 13;
        }

        String first43 = key.substring(0, 43);
        int dv = mod11(first43);
        return first43 + dv;
    }

    private static int mod11(String digits) {
        int weight = 2;
        int sum = 0;

        for (int i = digits.length() - 1; i >= 0; i--) {
            int value = digits.charAt(i) - '0';
            sum += value * weight;
            weight++;
            if (weight > 9) {
                weight = 2;
            }
        }

        int remainder = sum % 11;
        int dv = 11 - remainder;
        if (dv == 10 || dv == 11) {
            return 0;
        }
        return dv;
    }

    private static String formatAccessKey(String key) {
        String numeric = digitsOnly(key);
        if (numeric.isBlank()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < numeric.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(' ');
            }
            formatted.append(numeric.charAt(i));
        }
        return formatted.toString();
    }

    private static String formatMoney(BigDecimal value) {
        BigDecimal safeValue = value != null ? value : BigDecimal.ZERO;
        BigDecimal scaled = safeValue.setScale(2, RoundingMode.HALF_UP);
        String money = scaled.toPlainString().replace('.', ',');
        if (!money.contains(",")) {
            return money + ",00";
        }
        int decimals = money.length() - money.indexOf(',') - 1;
        if (decimals == 1) {
            return money + "0";
        }
        return money;
    }

    private static String safeText(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .replace('ª', 'a')
            .replace('º', 'o')
            .replaceAll("[^\\x20-\\x7E]", " ")
            .replaceAll("\\s+", " ")
            .trim();

        if (normalized.length() > maxLength) {
            return normalized.substring(0, maxLength);
        }
        return normalized;
    }

    private static String escapePdf(String value) {
        return value.replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)");
    }

    private static String digitsOnly(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }
}
