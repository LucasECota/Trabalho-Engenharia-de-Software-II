package ru.testes;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import ru.app.QrCode;
import ru.catraca.Catraca;
import ru.catraca.Decisao;
import ru.catraca.FilaPassagens;
import ru.catraca.Montagem;
import ru.catraca.RelogioFixo;
import ru.catraca.Replica;
import ru.catraca.Usuario;
import ru.catraca.Vinculo;
import ru.ferramentas.Fontes;
import ru.rede.ClienteHttp;

/**
 * TESTE DE ACEITAÇÃO · pedido 3 · RNF8 — a isenção do bolsista só vale para o titular.
 *
 *   "Carteirinha emprestada ou credencial copiada não autoriza a entrada; cada
 *    bolsista passa no máximo uma vez por refeição. Vale também com o enlace
 *    caído, sem violar os tempos do RNF2."
 *
 * Os testes usam o QR dinâmico do app (ru.app.QrCode) porque é a alternativa que
 * um protótipo consegue verificar. O ADR de vocês deve comparar as alternativas
 * (foto, QR dinâmico, biometria). Não edite.
 */
public class TestesPedido3 extends Teste {
    static final byte[] CHAVE_CARLA = "chave-do-app-da-carla".getBytes(StandardCharsets.UTF_8);
    Replica replica;
    FilaPassagens fila;
    RelogioFixo relogio;
    Catraca catraca;

    @Override protected void setUp() {
        ClienteHttp.definirOnline(false);                     // sempre sem rede
        replica = new Replica();
        replica.cadastrar(new Usuario("2026001", "Ana", Vinculo.ALUNO, new BigDecimal("10.00")));
        replica.cadastrar(new Usuario("2026003", "Carla", Vinculo.BOLSISTA_INTEGRAL, BigDecimal.ZERO), CHAVE_CARLA);
        relogio = new RelogioFixo(LocalDateTime.of(2026, 9, 29, 11, 45));
        fila = new FilaPassagens();
        catraca = Montagem.criarCatraca(replica, fila, relogio, "SALDO");
    }

    @Override protected void tearDown() { ClienteHttp.definirOnline(true); }

    String qr(byte[] chave, long atrasoSegundos) {
        long instante = relogio.agora().toEpochSecond(ZoneOffset.UTC) - atrasoSegundos;
        return QrCode.gerar("2026003", chave, instante);
    }

    public void testBolsistaComQrValidoEntraSemPagar() {
        Decisao d = catraca.passar(qr(CHAVE_CARLA, 0));
        verdadeiro(d.liberar(), "Carla, com o QR do próprio app, deveria entrar (motivo: " + d.motivo() + ")");
        igual(0, replica.usuario("2026003").getSaldo().signum(), "bolsista não paga");
    }

    public void testCarteirinhaDeBolsistaNaoBasta() {
        falso(catraca.passar("CART:2026003").liberar(), "a catraca não tem como saber se quem segura a carteirinha é o titular");
    }

    public void testQrExpiradoENegado() {
        falso(catraca.passar(qr(CHAVE_CARLA, 31)).liberar(), "um print do QR repassado 31 s depois não pode valer");
    }

    public void testQrAssinadoComOutraChaveENegado() {
        falso(catraca.passar(qr("chave-falsa".getBytes(StandardCharsets.UTF_8), 0)).liberar(), "assinatura falsa");
    }

    public void testQrAdulteradoENegado() {
        String l = qr(CHAVE_CARLA, 0);
        String adulterado = l.substring(0, l.length() - 1) + (l.endsWith("0") ? "1" : "0");
        falso(catraca.passar(adulterado).liberar(), "QR adulterado");
    }

    public void testUmaPassagemPorRefeicao() {
        verdadeiro(catraca.passar(qr(CHAVE_CARLA, 0)).liberar(), "primeira passagem no almoço");
        relogio.avancar(Duration.ofMinutes(2));
        falso(catraca.passar(qr(CHAVE_CARLA, 0)).liberar(), "segunda vez no mesmo almoço");
        relogio.avancar(Duration.ofHours(6));                  // 17:47 · jantar
        verdadeiro(catraca.passar(qr(CHAVE_CARLA, 0)).liberar(), "no jantar pode");
    }

    public void testAlunoPaganteContinuaEntrandoComCarteirinha() {
        verdadeiro(catraca.passar("CART:2026001").liberar(), "aluno pagante com carteirinha");
    }

    public void testTudoSemRedeEEmAte1Segundo() {
        long t0 = System.nanoTime();
        for (int i = 0; i < 20; i++) { catraca.passar(qr(CHAVE_CARLA, 0)); catraca.passar("CART:2026003"); }
        verdadeiro((System.nanoTime() - t0) / 40.0 / 1e9 < 1.0, "cada decisão deve levar menos de 1 s (RNF2)");
    }

    public void testExtensaoEntrouForaDaCatraca() {
        String codigo = Fontes.codigo("ru.catraca.Catraca").toLowerCase();
        for (String termo : new String[]{"bolsista", "qrcode", "hmac", "refeic"})
            falso(codigo.contains(termo), "o RNF8 deve entrar por extensão, não por edição da Catraca (achei: " + termo + ")");
    }
}
