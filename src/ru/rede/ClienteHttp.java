package ru.rede;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP SIMULADO. Não abre conexão de verdade: guarda o que seria enviado
 * em SERVIDOR e permite simular a queda do enlace com definirOnline(false).
 */
public final class ClienteHttp {
    public static final List<Map<String, String>> SERVIDOR = new ArrayList<>();
    private static boolean online = true;

    private ClienteHttp() { }

    public static void definirOnline(boolean valor) { online = valor; }
    public static boolean online() { return online; }

    public static void post(String url, Map<String, String> dados) {
        if (!online) throw new SemConexao("enlace indisponível: " + url);
        Map<String, String> copia = new HashMap<>(dados);
        copia.put("_url", url);
        SERVIDOR.add(copia);
    }
}
