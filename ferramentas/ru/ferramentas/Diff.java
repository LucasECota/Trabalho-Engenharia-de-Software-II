package ru.ferramentas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Lista os arquivos .java de src que mudaram.
 *   diff                em relação ao esqueleto original
 *   diff --marcar       guarda o estado atual como "marco"
 *   diff --desde-marco  em relação ao último marco
 */
public final class Diff {
    static final Path ORIGINAL = Paths.get("ferramentas", "original.txt");
    static final Path MARCO = Paths.get(".marco.txt");
    private Diff() { }

    static Map<String, String> hashes() throws Exception {
        Map<String, String> h = new TreeMap<>();
        for (Map.Entry<String, Path> e : Fontes.classes().entrySet()) {
            byte[] b = Fontes.texto(e.getValue()).getBytes(StandardCharsets.UTF_8);
            byte[] d = MessageDigest.getInstance("SHA-256").digest(b);
            StringBuilder sb = new StringBuilder();
            for (byte x : d) sb.append(String.format("%02x", x));
            h.put("src/" + e.getKey().replace('.', '/') + ".java", sb.toString());
        }
        return h;
    }

    static void gravar(Path p, Map<String, String> h) throws IOException {
        List<String> l = new ArrayList<>();
        h.forEach((k, v) -> l.add(k + " " + v));
        Files.write(p, l, StandardCharsets.UTF_8);
    }

    static Map<String, String> ler(Path p) throws IOException {
        Map<String, String> h = new TreeMap<>();
        for (String l : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            String[] x = l.trim().split(" ");
            if (x.length == 2) h.put(x[0], x[1]);
        }
        return h;
    }

    public static void main(String[] args) throws Exception {
        String op = args.length > 0 ? args[0] : "";
        if (op.equals("--gerar")) { gravar(ORIGINAL, hashes()); System.out.println("manifesto gerado"); return; }
        if (op.equals("--marcar")) {
            gravar(MARCO, hashes());
            System.out.println("Marco gravado. Faça a mudança e rode:  diff --desde-marco");
            return;
        }
        Map<String, String> base;
        if (op.equals("--desde-marco")) {
            if (!Files.exists(MARCO)) { System.out.println("Nenhum marco gravado. Rode antes:  diff --marcar"); return; }
            base = ler(MARCO);
            System.out.println("Mudanças desde o marco:\n");
        } else {
            base = ler(ORIGINAL);
            System.out.println("Mudanças desde o esqueleto original:\n");
        }
        Map<String, String> atual = hashes();
        List<String> alt = new ArrayList<>(), novos = new ArrayList<>(), rem = new ArrayList<>();
        for (String k : atual.keySet()) {
            if (!base.containsKey(k)) novos.add(k);
            else if (!base.get(k).equals(atual.get(k))) alt.add(k);
        }
        for (String k : base.keySet()) if (!atual.containsKey(k)) rem.add(k);
        imprimir("Alterados", alt); imprimir("Novos", novos); imprimir("Removidos", rem);
    }

    static void imprimir(String t, List<String> l) {
        System.out.println(t + " (" + l.size() + ")");
        l.forEach(x -> System.out.println("   " + x));
    }
}
