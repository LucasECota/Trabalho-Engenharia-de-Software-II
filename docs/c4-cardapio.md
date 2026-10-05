# C4 nível 3 · Cardápio e avisos

## Como estava (dado)

```mermaid
flowchart LR
    card["Cardapio"]
    app["AppAluno"]
    painel["PainelWeb"]
    email["EmailGestao"]
    card --> app
    card --> painel
    card --> email
```

## Depois do pedido 4 (ADR-005)

```mermaid
flowchart LR
    card["Cardapio<br/><small>sujeito: guarda os assinantes [RNF10]</small>"]
    obs["«interface» ObservadorCardapio<br/><small>[RNF10]</small>"]
    mont["MontagemCardapio<br/><small>faz as inscrições [RNF10]</small>"]
    app["AppAluno"]
    painel["PainelWeb"]
    email["EmailGestao"]
    dce["(futuro) bot do DCE<br/><small>[RNF10] uma linha na montagem</small>"]

    card --> obs
    mont --> card
    mont --> app
    mont --> painel
    mont --> email
    app -. "semanaPublicada" .-> obs
    painel -. "semanaPublicada" .-> obs
    email -. "semanaPublicada" .-> obs
    dce -. "semanaPublicada" .-> obs
```

(Na implementação os canais são assinados por referência de método, então satisfazem
`ObservadorCardapio` sem implementar a interface explicitamente.)

**Fronteira:** nenhuma seta de `Cardapio` para os canais; só `MontagemCardapio` os
conhece — `TestesFronteira.testCardapioSoAMontagemConheceOsCanais`.
