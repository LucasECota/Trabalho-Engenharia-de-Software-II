package ru.cardapio;

/** Contrato de quem quer ser avisado quando a semana é publicada (RNF10). */
public interface ObservadorCardapio {
    void semanaPublicada(Semana semana);
}
