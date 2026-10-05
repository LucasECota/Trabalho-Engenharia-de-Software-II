package ru.catraca;

/**
 * Contrato da identificação: de uma credencial lida, a matrícula de quem está
 * passando — ou null, se a credencial não prova ninguém.
 *
 * SEGREDO de quem implementa: como se prova quem é a pessoa na frente da catraca.
 */
public interface Identificador {
    String identificar(Credencial credencial);
}
