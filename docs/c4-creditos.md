# C4 nível 3 · API de Créditos

## Como estava (dado)

```mermaid
flowchart LR
    compra["ServicoCompra<br/><small>regra de crédito</small>"]
    saldo["Saldos"]
    aud["Auditoria · [RNF3]"]
    mont["MontagemCreditos"]
    pagapix[("SDK PagaPix<br/>(sistema externo)")]

    compra --> saldo
    compra --> aud
    compra --> pagapix
    mont --> compra
```

## Depois do pedido 1 (ADR-002)

```mermaid
flowchart LR
    compra["ServicoCompra<br/><small>regra de crédito</small>"]
    saldo["Saldos"]
    aud["Auditoria · [RNF3]"]
    mont["MontagemCreditos<br/><small>[RNF5] único arquivo a mudar</small>"]
    pag["«interface» Pagamento<br/><small>[RNF5] contrato do RU</small>"]
    res["ResultadoPagamento<br/><small>[RNF3] motivo pronto p/ auditoria</small>"]
    fab["FabricaPagamento<br/><small>[RNF5] único que conhece os adaptadores</small>"]
    ppa["PagaPixAdapter<br/><small>[RNF5] contrato atual</small>"]
    cfa["CobraFacilAdapter<br/><small>[RNF5] novo contrato</small>"]
    pagapix[("SDK PagaPix")]
    cobra[("SDK CobraFácil")]

    mont --> compra
    mont --> fab
    compra --> saldo
    compra --> aud
    compra --> pag
    pag --> res
    fab --> ppa
    fab --> cfa
    ppa -. implementa .-> pag
    cfa -. implementa .-> pag
    ppa --> pagapix
    cfa --> cobra
```

**Fronteira que o diagrama afirma:** nenhuma seta sai da regra de crédito para um SDK;
só os adaptadores (`ru.creditos.pagamento`) cruzam para `ru.gateways` — provado por
`TestesFronteira.testCreditosSoOsAdaptadoresUsamGateways`.
