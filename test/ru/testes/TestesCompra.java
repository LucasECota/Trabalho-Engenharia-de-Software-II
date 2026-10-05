package ru.testes;

import java.math.BigDecimal;
import ru.creditos.Auditoria;
import ru.creditos.MontagemCreditos;
import ru.creditos.Recibo;
import ru.creditos.Saldos;
import ru.creditos.ServicoCompra;

/** Comportamento da compra de créditos. Vale para QUALQUER gateway. Não edite. */
public class TestesCompra extends Teste {
    Saldos saldos;
    Auditoria auditoria;
    ServicoCompra servico;

    protected String gateway() { return "pagapix"; }

    @Override protected void setUp() {
        saldos = new Saldos();
        auditoria = new Auditoria();
        servico = MontagemCreditos.criarServicoCompra(gateway(), saldos, auditoria);
    }

    public void testCompraAprovadaCreditaEAudita() {
        Recibo r = servico.comprar("A1", new BigDecimal("20.00"), "tok_ok");
        verdadeiro(r.aprovado(), "compra deveria ser aprovada");
        igual(new BigDecimal("20.00"), saldos.saldo("A1"), "saldo creditado");
        igual("credito", auditoria.ultimo().get("tipo"), "evento de auditoria");
    }

    public void testCompraRecusadaNaoCreditaMasAudita() {
        Recibo r = servico.comprar("A1", new BigDecimal("20.00"), "tok_recusado");
        falso(r.aprovado(), "compra deveria ser recusada");
        igual(BigDecimal.ZERO, saldos.saldo("A1"), "saldo não pode mudar");
        igual("compra_negada", auditoria.ultimo().get("tipo"), "evento de auditoria");
    }

    public void testFalhaTecnicaNaoCredita() {
        Recibo r = servico.comprar("A1", new BigDecimal("20.00"), "tok_erro");
        falso(r.aprovado(), "falha técnica não aprova");
        igual(BigDecimal.ZERO, saldos.saldo("A1"), "saldo não pode mudar");
    }

    public void testValorInvalido() {
        try {
            servico.comprar("A1", BigDecimal.ZERO, "tok_ok");
            falhar("valor zero deveria lançar IllegalArgumentException");
        } catch (IllegalArgumentException esperado) { }
    }
}
