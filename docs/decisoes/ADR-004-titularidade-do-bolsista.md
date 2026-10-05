# ADR-004 · Titularidade do bolsista por QR dinâmico, entrando por decoradores

**Status:** aceita · **Requisito que motivou:** RNF8 (pedido 3)

## Contexto

O RNF8 diz que a isenção do bolsista integral só pode ser usada pelo titular: carteirinha emprestada ou credencial copiada não pode liberar a entrada. Além disso, cada bolsista passa no máximo **uma vez por refeição**, inclusive com o enlace caído, sem estourar o RNF2 (≤ 1 s sem rede). Do jeito que está hoje (simulação sem rede), o Diego almoça de graça, duas vezes, com a carteirinha da Carla.

Percebi que são duas perguntas diferentes: *quem é a pessoa?* e *ela já passou nesta refeição?*

Pra garantir que quem passa é o titular, comparei três caminhos:

| Cenário | A · foto do titular ao operador | B · QR dinâmico assinado no app | C · biometria facial |
|---|---|---|---|
| RNF8 · impede empréstimo | em parte (depende da atenção do operador) | sim; o QR vale 30 s e é assinado com a chave do titular | sim |
| RNF2 · sem rede, ≤ 1 s | atende, mas a fila anda no ritmo do operador | atende (HMAC local, a chave está na réplica) | atende se os gabaritos estiverem na réplica |
| RNF6 / LGPD | foto é dado pessoal | só matrícula e instante; nenhum dado novo | biometria é dado pessoal **sensível** (LGPD art. 5º, II e art. 11): exige base legal, consentimento e segurança reforçada |
| Inclusão (e quem não tem celular?) | atende | **não atende** quem não tem celular ou bateria | exclui quem não aceita a coleta; falha com óculos e máscara |
| Custo | baixo, mas exige operador na porta | baixo (o app já gera o QR) | alto (câmeras/leitores, gabarito replicado em cada catraca) |

**Ponto de sensibilidade:** o meio usado pra provar a titularidade, que afeta segurança, privacidade e inclusão.
**Ponto de trade-off:** a segurança da isenção sobe, mas a inclusão cai (exige smartphone) e a memória de "quem já passou" fica só local.

## Decisão

Escolhi o **QR dinâmico assinado**. A prova de titularidade é um **identificador que envolve o identificador por carteirinha, com o mesmo contrato** (`IdentificadorComTitularidade`): ele aceita o QR se a assinatura HMAC bate com a chave local e o instante está a ≤ 30 s, e recusa a carteirinha quando o usuário é bolsista (quem paga continua entrando com carteirinha). Já a restrição "uma vez por refeição" é **uma regra de entrada que envolve outra** (`UmaPorRefeicao`, sobre `RegraEntrada`). As duas peças entram só pela `Montagem`; a `Catraca` não foi editada e não sabe o que é bolsista, QR, HMAC nem refeição. São dois Decoradores (composição + aberto/fechado). Como o contrato `Identificador` não muda, trocar o QR por foto ou biometria no futuro é só trocar a peça na montagem, mas aí vem tudo o que a biometria exigiria: base legal e consentimento pela LGPD, gabaritos replicados e um plano pra quem recusar.

## Consequências

- **Ganha:** o empréstimo de carteirinha de bolsista deixa de funcionar; a verificação é local e rápida (RNF2 preservado); não coleta nenhum dado novo (LGPD); e a Catraca fica intocada.
- **Paga:**
  - **Inclusão:** bolsista sem celular não consegue entrar, então precisa de um procedimento alternativo (por exemplo, atendimento presencial).
  - **Catracas sem enlace** não se enxergam, então o bolsista pode passar uma vez em cada catraca na mesma refeição até sincronizar.
  - A memória de "quem já passou" se perde se a catraca reinicia.
  - Os relógios das catracas precisam estar sincronizados (tolerância de 30 s).
  - Pilhas de camadas são mais difíceis de depurar, e a ordem do empacotamento importa.
- **Estrutura que nasce:** nenhum contêiner novo. No **nível 3** do App da Catraca ficam `IdentificadorComTitularidade` (envolve `Identificador`) e `UmaPorRefeicao` (envolve `RegraEntrada`) (`docs/c4-catraca.md`).
- **Evidência:** `TestesPedido3` (QR válido, expirado, adulterado, de outra chave, carteirinha de bolsista, uma por refeição, tempo, Catraca sem menção a bolsista/QR/HMAC/refeição), `TestesFronteira.testCatracaNaoConheceRegrasConcretas` e o `rodar simular --sem-rede` (a Carla libera, o Diego bloqueia e a Carla bloqueia na segunda vez).

## Alternativas descartadas

**A · foto ao operador:** não escala no pico, depende da atenção de uma pessoa e trata dado pessoal sem dar um ganho de segurança que eu consiga verificar com teste.
**C · biometria facial:** é a mais forte contra empréstimo, mas é dado pessoal sensível (LGPD), custa hardware e exige gabaritos replicados em cada catraca. Para o risco atual é desproporcional, e num protótipo eu não teria como provar que funciona. Continua possível depois, trocando a peça de identificação.
