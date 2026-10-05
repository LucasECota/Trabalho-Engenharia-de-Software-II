package ru.ferramentas;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;
import ru.app.QrCode;
import ru.catraca.Catraca;
import ru.catraca.Decisao;
import ru.catraca.FilaPassagens;
import ru.catraca.Montagem;
import ru.catraca.RelogioFixo;
import ru.catraca.Replica;
import ru.catraca.Sincronizador;
import ru.catraca.Usuario;
import ru.catraca.Vinculo;
import ru.rede.ClienteHttp;

/** Simula um almoço na catraca, com ou sem rede. */
public final class Simular {
    private Simular() { }

    public static void main(String[] args) {
        boolean semRede = args.length > 0 && args[0].equals("--sem-rede");
        ClienteHttp.definirOnline(!semRede);
        System.out.println("Enlace: " + (semRede ? "CAÍDO" : "ok") + "\n");

        byte[] chaveCarla = "chave-do-app-da-carla".getBytes(StandardCharsets.UTF_8);
        Replica replica = new Replica();
        replica.cadastrar(new Usuario("2026001", "Ana", Vinculo.ALUNO, new BigDecimal("10.00")));
        replica.cadastrar(new Usuario("2026002", "Bruno", Vinculo.ALUNO, new BigDecimal("1.00")));
        replica.cadastrar(new Usuario("2026003", "Carla", Vinculo.BOLSISTA_INTEGRAL, BigDecimal.ZERO), chaveCarla);
        RelogioFixo relogio = new RelogioFixo(LocalDateTime.of(2026, 9, 29, 11, 45));
        FilaPassagens fila = new FilaPassagens();
        Catraca catraca = Montagem.criarCatraca(replica, fila, relogio, "SALDO");

        Supplier<String> qrCarla = () -> QrCode.gerar("2026003", chaveCarla, relogio.agora().toEpochSecond(ZoneOffset.UTC));
        Object[][] tentativas = {
            {"Ana, carteirinha", (Supplier<String>) () -> "CART:2026001"},
            {"Bruno (saldo R$ 1,00), carteirinha", (Supplier<String>) () -> "CART:2026002"},
            {"Carla (bolsista), QR do próprio app", qrCarla},
            {"Diego com a carteirinha EMPRESTADA da Carla", (Supplier<String>) () -> "CART:2026003"},
            {"Diego de novo, 1 min depois", (Supplier<String>) () -> "CART:2026003"},
            {"Carla volta com o QR, na mesma refeição", qrCarla},
        };
        for (Object[] t : tentativas) {
            @SuppressWarnings("unchecked") String leitura = ((Supplier<String>) t[1]).get();
            long t0 = System.nanoTime();
            Decisao d = catraca.passar(leitura);
            double ms = (System.nanoTime() - t0) / 1e6;
            System.out.printf("%-44s %-9s %-26s %.2f ms%n", t[0], d.liberar() ? "LIBERA" : "BLOQUEIA", d.motivo(), ms);
            relogio.avancar(Duration.ofMinutes(1));
        }
        System.out.println("\nPassagens na fila local: " + fila.tamanho());
        System.out.println("Sincronizadas agora: " + new Sincronizador(fila).sincronizar() + " | pendentes: " + fila.tamanho());
        if (semRede) {
            System.out.println("\nO enlace volta...");
            ClienteHttp.definirOnline(true);
            System.out.println("Sincronizadas agora: " + new Sincronizador(fila).sincronizar() + " | pendentes: " + fila.tamanho());
        }
    }
}
