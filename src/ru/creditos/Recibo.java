package ru.creditos;

import java.math.BigDecimal;

public record Recibo(boolean aprovado, String matricula, BigDecimal valor, String transacao, String motivo) { }
