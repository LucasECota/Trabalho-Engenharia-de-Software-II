package ru.ferramentas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/** Acesso ao código-fonte do projeto (pasta src), para as ferramentas e os testes de arquitetura. */
public final class Fontes {
    public static final Path SRC = Paths.get("src");
    private Fontes() { }

    /** FQN -> caminho, para todas as classes em src. */
    public static Map<String, Path> classes() {
        Map<String, Path> m = new TreeMap<>();
        try (Stream<Path> s = Files.walk(SRC)) {
            s.filter(p -> p.toString().endsWith(".java")).forEach(p -> {
                String rel = SRC.relativize(p).toString().replace('\\', '/');
                m.put(rel.substring(0, rel.length() - 5).replace('/', '.'), p);
            });
        } catch (IOException e) {
            throw new IllegalStateException("rode a partir da pasta do projeto (a que tem src/)", e);
        }
        return m;
    }

    public static String texto(Path p) {
        try {
            return new String(Files.readAllBytes(p), StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Código da classe sem comentários (strings mantidas). */
    public static String codigo(String fqn) {
        Path p = classes().get(fqn);
        if (p == null) throw new IllegalArgumentException("classe não encontrada em src: " + fqn);
        return semComentarios(texto(p));
    }

    static String semComentarios(String s) {
        StringBuilder out = new StringBuilder();
        int i = 0, n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (c == '"' ) {                         // string literal: copia inteira
                int j = i + 1;
                while (j < n && s.charAt(j) != '"') { if (s.charAt(j) == '\\') j++; j++; }
                out.append(s, i, Math.min(j + 1, n)); i = j + 1;
            } else if (c == '\'' ) {
                int j = i + 1;
                while (j < n && s.charAt(j) != '\'') { if (s.charAt(j) == '\\') j++; j++; }
                out.append(s, i, Math.min(j + 1, n)); i = j + 1;
            } else if (c == '/' && i + 1 < n && s.charAt(i + 1) == '/') {
                while (i < n && s.charAt(i) != '\n') i++;
            } else if (c == '/' && i + 1 < n && s.charAt(i + 1) == '*') {
                int j = s.indexOf("*/", i + 2); i = j < 0 ? n : j + 2; out.append(' ');
            } else { out.append(c); i++; }
        }
        return out.toString();
    }

    /** Código sem comentários E sem o conteúdo das strings (para achar identificadores). */
    public static String identificadores(String fqn) {
        return codigo(fqn).replaceAll("\"(\\\\.|[^\"\\\\])*\"", "\"\"");
    }
}
