package ru.creditos;

import ru.creditos.pagamento.FabricaPagamento;

/**
 * Montagem da API de Créditos: escolhe as implementações concretas.
 * Os testes de aceitação criam o serviço SEMPRE por aqui.
 * RNF5: trocar o gateway de pagamento deve alterar um único arquivo.
 */
public final class MontagemCreditos {
    private MontagemCreditos() { }

    public static final String GATEWAY_PADRAO = "cobrafacil";

    public static ServicoCompra criarServicoCompra(String gateway, Saldos saldos, Auditoria auditoria) {
        return new ServicoCompra(saldos, auditoria, FabricaPagamento.criar(gateway));
    }

    public static ServicoCompra criarServicoCompra(Saldos saldos, Auditoria auditoria) {
        return criarServicoCompra(GATEWAY_PADRAO, saldos, auditoria);
    }
}
