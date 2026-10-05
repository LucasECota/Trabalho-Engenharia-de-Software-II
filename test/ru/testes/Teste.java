package ru.testes;

import java.util.Objects;

/** Base mínima para os testes (sem JUnit, para não precisar instalar nada). */
public abstract class Teste {
    protected void setUp() throws Exception { }
    protected void tearDown() throws Exception { }

    protected static void verdadeiro(boolean cond, String msg) { if (!cond) throw new AssertionError(msg); }
    protected static void falso(boolean cond, String msg) { if (cond) throw new AssertionError(msg); }
    protected static void igual(Object esperado, Object obtido, String msg) {
        if (!Objects.equals(esperado, obtido))
            throw new AssertionError(msg + " — esperado: " + esperado + ", obtido: " + obtido);
    }
    protected static void falhar(String msg) { throw new AssertionError(msg); }
}
