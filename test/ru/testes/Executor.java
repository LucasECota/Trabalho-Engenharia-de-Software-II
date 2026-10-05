package ru.testes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Executor de testes: descobre os métodos public void testXxx() de cada classe. */
public final class Executor {
    private Executor() { }

    static final Map<String, List<Class<? extends Teste>>> SUITES = new LinkedHashMap<>();
    static {
        SUITES.put("funcionais", List.of(TestesFuncionais.class, TestesCompra.class));
        SUITES.put("fronteira", List.of(TestesFronteira.class));
        SUITES.put("pedido1", List.of(TestesPedido1.class, TestesPedido1.CompraComCobraFacil.class));
        SUITES.put("pedido2", List.of(TestesPedido2.class));
        SUITES.put("pedido3", List.of(TestesPedido3.class));
        SUITES.put("pedido4", List.of(TestesPedido4.class));
    }

    public static int rodar(String[] nomes) {
        List<String> escolhidas = nomes.length == 0 ? new ArrayList<>(SUITES.keySet()) : Arrays.asList(nomes);
        int tot = 0, ok = 0, falhas = 0, erros = 0;
        List<String> detalhes = new ArrayList<>();
        for (String nome : escolhidas) {
            List<Class<? extends Teste>> classes = SUITES.get(nome);
            if (classes == null) { System.out.println("Suíte desconhecida: " + nome + "  (use: " + SUITES.keySet() + ")"); return 2; }
            int sOk = 0, sTot = 0;
            for (Class<? extends Teste> c : classes) {
                Method[] ms = c.getMethods();
                Arrays.sort(ms, Comparator.comparing(Method::getName));
                for (Method m : ms) {
                    if (!m.getName().startsWith("test") || m.getParameterCount() != 0 || Modifier.isStatic(m.getModifiers())) continue;
                    sTot++; tot++;
                    String id = nome + " · " + c.getSimpleName() + "." + m.getName();
                    Teste t = null;
                    try {
                        t = c.getDeclaredConstructor().newInstance();
                        t.setUp();
                        m.invoke(t);
                        ok++; sOk++;
                    } catch (InvocationTargetException ite) {
                        Throwable cause = ite.getCause();
                        if (cause instanceof AssertionError) { falhas++; detalhes.add("FALHA  " + id + "\n       " + cause.getMessage()); }
                        else { erros++; detalhes.add("ERRO   " + id + "\n       " + cause); }
                    } catch (Throwable e) {
                        Throwable cause = e instanceof InvocationTargetException ? e.getCause() : e;
                        erros++; detalhes.add("ERRO   " + id + "\n       " + cause);
                    } finally {
                        try { if (t != null) t.tearDown(); } catch (Exception ignored) { }
                    }
                }
            }
            System.out.printf("%-11s %2d testes · %s%n", nome, sTot, sOk == sTot ? "OK" : (sTot - sOk) + " não passaram");
        }
        if (!detalhes.isEmpty()) { System.out.println(); detalhes.forEach(System.out::println); }
        System.out.printf("%nRodados %d testes: %d ok, %d falhas, %d erros%n", tot, ok, falhas, erros);
        return falhas + erros == 0 ? 0 : 1;
    }
}
