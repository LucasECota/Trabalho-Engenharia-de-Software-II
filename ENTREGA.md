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

- Lucas Emanuel Cota Carneiro (24.1.8980): fiz a atividade sozinho, do começo ao fim (os quatro ciclos, os ADRs, os diagramas C4, o SEGREDOS e os testes de fronteira). Usei o Claude Code, um assistente de IA, como ferramenta de apoio, como o roteiro permite.

## Reflexão (até 12 linhas)

1. **Código novo ou edição?** Em todos os pedidos a parte nova entrou como código novo: adaptadores e fábrica (1), regras de entrada (2), decoradores (3) e a interface do observador (4). Mesmo assim, tive que editar código existente em dois casos: o que tinha a decisão "chumbada" lá dentro (`ServicoCompra` criava o SDK da PagaPix, a `Catraca` tinha o `switch` da regra e o `Cardapio` criava os três canais) e as classes de montagem, porque escolher qual peça usar é o trabalho delas. Essa edição se paga uma vez só: trocar o gateway foi mudar uma linha (o `diff --desde-marco` mostrou apenas `MontagemCreditos.java`).

2. **Qual padrão seria cerimônia?** O Strategy do pedido 2, se o conselho nunca fosse mudar a regra: com um único `case`, seria só indireção sem motivo. A Fábrica do pedido 1 também seria exagero se a PagaPix fosse o fornecedor pra sempre. O que justifica o padrão é a mudança que ele absorve (o RNF9 e o RNF5 dizem que ela vai acontecer), e não o padrão em si.

3. **Decisão do ADR-001 que permitiu o RNF8 sem editar a Catraca:** a de autorizar na borda, com uma réplica local. Por causa do RNF2, a catraca já guarda localmente os usuários e a chave do QR de cada pessoa (`Replica.chaveQr`), o que me deixou verificar o HMAC sem rede. Junto com a injeção de dependências pela `Montagem`, isso permitiu empilhar os dois decoradores fora da `Catraca`, que só conhece as interfaces `Identificador` e `RegraEntrada`.
