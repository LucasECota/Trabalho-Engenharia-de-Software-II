# ADR-003 · A Catraca recebe a regra de entrada pronta

**Status:** aceita · **Requisito que motivou:** RNF9 (pedido 2)

## Contexto

RNF9: a regra de entrada é escolhida na montagem a partir da configuração — `SALDO` (saldo ≥ tarifa) ou `TOLERA_NEGATIVO` (saldo ≥ −R$ 5,00 após pagar) — e incluir uma regra nova **não altera a classe `Catraca`**. O conselho muda a regra de tempos em tempos. A decisão continua sob o RNF2 (decidir em ≤ 1 s sem rede): a regra roda local, sobre a réplica.

- **A · mais um `case` no `switch` da Catraca:** a Catraca continua lendo a `String` de configuração e comparando saldos.
- **B · a regra vira objeto:** interface `RegraEntrada`, uma classe por regra, a Catraca só a chama; a montagem traduz o texto em objeto.

| Cenário | A · `switch` na Catraca | B · `RegraEntrada` injetada | Trade-off |
|---|---|---|---|
| RNF9 · regra nova sem alterar a Catraca | não atende | atende | • |
| RNF2 · decisão em ≤ 1 s sem rede | atende | atende (uma chamada virtual a mais) | |
| Testabilidade de cada regra isolada | baixa (precisa montar a Catraca inteira) | alta (`avaliar(usuario, tarifa)`) | • |
| Quantidade de tipos / facilidade de ler o fluxo | menor, fluxo num lugar só | maior, regra fica em outro arquivo | • |

**Ponto de sensibilidade:** onde mora o critério de entrada (afeta a modificabilidade da classe mais crítica da porta).
**Ponto de trade-off:** modificabilidade e testabilidade sobem; simplicidade de leitura cai.

## Decisão

A `Catraca` **recebe pronta, no construtor, uma `RegraEntrada`** (contrato com um método: "este usuário pode entrar pagando esta tarifa?") e só a chama. Cada regra é uma classe (`SaldoSuficiente`, `ToleraSaldoNegativo(limite)`); o texto da configuração é traduzido em objeto **somente** em `Montagem.criarCatraca`, que também recusa valor desconhecido na montagem (`IllegalArgumentException`), não na hora da passagem. É o padrão Strategy; o princípio é aberto/fechado.

## Consequências

- **Ganha:** regra nova = classe nova + uma linha em `Montagem`; cada regra é testável isoladamente; a Catraca perde `switch`, `"SALDO"` e `compareTo`; configuração inválida falha ao subir, não com uma pessoa na fila.
- **Paga:** mais tipos; quem monta precisa saber qual estratégia escolher; um nível de indireção para quem lê o fluxo da autorização; o limite de R$ 5,00 ficou como constante na montagem (não é configurável por arquivo).
- **Estrutura que nasce:** nenhum contêiner novo; aparece no **nível 3** do App da Catraca (interior de um contêiner existente): `RegraEntrada` e suas implementações (`docs/c4-catraca.md`).
- **Evidência:** `TestesPedido2` (inclusive o limite exato e a Catraca sem `switch`/`compareTo`) e `TestesFronteira.testCatracaNaoConheceRegrasConcretas`.

## Alternativa descartada

**A · mais um `case` no `switch`:** simples e rápida hoje, mas cada regra aprovada reabre a classe mais crítica da porta e mistura critério de negócio com orquestração, contrariando o RNF9.
