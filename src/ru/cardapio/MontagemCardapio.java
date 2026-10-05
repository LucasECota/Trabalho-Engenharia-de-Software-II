package ru.cardapio;

import ru.avisos.AppAluno;
import ru.avisos.EmailGestao;
import ru.avisos.PainelWeb;

/**
 * Montagem do cardápio. Os testes de aceitação criam o Cardapio SEMPRE por aqui.
 * É o único lugar que conhece os canais de aviso: novo interessado = uma linha de assinatura.
 */
public final class MontagemCardapio {
    private MontagemCardapio() { }

    public static Cardapio criar() {
        Cardapio c = new Cardapio();
        AppAluno app = new AppAluno();
        PainelWeb painel = new PainelWeb();
        EmailGestao email = new EmailGestao();
        c.assinar(app::mostrarNotificacao);
        c.assinar(painel::atualizar);
        c.assinar(email::enviarResumo);
        return c;
    }
}
