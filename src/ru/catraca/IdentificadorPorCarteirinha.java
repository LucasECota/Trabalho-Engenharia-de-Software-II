package ru.catraca;

/**
 * Identificação fraca: o número da carteirinha basta. Qualquer pessoa com a
 * carteirinha de outra passa como se fosse o titular.
 */
public class IdentificadorPorCarteirinha implements Identificador {
    @Override
    public String identificar(Credencial c) {
        return "carteirinha".equals(c.tipo()) ? c.valor() : null;   // QR ainda não é aceito
    }
}
