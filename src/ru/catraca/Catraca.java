package ru.catraca;

import java.math.BigDecimal;

/**
 * Autorização de entrada — o coração da catraca.
 *
 * Os colaboradores chegam pelo construtor (injeção de dependência): leitor,
 * identificador, réplica, fila e relógio escondem, cada um, uma decisão que
 * pode mudar. A regra de fronteira em TestesFronteira garante que esta classe
 * não fala com a rede — é o que torna verdadeira, no código, a decisão ADR-001.
 *
 * A regra de entrada (Strategy) também chega pronta: a Catraca só a chama.
 */
public class Catraca {
    private final Leitor leitor;
    private final Identificador identificador;
    private final Replica replica;
    private final FilaPassagens fila;
    private final Relogio relogio;
    private final RegraEntrada regra;

    public Catraca(Leitor leitor, Identificador identificador, Replica replica,
                   FilaPassagens fila, Relogio relogio, RegraEntrada regra) {
        this.leitor = leitor;
        this.identificador = identificador;
        this.replica = replica;
        this.fila = fila;
        this.relogio = relogio;
        this.regra = regra;
    }

    public Decisao passar(String eventoBruto) {
        Credencial credencial;
        try {
            credencial = leitor.ler(eventoBruto);
        } catch (LeituraInvalida e) {
            return Decisao.bloqueia("leitura inválida", null);
        }
        String matricula = identificador.identificar(credencial);
        if (matricula == null) return Decisao.bloqueia("credencial não aceita", null);

        Usuario u = replica.usuario(matricula);
        if (u == null) return Decisao.bloqueia("usuário desconhecido", matricula);

        BigDecimal tarifa = replica.tarifa(u.getVinculo());
        Decisao d = regra.avaliar(u, tarifa);
        if (!d.liberar()) return d;

        replica.debitar(matricula, tarifa);
        fila.registrar(new Passagem(matricula, relogio.agora(), tarifa));
        return Decisao.libera(matricula);
    }
}
