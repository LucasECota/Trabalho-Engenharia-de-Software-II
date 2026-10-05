package ru.creditos.pagamento;

import java.math.BigDecimal;
import ru.gateways.cobrafacil.CobraFacil;
import ru.gateways.cobrafacil.CobraFacilErro;
import ru.gateways.cobrafacil.RetornoCobranca;

/** Adaptador do SDK CobraFácil (reais, retorno booleano, exceção verificada) para o contrato {@link Pagamento}. */
class CobraFacilAdapter implements Pagamento {
    private final CobraFacil cliente = new CobraFacil("ru-ufop", "segredo");

    @Override
    public ResultadoPagamento cobrar(String matricula, BigDecimal valor, String token) {
        try {
            RetornoCobranca r = cliente.cobrar(valor, token, matricula);
            if (r.ok()) return new ResultadoPagamento(true, r.idTransacao(), "");
            return new ResultadoPagamento(false, r.idTransacao(), "recusado: " + r.erro());
        } catch (CobraFacilErro e) {
            return new ResultadoPagamento(false, "", "falha no gateway: " + e.getMessage());
        }
    }
}
