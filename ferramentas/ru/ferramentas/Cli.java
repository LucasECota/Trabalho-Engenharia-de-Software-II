package ru.ferramentas;

import java.util.Arrays;
import ru.testes.Executor;

/** Ponto de entrada dos scripts rodar.sh / rodar.bat. */
public final class Cli {
    private Cli() { }

    public static void main(String[] args) throws Exception {
        String cmd = args.length > 0 ? args[0] : "ajuda";
        String[] resto = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        switch (cmd) {
            case "testes" -> System.exit(Executor.rodar(resto));
            case "simular" -> Simular.main(resto);
            case "grafo" -> Grafo.main(resto);
            case "diff" -> Diff.main(resto);
            default -> System.out.println("""
                Uso:  rodar <comando>
                  testes                     todos os testes
                  testes pedido1 fronteira   só as suítes indicadas
                  simular [--sem-rede]       um almoço na catraca
                  grafo [pacote]             dependências reais (ex.: grafo ru.creditos)
                  diff [--marcar | --desde-marco]
                """);
        }
    }
}
