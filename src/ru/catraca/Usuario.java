package ru.catraca;

import java.math.BigDecimal;

public class Usuario {
    private final String matricula;
    private final String nome;
    private final Vinculo vinculo;
    private BigDecimal saldo;

    public Usuario(String matricula, String nome, Vinculo vinculo, BigDecimal saldo) {
        this.matricula = matricula;
        this.nome = nome;
        this.vinculo = vinculo;
        this.saldo = saldo;
    }

    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public Vinculo getVinculo() { return vinculo; }
    public BigDecimal getSaldo() { return saldo; }
    public boolean isBolsista() { return vinculo == Vinculo.BOLSISTA_INTEGRAL; }

    void debitar(BigDecimal valor) { saldo = saldo.subtract(valor); }
}
