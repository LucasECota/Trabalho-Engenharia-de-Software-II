package ru.catraca;

import java.math.BigDecimal;

/**
 * Montagem da catraca: o lugar que escolhe QUAIS implementações usar.
 *
 * Os testes de aceitação criam a catraca SEMPRE por aqui. O parâmetro
 * "regra" vem da configuração do RU: "SALDO" ou "TOLERA_NEGATIVO" (pedido 2).
 * Configuração inválida é recusada aqui, na montagem, não na passagem.
 */
public final class Montagem {
    private Montagem() { }

    private static final BigDecimal LIMITE_NEGATIVO = new BigDecimal("5.00");

    public static Catraca criarCatraca(Replica replica, FilaPassagens fila, Relogio relogio, String regra) {
        RegraEntrada entrada = new UmaPorRefeicao(traduzir(regra), relogio);                      // pedido 3: bolsista, 1×/refeição
        Identificador identificador = new IdentificadorComTitularidade(
                new IdentificadorPorCarteirinha(), replica, relogio);                             // pedido 3: só o titular
        return new Catraca(new Leitor(), identificador, replica, fila, relogio, entrada);
    }

    private static RegraEntrada traduzir(String regra) {
        return switch (regra) {
            case "SALDO" -> new SaldoSuficiente();
            case "TOLERA_NEGATIVO" -> new ToleraSaldoNegativo(LIMITE_NEGATIVO);
            default -> throw new IllegalArgumentException("regra de entrada desconhecida: " + regra);
        };
    }
}
