# ADR-005 · O Cardápio avisa quem assinou, sem conhecê-los

**Status:** aceita · **Requisito que motivou:** RNF10 (pedido 4)

## Contexto

RNF10: quando a semana é publicada, app, painel e e-mail da gestão são avisados; um novo interessado (o bot do DCE já pediu; outros virão) pode ser acrescentado **sem alterar a classe `Cardapio`**, que não depende de nenhum canal de aviso. Hoje o `Cardapio` cria `AppAluno`, `PainelWeb` e `EmailGestao` e chama cada um.

| Cenário | A · Cardapio chama cada canal | B · Cardapio avisa assinantes |
|---|---|---|
| RNF10 · interessado novo sem editar o Cardapio | não atende | atende (uma linha em `MontagemCardapio`) |
| Cardapio sem dependência de canais | não | sim |
| Testar o Cardapio sem canais reais | difícil | fácil (assinante falso) |
| Clareza do fluxo (quem é avisado?) | explícita no Cardapio | indireta (está na montagem) |
| Falha de um canal | uma exceção interrompe os seguintes | exige política explícita |

**Ponto de sensibilidade:** quem conhece a lista de canais.
**Ponto de trade-off:** acoplamento fraco e extensibilidade sobem; a legibilidade do fluxo e a previsibilidade da ordem/falhas caem.

## Decisão

O `Cardapio` conhece só uma interface, `ObservadorCardapio` (`semanaPublicada(Semana)`), mantém a lista de assinantes e oferece `assinar(...)`; ao publicar, avisa todos. Quem inscreve os três canais atuais é `MontagemCardapio`, por referência de método (`app::mostrarNotificacao` etc.), então os canais nem mudam. **Política de falha:** a exceção de um canal é capturada e registrada, e os demais continuam sendo avisados (o e-mail fora do ar não pode impedir o aviso no app). Padrão: Observador; princípio: acoplamento fraco.

## Consequências

- **Ganha:** interessado novo = uma linha na montagem; `Cardapio` testável sem canais; os três canais seguem avisados exatamente uma vez.
- **Paga:** fluxo indireto (ler `Cardapio` não diz quem é avisado); avisos são síncronos e em ordem de inscrição, então um canal lento atrasa os seguintes; uma falha é só registrada em `stderr` (sem reenvio nem fila — o canal que falhou perde o aviso).
- **Estrutura que nasce:** nenhum contêiner novo; no **nível 3** do Painel Web/App Móvel: `ObservadorCardapio` (`docs/c4-cardapio.md`).
- **Evidência:** `TestesFronteira.testCardapioSoAMontagemConheceOsCanais` (só `MontagemCardapio` usa `ru.avisos`; o `Cardapio` original violava isso) e `TestesPedido4`.

## Alternativa descartada

**A · `Cardapio` chama cada canal:** simples e explícito, mas cada interessado novo reabre o `Cardapio`, que passa a conhecer todos os canais do sistema.
