package ru.catraca;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import ru.app.QrCode;

/**
 * Decorador de {@link Identificador} (RNF8): prova que quem passa é o titular.
 * - QR dinâmico: verifica a assinatura HMAC com a chave local e a validade (±30 s), sem rede;
 * - carteirinha: delega ao identificador envolvido, mas recusa bolsista (isenção exige o QR).
 * SEGREDO: como se prova a titularidade (hoje QR assinado; poderia ser biometria).
 */
public class IdentificadorComTitularidade implements Identificador {
    private static final long TOLERANCIA_SEGUNDOS = 30;

    private final Identificador base;
    private final Replica replica;
    private final Relogio relogio;

    public IdentificadorComTitularidade(Identificador base, Replica replica, Relogio relogio) {
        this.base = base;
        this.replica = replica;
        this.relogio = relogio;
    }

    @Override
    public String identificar(Credencial c) {
        if ("qr".equals(c.tipo())) return identificarPorQr(c.valor());
        String matricula = base.identificar(c);
        if (matricula == null) return null;
        Usuario u = replica.usuario(matricula);
        return (u != null && u.isBolsista()) ? null : matricula;
    }

    private String identificarPorQr(String valor) {
        String[] p = valor.split(":");
        if (p.length != 3) return null;
        String matricula = p[0];
        long instante;
        try { instante = Long.parseLong(p[1]); } catch (NumberFormatException e) { return null; }
        byte[] chave = replica.chaveQr(matricula);
        if (chave == null) return null;
        byte[] esperada = QrCode.assinar(matricula, instante, chave).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(esperada, p[2].getBytes(StandardCharsets.UTF_8))) return null;
        long agora = relogio.agora().toEpochSecond(ZoneOffset.UTC);
        return Math.abs(agora - instante) <= TOLERANCIA_SEGUNDOS ? matricula : null;
    }
}
