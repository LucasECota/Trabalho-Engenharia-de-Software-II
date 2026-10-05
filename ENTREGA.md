# Entrega · RU Digital v2

Grupo (individual): Lucas Emanuel Cota Carneiro – 24.1.8980

## Saída de `rodar testes` (só o resumo do topo e a última linha)

```
funcionais  10 testes · OK
fronteira    6 testes · OK
pedido1      7 testes · OK
pedido2      5 testes · OK
pedido3      9 testes · OK
pedido4      3 testes · OK
Rodados 40 testes: 40 ok, 0 falhas, 0 erros
```

## Saída de `rodar simular --sem-rede`

```
Enlace: CAÍDO

Ana, carteirinha                             LIBERA    ok                         1.83 ms
Bruno (saldo R$ 1,00), carteirinha           BLOQUEIA  saldo insuficiente         0.03 ms
Carla (bolsista), QR do próprio app          LIBERA    ok                         8.03 ms
Diego com a carteirinha EMPRESTADA da Carla  BLOQUEIA  credencial não aceita      0.03 ms
Diego de novo, 1 min depois                  BLOQUEIA  credencial não aceita      0.01 ms
Carla volta com o QR, na mesma refeição      BLOQUEIA  já passou nesta refeição   1.29 ms

Passagens na fila local: 2
Sincronizadas agora: 0 | pendentes: 2

O enlace volta...
Sincronizadas agora: 2 | pendentes: 0
```

## Saída de `rodar diff --desde-marco` (na troca do gateway padrão)

```
Mudanças desde o marco:

Alterados (1)
   src/ru/creditos/MontagemCreditos.java
Novos (0)
Removidos (0)
```

## Quem fez o quê (uma linha por integrante)

- Lucas Emanuel Cota Carneiro (24.1.8980) — único integrante: executou a atividade individualmente (os quatro ciclos, ADRs, diagramas C4, SEGREDOS e testes de fronteira), usando o Claude Code (assistente de IA) como ferramenta de apoio, conforme permitido no roteiro.

## Reflexão (até 12 linhas)

1. **Código novo ou edição?** Em todos os pedidos a parte nova entrou como código novo: adaptadores e fábrica (pedido 1), regras de entrada (2), os dois decoradores (3) e a interface do observador (4). Mesmo assim houve edição, e foi inevitável em dois tipos de lugar. O primeiro é o código que ainda tinha a decisão "chumbada" dentro: `ServicoCompra` criava o SDK da PagaPix, `Catraca` tinha o `switch` da regra e `Cardapio` criava os três canais. Para tirar a decisão de lá era preciso mexer neles uma vez. O segundo são as classes de montagem (`MontagemCreditos`, `Montagem`, `MontagemCardapio`), porque escolher qual peça usar é justamente o trabalho delas. A diferença é que essa edição é paga uma única vez: depois dela, trocar o gateway foi mudar uma linha (`diff --desde-marco` mostrou só `MontagemCreditos.java`), e a regra nova ou o canal novo são uma classe mais uma linha na montagem.

2. **Qual padrão seria cerimônia?** O Strategy do pedido 2, se o conselho nunca fosse mudar a regra: com um único `case`, uma interface e duas classes seriam só indireção. A Fábrica do pedido 1 também seria exagero se a PagaPix fosse o fornecedor para sempre. Em ambos, o que justifica o padrão é a mudança que ele absorve (o RNF9 e o RNF5 dizem que ela vai acontecer), não o padrão em si. Já o Observador e o Decorador respondem a mudanças que o enunciado também antecipa (o DCE e outros pedindo aviso; a regra do bolsista somada à de saldo).

3. **Decisão do ADR-001 que permitiu o RNF8 sem editar a Catraca:** a de autorizar na borda, com uma réplica local. Por causa do RNF2 (decidir em até 1 s sem rede), a catraca já guarda localmente os usuários, os saldos e a chave do QR de cada pessoa (`Replica.chaveQr`). Isso permitiu verificar o HMAC do QR sem rede. Junto com a injeção de dependências pela `Montagem`, também permitiu que os dois decoradores (`IdentificadorComTitularidade` e `UmaPorRefeicao`) fossem empilhados fora da `Catraca`, que só conhece as interfaces `Identificador` e `RegraEntrada`.
