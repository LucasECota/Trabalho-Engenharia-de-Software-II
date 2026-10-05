package ru.catraca;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.rede.ClienteHttp;
import ru.rede.SemConexao;

/**
 * Sincronizador com o servidor central.
 *
 * SEGREDO: o protocolo de comunicação com o Serviço de Acesso. É a ÚNICA classe
 * da catraca que pode falar com a rede — e a regra de fronteira em
 * TestesFronteira garante isso.
 */
public class Sincronizador {
    private static final String URL = "https://ru.exemplo.ufop.br/acesso/passagens";
    private final FilaPassagens fila;

    public Sincronizador(FilaPassagens fila) { this.fila = fila; }

    /** Envia as passagens pendentes; sem conexão, para sem erro. Devolve quantas foram confirmadas. */
    public int sincronizar() {
        List<Integer> enviados = new ArrayList<>();
        for (Map.Entry<Integer, Passagem> e : fila.pendentes().entrySet()) {
            Passagem p = e.getValue();
            try {
                ClienteHttp.post(URL, Map.of("matricula", p.matricula(),
                        "instante", p.instante().toString(), "valor", p.valor().toPlainString()));
            } catch (SemConexao sc) {
                break;
            }
            enviados.add(e.getKey());
        }
        fila.confirmar(enviados);
        return enviados.size();
    }
}
