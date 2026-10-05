package ru.catraca;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Decorador de {@link RegraEntrada}: depois da regra envolvida, o bolsista integral
 * só passa uma vez por refeição (RNF8). Os demais vínculos não são afetados.
 * SEGREDO: como se lembra quem já passou (aqui, conjunto em memória local da catraca).
 */
public class UmaPorRefeicao implements RegraEntrada {
    private final RegraEntrada interna;
    private final Relogio relogio;
    private final Set<String> jaPassaram = new HashSet<>();

    public UmaPorRefeicao(RegraEntrada interna, Relogio relogio) {
        this.interna = interna;
        this.relogio = relogio;
    }

    @Override
    public Decisao avaliar(Usuario u, BigDecimal tarifa) {
        Decisao d = interna.avaliar(u, tarifa);
        if (!d.liberar() || !u.isBolsista()) return d;
        var agora = relogio.agora();
        String refeicao = Refeicoes.refeicaoDe(agora);
        String chave = u.getMatricula() + "|" + agora.toLocalDate() + "|" + (refeicao == null ? "fora" : refeicao);
        if (!jaPassaram.add(chave)) return Decisao.bloqueia("já passou nesta refeição", u.getMatricula());
        return d;
    }
}
