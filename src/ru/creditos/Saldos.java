package ru.creditos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** Contas de crédito no servidor central. SEGREDO: onde os saldos ficam (aqui, memória). */
public class Saldos {
    private final Map<String, BigDecimal> contas = new HashMap<>();

    public BigDecimal saldo(String matricula) { return contas.getOrDefault(matricula, BigDecimal.ZERO); }
    public void creditar(String matricula, BigDecimal valor) { contas.put(matricula, saldo(matricula).add(valor)); }
}
