package ru.catraca;

/**
 * Leitor de carteirinha e de QR code.
 *
 * SEGREDO: o protocolo do hardware. Hoje o leitor entrega linhas como
 * "CART:2026123" ou "QR:<conteúdo>". Se o fabricante mudar, só esta classe muda.
 */
public class Leitor {
    public Credencial ler(String eventoBruto) throws LeituraInvalida {
        String t = eventoBruto == null ? "" : eventoBruto.trim();
        if (t.startsWith("CART:")) return new Credencial("carteirinha", t.substring(5));
        if (t.startsWith("QR:")) return new Credencial("qr", t.substring(3));
        throw new LeituraInvalida("leitura não reconhecida: " + eventoBruto);
    }
}
