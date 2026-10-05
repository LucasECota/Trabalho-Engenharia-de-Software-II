package ru.creditos;

import java.math.BigDecimal;
import java.util.Map;
import ru.creditos.pagamento.Pagamento;
import ru.creditos.pagamento.ResultadoPagamento;

/**
 * Compra de créditos pelo app (RF1).
 * Regra de crédito: cobra por um contrato do RU ({@link Pagamento}), credita e audita.
 * Não conhece nenhum fornecedor de pagamento.
 */
public class ServicoCompra {
    private final Saldos saldos;
    private final Auditoria auditoria;
    private final Pagamento pagamento;

    public ServicoCompra(Saldos saldos, Auditoria auditoria, Pagamento pagamento) {
        this.saldos = saldos;
        this.auditoria = auditoria;
        this.pagamento = pagamento;
    }

    public Recibo comprar(String matricula, BigDecimal valor, String tokenCartao) {
        if (valor.signum() <= 0) throw new IllegalArgumentException("valor da compra deve ser positivo");

        ResultadoPagamento r = pagamento.cobrar(matricula, valor, tokenCartao);

        if (r.aprovado()) {
            saldos.creditar(matricula, valor);
            auditoria.registrar(Map.of("tipo", "credito", "matricula", matricula,
                    "valor", valor.toPlainString(), "transacao", r.transacao()));
            return new Recibo(true, matricula, valor, r.transacao(), "");
        }
        auditoria.registrar(Map.of("tipo", "compra_negada", "matricula", matricula,
                "valor", valor.toPlainString(), "transacao", r.transacao(), "motivo", r.motivo()));
        return new Recibo(false, matricula, valor, r.transacao(), r.motivo());
    }
}
