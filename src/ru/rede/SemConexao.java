package ru.rede;

/** O enlace entre o prédio do RU e o servidor central está indisponível. */
public class SemConexao extends RuntimeException {
    public SemConexao(String msg) { super(msg); }
}
