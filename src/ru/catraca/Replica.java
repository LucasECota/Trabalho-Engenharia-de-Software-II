package ru.catraca;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Réplica local de usuários, saldos e tarifas.
 *
 * SEGREDO: como e onde os dados ficam guardados na catraca. No diagrama de
 * contêineres a réplica é um SQLite; aqui é um mapa em memória.
 */
public class Replica {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, byte[]> chavesQr = new HashMap<>();
    private final Map<Vinculo, BigDecimal> tarifas = new EnumMap<>(Vinculo.class);

    public Replica() {
        tarifas.put(Vinculo.ALUNO, new BigDecimal("3.00"));
        tarifas.put(Vinculo.SERVIDOR, new BigDecimal("8.00"));
        tarifas.put(Vinculo.VISITANTE, new BigDecimal("15.00"));
        tarifas.put(Vinculo.BOLSISTA_INTEGRAL, new BigDecimal("0.00"));
    }

    public void cadastrar(Usuario u) { usuarios.put(u.getMatricula(), u); }
    public void cadastrar(Usuario u, byte[] chaveQr) { cadastrar(u); chavesQr.put(u.getMatricula(), chaveQr); }

    public Usuario usuario(String matricula) { return usuarios.get(matricula); }
    public BigDecimal tarifa(Vinculo v) { return tarifas.get(v); }

    /** Chave secreta do app de cada usuário, sincronizada do servidor. Ainda não é usada na catraca. */
    public byte[] chaveQr(String matricula) { return chavesQr.get(matricula); }

    public void debitar(String matricula, BigDecimal valor) { usuarios.get(matricula).debitar(valor); }
}
