package ru.testes;

import ru.ferramentas.Fronteiras;

/**
 * Regras de fronteira: a arquitetura escrita como teste.
 *
 * Cada regra é a EVIDÊNCIA, no código, de uma decisão registrada em
 * docs/decisoes/. Se alguém criar um atalho que atravessa a fronteira, o teste
 * fica vermelho. Para ver as dependências reais:  rodar grafo <pacote>
 *
 * ESTE é o único arquivo de teste que vocês editam: acrescentem regras.
 */
public class TestesFronteira extends Teste {

    // ---------------- ADR-001 · autorização na borda (exemplo resolvido) ----------------

    /** Só o Sincronizador fala com a rede: é o que garante que a catraca decide sem enlace (RNF2). */
    public void testCatracaSoSincronizadorFalaComARede() {
        Fronteiras.naoPodeUsar("ru.catraca", new String[]{"ru.rede"}, "ru.catraca.Sincronizador");
    }

    /** A catraca não chama código do servidor central. */
    public void testCatracaNaoDependeDoServidor() {
        Fronteiras.naoPodeUsar("ru.catraca", new String[]{"ru.creditos", "ru.cardapio", "ru.avisos"});
    }

    // ---------------- CICLO 1 (pedido 1) · escrevam aqui a regra da API de Créditos --------
    // Ela deve FALHAR com o código atual — confiram antes de refatorar.

    /** ADR-002: dentro da API de Créditos, só os adaptadores (subpacote pagamento) conhecem os SDKs (RNF5). */
    public void testCreditosSoOsAdaptadoresUsamGateways() {
        Fronteiras.naoPodeUsar("ru.creditos", new String[]{"ru.gateways"}, "ru.creditos.pagamento");
    }

    /** ADR-002: os adaptadores traduzem o SDK; não conhecem a regra de crédito (sentido único de dependência). */
    public void testAdaptadoresNaoConhecemARegraDeCredito() {
        Fronteiras.naoPodeUsar("ru.creditos.pagamento",
                new String[]{"ru.creditos.ServicoCompra", "ru.creditos.Saldos", "ru.creditos.Auditoria", "ru.creditos.Recibo"});
    }

    // ---------------- CICLOS 2 a 4 · acrescentem aqui as regras que as decisões pedirem -----
    // O ciclo 4 (pedido 4) exige pelo menos uma: a do Cardapio.

    /** ADR-003: a Catraca não conhece regras de entrada concretas nem decoradores de bolsista; só o contrato (RNF9, RNF8). */
    public void testCatracaNaoConheceRegrasConcretas() {
        Fronteiras.naoPodeUsar("ru.catraca.Catraca", new String[]{
                "ru.catraca.SaldoSuficiente", "ru.catraca.ToleraSaldoNegativo", "ru.catraca.UmaPorRefeicao",
                "ru.catraca.IdentificadorComTitularidade", "ru.catraca.IdentificadorPorCarteirinha", "ru.app"});
    }

    /** ADR-005: o Cardapio não conhece canais de aviso; só a montagem os assina (RNF10). */
    public void testCardapioSoAMontagemConheceOsCanais() {
        Fronteiras.naoPodeUsar("ru.cardapio", new String[]{"ru.avisos"}, "ru.cardapio.MontagemCardapio");
    }

}
