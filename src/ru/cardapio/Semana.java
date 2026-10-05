package ru.cardapio;

import java.time.LocalDate;
import java.util.List;

public record Semana(LocalDate inicio, List<String> pratos) { }
