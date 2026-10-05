package ru.creditos.pagamento;

/** Resultado de uma cobrança. Em recusa ou falha, {@code motivo} já vem pronto para a auditoria. */
public record ResultadoPagamento(boolean aprovado, String transacao, String motivo) { }
