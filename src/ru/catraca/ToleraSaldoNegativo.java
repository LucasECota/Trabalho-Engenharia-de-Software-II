package ru.catraca;

import java.math.BigDecimal;

/** Regra TOLERA_NEGATIVO: entra quem fica com saldo ≥ −limite depois de pagar a tarifa. */
public class ToleraSaldoNegativo implements RegraEntrada {
    private final BigDecimal limite;

    public ToleraSaldoNegativo(BigDecimal limite) { this.limite = limite; }

    @Override
    public Decisao avaliar(Usuario u, BigDecimal tarifa) {
        if (u.getSaldo().subtract(tarifa).compareTo(limite.negate()) >= 0) return Decisao.libera(u.getMatricula());
        return Decisao.bloqueia("saldo insuficiente", u.getMatricula());
    }
}
