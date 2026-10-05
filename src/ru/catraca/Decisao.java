package ru.catraca;

public record Decisao(boolean liberar, String motivo, String matricula) {
    public static Decisao libera(String matricula) { return new Decisao(true, "ok", matricula); }
    public static Decisao bloqueia(String motivo, String matricula) { return new Decisao(false, motivo, matricula); }
}
