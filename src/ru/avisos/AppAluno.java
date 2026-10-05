package ru.avisos;

import ru.cardapio.Semana;

/** Notificação push no app do aluno (simulada). */
public class AppAluno {
    public void mostrarNotificacao(Semana s) { Avisos.REGISTRO.add("app:" + s.inicio()); }
}
