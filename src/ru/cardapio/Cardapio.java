package ru.cardapio;

import java.util.ArrayList;
import java.util.List;

/**
 * Cardápio da semana (RF3), publicado pela nutricionista.
 * Sujeito do Observador: conhece só {@link ObservadorCardapio}; quem assina é decidido de fora
 * (ver {@link MontagemCardapio}). Política: a falha de um canal não impede o aviso aos demais.
 */
public class Cardapio {
    private Semana atual;
    private final List<ObservadorCardapio> observadores = new ArrayList<>();

    public void assinar(ObservadorCardapio observador) { observadores.add(observador); }

    public void publicar(Semana s) {
        this.atual = s;
        for (ObservadorCardapio o : observadores) {
            try {
                o.semanaPublicada(s);
            } catch (RuntimeException e) {
                System.err.println("aviso do cardápio falhou: " + e);
            }
        }
    }

    public Semana atual() { return atual; }
}
