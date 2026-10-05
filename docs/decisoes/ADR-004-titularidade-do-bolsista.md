# ADR-004 · Titularidade do bolsista por QR dinâmico, entrando por decoradores

**Status:** aceita · **Requisito que motivou:** RNF8 (pedido 3)

## Contexto

RNF8: a isenção do bolsista integral só pode ser usada pelo titular — carteirinha emprestada ou credencial copiada não autoriza a entrada — e cada bolsista passa no máximo **uma vez por refeição**, também com o enlace caído, sem violar o RNF2 (≤ 1 s sem rede). Hoje (simulação sem rede) o Diego almoça de graça, duas vezes, com a carteirinha da Carla. Há duas perguntas separadas: *quem é a pessoa?* e *ela já passou nesta refeição?*

Alternativas para garantir que quem passa é o titular:

| Cenário | A · foto do titular ao operador | B · QR dinâmico assinado no app | C · biometria facial |
|---|---|---|---|
| RNF8 · impede empréstimo | parcial (depende da atenção do operador) | sim; QR vale 30 s e é assinado com chave do titular | sim |
| RNF2 · sem rede, ≤ 1 s | atende, mas a fila anda no ritmo do operador | atende (HMAC local, a chave está na réplica) | atende se os gabaritos estiverem na réplica |
| RNF6 / LGPD | foto é dado pessoal | só matrícula e instante; nenhum dado novo | dado biométrico é dado pessoal **sensível** (LGPD art. 5º, II e art. 11): base legal, consentimento, segurança reforçada |
| Inclusão (quem não tem celular?) | atende | **não atende** quem não tem celular ou bateria | exclui quem não aceita a coleta; falha com óculos, máscara |
| Custo | baixo, mas exige operador na porta | baixo (app já gera o QR) | alto (câmeras/leitores, gabarito replicado em cada catraca) |

**Ponto de sensibilidade:** o meio pelo qual se prova a titularidade (afeta segurança, privacidade e inclusão).
**Ponto de trade-off:** segurança da isenção sobe; inclusão cai (exige smartphone) e a memória de "quem já passou" é local.

## Decisão

Escolhemos o **QR dinâmico assinado**. A prova de titularidade é um **identificador que envolve o identificador por carteirinha com o mesmo contrato** (`IdentificadorComTitularidade`): aceita o QR se a assinatura HMAC bate com a chave local e o instante está a ≤ 30 s, e recusa a carteirinha quando o usuário é bolsista (pagantes seguem entrando com carteirinha). A restrição "uma vez por refeição" é **uma regra de entrada que envolve outra** (`UmaPorRefeicao`, sobre `RegraEntrada`). As duas peças entram só por `Montagem`; a `Catraca` não foi editada e não conhece bolsista, QR, HMAC nem refeição. São dois Decoradores (composição + aberto/fechado). Como o contrato `Identificador` não muda, trocar o QR por foto ou biometria depois é trocar a peça na montagem — com o que a biometria exigiria (base legal e consentimento LGPD, gabaritos replicados, plano para quem recusa).

## Consequências

- **Ganha:** o empréstimo de carteirinha de bolsista deixa de funcionar; a verificação é local e rápida (RNF2 preservado); sem coleta de dado novo (LGPD); a Catraca fica intocada.
- **Paga:** **inclusão** — bolsista sem celular não consegue entrar (precisa de procedimento alternativo, ex.: atendimento presencial); **duas catracas sem enlace** não se enxergam, então o bolsista pode passar uma vez em cada catraca na mesma refeição até sincronizar; a memória de "quem já passou" é perdida se a catraca reinicia; relógios das catracas precisam estar sincronizados (tolerância 30 s); pilhas de camadas são mais difíceis de depurar e a ordem do empacotamento importa.
- **Estrutura que nasce:** nenhum contêiner novo; no **nível 3** do App da Catraca, `IdentificadorComTitularidade` (envolve `Identificador`) e `UmaPorRefeicao` (envolve `RegraEntrada`) (`docs/c4-catraca.md`).
- **Evidência:** `TestesPedido3` (QR válido, expirado, adulterado, outra chave, carteirinha de bolsista, uma por refeição, tempo, Catraca sem menção a bolsista/QR/HMAC/refeição), `TestesFronteira.testCatracaNaoConheceRegrasConcretas` e `rodar simular --sem-rede` (Carla libera, Diego bloqueia, Carla bloqueia na 2ª vez).

## Alternativas descartadas

**A · foto ao operador:** não escala no pico, depende de atenção humana e trata dado pessoal sem ganho de segurança verificável por teste.
**C · biometria facial:** é a mais forte contra empréstimo, mas é dado pessoal sensível (LGPD), custa hardware e exige gabaritos replicados em cada catraca; desproporcional para o risco atual e inviável de provar em um protótipo. Fica possível depois, trocando a peça de identificação.
