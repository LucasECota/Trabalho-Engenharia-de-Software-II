# ADR-002 · A regra de crédito depende de um contrato de pagamento do RU

**Status:** aceita · **Requisito que motivou:** RNF5 (pedido 1)

## Contexto

RNF5: trocar o gateway de pagamento (novo contrato com a CobraFácil, que vence o da PagaPix no ano que vem) deve alterar **um único arquivo** da API de Créditos, sem tocar nas regras de crédito, com os testes existentes passando.

Hoje `ServicoCompra` fala a língua da PagaPix: centavos (`movePointRight(2)`), `createCharge`, `Status.PAID`/`DECLINED`, `chargeId`, `declineReason`. A CobraFácil é diferente em tudo: valor em `BigDecimal`, retorno booleano (`RetornoCobranca`) e falha técnica como exceção verificada (`CobraFacilErro`).

- **A · `if` no ServicoCompra:** `if (gateway.equals("cobrafacil")) ...` dentro da regra de crédito. Hoje exige duplicar o fluxo de cobrança e tratar uma exceção verificada a mais; a cada fornecedor novo, reabrir `ServicoCompra`.
- **B · contrato + adaptadores + fábrica:** a regra depende da interface `Pagamento`; `PagaPixAdapter` e `CobraFacilAdapter` traduzem cada SDK; `FabricaPagamento` escolhe qual pelo nome.

| Cenário | A · `if` no ServicoCompra | B · contrato + adaptadores | Trade-off |
|---|---|---|---|
| RNF5 · troca de gateway em um único arquivo | não atende (edita a regra de crédito) | atende (só `MontagemCreditos`) | • |
| RNF1 · compra em ≤ 2 s no pico | atende | atende (uma chamada indireta a mais, desprezível) | |
| RNF3 · toda alteração de saldo auditada | atende, mas a mensagem de erro depende do SDK dentro da regra | atende; o adaptador entrega o motivo pronto | • |
| Testar a regra de crédito sem o SDK | não (o SDK é criado dentro da classe) | sim (basta um `Pagamento` falso) | • |
| Quantidade de classes e de conceitos | menor | maior (5 classes/tipos novos) | • |

**Ponto de sensibilidade:** quem conhece o vocabulário do fornecedor (valor, status, erro) determina o custo de trocar de gateway.
**Ponto de trade-off:** a mesma decisão melhora a modificabilidade (RNF5) e a testabilidade, e piora a simplicidade: mais classes e uma tradução a manter por fornecedor.

## Decisão

A regra de crédito (`ServicoCompra`) passa a depender de um **contrato do RU**, `Pagamento.cobrar(matrícula, valor, token) → ResultadoPagamento`, no vocabulário do RU. Cada fornecedor ganha **uma classe que traduz** o contrato para o SDK dele (valor, status e exceções), e **um único lugar** (`FabricaPagamento`) sabe qual classe concreta criar a partir do nome do gateway; `MontagemCreditos` injeta o resultado em `ServicoCompra`. Em vocabulário de padrões: Adaptador (um por fornecedor) + Fábrica simples; princípios: ocultamento de informação e inversão de dependências.

## Consequências

- **Ganha:** trocar o gateway padrão é uma linha em `MontagemCreditos` (`GATEWAY_PADRAO`); fornecedor novo = um adaptador + uma linha na fábrica; a regra de crédito é testável sem SDK.
- **Paga:** mais classes e indireção; uma tradução a manter por fornecedor; o contrato só oferece o que é comum a todos (o detalhe do erro original se perde — vira texto em `motivo`).
- **Estrutura que nasce:** nenhum contêiner novo (nível 2 do C4 inalterado: continua tudo dentro da API de Créditos, que já falava com o gateway externo). Nasce no **nível 3**, dentro da API de Créditos: `Pagamento`, `ResultadoPagamento`, `PagaPixAdapter`, `CobraFacilAdapter`, `FabricaPagamento` (subpacote `ru.creditos.pagamento`; ver `docs/c4-creditos.md`).
- **Evidência:** `TestesFronteira.testCreditosSoOsAdaptadoresUsamGateways` (só `ru.creditos.pagamento` pode usar `ru.gateways`; falhou antes com `ServicoCompra -> ru.gateways.pagapix...` e passa depois) e `testAdaptadoresNaoConhecemARegraDeCredito`; `TestesPedido1`; e `rodar diff --desde-marco` mostrando só `MontagemCreditos.java` na troca para a CobraFácil.

## Alternativa descartada

**A · `if` no `ServicoCompra`:** é a mais simples hoje, mas obriga a editar a regra de crédito a cada fornecedor, mistura o vocabulário do SDK com a regra (vazamento pela porta da frente) e impede testar a regra sem o SDK; não atende o RNF5.
