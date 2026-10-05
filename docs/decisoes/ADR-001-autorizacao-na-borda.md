# ADR-001 · Autorização de entrada na borda

**Status:** aceita · **Requisito que motivou:** RNF2 (antigo R3)

## Contexto

O enlace de rede entre o prédio do RU e o servidor central é instável. O RNF2
exige que, com o enlace indisponível, a catraca autorize ou negue a entrada em
até 1 s, e que as passagens cheguem ao servidor em até 5 min depois que o
enlace voltar, sem perder nenhuma.

Avaliamos duas alternativas contra os mesmos cenários:

| Cenário | A · cliente-servidor síncrona | B · autorização na borda |
|---|---|---|
| RNF1 · compra em ≤ 2 s no pico | atende | atende |
| RNF2 · decisão em ≤ 1 s sem enlace | **não atende** | **atende** |
| Custo do primeiro release | menor | maior |
| Esforço para depurar saldo divergente | baixo | alto |
| Indisponibilidade percebida na porta | alta | quase nula |
| Complexidade de implantação e monitoramento | baixa | alta |

**Ponto de sensibilidade:** onde a autorização é processada.
**Ponto de trade-off:** a mesma decisão melhora a disponibilidade e piora a
manutenibilidade e a simplicidade.

## Decisão

Processar a autorização **na catraca**, a partir de uma réplica local de saldos,
registrando cada passagem numa fila persistente que um sincronizador envia ao
servidor quando o enlace estiver disponível.

## Consequências

- **Ganha:** disponibilidade na porta mesmo sem rede; latência baixa na decisão.
- **Paga:** estado duplicado (réplica × banco central); um caminho de
  sincronização a mais para construir, testar e monitorar; saldo pode ficar
  temporariamente divergente, e duas catracas sem enlace não se enxergam — exige
  política de conflito e auditoria na sincronização.
- **Estrutura que nasce:** a catraca deixa de ser sistema externo e vira
  contêiner nosso, com Réplica local, Fila de passagens e Sincronizador
  (ver `docs/c4-catraca.md`).
- **Evidência:** `test/ru/testes/TestesFronteira.java` — só o Sincronizador pode usar o pacote
  `ru.rede`. Se a autorização passar a depender da rede, o teste fica vermelho.

## Alternativa descartada

**A · cliente-servidor síncrona**: mais simples e mais barata, mas a catraca para
quando o enlace cai — não atende o RNF2.
