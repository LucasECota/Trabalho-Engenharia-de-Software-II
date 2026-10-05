package ru.testes;

import java.util.List;
import java.util.Set;
import ru.creditos.MontagemCreditos;
import ru.ferramentas.Fontes;
import ru.ferramentas.Fronteiras;

/**
 * TESTE DE ACEITAÇÃO · pedido 1 · RNF5 — trocar o gateway de pagamento.
 *
 *   "Trocar o gateway de pagamento altera um único arquivo da API de Créditos,
 *    sem tocar nas regras de crédito, e os testes existentes continuam passando."
 *
 * Não edite. Cumprido quando fica verde E "rodar diff --desde-marco", na troca
 * do gateway padrão, mostra um único arquivo alterado.
 */
public class TestesPedido1 extends Teste {
    static final List<String> REGRAS_DE_CREDITO = List.of(
            "ru.creditos.ServicoCompra", "ru.creditos.Saldos", "ru.creditos.Auditoria", "ru.creditos.Recibo");
    static final List<String> VOCABULARIO_DO_FORNECEDOR = List.of(
            "PAID", "DECLINED", "chargeId", "createCharge", "declineReason", "PagaPix", "CobraFacil", "RetornoCobranca");

    public void testRegrasDeCreditoNaoUsamGateways() {
        for (String c : REGRAS_DE_CREDITO) {
            Set<String> deps = Fronteiras.dependencias(c);
            deps.removeIf(d -> !d.startsWith("ru.gateways."));
            igual(Set.of(), deps, c + " ainda usa um gateway");
        }
    }

    public void testServicoCompraNaoFalaALinguaDoFornecedor() {
        String codigo = Fontes.codigo("ru.creditos.ServicoCompra");
        for (String termo : VOCABULARIO_DO_FORNECEDOR)
            falso(codigo.contains(termo), "ServicoCompra ainda usa um termo do fornecedor: " + termo);
    }

    public void testPagaPixContinuaDisponivel() {
        verdadeiro(MontagemCreditos.criarServicoCompra("pagapix", new ru.creditos.Saldos(), new ru.creditos.Auditoria()) != null,
                "o PagaPix precisa continuar funcionando até o fim do contrato");
    }

    /** Os MESMOS testes da compra, agora com o novo gateway. */
    public static class CompraComCobraFacil extends TestesCompra {
        @Override protected String gateway() { return "cobrafacil"; }
    }
}
