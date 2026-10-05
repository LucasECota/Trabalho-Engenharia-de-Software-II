package ru.avisos;

import ru.cardapio.Semana;

/** E-mail de resumo para a gestão (simulado). */
public class EmailGestao {
    public void enviarResumo(Semana s) { Avisos.REGISTRO.add("email:" + s.inicio()); }
}
