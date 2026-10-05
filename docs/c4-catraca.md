# C4 nível 3 · App da Catraca

## Como está (exemplo resolvido do ADR-001)

Cada seta vai de quem usa para quem é usado. Entre colchetes, o cenário da
especificação v2 que justifica o componente.

```mermaid
flowchart LR
    leitor["Leitor<br/><small>protocolo do hardware</small>"]
    ident["Identificador<br/><small>quem é a pessoa</small>"]
    cat["Catraca<br/><small>autorização</small>"]
    rep["Réplica local<br/><small>saldos e tarifas · [RNF2]</small>"]
    fila["Fila de passagens<br/><small>formato em disco · [RNF2]</small>"]
    sinc["Sincronizador<br/><small>protocolo com o servidor · [RNF2]</small>"]
    mont["Montagem<br/><small>escolhe as peças</small>"]
    srv[("Serviço de Acesso<br/>(contêiner na nuvem)")]

    cat --> leitor
    cat --> ident
    cat --> rep
    cat --> fila
    sinc --> fila
    mont --> cat
    sinc -. "HTTPS, quando há enlace" .-> srv
```

**Fronteira que o diagrama afirma:** nenhuma seta sai da Catraca em direção à
rede. Só o Sincronizador cruza a borda — e `TestesFronteira` prova isso.

## Depois dos pedidos 2 e 3 (ADR-003 e ADR-004)

```mermaid
flowchart LR
    leitor["Leitor"]
    cat["Catraca<br/><small>só orquestra</small>"]
    ident["«interface» Identificador"]
    porCart["IdentificadorPorCarteirinha<br/><small>carteirinha (pagantes)</small>"]
    titular["IdentificadorComTitularidade<br/><small>Decorador · QR assinado, recusa carteirinha de bolsista · [RNF8]</small>"]
    regra["«interface» RegraEntrada<br/><small>[RNF9]</small>"]
    saldo["SaldoSuficiente<br/><small>[RNF9]</small>"]
    tolera["ToleraSaldoNegativo<br/><small>[RNF9]</small>"]
    uma["UmaPorRefeicao<br/><small>Decorador · bolsista 1×/refeição · [RNF8]</small>"]
    rep["Réplica local<br/><small>[RNF2] · chave do QR [RNF8]</small>"]
    fila["Fila de passagens · [RNF2]"]
    mont["Montagem<br/><small>traduz a configuração e empilha os decoradores</small>"]

    cat --> leitor
    cat --> ident
    cat --> regra
    cat --> rep
    cat --> fila
    porCart -. implementa .-> ident
    titular -. implementa .-> ident
    titular --> ident
    titular --> rep
    saldo -. implementa .-> regra
    tolera -. implementa .-> regra
    uma -. implementa .-> regra
    uma --> regra
    mont --> cat
    mont --> titular
    mont --> porCart
    mont --> uma
    mont --> saldo
    mont --> tolera
```

A `Catraca` só depende das interfaces `Identificador` e `RegraEntrada`; todas as peças
novas entram por `Montagem`. Os decoradores usam apenas dados locais (réplica e
relógio), então o RNF2 continua valendo sem rede.
