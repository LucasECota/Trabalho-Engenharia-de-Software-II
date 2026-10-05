package ru.catraca;

import java.time.LocalDateTime;
import java.time.LocalTime;

/** Horários das refeições. Utilitário: pode ser útil no pedido 3. */
public final class Refeicoes {
    private Refeicoes() { }

    /** "almoco", "jantar" ou null (fora de horário). */
    public static String refeicaoDe(LocalDateTime instante) {
        LocalTime h = instante.toLocalTime();
        if (!h.isBefore(LocalTime.of(10, 30)) && !h.isAfter(LocalTime.of(14, 0))) return "almoco";
        if (!h.isBefore(LocalTime.of(17, 0)) && !h.isAfter(LocalTime.of(19, 30))) return "jantar";
        return null;
    }
}
