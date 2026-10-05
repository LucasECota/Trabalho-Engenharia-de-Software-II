package ru.gateways.pagapix;

import java.util.Map;

/**
 * SDK (simulado) do gateway PagaPix — o contrato ATUAL do RU.
 * Código de terceiro: NÃO edite. Valores em centavos, nomes em inglês.
 *
 * Tokens de teste: "tok_ok" -> PAID · "tok_recusado" -> DECLINED · "tok_erro" -> ERROR
 */
public class PagaPixClient {
    private static int seq = 1;
    private final String apiKey;

    public PagaPixClient(String apiKey) { this.apiKey = apiKey; }

    public PagaPixResponse createCharge(long amountCents, String source, Map<String, String> metadata) {
        String id = String.format("ch_%06d", seq++);
        if ("tok_recusado".equals(source)) return new PagaPixResponse(Status.DECLINED, id, "insufficient_funds");
        if ("tok_erro".equals(source)) return new PagaPixResponse(Status.ERROR, id, "gateway_timeout");
        return new PagaPixResponse(Status.PAID, id, "");
    }
}
