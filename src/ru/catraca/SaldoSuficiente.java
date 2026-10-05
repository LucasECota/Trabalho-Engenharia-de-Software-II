package ru.catraca;

import java.math.BigDecimal;

/** Regra SALDO: entra quem tem saldo para pagar a tarifa. */
public class SaldoSuficiente implements RegraEntrada {
    @Override
    public Decisao avaliar(Usuario u, BigDecimal tarifa) {
        if (u.getSaldo().compareTo(tarifa) >= 0) return Decisao.libera(u.getMatricula());
        return Decisao.bloqueia("saldo insuficiente", u.getMatricula());
    }
}
