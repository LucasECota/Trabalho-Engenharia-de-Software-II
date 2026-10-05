package ru.testes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import ru.catraca.Catraca;
import ru.catraca.FilaPassagens;
import ru.catraca.Montagem;
import ru.catraca.RelogioFixo;
import ru.catraca.Replica;
import ru.catraca.Usuario;
import ru.catraca.Vinculo;
import ru.ferramentas.Fontes;
import ru.rede.ClienteHttp;

/**
 * TESTE DE ACEITAÇÃO · pedido 2 · RNF9 — a regra de entrada muda por decisão do conselho.
 *
 *   "Além da regra atual (SALDO: entra quem tem saldo para a tarifa), a catraca
 *    passa a aceitar TOLERA_NEGATIVO: entra quem fica com saldo de até −R$ 5,00
 *    depois de pagar. A regra vigente é escolhida na montagem; trocar ou criar
 *    uma regra não altera a classe Catraca."
 *
 * Não edite. Tarifa do aluno: R$ 3,00.
 */
public class TestesPedido2 extends Teste {
    Replica replica;
    FilaPassagens fila;
    RelogioFixo relogio;

    @Override protected void setUp() {
        ClienteHttp.definirOnline(false);
        replica = new Replica();
        replica.cadastrar(new Usuario("ANA", "Ana", Vinculo.ALUNO, new BigDecimal("10.00")));
        replica.cadastrar(new Usuario("BIA", "Bia", Vinculo.ALUNO, new BigDecimal("1.00")));    // fica com -2,00
        replica.cadastrar(new Usuario("DUDA", "Duda", Vinculo.ALUNO, new BigDecimal("-2.00"))); // fica com -5,00
        replica.cadastrar(new Usuario("CAIO", "Caio", Vinculo.ALUNO, new BigDecimal("-2.50"))); // ficaria com -5,50
        fila = new FilaPassagens();
        relogio = new RelogioFixo(LocalDateTime.of(2026, 9, 29, 12, 0));
    }

    @Override protected void tearDown() { ClienteHttp.definirOnline(true); }

    Catraca catraca(String regra) { return Montagem.criarCatraca(replica, fila, relogio, regra); }

    public void testRegraSaldoContinuaComoAntes() {
        Catraca c = catraca("SALDO");
        verdadeiro(c.passar("CART:ANA").liberar(), "SALDO: Ana tem saldo");
        falso(c.passar("CART:BIA").liberar(), "SALDO: Bia não tem saldo para a tarifa");
    }

    public void testToleraNegativoLiberaQuemFicaAcimaDoLimite() {
        Catraca c = catraca("TOLERA_NEGATIVO");
        verdadeiro(c.passar("CART:BIA").liberar(), "TOLERA_NEGATIVO: Bia fica com -2,00 e deve entrar");
        igual(new BigDecimal("-2.00"), replica.usuario("BIA").getSaldo(), "saldo da Bia");
    }

    public void testToleraNegativoLiberaNoLimiteExato() {
        verdadeiro(catraca("TOLERA_NEGATIVO").passar("CART:DUDA").liberar(), "Duda fica com exatamente -5,00: entra");
    }

    public void testToleraNegativoBloqueiaAlemDoLimite() {
        falso(catraca("TOLERA_NEGATIVO").passar("CART:CAIO").liberar(), "Caio ficaria com -5,50: não entra");
        igual(new BigDecimal("-2.50"), replica.usuario("CAIO").getSaldo(), "saldo do Caio não muda");
    }

    public void testCatracaNaoConheceAsRegras() {
        String codigo = Fontes.codigo("ru.catraca.Catraca");
        for (String termo : new String[]{"switch", "\"SALDO\"", "TOLERA", "compareTo"})
            falso(codigo.contains(termo), "a classe Catraca ainda contém a regra (achei: " + termo + ")");
    }
}
