package ru.catraca;

import java.math.BigDecimal;

/**
 * Contrato da regra de entrada: "este usuário pode entrar pagando esta tarifa?".
 * SEGREDO de quem implementa: o critério que o conselho do RU aprovou.
 */
public interface RegraEntrada {
    Decisao avaliar(Usuario usuario, BigDecimal tarifa);
}
