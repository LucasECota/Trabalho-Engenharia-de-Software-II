package ru.ferramentas;

import java.util.Set;

/** Mostra as dependências reais de um pacote (ou do projeto todo). */
public final class Grafo {
    private Grafo() { }

    public static void main(String[] args) {
        String pacote = args.length > 0 ? args[0] : null;
        System.out.println("Dependências " + (pacote == null ? "do projeto" : "de " + pacote) + "\n");
        for (String c : Fontes.classes().keySet()) {
            if (pacote != null && !Fronteiras.casa(c, pacote)) continue;
            Set<String> deps = Fronteiras.dependencias(c);
            if (deps.isEmpty()) continue;
            System.out.println(c);
            for (String d : deps) {
                boolean fora = pacote != null && !Fronteiras.casa(d, pacote);
                System.out.println("   -> " + d + (fora ? "    <-- sai do pacote" : ""));
            }
        }
    }
}
