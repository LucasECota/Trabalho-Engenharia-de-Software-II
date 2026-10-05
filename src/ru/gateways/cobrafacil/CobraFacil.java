package ru.gateways.cobrafacil;

import java.math.BigDecimal;

/**
 * SDK (simulado) do gateway CobraFácil — o NOVO contrato.
 * Código de terceiro: NÃO edite. Repare que tudo difere do PagaPix: valor em
 * reais (BigDecimal), retorno com booleano, falha técnica como exceção checada.
 *
 * Tokens de teste: "tok_ok" -> ok · "tok_recusado" -> ok=false, erro="saldo_insuficiente"
 *                  "tok_erro" -> lança CobraFacilErro
 */
public class CobraFacil {
    private static int seq = 1;

    public CobraFacil(String usuario, String senha) { }

    public RetornoCobranca cobrar(BigDecimal valorReais, String tokenCartao, String referencia) throws CobraFacilErro {
        if ("tok_erro".equals(tokenCartao)) throw new CobraFacilErro("indisponível");
        String id = String.format("CF-%06d", seq++);
        if ("tok_recusado".equals(tokenCartao)) return new RetornoCobranca(false, id, "saldo_insuficiente");
        return new RetornoCobranca(true, id, null);
    }
}
