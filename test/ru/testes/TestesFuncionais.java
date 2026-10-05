package ru.testes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import ru.avisos.Avisos;
import ru.cardapio.Cardapio;
import ru.cardapio.MontagemCardapio;
import ru.cardapio.Semana;
import ru.catraca.Catraca;
import ru.catraca.FilaPassagens;
import ru.catraca.Montagem;
import ru.catraca.Refeicoes;
import ru.catraca.RelogioFixo;
import ru.catraca.Replica;
import ru.catraca.Sincronizador;
import ru.catraca.Usuario;
import ru.catraca.Vinculo;
import ru.rede.ClienteHttp;

/** Comportamento que NÃO pode quebrar em nenhuma refatoração. Não edite. */
public class TestesFuncionais extends Teste {
    Replica replica;
    FilaPassagens fila;
    Catraca catraca;

    @Override protected void setUp() {
        ClienteHttp.definirOnline(true);
        replica = new Replica();
        replica.cadastrar(new Usuario("A1", "Ana", Vinculo.ALUNO, new BigDecimal("10.00")));
        replica.cadastrar(new Usuario("A2", "Bia", Vinculo.ALUNO, new BigDecimal("1.00")));
        fila = new FilaPassagens();
        catraca = Montagem.criarCatraca(replica, fila, new RelogioFixo(LocalDateTime.of(2026, 9, 29, 12, 0)), "SALDO");
    }

    @Override protected void tearDown() { ClienteHttp.definirOnline(true); }

    public void testAlunoComSaldoEntraEPaga() {
        verdadeiro(catraca.passar("CART:A1").liberar(), "Ana tem saldo e deveria entrar");
        igual(new BigDecimal("7.00"), replica.usuario("A1").getSaldo(), "saldo após a passagem");
        igual(1, fila.tamanho(), "passagens na fila");
    }

    public void testAlunoSemSaldoNaoEntra() {
        falso(catraca.passar("CART:A2").liberar(), "Bia não tem saldo");
        igual(0, fila.tamanho(), "passagens na fila");
    }

    public void testDesconhecidoELeituraInvalida() {
        falso(catraca.passar("CART:999").liberar(), "matrícula desconhecida");
        falso(catraca.passar("lixo").liberar(), "leitura inválida");
    }

    public void testSemRedeDecideESincronizaDepois() {
        ClienteHttp.definirOnline(false);
        verdadeiro(catraca.passar("CART:A1").liberar(), "sem rede a catraca decide sozinha (RNF2)");
        igual(0, new Sincronizador(fila).sincronizar(), "nada sincroniza sem rede");
        ClienteHttp.definirOnline(true);
        igual(1, new Sincronizador(fila).sincronizar(), "sincroniza quando a rede volta");
        igual(0, fila.tamanho(), "fila vazia depois de sincronizar");
    }

    public void testRefeicoes() {
        igual("almoco", Refeicoes.refeicaoDe(LocalDateTime.of(2026, 9, 29, 11, 0)), "11h");
        igual("jantar", Refeicoes.refeicaoDe(LocalDateTime.of(2026, 9, 29, 18, 0)), "18h");
        igual(null, Refeicoes.refeicaoDe(LocalDateTime.of(2026, 9, 29, 15, 0)), "15h");
    }

    public void testCardapioPublicadoAvisaAppPainelEEmail() {
        Avisos.REGISTRO.clear();
        Cardapio c = MontagemCardapio.criar();
        Semana s = new Semana(LocalDate.of(2026, 10, 5), List.of("arroz", "feijão", "frango"));
        c.publicar(s);
        igual(s, c.atual(), "semana publicada");
        verdadeiro(Avisos.REGISTRO.containsAll(List.of("app:2026-10-05", "painel:2026-10-05", "email:2026-10-05")),
                "app, painel e e-mail devem ser avisados; registro: " + Avisos.REGISTRO);
        igual(3, Avisos.REGISTRO.size(), "cada canal avisado uma única vez");
    }
}
