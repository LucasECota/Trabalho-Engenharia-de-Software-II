package ru.avisos;

import ru.cardapio.Semana;

/** Painel da gestão no site (simulado). */
public class PainelWeb {
    public void atualizar(Semana s) { Avisos.REGISTRO.add("painel:" + s.inicio()); }
}
