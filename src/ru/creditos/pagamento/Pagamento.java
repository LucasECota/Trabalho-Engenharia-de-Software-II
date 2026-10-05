package ru.creditos.pagamento;

import java.math.BigDecimal;

/**
 * Contrato de cobrança do RU, no vocabulário do RU (não do fornecedor).
 * SEGREDO de quem implementa: como o fornecedor é chamado (unidade do valor, status, exceções).
 */
public interface Pagamento {
    ResultadoPagamento cobrar(String matricula, BigDecimal valor, String token);
}
