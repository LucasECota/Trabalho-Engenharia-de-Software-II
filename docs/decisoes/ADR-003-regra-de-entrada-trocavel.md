# ADR-003 · A Catraca recebe a regra de entrada pronta

**Status:** aceita · **Requisito que motivou:** RNF9 (pedido 2)

## Contexto

O RNF9 diz que a regra de entrada é escolhida na montagem, a partir da configuração, entre `SALDO` (saldo ≥ tarifa) e `TOLERA_NEGATIVO` (saldo ≥ −R$ 5,00 depois de pagar), e que acrescentar uma regra nova **não pode alterar a classe `Catraca`**. O conselho muda a regra de tempos em tempos. A decisão continua sob o RNF2 (decidir em ≤ 1 s sem rede), então a regra roda local, em cima da réplica.

Duas opções:

- **A · mais um `case` no `switch` da Catraca:** a Catraca continua lendo a `String` da configuração e comparando saldos.
- **B · a regra vira objeto:** uma interface `RegraEntrada`, uma classe por regra, e a Catraca só chama; quem traduz o texto em objeto é a montagem.

| Cenário | A · `switch` na Catraca | B · `RegraEntrada` injetada | Trade-off |
|---|---|---|---|
| RNF9 · regra nova sem alterar a Catraca | não atende | atende | • |
| RNF2 · decisão em ≤ 1 s sem rede | atende | atende (uma chamada virtual a mais) | |
| Testar cada regra isolada | difícil (precisa montar a Catraca inteira) | fácil (`avaliar(usuario, tarifa)`) | • |
| Quantidade de tipos / facilidade de ler o fluxo | menos tipos, fluxo num lugar só | mais tipos, regra fica em outro arquivo | • |

**Ponto de sensibilidade:** onde mora o critério de entrada, o que afeta a modificabilidade da classe mais crítica da porta.
**Ponto de trade-off:** modificabilidade e testabilidade sobem; a facilidade de ler o fluxo cai.

## Decisão

A `Catraca` **recebe uma `RegraEntrada` já pronta, no construtor**, e só chama ela. A `RegraEntrada` é um contrato com um único método ("esse usuário pode entrar pagando essa tarifa?"). Cada regra virou uma classe (`SaldoSuficiente`, `ToleraSaldoNegativo(limite)`), e o texto da configuração só é traduzido em objeto dentro de `Montagem.criarCatraca`. É ali também que um valor desconhecido é recusado (`IllegalArgumentException`), na hora de montar e não na hora da passagem. É o padrão Strategy, e o princípio por trás é o aberto/fechado.

## Consequências

- **Ganha:** regra nova é uma classe nova mais uma linha na `Montagem`; cada regra dá pra testar sozinha; a Catraca perdeu o `switch`, o `"SALDO"` e o `compareTo`; e uma configuração inválida derruba o sistema ao subir, e não com uma pessoa esperando na fila.
- **Paga:** mais tipos; quem monta precisa saber qual estratégia escolher; quem lê o fluxo de autorização enfrenta um nível a mais de indireção; e o limite de R$ 5,00 ficou como constante na montagem (não dá pra configurar por arquivo).
- **Estrutura que nasce:** nenhum contêiner novo. O que aparece é no **nível 3** do App da Catraca (dentro de um contêiner que já existia): a `RegraEntrada` e suas implementações (`docs/c4-catraca.md`).
- **Evidência:** `TestesPedido2` (incluindo o limite exato e a Catraca sem `switch`/`compareTo`) e `TestesFronteira.testCatracaNaoConheceRegrasConcretas`.

## Alternativa descartada

**A · mais um `case` no `switch`:** é simples e rápida hoje, mas toda regra aprovada pelo conselho reabriria a classe mais crítica da porta e misturaria critério de negócio com orquestração, o que vai contra o RNF9.
