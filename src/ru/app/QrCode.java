package ru.app;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Geração do QR code dinâmico no app do aluno (já pronto — não precisa mudar).
 *
 * Conteúdo (o que vem depois de "QR:" na leitura):   matricula:instante:assinatura
 *  - instante: segundos desde a época, no momento em que o QR foi gerado;
 *  - assinatura: HMAC-SHA256, em hexadecimal, de "matricula:instante" com a chave
 *    secreta daquele usuário. A mesma chave está na réplica da catraca
 *    (Replica.chaveQr), sincronizada pelo servidor.
 *
 * Verificar um QR, portanto, NÃO exige rede: recalcule a assinatura com a chave
 * local, compare (MessageDigest.isEqual) e confira se o instante é recente.
 */
public final class QrCode {
    private QrCode() { }

    public static String assinar(String matricula, long instante, byte[] chave) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(chave, "HmacSHA256"));
            byte[] h = mac.doFinal((matricula + ":" + instante).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** A leitura completa, como o leitor da catraca a receberia. */
    public static String gerar(String matricula, byte[] chave, long instante) {
        return "QR:" + matricula + ":" + instante + ":" + assinar(matricula, instante, chave);
    }
}
