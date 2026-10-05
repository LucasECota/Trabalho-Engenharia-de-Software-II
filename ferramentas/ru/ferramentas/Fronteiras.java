package ru.ferramentas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai as dependências entre as classes do projeto lendo o código-fonte
 * (imports, nomes qualificados e classes do mesmo pacote) e oferece a base
 * para escrever regras de fronteira como testes.
 */
public final class Fronteiras {
    private Fronteiras() { }

    private static final Pattern IMPORT = Pattern.compile("^\\s*import\\s+(static\\s+)?(ru(?:\\.\\w+)+?)(\\.\\*)?\\s*;", Pattern.MULTILINE);
    private static final Pattern QUALIFICADO = Pattern.compile("\\bru(?:\\.[a-z]\\w*)+\\.[A-Z]\\w*");
    private static final Pattern NOME = Pattern.compile("\\b[A-Z]\\w*\\b");

    static String pacote(String fqn) { return fqn.substring(0, fqn.lastIndexOf('.')); }
    static String simples(String fqn) { return fqn.substring(fqn.lastIndexOf('.') + 1); }

    /** Classes DO PROJETO usadas por fqn. */
    public static Set<String> dependencias(String fqn) {
        Map<String, java.nio.file.Path> todas = Fontes.classes();
        String codigo = Fontes.identificadores(fqn);
        Set<String> deps = new TreeSet<>();
        Matcher m = IMPORT.matcher(codigo);
        while (m.find()) {
            String alvo = m.group(2);
            if (m.group(3) != null) {                       // import ru.x.*;
                for (String c : todas.keySet()) if (pacote(c).equals(alvo)) deps.add(c);
            } else if (m.group(1) != null) {                // import static ru.x.C.metodo;
                String cls = pacote(alvo);
                if (todas.containsKey(cls)) deps.add(cls);
            } else if (todas.containsKey(alvo)) deps.add(alvo);
        }
        m = QUALIFICADO.matcher(codigo);
        while (m.find()) if (todas.containsKey(m.group())) deps.add(m.group());
        String pac = pacote(fqn);
        Set<String> nomes = new TreeSet<>();
        m = NOME.matcher(codigo.replaceAll("(?m)^\\s*(package|import)\\s.*$", ""));
        while (m.find()) nomes.add(m.group());
        for (String c : todas.keySet())
            if (pacote(c).equals(pac) && nomes.contains(simples(c))) deps.add(c);
        deps.remove(fqn);
        return deps;
    }

    static boolean casa(String nome, String... prefixos) {
        for (String p : prefixos) if (nome.equals(p) || nome.startsWith(p + ".")) return true;
        return false;
    }

    /** "a -> b" para cada classe dentro de origem (pacote ou classe) que usa algo em proibidos. */
    public static List<String> violacoes(String origem, String[] proibidos, String... exceto) {
        List<String> v = new ArrayList<>();
        for (String c : Fontes.classes().keySet()) {
            if (!casa(c, origem) || casa(c, exceto)) continue;
            for (String d : dependencias(c)) if (casa(d, proibidos)) v.add(c + " -> " + d);
        }
        return v;
    }

    /**
     * Regra de fronteira: nenhuma classe em "origem" (exceto as listadas) pode usar
     * algo em "proibidos". Lança AssertionError listando as violações.
     *
     *   Fronteiras.naoPodeUsar("ru.catraca", new String[]{"ru.rede"}, "ru.catraca.Sincronizador");
     */
    public static void naoPodeUsar(String origem, String[] proibidos, String... exceto) {
        List<String> v = violacoes(origem, proibidos, exceto);
        if (!v.isEmpty()) throw new AssertionError("Fronteira violada:\n      " + String.join("\n      ", v));
    }
}
