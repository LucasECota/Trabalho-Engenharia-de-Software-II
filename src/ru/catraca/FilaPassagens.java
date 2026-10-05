package ru.catraca;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fila persistente de passagens.
 *
 * SEGREDO: o formato em disco. Neste protótipo, fica só em memória.
 */
public class FilaPassagens {
    private final Map<Integer, Passagem> itens = new LinkedHashMap<>();
    private int proximoId = 1;

    public int registrar(Passagem p) { itens.put(proximoId, p); return proximoId++; }
    public Map<Integer, Passagem> pendentes() { return new LinkedHashMap<>(itens); }
    public void confirmar(List<Integer> ids) { ids.forEach(itens::remove); }
    public int tamanho() { return itens.size(); }
    List<Integer> ids() { return new ArrayList<>(itens.keySet()); }
}
