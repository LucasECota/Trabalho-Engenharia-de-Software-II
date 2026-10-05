package ru.creditos.pagamento;

import java.math.BigDecimal;
import java.util.Map;
import ru.gateways.pagapix.PagaPixClient;
import ru.gateways.pagapix.PagaPixResponse;
import ru.gateways.pagapix.Status;

/** Adaptador do SDK PagaPix (centavos, createCharge, Status) para o contrato {@link Pagamento}. */
class PagaPixAdapter implements Pagamento {
    private final PagaPixClient cliente = new PagaPixClient("pk_live_ru_ufop");

    @Override
    public ResultadoPagamento cobrar(String matricula, BigDecimal valor, String token) {
        PagaPixResponse r = cliente.createCharge(valor.movePointRight(2).longValueExact(), token,
                Map.of("matricula", matricula, "origem", "ru-digital"));
        if (r.status() == Status.PAID) return new ResultadoPagamento(true, r.chargeId(), "");
        String motivo = (r.status() == Status.DECLINED ? "recusado: " : "falha no gateway: ") + r.declineReason();
        return new ResultadoPagamento(false, r.chargeId(), motivo);
    }
}
