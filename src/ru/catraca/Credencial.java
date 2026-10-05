package ru.catraca;

/** O que o leitor entregou, já decodificado. tipo: "carteirinha" ou "qr". */
public record Credencial(String tipo, String valor) { }
