package ru.catraca;

import java.time.Duration;
import java.time.LocalDateTime;

/** Relógio controlável, para testes e simulação. */
public class RelogioFixo implements Relogio {
    private LocalDateTime t;

    public RelogioFixo(LocalDateTime t) { this.t = t; }
    @Override public LocalDateTime agora() { return t; }
    public void avancar(Duration d) { t = t.plus(d); }
}
