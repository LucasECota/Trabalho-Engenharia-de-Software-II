# ADR-005 · O Cardápio avisa quem assinou, sem conhecê-los

**Status:** aceita · **Requisito que motivou:** RNF10 (pedido 4)

## Contexto

O RNF10 diz que, quando a semana é publicada, o app, o painel e o e-mail da gestão são avisados, e que um novo interessado (o bot do DCE já pediu, e outros vão pedir) pode ser acrescentado **sem alterar a classe `Cardapio`**, que não pode depender de nenhum canal de aviso. Hoje o `Cardapio` cria `AppAluno`, `PainelWeb` e `EmailGestao` e chama um por um.

| Cenário | A · Cardapio chama cada canal | B · Cardapio avisa assinantes |
|---|---|---|
| RNF10 · interessado novo sem editar o Cardapio | não atende | atende (uma linha em `MontagemCardapio`) |
| Cardapio sem dependência de canais | não | sim |
| Testar o Cardapio sem canais reais | difícil | fácil (assinante falso) |
| Clareza do fluxo (quem é avisado?) | explícita no Cardapio | indireta (está na montagem) |
| Falha de um canal | uma exceção interrompe os seguintes | exige uma política explícita |

**Ponto de sensibilidade:** quem conhece a lista de canais.
**Ponto de trade-off:** o acoplamento fraco e a extensibilidade sobem; a legibilidade do fluxo e a previsibilidade de ordem e falhas caem.

## Decisão

O `Cardapio` passa a conhecer só uma interface, `ObservadorCardapio` (`semanaPublicada(Semana)`), guarda a lista de assinantes e oferece o método `assinar(...)`. Ao publicar, ele avisa todo mundo da lista. Quem inscreve os três canais atuais é a `MontagemCardapio`, usando referência de método (`app::mostrarNotificacao` etc.), então os canais nem precisaram mudar.

**Política de falha:** se um canal lançar exceção, ela é capturada e registrada, e os outros continuam sendo avisados (o e-mail fora do ar não pode impedir o aviso no app). O padrão é o Observador, e o princípio é o acoplamento fraco.

## Consequências

- **Ganha:** interessado novo é uma linha na montagem; o `Cardapio` dá pra testar sem canais; e os três canais continuam sendo avisados exatamente uma vez.
- **Paga:** o fluxo fica indireto (ler o `Cardapio` não diz quem é avisado); os avisos são síncronos e na ordem de inscrição, então um canal lento atrasa os seguintes; e uma falha só é registrada em `stderr`, sem reenvio nem fila, ou seja, o canal que falhou perde o aviso.
- **Estrutura que nasce:** nenhum contêiner novo. No **nível 3** do Painel Web/App Móvel aparece o `ObservadorCardapio` (`docs/c4-cardapio.md`).
- **Evidência:** `TestesFronteira.testCardapioSoAMontagemConheceOsCanais` (só a `MontagemCardapio` usa `ru.avisos`; o `Cardapio` original violava isso) e `TestesPedido4`.

## Alternativa descartada

**A · `Cardapio` chama cada canal:** é simples e explícito, mas cada interessado novo obrigaria a reabrir o `Cardapio`, que acabaria conhecendo todos os canais do sistema.
