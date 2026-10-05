package ru.catraca;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Passagem(String matricula, LocalDateTime instante, BigDecimal valor) { }
