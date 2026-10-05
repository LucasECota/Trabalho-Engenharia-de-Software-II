# ADR-002 · A regra de crédito depende de um contrato de pagamento do RU

**Status:** aceita · **Requisito que motivou:** RNF5 (pedido 1)

## Contexto

O RNF5 pede que a troca do gateway de pagamento (o novo contrato com a CobraFácil, que vence o da PagaPix no ano que vem) mude **um único arquivo** da API de Créditos, sem mexer nas regras de crédito e com os testes existentes passando.

Hoje o `ServicoCompra` fala a língua da PagaPix: centavos (`movePointRight(2)`), `createCharge`, `Status.PAID`/`DECLINED`, `chargeId`, `declineReason`. A CobraFácil é diferente em tudo: o valor é `BigDecimal`, o retorno é booleano (`RetornoCobranca`) e a falha técnica vem como exceção verificada (`CobraFacilErro`).

Pensei em duas saídas:

- **A · `if` no ServicoCompra:** algo como `if (gateway.equals("cobrafacil")) ...` dentro da regra de crédito. Já de cara eu teria que duplicar o fluxo de cobrança e tratar mais uma exceção verificada, e a cada fornecedor novo teria que reabrir o `ServicoCompra`.
- **B · contrato + adaptadores + fábrica:** a regra passa a depender da interface `Pagamento`; o `PagaPixAdapter` e o `CobraFacilAdapter` traduzem cada SDK; e a `FabricaPagamento` escolhe qual usar pelo nome.

| Cenário | A · `if` no ServicoCompra | B · contrato + adaptadores | Trade-off |
|---|---|---|---|
| RNF5 · troca de gateway em um único arquivo | não atende (edita a regra de crédito) | atende (só `MontagemCreditos`) | • |
| RNF1 · compra em ≤ 2 s no pico | atende | atende (uma chamada indireta a mais, desprezível) | |
| RNF3 · toda alteração de saldo auditada | atende, mas a mensagem de erro depende do SDK dentro da regra | atende; o adaptador já entrega o motivo pronto | • |
| Testar a regra de crédito sem o SDK | não (o SDK é criado dentro da classe) | sim (basta um `Pagamento` falso) | • |
| Quantidade de classes e de conceitos | menor | maior (5 classes/tipos novos) | • |

**Ponto de sensibilidade:** quem conhece o vocabulário do fornecedor (valor, status, erro) define o custo de trocar de gateway.
**Ponto de trade-off:** a mesma decisão melhora a modificabilidade (RNF5) e a testabilidade, mas piora a simplicidade: ficam mais classes e uma tradução pra manter por fornecedor.

## Decisão

Fiz o `ServicoCompra` depender de um **contrato do RU**, `Pagamento.cobrar(matrícula, valor, token) → ResultadoPagamento`, escrito no vocabulário do RU. Cada fornecedor ganha **uma classe que traduz** esse contrato pro SDK dele (valor, status e exceções), e **um único lugar** (`FabricaPagamento`) sabe qual classe concreta criar a partir do nome do gateway. A `MontagemCreditos` injeta o resultado no `ServicoCompra`. Em termos de padrões, é Adaptador (um por fornecedor) mais uma Fábrica simples; em termos de princípios, ocultamento de informação e inversão de dependências.

## Consequências

- **Ganha:** trocar o gateway padrão é mexer em uma linha da `MontagemCreditos` (`GATEWAY_PADRAO`); fornecedor novo é um adaptador mais uma linha na fábrica; e a regra de crédito dá pra testar sem SDK.
- **Paga:** mais classes e mais indireção; uma tradução pra manter por fornecedor; e o contrato só oferece o que é comum a todos, então o detalhe do erro original se perde (vira texto em `motivo`).
- **Estrutura que nasce:** nenhum contêiner novo, o nível 2 do C4 continua igual (tudo segue dentro da API de Créditos, que já falava com o gateway externo). O que nasce é no **nível 3**, dentro da API de Créditos: `Pagamento`, `ResultadoPagamento`, `PagaPixAdapter`, `CobraFacilAdapter` e `FabricaPagamento` (subpacote `ru.creditos.pagamento`; ver `docs/c4-creditos.md`).
- **Evidência:** `TestesFronteira.testCreditosSoOsAdaptadoresUsamGateways` (só `ru.creditos.pagamento` pode usar `ru.gateways`; ele falhou antes, com `ServicoCompra -> ru.gateways.pagapix...`, e passa depois) e `testAdaptadoresNaoConhecemARegraDeCredito`; além do `TestesPedido1` e do `rodar diff --desde-marco`, que mostrou só `MontagemCreditos.java` na troca para a CobraFácil.

## Alternativa descartada

**A · `if` no `ServicoCompra`:** é a mais simples hoje, mas me obrigaria a editar a regra de crédito a cada fornecedor, misturaria o vocabulário do SDK com a regra (o vazamento entra pela porta da frente) e não deixaria testar a regra sem o SDK. Não atende o RNF5.
