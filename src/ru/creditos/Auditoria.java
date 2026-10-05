package ru.creditos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Trilha de auditoria (RNF3). SEGREDO: onde e em que formato a trilha é gravada. */
public class Auditoria {
    private final List<Map<String, String>> eventos = new ArrayList<>();

    public void registrar(Map<String, String> evento) { eventos.add(evento); }
    public List<Map<String, String>> eventos() { return eventos; }
    public Map<String, String> ultimo() { return eventos.get(eventos.size() - 1); }
}
