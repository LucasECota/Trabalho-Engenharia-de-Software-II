package ru.testes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ru.cardapio.Cardapio;
import ru.cardapio.MontagemCardapio;
import ru.cardapio.Semana;
import ru.ferramentas.Fronteiras;

/**
 * TESTE DE ACEITAÇÃO · pedido 4 · RNF10 — avisar sobre o cardápio.
 *
 *   "Quando a semana é publicada, app, painel e e-mail da gestão são avisados.
 *    Um novo interessado (ex.: o bot do DCE) pode ser acrescentado sem alterar
 *    a classe Cardapio, que não depende de nenhum canal de aviso."
 *
 * O teste não impõe nomes: procura em Cardapio um método público que receba um
 * observador — uma interface com um único método abstrato que recebe a Semana.
 * Não edite.
 */
public class TestesPedido4 extends Teste {
    final Semana semana = new Semana(LocalDate.of(2026, 10, 5), List.of("arroz", "feijão"));

    public void testCardapioNaoDependeDosCanais() {
        Set<String> deps = Fronteiras.dependencias("ru.cardapio.Cardapio");
        deps.removeIf(d -> !d.startsWith("ru.avisos."));
        igual(Set.of(), deps, "Cardapio ainda conhece canais de aviso");
    }

    public void testNovoInteressadoAssinaSemEditarOCardapio() throws Exception {
        Cardapio c = MontagemCardapio.criar();
        List<Object> recebidos = new ArrayList<>();
        assinar(c, recebidos);
        c.publicar(semana);
        igual(List.of(semana), recebidos, "o novo interessado deveria receber a semana publicada");
    }

    public void testVariosInteressadosNovos() throws Exception {
        Cardapio c = MontagemCardapio.criar();
        List<Object> a = new ArrayList<>(), b = new ArrayList<>();
        assinar(c, a);
        assinar(c, b);
        c.publicar(semana);
        verdadeiro(a.size() == 1 && b.size() == 1, "os dois interessados deveriam ser avisados uma vez");
    }

    /** Acha o método de assinatura e registra um observador falso, criado em tempo de execução. */
    static void assinar(Cardapio c, List<Object> recebidos) throws Exception {
        for (Method m : Cardapio.class.getMethods()) {
            if (m.getParameterCount() != 1 || Modifier.isStatic(m.getModifiers())) continue;
            Class<?> tipo = m.getParameterTypes()[0];
            if (!tipo.isInterface()) continue;
            List<Method> abstratos = new ArrayList<>();
            for (Method x : tipo.getMethods()) if (Modifier.isAbstract(x.getModifiers())) abstratos.add(x);
            if (abstratos.size() != 1) continue;
            Method alvo = abstratos.get(0);
            if (alvo.getParameterCount() != 1 || !alvo.getParameterTypes()[0].isAssignableFrom(Semana.class)) continue;
            Object obs = Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, (p, met, args) -> {
                if (met.equals(alvo)) { recebidos.add(args[0]); return null; }
                if (met.getName().equals("hashCode")) return System.identityHashCode(p);
                if (met.getName().equals("equals")) return p == args[0];
                if (met.getName().equals("toString")) return "ObservadorDeTeste";
                return null;
            });
            m.invoke(c, obs);
            return;
        }
        falhar("não encontrei em Cardapio um método público que receba um observador "
                + "(uma interface com um único método abstrato que recebe a Semana)");
    }
}
